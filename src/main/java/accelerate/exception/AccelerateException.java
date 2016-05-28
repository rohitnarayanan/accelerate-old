package accelerate.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * This is a simple wrapper exception class that is used by the Accelerate
 * library.
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Nov 13, 2008
 */
public class AccelerateException extends NestedRuntimeException {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default Constructor
	 *
	 * @param aCause
	 */
	public AccelerateException(Throwable aCause) {
		super(aCause.getMessage(), aCause);
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

	/**
	 * @param <K>
	 *            Generic return type
	 * @param aError
	 *            see {@link #checkAndThrow(Exception, String, Object...)}
	 * @return see {@link #checkAndThrow(Exception, String, Object...)}
	 * @throws AccelerateException
	 *             checks instance throws original or wrapped exception
	 */
	public static <K> K checkAndThrow(Exception aError) throws AccelerateException {
		throw (aError instanceof AccelerateException) ? (AccelerateException) aError : new AccelerateException(aError);
	}

	/**
	 * @param <K>
	 *            Generic return type
	 * @param aError
	 * @param aMessage
	 * @param aMessageArgs
	 * @return need a return clause to satify compiler checks when this method
	 *         gets calls from within catch block of methods with a return
	 *         clause
	 * @throws AccelerateException
	 *             checks instance throws original or wrapped exception
	 */
	public static <K> K checkAndThrow(Exception aError, String aMessage, Object... aMessageArgs)
			throws AccelerateException {
		throw (aError instanceof AccelerateException) ? (AccelerateException) aError
				: new AccelerateException(aError, aMessage, aMessageArgs);
	}
}
