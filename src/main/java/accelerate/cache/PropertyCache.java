package accelerate.cache;

import static accelerate.util.AccelerateConstants.COMMA_CHAR;
import static accelerate.util.AccelerateConstants.YES;
import static accelerate.util.AppUtil.compare;
import static accelerate.util.ResourceUtil.LoadPropertyMap;
import static accelerate.util.StringUtil.join;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.cache.Cache;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;

import com.fasterxml.jackson.databind.JsonNode;

import accelerate.exception.AccelerateException;
import accelerate.util.AccelerateConstants;
import accelerate.util.ReflectionUtil;
import accelerate.util.ResourceUtil;
import accelerate.util.StringUtil;

/**
 * <p>
 * This class provides an implementation for {@link AccelerateCache} to store
 * configuration properties in the form of key-value pairs
 * </p>
 * <p>
 * The properties can also be defined in a database table named as
 * {@link #configTableName} table with the key stored in {@link #keyColumnName}
 * column and value in {@link #valueColumnName} column. These properties can be
 * overridden too.
 * </p>
 * <p>
 * Users also have the option of saving the properties for multiple environments
 * and profiles in the same table. All they need to do is to set the
 * {@link #profileName} property and it will be taken care of.
 * </p>
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Sep 16, 2008
 */
public class PropertyCache extends AccelerateCache<String, String> {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The attribute contains the URL to the property file. The URL can be in
	 * any format supported by the {@link ResourceUtil} class.
	 */
	private String configURL = null;

	/**
	 * {@link DataSource} instance to query db for properties
	 */
	private DataSource dataSource = null;

	/**
	 * Name of the database table that contains the properties
	 */
	private String configTableName = "APP_PROPERTIES";

	/**
	 * Name of the table column that contains the property key
	 */
	private String keyColumnName = "PROPERTY_KEY";

	/**
	 * Name of the table column that contains the property value
	 */
	private String valueColumnName = "PROPERTY_VALUE";

	/**
	 * Name of the profile for which to cache the properties. Usually properties
	 * would be in the format of <profileName>.x.y.z=value. It will be cached as
	 * x.y.z=value
	 * <p>
	 * Allows to store same properties with different values.
	 */
	private String profileName = "";

	/**
	 * Default value if property cannot be converted
	 */
	private int errorIntValue = Integer.MIN_VALUE;

	/**
	 * Default Constructor
	 *
	 * @param aCacheName
	 */
	public PropertyCache(String aCacheName) {
		super(aCacheName, String.class, String.class);
	}

	/**
	 * Overloaded Constructor
	 *
	 * @param aCacheName
	 * @param aConfigURL
	 */
	public PropertyCache(String aCacheName, String aConfigURL) {
		this(aCacheName);
		setConfigURL(aConfigURL);
	}

	/**
	 * Overloaded Constructor
	 *
	 * @param aJsonNode
	 * @throws AccelerateException
	 */
	public PropertyCache(JsonNode aJsonNode) throws AccelerateException {
		this(AccelerateConstants.EMPTY_STRING);
		Iterator<String> fields = aJsonNode.fieldNames();
		while (fields.hasNext()) {
			String field = fields.next();
			ReflectionUtil.setFieldValue(this.getClass(), this, field, aJsonNode.get(field).asText());
		}
	}

	/**
	 * This method creates a "." seperated key from the array of tokens passed
	 * and delegates to the {@link #get(String)} method for lookup.
	 *
	 * @param aPropertyKeys
	 *            array containing string to be concatenated to form the
	 *            property key
	 * @return {@link String} value stored against the key
	 */
	@ManagedOperation(description = "This method returns the element stored in cache against the given key")
	public String get(String... aPropertyKeys) {
		return get(join(aPropertyKeys));
	}

	/**
	 * This method delegates to the
	 * {@link #checkPropertyWithValue(String, String...)} method to check if the
	 * property value is "Y".
	 *
	 * @param aPropertyKey
	 *            key to be looked up
	 * @return boolean result of the comparison
	 */
	public boolean checkProperty(String aPropertyKey) {
		return checkPropertyWithValue(YES, aPropertyKey);
	}

	/**
	 * This method creates a "." seperated key from the array of tokens passed
	 * and delegates to the {@link #checkPropertyWithValue(String, String...)}
	 * method to check if the property value is "Y".
	 *
	 * @param aPropertyKeys
	 *            array containing string to be concatenated to form the
	 *            property key
	 * @return boolean result of the comparison
	 */
	public boolean checkProperty(String... aPropertyKeys) {
		return checkPropertyWithValue(YES, aPropertyKeys);
	}

	/**
	 * This method creates a "." seperated key from the array of tokens passed
	 * and compares the property value against a user specified constant.
	 *
	 * @param aCompareValue
	 *            value to be compared against
	 * @param aPropertyKeys
	 *            array of strings to be concatenated to form the property key
	 * @return boolean result of the comparison
	 */
	public boolean checkPropertyWithValue(String aCompareValue, String... aPropertyKeys) {
		return compare(get(aPropertyKeys), aCompareValue);
	}

	/**
	 * This method returns the integer value for property value
	 *
	 * @param aPropertyKey
	 *            key to be looked up
	 * @return int value for the property
	 */
	public int getIntProperty(String aPropertyKey) {
		String propertyValue = get(aPropertyKey);
		if (!isEmpty(propertyValue)) {
			return Integer.parseInt(propertyValue);
		}

		return 0;
	}

	/**
	 * This method creates a "." seperated key from the array of tokens passed
	 * and delegates to {@link #getIntProperty(String)} method to get the
	 * integer property value.
	 *
	 * @param aPropertyKeys
	 *            array of strings to be concatenated to form the property key
	 * @return int value for the property
	 */
	public int getIntProperty(String... aPropertyKeys) {
		return getIntProperty(join(aPropertyKeys));
	}

	/**
	 * This method returns an array of values, delimited by ",", stored against
	 * the given property key.
	 *
	 * @param aPropertyKey
	 *            - set of property keys to be appended
	 * @return array of values
	 */
	public String[] getPropertyList(String aPropertyKey) {
		return StringUtil.split(get(aPropertyKey), COMMA_CHAR);
	}

	/**
	 * This method creates a "." separated key from the array of tokens passed
	 * and delegates to {@link #getPropertyList(String)} method to get the array
	 * of property values.
	 *
	 * @param aPropertyKeys
	 *            array of strings to be concatenated to form the property key
	 * @return array of values
	 */
	public String[] getPropertyList(String... aPropertyKeys) {
		return getPropertyList(join(aPropertyKeys));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see accelerate.cache.AccelerateCache#isLoadedAtStartup()
	 */
	/**
	 * @return
	 */
	@Override
	public boolean isLoadedAtStartup() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see accelerate.cache.AccelerateCache#loadCache(org.springframework.
	 * cache.Cache)
	 */
	/**
	 * @param aCache
	 * @return
	 * @throws AccelerateException
	 */
	@Override
	protected Set<String> loadCache(Cache aCache) throws AccelerateException {
		Set<String> keySet = new HashSet<>();

		if (isEmpty(getConfigURL())) {
			return keySet;
		}

		final Map<String, String> propertyMap = LoadPropertyMap(this.applicationContext, getConfigURL());
		if (compare(propertyMap.get(join(this.profileName, "fetchFromDB")), YES)) {
			final int length = !isEmpty(this.profileName) ? this.profileName.length() + 1 : 0;

			StringBuilder sql = new StringBuilder();
			sql.append("select * from ").append(getConfigTableName());
			if (length > 0) {
				sql.append("where ").append(getKeyColumnName()).append(" like '").append(this.profileName)
						.append(".%'");
			}

			/*
			 * Query the database
			 */
			JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
			jdbcTemplate.query(sql.toString(), (RowMapper<Object>) (aResultSet, aRowNum) -> {
				propertyMap.put(aResultSet.getString(getKeyColumnName()).substring(length),
						aResultSet.getString(getValueColumnName()));
				return null;
			});
		}

		for (Entry<String, String> entry : propertyMap.entrySet()) {
			aCache.put(entry.getKey(), entry.getValue());
		}
		keySet.addAll(propertyMap.keySet());

		return keySet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see accelerate.cache.AccelerateCache#fetch(java.lang.Object)
	 */
	/**
	 * @param aKey
	 * @return
	 */
	@Override
	protected String fetch(String aKey) {
		throw new AccelerateException("This cache is always loaded at startup !");
	}

	/**
	 * Getter method for "configURL" property
	 *
	 * @return configURL
	 */
	@ManagedAttribute
	protected String getConfigURL() {
		return this.configURL;
	}

	/**
	 * Setter method for "configURL" property
	 *
	 * @param aConfigURL
	 */
	@ManagedAttribute
	protected void setConfigURL(String aConfigURL) {
		this.configURL = aConfigURL;
	}

	/**
	 * Getter method for "dataSource" property
	 *
	 * @return dataSource
	 */
	protected DataSource getDataSource() {
		return this.dataSource;
	}

	/**
	 * Setter method for "dataSource" property
	 *
	 * @param aDataSource
	 */
	protected void setDataSource(DataSource aDataSource) {
		this.dataSource = aDataSource;
	}

	/**
	 * Getter method for "configTableName" property
	 *
	 * @return configTableName
	 */
	@ManagedAttribute
	protected String getConfigTableName() {
		return this.configTableName;
	}

	/**
	 * Setter method for "configTableName" property
	 *
	 * @param aConfigTableName
	 */
	@ManagedAttribute
	protected void setConfigTableName(String aConfigTableName) {
		this.configTableName = aConfigTableName;
	}

	/**
	 * Getter method for "keyColumnName" property
	 *
	 * @return keyColumnName
	 */
	@ManagedAttribute
	protected String getKeyColumnName() {
		return this.keyColumnName;
	}

	/**
	 * Setter method for "keyColumnName" property
	 *
	 * @param aKeyColumnName
	 */
	@ManagedAttribute
	protected void setKeyColumnName(String aKeyColumnName) {
		this.keyColumnName = aKeyColumnName;
	}

	/**
	 * Getter method for "valueColumnName" property
	 *
	 * @return valueColumnName
	 */
	@ManagedAttribute
	protected String getValueColumnName() {
		return this.valueColumnName;
	}

	/**
	 * Setter method for "valueColumnName" property
	 *
	 * @param aValueColumnName
	 */
	@ManagedAttribute
	protected void setValueColumnName(String aValueColumnName) {
		this.valueColumnName = aValueColumnName;
	}

	/**
	 * Getter method for "profileName" property
	 *
	 * @return profileName
	 */
	@ManagedAttribute
	protected String getProfileName() {
		return this.profileName;
	}

	/**
	 * Setter method for "profileName" property
	 *
	 * @param aProfileName
	 */
	@ManagedAttribute
	protected void setProfileName(String aProfileName) {
		this.profileName = aProfileName;
	}

	/**
	 * Getter method for "errorIntValue" property
	 *
	 * @return errorIntValue
	 */
	@ManagedAttribute
	protected int getErrorIntValue() {
		return this.errorIntValue;
	}

	/**
	 * Setter method for "errorIntValue" property
	 *
	 * @param aErrorIntValue
	 */
	@ManagedAttribute
	protected void setErrorIntValue(int aErrorIntValue) {
		this.errorIntValue = aErrorIntValue;
	}
}