package accelerate.exception;

/**
 * This is an extension of {@link AccelerateException} class. Its main purpose
 * is to allow developers to skip code blocks and manage code flow instead of
 * writing verbose conditional statements.
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jan 13, 2011
 */
public class FlowControlException extends AccelerateException {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private Object data = null;

	/**
	 * Default Constructor
	 * 
	 * @param aMessage
	 */
	public FlowControlException(String aMessage) {
		super(aMessage);
	}

	/**
	 * Overloaded Constructor
	 * 
	 * @param aMessage
	 * @param aData
	 */
	public FlowControlException(String aMessage, Object aData) {
		super(aMessage);
		this.data = aData;
	}

	/**
	 * Getter method for "data" property
	 * 
	 * @return data
	 */
	public Object getData() {
		return this.data;
	}

}