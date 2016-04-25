package accelerate.databean;

import java.util.Collection;
import java.util.Map;

import org.springframework.ui.Model;

import accelerate.util.AppUtil;
import accelerate.util.JSONUtil;

/**
 * Generic Response Bean for Web applications
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jul 30, 2009
 */
public class AccelerateModel extends AccelerateDataBean implements Model {
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
	 * {@link AccelerateRequest} instance
	 */
	private transient AccelerateRequest request = null;

	/**
	 * default constructor
	 */
	public AccelerateModel() {
	}

	/**
	 * shortcut constructor to set the {@link #viewName}
	 *
	 * @param aViewName
	 */
	public AccelerateModel(String aViewName) {
		this.viewName = aViewName;
	}

	/**
	 * shortcut constructor to set the {@link #viewName} and {@link #request}
	 *
	 * @param aViewName
	 * @param aAccelerateRequest
	 */
	public AccelerateModel(String aViewName, AccelerateRequest aAccelerateRequest) {
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
	 */
	@Override
	public String toShortJSON() {
		return JSONUtil.serializeOnly(this, "returnCode", "viewName", "accelerateMessage");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.ui.Model#addAttribute(java.lang.String,
	 * java.lang.Object)
	 */
	/**
	 * @param aAttributeName
	 * @param aAttributeValue
	 * @return
	 */
	@Override
	public AccelerateModel addAttribute(String aAttributeName, Object aAttributeValue) {
		super.addAttribute(aAttributeName, aAttributeValue);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.ui.Model#addAttribute(java.lang.Object)
	 */
	/**
	 * @param aAttributeValue
	 * @return
	 */
	@Override
	public AccelerateModel addAttribute(Object aAttributeValue) {
		super.addAttribute(aAttributeValue);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.ui.Model#addAllAttributes(java.util.Collection)
	 */
	/**
	 * @param aAttributeValues
	 * @return
	 */
	@Override
	public AccelerateModel addAllAttributes(Collection<?> aAttributeValues) {
		if (!AppUtil.isEmpty(aAttributeValues)) {
			aAttributeValues.forEach(value -> super.addAttribute(value));
		}

		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.ui.Model#addAllAttributes(java.util.Map)
	 */
	/**
	 * @param aAttributes
	 * @return
	 */
	@Override
	public AccelerateModel addAllAttributes(Map<String, ?> aAttributes) {
		if (!AppUtil.isEmpty(aAttributes)) {
			aAttributes.forEach((key, value) -> super.addAttribute(key, value));
		}

		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.ui.Model#mergeAttributes(java.util.Map)
	 */
	/**
	 * @param aAttributes
	 * @return
	 */
	@Override
	public AccelerateModel mergeAttributes(Map<String, ?> aAttributes) {
		if (!AppUtil.isEmpty(aAttributes)) {
			aAttributes.forEach((key, value) -> {
				if (containsKey(key)) {
					super.addAttribute(key, value);
				}
			});
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.ui.Model#containsAttribute(java.lang.String)
	 */
	/**
	 * @param aAttributeName
	 * @return
	 */
	@Override
	public boolean containsAttribute(String aAttributeName) {
		return super.containsKey(aAttributeName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.ui.Model#asMap()
	 */
	/**
	 * @return
	 */
	@Override
	public Map<String, Object> asMap() {
		return this;
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
	public AccelerateRequest getRequest() {
		return this.request;
	}

	/**
	 * Setter method for "request" property
	 * 
	 * @param aRequest
	 */
	public void setRequest(AccelerateRequest aRequest) {
		this.request = aRequest;
	}
}