package accelerate.batch;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import accelerate.databean.AccelerateDataBean;
import accelerate.exception.AccelerateException;
import accelerate.util.StringUtil;

/**
 * Abstract implementation for {@link Runnable}
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Feb 12, 2010
 */
public abstract class AccelerateTask extends AccelerateDataBean implements Callable<AccelerateTask> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	protected static final Logger _logger = LoggerFactory.getLogger(AccelerateTask.class);

	/**
	 * Key Id for the task
	 */
	private String taskKey = null;

	/**
	 * {@link List} of {@link Consumer} registered to to be called after
	 * execution
	 */
	private Consumer<AccelerateTask> postProccessor = null;

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
	 * {@link Future} instance return on task submit
	 */
	private Future<AccelerateTask> future = null;

	/**
	 * default constructor
	 */
	public AccelerateTask() {
		this(StringUtil.createKey("AccelerateTask", Thread.currentThread().getName(),
				String.valueOf(System.currentTimeMillis())));
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
	 * @param aConsumer
	 */
	public final void registerPostProcessor(Consumer<AccelerateTask> aConsumer) {
		this.postProccessor = aConsumer;
	}

	/**
	 * @param aFuture
	 */
	public synchronized void submitted(Future<AccelerateTask> aFuture) {
		this.submitTime = System.currentTimeMillis();
		this.future = aFuture;
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

	/**
	 * @throws ExecutionException
	 * @throws InterruptedException
	 *
	 */
	public synchronized void waitForCompletion() throws InterruptedException, ExecutionException {
		this.future.get();
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
	public AccelerateTask call() throws AccelerateException {
		try {
			this.thread = Thread.currentThread();
			this.startTime = System.currentTimeMillis();
			this.executing = true;
			execute();
		} finally {
			this.endTime = System.currentTimeMillis();
			this.executing = false;
			this.complete = true;

			this.postProccessor.accept(this);
		}

		return this;
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
	 * Getter method for "taskKey" property
	 * 
	 * @return taskKey
	 */
	public String getTaskKey() {
		return this.taskKey;
	}

	/**
	 * Setter method for "taskKey" property
	 * 
	 * @param aTaskKey
	 */
	public void setTaskKey(String aTaskKey) {
		this.taskKey = aTaskKey;
	}

	/**
	 * Getter method for "postProccessor" property
	 * 
	 * @return postProccessor
	 */
	public Consumer<? extends AccelerateTask> getPostProccessor() {
		return this.postProccessor;
	}

	/**
	 * Getter method for "submitTime" property
	 * 
	 * @return submitTime
	 */
	public long getSubmitTime() {
		return this.submitTime;
	}

	/**
	 * Getter method for "startTime" property
	 * 
	 * @return startTime
	 */
	public long getStartTime() {
		return this.startTime;
	}

	/**
	 * Getter method for "endTime" property
	 * 
	 * @return endTime
	 */
	public long getEndTime() {
		return this.endTime;
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
	 * Getter method for "pause" property
	 * 
	 * @return pause
	 */
	public boolean isPause() {
		return this.pause;
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
}