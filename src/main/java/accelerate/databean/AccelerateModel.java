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
	private AccelerateMessage accelerateMessage = null;

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
	private transient AccelerateRequest accelerateRequest = null;

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
	 * shortcut constructor to set the {@link #viewName} and
	 * {@link #accelerateRequest}
	 *
	 * @param aViewName
	 * @param aAccelerateRequest
	 */
	public AccelerateModel(String aViewName, AccelerateRequest aAccelerateRequest) {
		setViewName(aViewName);
		setRequestBean(aAccelerateRequest);
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
		addAttribute(aAttributeName, aAttributeValue);
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
		addAttribute(aAttributeValue);
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
			aAttributeValues.forEach(value -> addAttribute(value));
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
			aAttributes.forEach((key, value) -> addAttribute(key, value));
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
					addAttribute(key, value);
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
		return containsKey(aAttributeName);
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
	 * Getter method for "accelerateRequest" property
	 * 
	 * @return accelerateRequest
	 */
	public AccelerateRequest getRequestBean() {
		return this.accelerateRequest;
	}

	/**
	 * Setter method for "accelerateRequest" property
	 * 
	 * @param aAccelerateRequest
	 */
	public void setRequestBean(AccelerateRequest aAccelerateRequest) {
		this.accelerateRequest = aAccelerateRequest;
	}

	/**
	 * Getter method for "accelerateMessage" property
	 * 
	 * @return accelerateMessage
	 */
	public AccelerateMessage getMessage() {
		return this.accelerateMessage;
	}

	/**
	 * Setter method for "accelerateMessage" property
	 * 
	 * @param aAccelerateMessage
	 */
	public void setMessage(AccelerateMessage aAccelerateMessage) {
		this.accelerateMessage = aAccelerateMessage;
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