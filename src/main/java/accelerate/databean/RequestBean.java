package accelerate.databean;

import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import accelerate.util.StringUtil;

/**
 * Generic Request Bean for Web Application
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jul 30, 2009
 */
public class RequestBean extends AccelerateDataBean {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the action to be invoked. Can be used across multiple frameworks
	 */
	private String actionId = null;

	/**
	 * Generic query string. Can be used in customized ways by applications
	 */
	private String queryString = null;

	/**
	 * Request Parameters
	 */
	private Map<String, Object> requestParams = null;

	/**
	 * {@link Throwable} instance caught required to be passed to another
	 * service
	 */
	private transient Exception error = null;

	/**
	 * default constructor
	 */
	public RequestBean() {
	}

	/**
	 * @param aParam
	 * @return
	 */
	public String getParameter(String aParam) {
		Object paramValue = this.requestParams.get(aParam);
		if (paramValue instanceof String[]) {
			String[] paramValues = (String[]) paramValue;
			return paramValues[0];
		}

		return StringUtil.toString(paramValue);
	}

	/**
	 * @param aParam
	 * @return
	 */
	public FileItem getFileItemParameter(String aParam) {
		Object paramValue = this.requestParams.get(aParam);
		if (paramValue instanceof FileItem) {
			return (FileItem) paramValue;
		}

		return null;
	}

	/**
	 * @param aParam
	 * @return
	 */
	public byte[] getParameterStream(String aParam) {
		Object paramValue = this.requestParams.get(aParam);
		if (paramValue instanceof byte[]) {
			return (byte[]) paramValue;
		}

		return null;
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
	 * Getter method for "requestParams" property
	 * 
	 * @return requestParams
	 */
	public Map<String, Object> getRequestParams() {
		return this.requestParams;
	}

	/**
	 * Setter method for "requestParams" property
	 * 
	 * @param aRequestParams
	 */
	public void setRequestParams(Map<String, Object> aRequestParams) {
		this.requestParams = aRequestParams;
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