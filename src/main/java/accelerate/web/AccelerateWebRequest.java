package accelerate.web;

import com.fasterxml.jackson.annotation.JsonIgnore;

import accelerate.databean.AccelerateDataBean;

/**
 * Generic Request Bean for Web Application
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jul 30, 2009
 */
public class AccelerateWebRequest extends AccelerateDataBean {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the action to be invoked. Can be used across multiple frameworks
	 */
	private String actionId;

	/**
	 * Generic query string. Can be used in customized ways by applications
	 */
	private String queryString;

	/**
	 * {@link Throwable} instance caught required to be passed to another
	 * service
	 */
	@JsonIgnore
	private Exception error;

	/**
	 * default constructor
	 */
	public AccelerateWebRequest() {
	}

	/**
	 * Getter method for "actionId" property
	 * 
	 * @return actionId
	 */
	public String getActionId() {
		return this.actionId;
	}

	/**
	 * Setter method for "actionId" property
	 * 
	 * @param aActionId
	 */
	public void setActionId(String aActionId) {
		this.actionId = aActionId;
	}

	/**
	 * Getter method for "queryString" property
	 * 
	 * @return queryString
	 */
	public String getQueryString() {
		return this.queryString;
	}

	/**
	 * Setter method for "queryString" property
	 * 
	 * @param aQueryString
	 */
	public void setQueryString(String aQueryString) {
		this.queryString = aQueryString;
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