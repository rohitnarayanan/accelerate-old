package accelerate.batch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import accelerate.exception.AccelerateException;

/**
 * Abstract implementation for {@link Runnable}
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Feb 12, 2010
 */
public abstract class AccelerateTask implements Callable<Map<String, Object>>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	protected static final Logger _logger = LoggerFactory.getLogger(AccelerateTask.class);

	/**
	 * {@link Future} instance containing result
	 */
	protected Map<String, Object> taskResult = null;

	/**
	 * Key Id for the task
	 */
	private String taskKey = null;

	/**
	 * {@link List} of {@link AccelerateTaskListener} registered to this
	 * instance
	 */
	private List<AccelerateTaskListener> taskEventListeners = new ArrayList<>();

	/**
	 * {@link System#currentTimeMillis()} when the task was submitted for
	 * execution
	 */
	private long submitTime = 0;

	/**
	 * {@link System#currentTimeMillis()} when the task started execution
	 */
	private long startTime = 0;

	/**
	 * {@link System#currentTimeMillis()} when the task completed execution
	 */
	private long endTime = 0;

	/**
	 * Flag to indicate whether the task is executing
	 */
	private boolean executing = false;

	/**
	 * Flag to indicate whether the task is complete
	 */
	private boolean complete = false;

	/**
	 * {@link Exception} instance encounter during execution
	 */
	private Exception taskError = null;

	/**
	 * Semaphore for thread coordination
	 */
	private boolean pause = false;

	/**
	 * Semaphore for thread coordination
	 */
	private Object monitor = null;

	/**
	 * {@link Thread} instance in which this task is running
	 */
	private Thread thread = null;

	/**
	 * {@link Future} instance containing result
	 */
	private Future<Map<String, Object>> future = null;

	/**
	 * default constructor
	 */
	public AccelerateTask() {
		this("Task#" + Thread.currentThread().getName());
	}

	/**
	 * Overloaded constructor
	 *
	 * @param aTaskKey
	 */
	public AccelerateTask(String aTaskKey) {
		this.taskKey = aTaskKey;
	}

	/**
	 * @param aTaskEventListener
	 */
	public void registerTaskEventListener(AccelerateTaskListener aTaskEventListener) {
		this.taskEventListeners.add(aTaskEventListener);
	}

	/**
	 * Getter Method for taskKey
	 *
	 * @return taskKey
	 */
	public String getTaskKey() {
		return this.taskKey;
	}

	/**
	 * @return
	 */
	public long getSubmitTime() {
		return this.submitTime;
	}

	/**
	 * @return
	 */
	public long getStartTime() {
		return this.startTime;
	}

	/**
	 * @return
	 */
	public long getWaitTime() {
		return this.startTime - this.submitTime;
	}

	/**
	 * @return
	 */
	public long getEndTime() {
		return this.endTime;
	}

	/**
	 * @param aMonitor
	 */
	public synchronized void pause(Object aMonitor) {
		this.pause = true;
		this.monitor = aMonitor;
	}

	/**
	 *
	 */
	public synchronized void resume() {
		this.pause = false;
		this.monitor = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */

	/**
	 * @return
	 * @throws AccelerateException
	 */
	@Override
	public Map<String, Object> call() throws AccelerateException {
		try {
			this.thread = Thread.currentThread();
			this.startTime = System.currentTimeMillis();
			notifyListeners(0);
			this.executing = true;
			execute();
		} finally {
			this.endTime = System.currentTimeMillis();
			this.executing = false;
			this.complete = true;
			notifyListeners(1);
		}

		return this.taskResult;
	}

	/**
	 * This method notifies all the registered listeners of the task's life
	 * cycle
	 *
	 * @param aSwitchFlag
	 */
	private void notifyListeners(int aSwitchFlag) {
		for (AccelerateTaskListener taskEventListener : this.taskEventListeners) {
			switch (aSwitchFlag) {
			case 0:
				taskEventListener.beforeStart(this);
				break;
			case 1:
				taskEventListener.afterComplete(this);
				break;
			}
		}
	}

	/**
	 * @throws AccelerateException
	 */
	public abstract void execute() throws AccelerateException;

	/**
	 * This method is a utility provided to implementations of this class to
	 * pause execution on receiving an interrupt by {@link AccelerateBatch}
	 *
	 * @throws AccelerateException
	 */
	public void checkPause() throws AccelerateException {
		if (!this.pause) {
			return;
		}

		synchronized (this.monitor) {
			try {
				_logger.info("Pausing: {}", getTaskKey());
				this.monitor.wait();
			} catch (InterruptedException error) {
				throw new AccelerateException(error);
			}
		}

		_logger.info("Resuming: {}", getTaskKey());
	}

	/**
	 * Getter method for "taskEventListeners" property
	 *
	 * @return taskEventListeners
	 */
	public List<AccelerateTaskListener> getTaskEventListeners() {
		return this.taskEventListeners;
	}

	/**
	 * Getter method for "executing" property
	 *
	 * @return executing
	 */
	public boolean isExecuting() {
		return this.executing;
	}

	/**
	 * Getter method for "complete" property
	 *
	 * @return complete
	 */
	public boolean isComplete() {
		return this.complete;
	}

	/**
	 * Getter method for "taskError" property
	 *
	 * @return taskError
	 */
	public Exception getTaskError() {
		return this.taskError;
	}

	/**
	 * Getter method for "taskResult" property
	 *
	 * @return taskResult
	 */
	public Map<String, Object> getTaskResult() {
		return this.taskResult;
	}

	/**
	 * Getter method for "monitor" property
	 *
	 * @return monitor
	 */
	public Object getMonitor() {
		return this.monitor;
	}

	/**
	 * Getter method for "thread" property
	 *
	 * @return thread
	 */
	public Thread getThread() {
		return this.thread;
	}

	/**
	 * Getter method for "future" property
	 *
	 * @return future
	 */
	public Future<Map<String, Object>> getFuture() {
		return this.future;
	}

	/**
	 * Setter method for "future" property
	 *
	 * @param aFuture
	 */
	public void setFuture(Future<Map<String, Object>> aFuture) {
		this.future = aFuture;
	}
}