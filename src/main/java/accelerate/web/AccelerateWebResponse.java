package accelerate.web;

import accelerate.databean.AccelerateDataBean;
import accelerate.databean.AccelerateMessage;
import accelerate.exception.AccelerateException;
import accelerate.util.JSONUtil;

/**
 * Generic Response Bean for Web applications
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jul 30, 2009
 */
public class AccelerateWebResponse extends AccelerateDataBean {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

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
	 * {@link AccelerateMessage} instance
	 */
	private AccelerateMessage message = null;

	/**
	 * {@link Exception} caught during process
	 */
	private Exception error = null;

	/**
	 * Context Path of the application
	 */
	private String contextPath = null;

	/**
	 * {@link AccelerateWebRequest} instance
	 */
	private transient AccelerateWebRequest request = null;

	/**
	 * default constructor
	 */
	public AccelerateWebResponse() {
	}

	/**
	 * shortcut constructor to set the {@link #viewName}
	 *
	 * @param aViewName
	 */
	public AccelerateWebResponse(String aViewName) {
		this.viewName = aViewName;
	}

	/**
	 * shortcut constructor to set the {@link #viewName} and {@link #request}
	 *
	 * @param aViewName
	 * @param aAccelerateRequest
	 */
	public AccelerateWebResponse(String aViewName, AccelerateWebRequest aAccelerateRequest) {
		this.viewName = aViewName;
		this.request = aAccelerateRequest;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see accelerate.databean.AccelerateDataBean#toShortJSON()
	 */
	/**
	 * @return
	 * @throws AccelerateException
	 *             thrown due to
	 *             {@link JSONUtil#serializeOnly(Object, String...)}
	 */
	@Override
	public String toShortJSON() throws AccelerateException {
		return JSONUtil.serializeOnly(this, "returnCode", "viewName", "accelerateMessage");
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
	 * Getter method for "message" property
	 * 
	 * @return message
	 */
	public AccelerateMessage getMessage() {
		return this.message;
	}

	/**
	 * Setter method for "message" property
	 * 
	 * @param aMessage
	 */
	public void setMessage(AccelerateMessage aMessage) {
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
	 * Getter method for "request" property
	 * 
	 * @return request
	 */
	public AccelerateWebRequest getRequest() {
		return this.request;
	}

	/**
	 * Setter method for "request" property
	 * 
	 * @param aRequest
	 */
	public void setRequest(AccelerateWebRequest aRequest) {
		this.request = aRequest;
	}
}