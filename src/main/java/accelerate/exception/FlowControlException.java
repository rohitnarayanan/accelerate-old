package accelerate.exception;

/**
 * This is an extension of {@link AccelerateRuntimeException} class. Its main
 * purpose is to allow developers to skip code blocks and manage code flow
 * instead of writing verbose conditional statements.
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jan 13, 2011
 */
public class FlowControlException extends AccelerateRuntimeException {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8887623006177753597L;

	/**
	 * @param aMessage
	 */
	public FlowControlException(String aMessage) {
		super(aMessage);
	}
}