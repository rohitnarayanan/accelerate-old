package accelerate.exception;

/**
 * This is a simple wrapper exception class that is used by the Accelerate
 * library.
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Nov 13, 2008
 */
public class AccelerateException extends Exception {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default Constructor
	 */
	public AccelerateException() {
		super();
	}

	/**
	 * Overloaded Constructor
	 *
	 * @param aCause
	 */
	public AccelerateException(Throwable aCause) {
		super(aCause);
	}

	/**
	 * Overloaded Constructor
	 *
	 * @param aMessage
	 * @param aMessageArgs
	 */
	public AccelerateException(String aMessage, Object... aMessageArgs) {
		super(String.format(aMessage, aMessageArgs));
	}

	/**
	 * Overloaded Constructor
	 * 
	 * @param aCause
	 * @param aMessage
	 * @param aMessageArgs
	 */
	public AccelerateException(Throwable aCause, String aMessage, Object... aMessageArgs) {
		super(String.format(aMessage, aMessageArgs), aCause);
	}
}
