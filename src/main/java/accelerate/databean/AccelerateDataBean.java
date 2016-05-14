package accelerate.databean;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.ui.ModelMap;

import accelerate.exception.AccelerateRuntimeException;
import accelerate.util.AppUtil;
import accelerate.util.JSONUtil;

/**
 * Abstract class serving as parent for JavaBeans providing utility methods
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 21-May-2015
 */
public class AccelerateDataBean implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@link Set} to include names of fields to exclude while logging
	 */
	protected transient Set<String> logExcludedFields = null;

	/**
	 * Name of the id field for the bean
	 */
	private transient String idField = null;

	/**
	 * Flag to indicate that the bean stores a huge amount of data, so exception
	 * handlers and interceptors can avoid serializing the entire bean
	 */
	private transient boolean largeDataset = false;

	/**
	 * Instance of {@link ModelMap} for generic storage
	 */
	private ModelMap model = null;

	/**
	 * default constructor
	 */
	public AccelerateDataBean() {
		this.logExcludedFields = new HashSet<>();
		this.model = new ModelMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	/**
	 * @return
	 */
	@Override
	public String toString() {
		return toJSON();
	}

	/**
	 * @param aAttributeName
	 * @param aAttributeValue
	 * @return
	 * @see org.springframework.ui.ModelMap#addAttribute(java.lang.String,
	 *      java.lang.Object)
	 */
	public ModelMap put(String aAttributeName, Object aAttributeValue) {
		return this.model.addAttribute(aAttributeName, aAttributeValue);
	}

	/**
	 * @param aAttributeValue
	 * @return
	 * @see org.springframework.ui.ModelMap#addAttribute(java.lang.Object)
	 */
	public ModelMap put(Object aAttributeValue) {
		return this.model.addAttribute(aAttributeValue);
	}

	/**
	 * @param aKey
	 * @return
	 * @see java.util.LinkedHashMap#get(java.lang.Object)
	 */
	public Boolean has(Object aKey) {
		return this.model.containsKey(aKey);
	}

	/**
	 * @param aKey
	 * @return
	 * @see java.util.LinkedHashMap#get(java.lang.Object)
	 */
	public Object get(Object aKey) {
		return this.model.get(aKey);
	}

	/**
	 * @param aKey
	 * @param aDefaultValue
	 * @return
	 * @see java.util.LinkedHashMap#getOrDefault(java.lang.Object,
	 *      java.lang.Object)
	 */
	public Object getOrDefault(Object aKey, Object aDefaultValue) {
		return this.model.getOrDefault(aKey, aDefaultValue);
	}

	/**
	 * @param aAttributeValues
	 * @return
	 * @see org.springframework.ui.ModelMap#addAllAttributes(java.util.Collection)
	 */
	public ModelMap putAll(Collection<?> aAttributeValues) {
		return this.model.addAllAttributes(aAttributeValues);
	}

	/**
	 * @param aAttributes
	 * @return
	 * @see org.springframework.ui.ModelMap#addAllAttributes(java.util.Map)
	 */
	public ModelMap putAll(Map<String, ?> aAttributes) {
		return this.model.addAllAttributes(aAttributes);
	}

	/**
	 * Shortcut method to add multiple key value pairs to the map. Though it
	 * accepts an Object array by definition, but it expects the arguments to be
	 * in the order of key1, value1, key2, value2.. and so on.
	 * 
	 * @param aArgs
	 *            Variable number of key value pairs
	 * @return selft instance for chaining calls
	 * @throws AccelerateRuntimeException
	 *             If key value pairs do not match, or keys are not of type
	 *             {@link String}
	 */
	public final AccelerateDataBean addAll(Object... aArgs) throws AccelerateRuntimeException {
		if (AppUtil.isEmpty(aArgs)) {
			throw new AccelerateRuntimeException("Empty arguments are not allowed");
		}

		if ((aArgs.length % 2) != 0) {
			throw new AccelerateRuntimeException(
					"Incorrect number of arguments or array size not correct:" + aArgs.length);
		}

		for (int idx = 0; idx < aArgs.length; idx += 2) {
			try {
				this.model.put((String) aArgs[idx], aArgs[idx + 1]);
			} catch (ClassCastException error) {
				throw new AccelerateRuntimeException("Error:[{}] for key:[{}] at Index:[{}]", error.getMessage(),
						aArgs[idx], idx);
			}
		}

		return this;
	}

	/**
	 * Static shortcut method to build a new instance
	 * 
	 * @param aArgs
	 * @return
	 * @throws AccelerateRuntimeException
	 */
	public static final AccelerateDataBean build(Object... aArgs) throws AccelerateRuntimeException {
		AccelerateDataBean bean = new AccelerateDataBean();
		bean.addAll(aArgs);
		return bean;
	}

	/**
	 * This methods returns a JSON representation of this bean
	 *
	 * @return JSON Representation
	 */
	public String toJSON() {
		return toJSON(false);
	}

	/**
	 * This methods returns a JSON representation of this bean
	 * 
	 * @param aForce
	 * @return
	 */
	public String toJSON(boolean aForce) {
		if (isLargeDataset() && !aForce) {
			return toShortJSON();
		}

		if (AppUtil.isEmpty(this.logExcludedFields)) {
			return JSONUtil.serialize(this);
		}

		return JSONUtil.serializeExcept(this,
				this.logExcludedFields.toArray(new String[this.logExcludedFields.size()]));
	}

	/**
	 * This method return a short JSON representation of this bean to save
	 * memory or disk space
	 *
	 * @return log string
	 */
	public String toShortJSON() {
		return (getIdField() != null) ? JSONUtil.serializeOnly(this, getIdField())
				: "{\"id\":\"" + super.toString() + "\"}";
	}

	/**
	 * This method registers the aFieldName as a field to be excluded from
	 * logging
	 *
	 * @param aFieldNames
	 */
	public synchronized void addJsonIgnoreFields(String... aFieldNames) {
		for (String field : aFieldNames) {
			this.logExcludedFields.add(field);
		}
	}

	/**
	 * This method registers the aFieldName as a field to be excluded from
	 * logging
	 *
	 * @param aFieldNames
	 */
	public synchronized void removeJsonIgnoreFields(String... aFieldNames) {
		if (this.logExcludedFields == null) {
			return;
		}

		for (String field : aFieldNames) {
			this.logExcludedFields.remove(field);
		}
	}

	/**
	 * Getter method for "idField" property
	 * 
	 * @return idField
	 */
	public String getIdField() {
		return this.idField;
	}

	/**
	 * Setter method for "idField" property
	 * 
	 * @param aIdField
	 */
	public void setIdField(String aIdField) {
		this.idField = aIdField;
	}

	/**
	 * Getter method for "largeDataset" property
	 * 
	 * @return largeDataset
	 */
	public boolean isLargeDataset() {
		return this.largeDataset;
	}

	/**
	 * Setter method for "largeDataset" property
	 * 
	 * @param aLargeDataset
	 */
	public void setLargeDataset(boolean aLargeDataset) {
		this.largeDataset = aLargeDataset;
	}
}