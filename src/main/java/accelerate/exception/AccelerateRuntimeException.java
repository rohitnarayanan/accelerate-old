package accelerate.exception;

/**
 * This is a copy of the {@link AccelerateException} class. It extends
 * {@link RuntimeException} to help manage unchecked exceptions.
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Nov 13, 2008
 */
public class AccelerateRuntimeException extends RuntimeException {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default Constructor
	 */
	public AccelerateRuntimeException() {
		super();
	}

	/**
	 * Overloaded Constructor
	 *
	 * @param aCause
	 */
	public AccelerateRuntimeException(Throwable aCause) {
		super(aCause);
	}

	/**
	 * Overloaded Constructor
	 *
	 * @param aMessage
	 * @param aMessageArgs
	 */
	public AccelerateRuntimeException(String aMessage, Object... aMessageArgs) {
		super(String.format(aMessage, aMessageArgs));
	}

	/**
	 * Overloaded Constructor
	 * 
	 * @param aCause
	 * @param aMessage
	 * @param aMessageArgs
	 */
	public AccelerateRuntimeException(Throwable aCause, String aMessage, Object... aMessageArgs) {
		super(String.format(aMessage, aMessageArgs), aCause);
	}
}
