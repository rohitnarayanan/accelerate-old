package accelerate.databean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import accelerate.util.JSONUtil;

/**
 * Abstract class serving as parent for JavaBeans providing utility methods
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 21-May-2015
 */
@JsonFilter("accelerate.databean.AccelerateDataBean")
public class AccelerateDataBean implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6296002276646863077L;

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
	@JsonIgnore
	private boolean largeDataset = false;

	/**
	 * Generic Map containing dataMap to be stored in this bean object. This
	 * works more like java bean get/set attribute incase developers do not want
	 * to create new beans with specific attributes/fields
	 */
	private Map<String, Object> dataMap = new HashMap<>();

	/**
	 * This method registers the aFieldName as a field to be excluded from
	 * logging
	 *
	 * @param aFieldNames
	 */
	public synchronized void addJsonIgnoreFields(String... aFieldNames) {
		if (this.logExcludedFields == null) {
			synchronized (this) {
				if (this.logExcludedFields == null) {
					this.logExcludedFields = new HashSet<>();
				}
			}
		}

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
	 * Function to add data to {@link #dataMap}
	 * 
	 * @param aKey
	 * @param aValue
	 */
	public void putData(String aKey, Object aValue) {
		this.dataMap.put(aKey, aValue);
	}

	/**
	 * Function to get data from {@link #dataMap}
	 * 
	 * @param aKey
	 * @return
	 */
	public Object getData(String aKey) {
		return this.dataMap.get(aKey);
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

	/**
	 * Getter method for "dataMap" property
	 * 
	 * @return dataMap
	 */
	public Map<String, Object> getDataMap() {
		return this.dataMap;
	}
}