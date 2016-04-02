package accelerate.batch;

import static accelerate.util.AppUtil.isEmpty;
import static accelerate.util.CollectionUtil.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import accelerate.exception.AccelerateException;
import accelerate.util.DataMap;
import accelerate.util.JSONUtil;

/**
 * Wrapper class providing easy-to-use version of spring's
 * {@link ThreadPoolTaskExecutor}
 *
 * @param <T>
 *            extension of {@link AccelerateTask} that this batch handles
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Feb 12, 2010
 */
@ManagedResource(description = "Wrapper class providing easy-to-use version of spring's ThreadPoolTaskExecutor")
public class AccelerateBatch<T extends AccelerateTask> {
	/**
	 * Name of the Batch
	 */
	private String batchName = null;

	/**
	 * Maximum thread pool size for a multi threaded batch
	 */
	private int threadPoolSize = 0;

	/**
	 * Flag to indicate multi threading is enabled
	 */
	private boolean multiThreadingEnabled = true;

	/**
	 * Flag to indicate whether this instance is active or not
	 */
	private boolean active = false;

	/**
	 * Flag to indicate whether the batch is currently paused
	 */
	private boolean paused = false;

	/**
	 * Semaphore for pausing the tasks
	 */
	private Object monitor = new Object();

	/**
	 * {@link Map} of tasks submitted to the batch
	 */
	private Map<String, T> pendingTasks = null;

	/**
	 * {@link Map} of tasks currently being processed by the batch
	 */
	private Map<String, T> activeTasks = null;

	/**
	 * Count of tasks processed by this batch
	 */
	private long completedTaskCount = 0l;

	/**
	 * {@link AccelerateTask} instance currently executing if MULTI_THREADING is
	 * disabled
	 */
	private T currentTask = null;

	/**
	 * {@link ThreadPoolTaskExecutor} instance
	 */
	private ThreadPoolTaskExecutor executor = null;

	/**
	 * {@link BETaskEventListener} instance to keep counters updated
	 */
	private BETaskEventListener batchEventListener = null;

	/**
	 * Default constructor
	 *
	 * @param aBatchName
	 * @param aThreadPoolSize
	 */
	public AccelerateBatch(String aBatchName, int aThreadPoolSize) {
		this(aBatchName, aThreadPoolSize, true);
	}

	/**
	 * Overloaded constructor to disable multithreading
	 *
	 * @param aBatchName
	 * @param aThreadPoolSize
	 * @param aEnableMultiThreading
	 */
	public AccelerateBatch(String aBatchName, int aThreadPoolSize, boolean aEnableMultiThreading) {
		this.batchName = aBatchName;
		this.threadPoolSize = aThreadPoolSize;
		this.multiThreadingEnabled = aEnableMultiThreading;
	}

	/**
	 * This methods activates the batch. It is setup as a @PostConstruct method
	 * to allow automatic activation on spring initialization.
	 *
	 * @throws AccelerateException
	 */
	@PostConstruct
	@ManagedOperation(description = "This methods activates the batch")
	public synchronized void activate() throws AccelerateException {
		if (this.active) {
			throw new AccelerateException(
					"Batch is already active! Invoke shutdown() to close the batch before reinitializing");
		}

		this.executor = new ThreadPoolTaskExecutor();
		this.executor.setCorePoolSize(this.threadPoolSize);
		this.executor.setMaxPoolSize(this.threadPoolSize);
		this.executor.setThreadGroupName(this.batchName);
		this.executor.setThreadNamePrefix("AccelerateTask");
		this.executor.initialize();

		this.batchEventListener = new BETaskEventListener();
		this.pendingTasks = new HashMap<>();
		this.activeTasks = new HashMap<>();
		this.completedTaskCount = 0l;
		this.active = true;
	}

	/**
	 * This method tries to shutdown the current batch instance immediately
	 */
	@ManagedOperation(description = "This method tries to shutdown the current batch instance immediately")
	public synchronized void shutdownNow() {
		this.active = false;
		this.executor.getThreadPoolExecutor().shutdownNow();
	}

	/**
	 * This method shuts down the current batch instance and blocks the caller
	 * till it shuts down or the timeout provided elapses, whichever is earlier.
	 *
	 * @param aTimeUnit
	 * @param aTimeout
	 * @throws AccelerateException
	 */
	@ManagedOperation(description = "This method shuts down the current batch instance and blocks the caller till it shuts down or the timeout provided elapses, whichever is earlier.")
	public synchronized void shutdown(String aTimeUnit, long aTimeout) throws AccelerateException {
		TimeUnit timeUnit = TimeUnit.valueOf(aTimeUnit);

		this.active = false;
		this.executor.getThreadPoolExecutor().shutdown();
		try {
			this.executor.getThreadPoolExecutor().awaitTermination(aTimeout, timeUnit);
		} catch (IllegalStateException | InterruptedException error) {
			throw new AccelerateException(error);
		}
	}

	/**
	 * This method handles a pause interrupt via by JMX and attempts to pause
	 * the execution of all running tasks
	 */
	@ManagedOperation(description = "This method handles a pause interrupt sent by JMX and attempts to pause the execution of all running tasks")
	public synchronized void pause() {
		this.paused = true;

		if (this.multiThreadingEnabled) {
			this.pendingTasks.values().forEach(task -> task.pause(this.monitor));
			this.activeTasks.values().forEach(task -> task.pause(this.monitor));
		} else {
			this.currentTask.pause(this.monitor);
		}
	}

	/**
	 * This method handles a resume interrupt via by the JMX and attempts to
	 * resume the execution of all paused tasks
	 */
	@ManagedOperation(description = "This method handles a resume interrupt via by the JMX and attempts to resume the execution of all paused tasks")
	public synchronized void resume() {
		this.paused = false;

		if (this.multiThreadingEnabled) {
			this.pendingTasks.values().forEach(task -> task.resume());
			this.activeTasks.values().forEach(task -> task.resume());
		} else {
			this.currentTask.resume();
		}

		synchronized (this.monitor) {
			this.monitor.notifyAll();
		}
	}

	/**
	 * This method returns JSON string with basic status information on the
	 * batch
	 *
	 * @return JSON string
	 */
	@ManagedOperation(description = "This method returns JSON string with basic status information on the batch")
	public String getStatus() {
		DataMap dataMap = new DataMap();
		dataMap.addData("1.name", this.batchName, "2.multithreaded", this.multiThreadingEnabled, "3.poolSize",
				this.threadPoolSize, "4.active", this.active, "5.completedTasks", this.completedTaskCount,
				"6.activeTasks", this.activeTasks.size(), "7.waitingTasks", this.pendingTasks.size());

		return JSONUtil.serialize(dataMap);
	}

	/**
	 * @param aTaskList
	 * @throws AccelerateException
	 */
	@SafeVarargs
	public final void submitTasks(T... aTaskList) throws AccelerateException {
		submitTasks(toList(aTaskList));
	}

	/**
	 * @param aTaskList
	 * @throws AccelerateException
	 */
	public final synchronized void submitTasks(List<T> aTaskList) throws AccelerateException {
		if (!this.active) {
			throw new AccelerateException(
					"Batch has been shutdown! Invoke activate() reinitialize the batch before submitting tasks");
		}

		if (isEmpty(aTaskList)) {
			throw new AccelerateException("No tasks provided!");
		}

		aTaskList.forEach(task -> this.pendingTasks.put(task.getTaskKey(), task));

		for (T task : aTaskList) {
			task.registerTaskEventListener(this.batchEventListener);

			if (this.paused) {
				task.pause(this.monitor);
			}

			if (this.multiThreadingEnabled) {
				task.setFuture(this.executor.submit(task));
			} else {
				this.currentTask = task;
				this.activeTasks.put(task.getTaskKey(), this.pendingTasks.get(task.getTaskKey()));
				task.call();
				this.completedTaskCount++;
			}
		}
	}

	/**
	 * @param aTask
	 */
	protected synchronized void beforeStart(T aTask) {
		this.activeTasks.put(aTask.getTaskKey(), this.pendingTasks.remove(aTask.getTaskKey()));
	}

	/**
	 * @param aTask
	 */
	protected synchronized void afterComplete(T aTask) {
		this.activeTasks.remove(aTask.getTaskKey());
		this.completedTaskCount++;
	}

	/**
	 * Getter method for "executor" property
	 *
	 * @return executorService
	 */
	public ThreadPoolTaskExecutor getSpringExecutor() {
		return this.executor;
	}

	/**
	 * Getter method for underlying {@link ThreadPoolExecutor}
	 *
	 * @return executorService
	 */
	public ExecutorService getJavaExecutor() {
		return this.executor.getThreadPoolExecutor();
	}

	/**
	 * Getter method for "multiThreadingEnabled" property
	 *
	 * @return multiThreadingEnabled
	 */
	public synchronized boolean isMultiThreadingEnabled() {
		return this.multiThreadingEnabled;
	}

	/**
	 * Getter method for "threadPoolSize" property
	 *
	 * @return threadPoolSize
	 */
	public synchronized int getThreadPoolSize() {
		return this.threadPoolSize;
	}

	/**
	 * Getter method for "allTasks" property
	 *
	 * @return allTasks
	 */
	public synchronized Map<String, T> getPendingTasks() {
		return this.pendingTasks;
	}

	/**
	 * Getter method for "activeTasks" property
	 *
	 * @return activeTasks
	 */
	public synchronized Map<String, T> getActiveTasks() {
		return this.activeTasks;
	}

	/**
	 * Getter method for "completedTaskCount" property
	 *
	 * @return completedTaskCount
	 */
	public synchronized long getCompletedTaskCount() {
		return this.completedTaskCount;
	}

	/**
	 * Getter method for "activeTaskCount" property
	 *
	 * @return activeTaskCount
	 */
	public synchronized long getActiveTaskCount() {
		return this.activeTasks.size();
	}

	/**
	 * Getter method for "waitingTaskCount" property
	 *
	 * @return waitingTaskCount
	 */
	public synchronized long getWaitingTaskCount() {
		return this.pendingTasks.size();
	}

	/**
	 * Getter method for "active" property
	 *
	 * @return active
	 */
	public synchronized boolean isActive() {
		return this.active;
	}

	/**
	 * Getter method for "paused" property
	 *
	 * @return paused
	 */
	public synchronized boolean isPaused() {
		return this.paused;
	}

	/**
	 * Getter method for "currentTask" property
	 *
	 * @return currentTask
	 */
	public synchronized T getCurrentTask() {
		return this.currentTask;
	}

	/**
	 * {@link AccelerateTaskListener} implementation to maintain the
	 * status/count of total/active tasks
	 *
	 * @author Rohit Narayanan
	 * @version 1.0 Initial Version
	 * @since 25-May-2015
	 */
	private class BETaskEventListener implements AccelerateTaskListener {
		/**
		 *
		 */
		public BETaskEventListener() {
		}

		/**
		 * @param aTask
		 */
		@Override
		@SuppressWarnings("unchecked")
		public void beforeStart(AccelerateTask aTask) {
			AccelerateBatch.this.beforeStart((T) aTask);
		}

		/**
		 * @param aTask
		 */
		@Override
		@SuppressWarnings("unchecked")
		public void afterComplete(AccelerateTask aTask) {
			AccelerateBatch.this.afterComplete((T) aTask);
		}
	}
}