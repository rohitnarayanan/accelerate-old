package accelerate.databean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import accelerate.exception.AccelerateException;
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
	 * Instance of {@link DataMap} for generic storage
	 */
	@JsonProperty
	private DataMap dataMap = null;

	/**
	 * default constructor
	 */
	public AccelerateDataBean() {
		this.logExcludedFields = new HashSet<>();
		this.dataMap = new DataMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	/**
	 * @return
	 * @throws AccelerateException
	 *             thrown due to {@link #toJSON()}
	 */
	@Override
	public String toString() throws AccelerateException {
		return toJSON();
	}

	/**
	 * @param aKey
	 * @return
	 * @see java.util.LinkedHashMap#get(java.lang.Object)
	 */
	public Boolean contains(Object aKey) {
		return this.dataMap.containsKey(aKey);
	}

	/**
	 * Shortcut for {@link HashMap#get(Object)}
	 * 
	 * @param aKey
	 * @return Mapped value
	 */
	public Object get(Object aKey) {
		return this.dataMap.get(aKey);
	}

	/**
	 * Shortcut to handle null values
	 * 
	 * @param aKey
	 * @param aDefaultValue
	 * @return
	 * @see java.util.LinkedHashMap#getOrDefault(java.lang.Object,
	 *      java.lang.Object)
	 */
	public Object getOrDefault(Object aKey, Object aDefaultValue) {
		return this.dataMap.getOrDefault(aKey, aDefaultValue);
	}

	/**
	 * @param aAttributeName
	 * @param aAttributeValue
	 * @return {@link DataMap} instance for call chaining
	 */
	public AccelerateDataBean put(String aAttributeName, Object aAttributeValue) {
		this.dataMap.putData(aAttributeName, aAttributeValue);
		return this;
	}

	/**
	 * @param aAttributes
	 * @return {@link DataMap} instance for call chaining
	 */
	public AccelerateDataBean putAll(Map<String, Object> aAttributes) {
		this.dataMap.putAll(aAttributes);
		return this;
	}

	/**
	 * Shortcut method to add multiple key value pairs to the map. Though it
	 * accepts an Object array by definition, but it expects the arguments to be
	 * in the order of key1, value1, key2, value2.. and so on.
	 * 
	 * @param aArgs
	 *            Variable number of key value pairs
	 * @return selft instance for chaining calls
	 */
	public final AccelerateDataBean putAll(Object... aArgs) {
		this.dataMap.putAllData(aArgs);
		return this;
	}

	/**
	 * Static shortcut method to build a new instance using
	 * {@link #putAll(Object...)}
	 * 
	 * @param aArgs
	 * @return
	 */
	public static final AccelerateDataBean build(Object... aArgs) {
		AccelerateDataBean bean = new AccelerateDataBean();
		bean.putAll(aArgs);
		return bean;
	}

	/**
	 * This methods returns a JSON representation of this bean
	 *
	 * @return JSON Representation
	 * @throws AccelerateException
	 *             thrown due to {@link #toJSON(boolean)}
	 */
	public String toJSON() throws AccelerateException {
		return toJSON(false);
	}

	/**
	 * This methods returns a JSON representation of this bean
	 * 
	 * @param aForce
	 * @return
	 * @throws AccelerateException
	 *             thrown due to {@link JSONUtil#serialize(Object)}
	 */
	public String toJSON(boolean aForce) throws AccelerateException {
		if (isLargeDataset() && !aForce) {
			return toShortJSON();
		}

		if (ObjectUtils.isEmpty(this.logExcludedFields)) {
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
	 * @throws AccelerateException
	 *             thrown due to {@link JSONUtil#serialize(Object)}
	 */
	public String toShortJSON() throws AccelerateException {
		return (this.idField != null) ? JSONUtil.serializeOnly(this, this.idField)
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