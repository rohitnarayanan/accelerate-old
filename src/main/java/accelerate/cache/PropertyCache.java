package accelerate.cache;

import static accelerate.util.AccelerateConstants.COMMA_CHAR;
import static accelerate.util.AccelerateConstants.DOT_CHAR;
import static accelerate.util.AccelerateConstants.EMPTY_STRING;
import static accelerate.util.AppUtil.compare;
import static accelerate.util.ResourceUtil.LoadPropertyMap;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;

import com.fasterxml.jackson.databind.JsonNode;

import accelerate.exception.AccelerateException;
import accelerate.util.AccelerateConstants;
import accelerate.util.ReflectionUtil;
import accelerate.util.ResourceUtil;

/**
 * <p>
 * This class provides an implementation for {@link AccelerateCache} to store
 * configuration properties in the form of key-value pairs
 * </p>
 * <p>
 * The properties can also be defined in a database table and the user will have
 * to provide the query ({@link #configQuery}) to fetch them.
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
	private String configURL;

	/**
	 * SQL query to fetch properties from the database. It is expected that the
	 * select clause will be of the format key_column, value_column
	 */
	private String configQuery;

	/**
	 * {@link DataSource} instance to query db for properties
	 */
	private DataSource dataSource;

	/**
	 * Name of the profile for which to cache the properties. Usually properties
	 * would be in the format of <profileName>.x.y.z=value. It will be cached as
	 * x.y.z=value
	 * <p>
	 * Allows to store same properties with different values.
	 */
	private String profileName;

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
	 *             thrown by
	 *             {@link ReflectionUtil#setFieldValue(Class, Object, String, Object)}
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
		return get(StringUtils.join(ArrayUtils.toArray(aPropertyKeys), DOT_CHAR));
	}

	/**
	 * This method get the property value using {@link #get(String...)} and then
	 * return a {@link List} of tokens by spliting the value by ','.
	 *
	 * @param aPropertyKeys
	 *            array of strings to be concatenated to form the property key
	 * @return array of values
	 */
	public String[] getPropertyList(String... aPropertyKeys) {
		return StringUtils.split(get(aPropertyKeys), COMMA_CHAR);
	}

	/**
	 * This method uses the {@link #get(String...)} method to get the property
	 * value and checks if the value is "true".
	 *
	 * @param aPropertyKeys
	 *            array containing string to be concatenated to form the
	 *            property key
	 * @return boolean result of the comparison
	 */
	public boolean isEnabled(String... aPropertyKeys) {
		return hasValue(Boolean.TRUE.toString(), aPropertyKeys);
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
	public boolean hasValue(String aCompareValue, String... aPropertyKeys) {
		return compare(get(aPropertyKeys), aCompareValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see accelerate.cache.AccelerateCache#loadCache(org.springframework.
	 * cache.Cache)
	 */
	/**
	 * @param aCacheMap
	 * @throws AccelerateException
	 *             thrown by
	 *             {@link ResourceUtil#LoadPropertyMap(org.springframework.context.ApplicationContext, String)}
	 */
	@Override
	protected void loadCache(final Map<String, String> aCacheMap) throws AccelerateException {
		final String prefix = isEmpty(this.profileName) ? EMPTY_STRING : this.profileName + DOT_CHAR;

		/*
		 * If config URL is provided load properties
		 */
		if (!isEmpty(getConfigURL())) {
			LoadPropertyMap(this.applicationContext, getConfigURL()).forEach((key, value) -> {
				if (key.startsWith(prefix)) {
					aCacheMap.put(key.substring(prefix.length()), value);
				}
			});
		}

		/*
		 * If configQuery is not set or db loading is disabled in the property
		 * file skipthen try loading properties from database.
		 */
		if (isEmpty(getConfigQuery()) || !compare(aCacheMap.get("fetchFromDB"), Boolean.TRUE.toString())) {
			return;
		}

		/*
		 * Query the database
		 */
		JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
		jdbcTemplate.query(this.configQuery, (aResultSet, aRowNum) -> {
			String key = aResultSet.getString(1);
			if (key.startsWith(prefix)) {
				aCacheMap.put(key.substring(prefix.length()), aResultSet.getString(2));
			}

			return null;
		});
	}

	/**
	 * Getter method for "configURL" property
	 *
	 * @return configURL
	 */
	@ManagedAttribute
	public String getConfigURL() {
		return this.configURL;
	}

	/**
	 * Setter method for "configURL" property
	 *
	 * @param aConfigURL
	 */
	@ManagedAttribute
	public void setConfigURL(String aConfigURL) {
		this.configURL = aConfigURL;
	}

	/**
	 * Getter method for "configQuery" property
	 * 
	 * @return configQuery
	 */
	@ManagedAttribute
	public String getConfigQuery() {
		return this.configQuery;
	}

	/**
	 * Setter method for "configQuery" property
	 * 
	 * @param aConfigQuery
	 */
	@ManagedAttribute
	public void setConfigQuery(String aConfigQuery) {
		this.configQuery = aConfigQuery;
	}

	/**
	 * Getter method for "profileName" property
	 *
	 * @return profileName
	 */
	@ManagedAttribute
	public String getProfileName() {
		return this.profileName;
	}

	/**
	 * Setter method for "profileName" property
	 *
	 * @param aProfileName
	 */
	@ManagedAttribute
	public void setProfileName(String aProfileName) {
		this.profileName = aProfileName;
	}

	/**
	 * Setter method for "dataSource" property
	 *
	 * @param aDataSource
	 */
	public void setDataSource(DataSource aDataSource) {
		this.dataSource = aDataSource;
	}
}