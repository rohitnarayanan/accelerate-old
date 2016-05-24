package accelerate.batch;

import static accelerate.util.AppUtil.isEmpty;
import static accelerate.util.CollectionUtil.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import accelerate.databean.AccelerateDataBean;
import accelerate.exception.AccelerateRuntimeException;
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
public class AccelerateBatch<T extends AccelerateTask> extends ThreadPoolTaskExecutor {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the Batch
	 */
	private String batchName = null;

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
	private final Object monitor = new Object();

	/**
	 * Semaphore for pausing the tasks
	 */
	private Consumer<T> taskPostProcessor = null;

	/**
	 * {@link Map} of tasks submitted to the batch
	 */
	private final Map<String, T> tasks = new HashMap<>();

	/**
	 * Count of tasks processed by this batch
	 */
	protected long completedTaskCount = 0l;

	/**
	 * Default constructor
	 *
	 * @param aBatchName
	 * @param aThreadPoolSize
	 */
	public AccelerateBatch(String aBatchName, int aThreadPoolSize) {
		this(aBatchName, aThreadPoolSize, aThreadPoolSize);
	}

	/**
	 * Constructor 2
	 *
	 * @param aBatchName
	 * @param aThreadPoolSize
	 * @param aMaxPoolSize
	 */
	public AccelerateBatch(String aBatchName, int aThreadPoolSize, int aMaxPoolSize) {
		this.batchName = aBatchName;
		setCorePoolSize(aThreadPoolSize);
		setMaxPoolSize(aMaxPoolSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.scheduling.concurrent.ExecutorConfigurationSupport#
	 * initialize()
	 */
	/**
	 */
	@Override
	@ManagedOperation(description = "This methods activates the batch")
	public synchronized void initialize() {
		if (this.active) {
			throw new AccelerateRuntimeException(
					"Batch is already active! Invoke shutdown() to close the batch before reinitializing");
		}

		setThreadGroupName(this.batchName);
		setThreadNamePrefix("AccelerateTask");

		super.initialize();
		this.completedTaskCount = 0l;
		this.active = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.scheduling.concurrent.ExecutorConfigurationSupport#
	 * shutdown()
	 */
	/**
	 */
	@Override
	@ManagedOperation(description = "This method shuts down the current batch instance")
	public void shutdown() {
		this.active = false;
		super.shutdown();
	}

	/**
	 * This method shuts down the current batch instance and blocks the caller
	 * till it shuts down or the timeout provided elapses, whichever is earlier.
	 *
	 * @param aTimeUnit
	 * @param aTimeout
	 */
	@ManagedOperation(description = "This method shuts down the current batch instance and blocks the caller till it shuts down or the timeout provided elapses, whichever is earlier.")
	public synchronized void shutdown(TimeUnit aTimeUnit, long aTimeout) {
		setAwaitTerminationSeconds((int) aTimeUnit.toSeconds(aTimeout));
		this.shutdown();
	}

	/**
	 * This method tries to shutdown the current batch instance immediately
	 */
	@ManagedOperation(description = "This method tries to shutdown the current batch instance immediately")
	public synchronized void shutdownNow() {
		this.active = false;
		getThreadPoolExecutor().shutdownNow();
	}

	/**
	 * @param aConsumer
	 */
	public final synchronized void registerTaskPostProcessor(Consumer<T> aConsumer) {
		this.taskPostProcessor = aConsumer;
	}

	/**
	 * This method handles a pause interrupt via by JMX and attempts to pause
	 * the execution of all running tasks
	 */
	@ManagedOperation(description = "This method handles a pause interrupt sent by JMX and attempts to pause the execution of all running tasks")
	public synchronized void pause() {
		this.paused = true;
		this.tasks.values().forEach(task -> task.pause(this.monitor));
	}

	/**
	 * This method handles a resume interrupt via by the JMX and attempts to
	 * resume the execution of all paused tasks
	 */
	@ManagedOperation(description = "This method handles a resume interrupt via by the JMX and attempts to resume the execution of all paused tasks")
	public synchronized void resume() {
		this.paused = false;
		this.tasks.values().forEach(task -> task.resume());

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
		AccelerateDataBean dataBean = AccelerateDataBean.build("1.name", this.batchName, "2.corePoolSize",
				getCorePoolSize(), "3.maxPoolSize", getMaxPoolSize(), "4.active", this.active, "5.completedTasks",
				this.completedTaskCount, "6.taskCount", this.tasks.size());

		return JSONUtil.serialize(dataBean);
	}

	/**
	 * @param aTaskList
	 */
	@SafeVarargs
	public final void submitTasks(T... aTaskList) {
		submitTasks(toList(aTaskList));
	}

	/**
	 * @param aTaskList
	 * @throws AccelerateRuntimeException
	 *             on invalid batch state or empty argument
	 */
	@SuppressWarnings("unchecked")
	public final synchronized void submitTasks(List<T> aTaskList) throws AccelerateRuntimeException {
		if (!this.active) {
			throw new AccelerateRuntimeException(
					"Batch has been shutdown! Invoke activate() reinitialize the batch before submitting tasks");
		}

		if (isEmpty(aTaskList)) {
			throw new AccelerateRuntimeException("No tasks provided!");
		}

		aTaskList.stream().forEach(task -> {
			this.tasks.put(task.getTaskKey(), task);

			task.registerPostProcessor(_task -> {
				this.tasks.remove(_task.getTaskKey());
				this.completedTaskCount++;
				if (this.taskPostProcessor != null) {
					this.taskPostProcessor.accept((T) _task);
				}
			});

			if (this.paused) {
				task.pause(this.monitor);
			}

			task.submitted(submit(task));
		});
	}

	/**
	 * Getter method for "batchName" property
	 * 
	 * @return batchName
	 */
	public String getBatchName() {
		return this.batchName;
	}

	/**
	 * Getter method for "active" property
	 * 
	 * @return active
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * Getter method for "paused" property
	 * 
	 * @return paused
	 */
	public boolean isPaused() {
		return this.paused;
	}

	/**
	 * Getter method for "tasks" property
	 * 
	 * @return tasks
	 */
	public Map<String, T> getTasks() {
		return this.tasks;
	}

	/**
	 * Getter method for "completedTaskCount" property
	 * 
	 * @return completedTaskCount
	 */
	public long getCompletedTaskCount() {
		return this.completedTaskCount;
	}
}