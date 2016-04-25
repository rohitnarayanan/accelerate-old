package accelerate.databean;

import java.util.HashSet;
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
public class AccelerateDataBean extends ModelMap {
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
	 * default constructor
	 */
	public AccelerateDataBean() {
		this.logExcludedFields = new HashSet<>();
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
	public final ModelMap addAllAttributes(Object... aArgs) throws AccelerateRuntimeException {
		if (AppUtil.isEmpty(aArgs)) {
			throw new AccelerateRuntimeException("Empty arguments are not allowed");
		}

		if ((aArgs.length % 2) != 0) {
			throw new AccelerateRuntimeException(
					"Incorrect number of arguments or array size not correct:" + aArgs.length);
		}

		for (int idx = 0; idx < aArgs.length; idx += 2) {
			try {
				put((String) aArgs[idx], aArgs[idx + 1]);
			} catch (ClassCastException error) {
				throw new AccelerateRuntimeException("Error:[{}] for key:[{}] at Index:[{}]", error.getMessage(),
						aArgs[idx], idx);
			}
		}

		return this;
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