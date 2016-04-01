package accelerate.batch;

/**
 * Interface defining methods to be implemented by any class that needs to be
 * registered as a task event listener
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 25-May-2015
 */
public interface AccelerateTaskListener {
	/**
	 * @param aTask
	 */
	public void beforeStart(AccelerateTask aTask);

	/**
	 * @param aTask
	 */
	public void afterComplete(AccelerateTask aTask);
}
