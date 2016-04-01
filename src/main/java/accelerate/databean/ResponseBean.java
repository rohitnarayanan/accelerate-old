package accelerate.databean;

import accelerate.util.JSONUtil;

/**
 * Generic Response Bean for Web applications
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jul 30, 2009
 */
public class ResponseBean extends AccelerateDataBean {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6650044748250728353L;

	/**
	 * Return Code. <code>
	 * 0: Success
	 * !=0: Failure/Alternate Result
	 * </code>
	 */
	private int returnCode = 0;

	/**
	 * Result as mapped in the RequestHandlers.xml file
	 */
	private String viewName = null;

	/**
	 * {@link Message} instance
	 */
	private Message message = null;

	/**
	 * {@link Exception} caught during process
	 */
	private Exception error = null;

	/**
	 * Context Path of the application
	 */
	private String contextPath = null;

	/**
	 * {@link RequestBean} instance
	 */
	private transient RequestBean requestBean = null;

	/**
	 * default constructor
	 */
	public ResponseBean() {
	}

	/**
	 * shortcut constructor to set the {@link #viewName}
	 *
	 * @param aViewName
	 */
	public ResponseBean(String aViewName) {
		this.viewName = aViewName;
	}

	/**
	 * shortcut constructor to set the {@link #viewName} and
	 * {@link #requestBean}
	 *
	 * @param aViewName
	 * @param aRequestBean
	 */
	public ResponseBean(String aViewName, RequestBean aRequestBean) {
		setViewName(aViewName);
		setRequestBean(aRequestBean);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see accelerate.databean.AccelerateDataBean#toShortJSON()
	 */
	/**
	 * @return
	 */
	@Override
	public String toShortJSON() {
		return JSONUtil.serializeOnly(this, "returnCode", "viewName", "message");
	}

	/**
	 * Getter method for "returnCode" property
	 * 
	 * @return returnCode
	 */
	public int getReturnCode() {
		return this.returnCode;
	}

	/**
	 * Setter method for "returnCode" property
	 * 
	 * @param aReturnCode
	 */
	public void setReturnCode(int aReturnCode) {
		this.returnCode = aReturnCode;
	}

	/**
	 * Getter method for "viewName" property
	 * 
	 * @return viewName
	 */
	public String getViewName() {
		return this.viewName;
	}

	/**
	 * Setter method for "viewName" property
	 * 
	 * @param aViewName
	 */
	public void setViewName(String aViewName) {
		this.viewName = aViewName;
	}

	/**
	 * Getter method for "contextPath" property
	 * 
	 * @return contextPath
	 */
	public String getContextPath() {
		return this.contextPath;
	}

	/**
	 * Setter method for "contextPath" property
	 * 
	 * @param aContextPath
	 */
	public void setContextPath(String aContextPath) {
		this.contextPath = aContextPath;
	}

	/**
	 * Getter method for "requestBean" property
	 * 
	 * @return requestBean
	 */
	public RequestBean getRequestBean() {
		return this.requestBean;
	}

	/**
	 * Setter method for "requestBean" property
	 * 
	 * @param aRequestBean
	 */
	public void setRequestBean(RequestBean aRequestBean) {
		this.requestBean = aRequestBean;
	}

	/**
	 * Getter method for "message" property
	 * 
	 * @return message
	 */
	public Message getMessage() {
		return this.message;
	}

	/**
	 * Setter method for "message" property
	 * 
	 * @param aMessage
	 */
	public void setMessage(Message aMessage) {
		this.message = aMessage;
	}

	/**
	 * Getter method for "error" property
	 * 
	 * @return error
	 */
	public Exception getError() {
		return this.error;
	}

	/**
	 * Setter method for "error" property
	 * 
	 * @param aError
	 */
	public void setError(Exception aError) {
		this.error = aError;
	}
}