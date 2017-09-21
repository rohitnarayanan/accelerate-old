package accelerate.databean;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import accelerate.exception.AccelerateException;
import accelerate.util.JSONUtil;

/**
 * Abstract class serving as parent for JavaBeans providing utility methods
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 21-May-2015
 */
@JsonFilter("default")
public class AccelerateDataBean implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@link Set} to include names of fields to exclude while logging
	 */
	protected transient Set<String> logExcludedFields = Collections.EMPTY_SET;

	/**
	 * Name of the id field for the bean
	 */
	private transient String idField;

	/**
	 * Flag to indicate that the bean stores a huge amount of data, so exception
	 * handlers and interceptors can avoid serializing the entire bean
	 */
	private transient boolean largeDataset;

	/**
	 * Instance of {@link DataMap} for generic storage
	 */
	@JsonIgnore
	private DataMap dataMap;

	/*
	 * Static Methods
	 */
	/**
	 * Static shortcut method to build a new instance using
	 * {@link #putAllAnd(Object...)}
	 * 
	 * @param aArgs
	 * @return
	 */
	public static final AccelerateDataBean build(Object... aArgs) {
		AccelerateDataBean bean = new AccelerateDataBean();
		bean.putAllAnd(aArgs);
		return bean;
	}

	/*
	 * Constructors
	 */
	/**
	 * default constructor
	 */
	public AccelerateDataBean() {
	}

	/*
	 * Override Methods
	 */
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

	/*
	 * Delegate Methods
	 */
	/**
	 * @param aKey
	 * @param aValue
	 * @return
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(String aKey, Object aValue) {
		return getDataMap().put(aKey, aValue);
	}

	/**
	 * @param aMap
	 * @see java.util.HashMap#putAll(java.util.Map)
	 */
	public void putAll(Map<? extends String, ? extends Object> aMap) {
		getDataMap().putAll(aMap);
	}

	/**
	 * @param aKey
	 * @param aValue
	 * @return
	 * @see java.util.HashMap#putIfAbsent(java.lang.Object, java.lang.Object)
	 */
	public Object putIfAbsent(String aKey, Object aValue) {
		return getDataMap().putIfAbsent(aKey, aValue);
	}

	/**
	 * @param aKey
	 * @param aValue
	 * @return
	 * @see java.util.HashMap#replace(java.lang.Object, java.lang.Object)
	 */
	public Object replace(String aKey, Object aValue) {
		return getDataMap().replace(aKey, aValue);
	}

	/**
	 * @param aFunction
	 * @see java.util.HashMap#replaceAll(java.util.function.BiFunction)
	 */
	public void replaceAll(BiFunction<? super String, ? super Object, ? extends Object> aFunction) {
		getDataMap().replaceAll(aFunction);
	}

	/**
	 * @param <T>
	 * @param aKey
	 * @return
	 * @see java.util.HashMap#remove(java.lang.Object)
	 */
	public <T> T remove(String aKey) {
		return getDataMap().remove(aKey);
	}

	/**
	 * @param aKey
	 * @param aValue
	 * @return
	 * @see java.util.HashMap#remove(java.lang.Object, java.lang.Object)
	 */
	public boolean remove(String aKey, Object aValue) {
		return getDataMap().remove(aKey, aValue);
	}

	/**
	 * @param aKey
	 * @param aValue
	 * @param aRemappingFunction
	 * @return
	 * @see java.util.HashMap#merge(java.lang.Object, java.lang.Object,
	 *      java.util.function.BiFunction)
	 */
	public Object merge(String aKey, Object aValue,
			BiFunction<? super Object, ? super Object, ? extends Object> aRemappingFunction) {
		return getDataMap().merge(aKey, aValue, aRemappingFunction);
	}

	/**
	 * 
	 * @see java.util.HashMap#clear()
	 */
	public void clear() {
		getDataMap().clear();
	}

	/**
	 * @param aKey
	 * @param aDefaultValue
	 * @return
	 * @see java.util.HashMap#getOrDefault(java.lang.Object, java.lang.Object)
	 */
	public Object getOrDefault(String aKey, Object aDefaultValue) {
		return getDataMap().getOrDefault(aKey, aDefaultValue);
	}

	/**
	 * @param aKey
	 * @return
	 * @see java.util.HashMap#containsKey(java.lang.Object)
	 */
	public boolean containsKey(String aKey) {
		return getDataMap().containsKey(aKey);
	}

	/**
	 * @return
	 * @see java.util.HashMap#keySet()
	 */
	public Set<String> keySet() {
		return getDataMap().keySet();
	}

	/**
	 * @return
	 * @see java.util.HashMap#entrySet()
	 */
	public Set<Entry<String, Object>> entrySet() {
		return getDataMap().entrySet();
	}

	/**
	 * @param aAction
	 * @see java.util.HashMap#forEach(java.util.function.BiConsumer)
	 */
	public void forEach(BiConsumer<? super String, ? super Object> aAction) {
		getDataMap().forEach(aAction);
	}

	/**
	 * @param aKey
	 * @param aValue
	 * @return
	 * @see accelerate.databean.DataMap#putAnd(java.lang.String, java.lang.Object)
	 */
	public DataMap putAnd(String aKey, Object aValue) {
		return getDataMap().putAnd(aKey, aValue);
	}

	/**
	 * @param aArgs
	 * @return
	 * @see accelerate.databean.DataMap#putAllAnd(java.lang.Object[])
	 */
	public DataMap putAllAnd(Object... aArgs) {
		return getDataMap().putAllAnd(aArgs);
	}

	/**
	 * @param <T>
	 * @param aKey
	 * @return
	 * @see accelerate.databean.DataMap#get(java.lang.String)
	 */
	public <T> T get(String aKey) {
		return getDataMap().get(aKey);
	}

	/**
	 * @param aKey
	 * @return
	 * @see accelerate.databean.DataMap#getString(java.lang.String)
	 */
	public String getString(String aKey) {
		return getDataMap().getString(aKey);
	}

	/**
	 * @param aKey
	 * @return
	 * @see accelerate.databean.DataMap#getInt(java.lang.String)
	 */
	public Integer getInt(String aKey) {
		return getDataMap().getInt(aKey);
	}

	/**
	 * @param aKey
	 * @return
	 * @see accelerate.databean.DataMap#is(java.lang.String)
	 */
	public boolean is(String aKey) {
		return getDataMap().is(aKey);
	}

	/*
	 * Public API
	 */
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

		if (ObjectUtils.isEmpty(getLogExcludedFields())) {
			return JSONUtil.serialize(this);
		}

		return JSONUtil.serializeExcept(this,
				getLogExcludedFields().toArray(new String[getLogExcludedFields().size()]));
	}

	/**
	 * This method return a short JSON representation of this bean to save memory or
	 * disk space
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
	 * This method registers the aFieldName as a field to be excluded from logging
	 *
	 * @param aFieldNames
	 */
	public synchronized void addJsonIgnoreFields(String... aFieldNames) {
		if (this.logExcludedFields == Collections.EMPTY_SET) {
			this.logExcludedFields = new HashSet<>();
		}

		for (String field : aFieldNames) {
			this.logExcludedFields.add(field);
		}
	}

	/**
	 * This method registers the aFieldName as a field to be excluded from logging
	 *
	 * @param aFieldNames
	 */
	public synchronized void removeJsonIgnoreFields(String... aFieldNames) {
		if (ObjectUtils.isEmpty(this.logExcludedFields)) {
			return;
		}

		for (String field : aFieldNames) {
			this.logExcludedFields.remove(field);
		}
	}

	/*
	 * Private Methods
	 */
	/**
	 * @return
	 */
	@JsonAnyGetter
	private DataMap getDataMap() {
		if (this.dataMap == null) {
			this.dataMap = new DataMap();
		}

		return this.dataMap;
	}

	/**
	 * @return
	 */
	private Set<String> getLogExcludedFields() {
		return this.logExcludedFields;
	}

	/*
	 * Getters/Setters
	 */
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