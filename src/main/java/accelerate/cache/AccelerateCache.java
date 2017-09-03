package accelerate.cache;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationContext;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

import accelerate.exception.AccelerateException;
import accelerate.spring.StaticListenerHelper;
import accelerate.util.AccelerateConstants;
import accelerate.util.JSONUtil;

/**
 * This is a generic {@link Map} based cache stored on the JVM heap. It has no
 * persistence mechanism. It is designed to be loaded at startup and provide
 * quick lookup to small data sets. Accelerate also provides JMX operations to
 * manage the cache and a web UI to view the cache.
 * 
 * It is not a replacement for more comprehensive caching frameworks like
 * ehcache etc.
 *
 * @param <K>
 *            Type Variable for the keys
 * @param <V>
 *            Type Variable for the values
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Nov 12, 2009
 */
@ManagedResource
public abstract class AccelerateCache<K, V> implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	protected static final Logger LOGGER = LoggerFactory.getLogger(AccelerateCache.class);

	/**
	 * {@link Class} instance for the cache key type
	 */
	protected Class<K> keyClass;

	/**
	 * {@link Class} instance for the cache value type
	 */
	protected Class<V> valueClass;

	/**
	 * {@link ApplicationContext} instance
	 */
	@Autowired
	protected ApplicationContext applicationContext;

	/**
	 * {@link StaticListenerHelper} instance
	 */
	@Autowired
	private StaticListenerHelper staticListenerHelper;

	/**
	 * 
	 */
	protected Map<K, V> cacheMap = Collections.emptyMap();

	/**
	 * Name of the cache
	 */
	private String cacheName;

	/**
	 * Cache Age
	 */
	private String cacheAge;

	/**
	 * Cache Duration
	 */
	protected long cacheDuration = -1;

	/**
	 * Is the cache refreshable or permanent
	 */
	protected boolean refreshable;

	/**
	 * Init time of Cache
	 */
	protected long cacheInitializedTime = -1;

	/**
	 * Refresh Time of Cache
	 */
	protected long cacheRefreshedTime = -1;

	/**
	 * Semaphore to block while cache is being refresh
	 */
	protected Boolean refreshMonitor;

	/**
	 * Is the cache initialized
	 */
	protected boolean initialized;

	/**
	 * Default Constructor
	 *
	 * @param aCacheName
	 *            Name of the cache
	 * @param aKeyClass
	 *            {@link Class} instance for the cache key type
	 * @param aValueClass
	 *            {@link Class} instance for the cache value type
	 */
	public AccelerateCache(String aCacheName, Class<K> aKeyClass, Class<V> aValueClass) {
		Assert.noNullElements(new Object[] { aCacheName, aKeyClass, aValueClass }, "All arguments are required");
		this.cacheName = aCacheName;
		this.keyClass = aKeyClass;
		this.valueClass = aValueClass;
	}

	/**
	 * This method initializes the cache. This is the first method that should be
	 * called before the cache can be used. It also should be registered as the
	 * "init-method" method in case the Cache class is going to be managed by Spring
	 * Framework.
	 * 
	 * @throws AccelerateException
	 *             wrapping all possible exceptions
	 */
	@PostConstruct
	public void initialize() {
		Assert.state(!this.initialized, "Batch already initialized");

		try {
			this.cacheMap = new HashMap<>();
			loadCache(this.cacheMap);
			this.cacheInitializedTime = System.currentTimeMillis();
			this.cacheRefreshedTime = System.currentTimeMillis();

			calculateCacheDuration();
			this.initialized = true;
			this.staticListenerHelper.notifyCacheLoad(AccelerateCache.this);

			this.initialized = true;
			LOGGER.info("Cache [{}] Initialized", name());
		} catch (Exception error) {
			AccelerateException.checkAndThrow(error, "Error in initializing cache [%s]", name());
		}
	}

	/*
	 * Cache main operations
	 */
	/**
	 * This method refreshes the cache
	 *
	 * @throws AccelerateException
	 *             thrown by {@link #loadCache(Map)} and
	 *             {@link StaticListenerHelper#notifyCacheLoad(AccelerateCache)}
	 */
	@ManagedOperation(description = "This method refreshes the cache")
	public void refresh() throws AccelerateException {
		Assert.state(this.initialized, "Batch not initialized");
		Assert.state(this.refreshable, "Batch not refreshable");

		LOGGER.debug("Refreshing Cache [{}]", name());

		this.refreshMonitor = true;

		// reload the cache
		Map<K, V> tmpMap = new HashMap<>();
		loadCache(tmpMap);
		this.cacheMap = tmpMap;

		// update refresh time and age
		this.cacheRefreshedTime = System.currentTimeMillis();

		// notify all registered listeners
		this.staticListenerHelper.notifyCacheLoad(AccelerateCache.this);

		// reset refresh monitor and wake all threads waiting for access
		this.refreshMonitor = false;
		synchronized (this.refreshMonitor) {
			this.refreshMonitor.notifyAll();
		}

		LOGGER.info("Cache [{}] Refreshed", name());
	}

	/**
	 * This method sets the age of the cache. The format of the age should be
	 * [Duration {@link TimeUnit}].
	 * 
	 * <p>
	 * Examples: 4.5 SECONDS, 8 HOURS, 2 DAYS
	 * </p>
	 *
	 * @param aCacheAge
	 */
	@ManagedOperation(description = "This method sets the age of the cache")
	public void setCacheAge(String aCacheAge) {
		this.cacheAge = aCacheAge;
		calculateCacheDuration();
	}

	/*
	 * Cache modification operations
	 */
	/**
	 * This method returns the element stored in cache against the given key
	 *
	 * @param aKey
	 *            key to be looked up in the cache
	 * @return value instance stored against the key
	 */
	public V get(K aKey) {
		return this.cacheMap.get(aKey);
	}

	/**
	 * This method returns the JSON form of value stored in cache against the given
	 * key
	 *
	 * @param aKeyString
	 *            key string to fetch value stored in the cache
	 * @return value instance stored against the key
	 * @throws AccelerateException
	 *             thrown by {@link JSONUtil#serialize(Object)} and
	 *             {@link JSONUtil#deserialize(String, Class)}
	 */
	@ManagedOperation(description = "This method returns the JSON form of value stored in cache against the given key")
	public String getJSON(String aKeyString) throws AccelerateException {
		K key = JSONUtil.deserialize(aKeyString, this.keyClass);
		return JSONUtil.serialize(get(key));
	}

	/**
	 * This method stores the given key-value pair in cache
	 *
	 * @param aKey
	 *            key against which the value should be stored
	 * @param aValue
	 *            value which has to be added to the cache
	 */
	public void put(K aKey, V aValue) {
		Assert.state(this.initialized, "Batch not initialized");
		this.cacheMap.put(aKey, aValue);
	}

	/**
	 * This method stores the given key-value pair in cache after converting them
	 *
	 * @param aKeyString
	 *            Key to be added to the cache
	 * @param aValueString
	 *            Value to be stored in the cache. It can be a simple String or JSON
	 *            representation
	 * @throws AccelerateException
	 *             thrown by {@link JSONUtil#serialize(Object)} and
	 *             {@link JSONUtil#deserialize(String, Class)}
	 */
	@SuppressWarnings("unchecked")
	@ManagedOperation(description = "This method stores the given key-value pair in cache after converting them")
	public void putJSON(String aKeyString, String aValueString) throws AccelerateException {
		Assert.state(this.initialized, "Batch not initialized");

		K key = JSONUtil.deserialize(aKeyString, this.keyClass);
		V value = null;
		if (String.class.equals(this.valueClass)) {
			value = (V) aValueString;
		} else {
			value = JSONUtil.deserialize(aValueString, this.valueClass);
		}

		put(key, value);
	}

	/**
	 * This method removes the stored element from cache against the given key
	 *
	 * @param aKey
	 *            cache key which is to be removed
	 * @return value that was removed. null, if the key was not found in the map
	 */
	public V remove(K aKey) {
		Assert.state(this.initialized, "Batch not initialized");

		return this.cacheMap.remove(aKey);
	}

	/**
	 * This method removes the stored element from cache against the given key
	 *
	 * @param aKeyString
	 *            cache key which is to be removed
	 * @return value that was removed. null, if the key was not found in the map
	 * @throws AccelerateException
	 *             thrown by {@link JSONUtil#serialize(Object)} and
	 *             {@link JSONUtil#deserialize(String, Class)}
	 */
	@ManagedOperation(description = "This method allows to remove the given key from the cache")
	public String removeJSON(String aKeyString) throws AccelerateException {
		Assert.state(this.initialized, "Batch not initialized");

		K key = JSONUtil.deserialize(aKeyString, this.keyClass);
		V value = this.cacheMap.remove(key);

		return JSONUtil.serialize(value);
	}

	/*
	 * Cache info methods
	 */
	/**
	 * This method returns the name of the cache
	 *
	 * @return cache name
	 */
	@ManagedOperation(description = "This method returns the name of the cache")
	@JsonView(JsonSummary.class)
	public String name() {
		return this.cacheName;
	}

	/**
	 * This method returns the number of keys stored in cache
	 *
	 * @return cache size
	 */
	@ManagedOperation(description = "This method returns the number of keys stored in cache")
	@JsonView(JsonSummary.class)
	public int size() {
		return this.cacheMap.size();
	}

	/**
	 * This method returns the base map which stores the cache
	 *
	 * @return {@link Cache} instance
	 */
	public Map<K, V> cache() {
		return Collections.unmodifiableMap(this.cacheMap);
	}

	/**
	 * This method returns all the keys stored in cache
	 *
	 * @return {@link List} instance
	 */
	@ManagedOperation(description = "This method returns all the keys stored in cache")
	@JsonView(JsonDetails.class)
	public Set<K> keys() {
		return this.cacheMap.keySet();
	}

	/**
	 * This method returns the age of the cache
	 *
	 * @return cache name
	 */
	@ManagedOperation(description = "This method returns the age of the cache")
	public String age() {
		return this.cacheAge;
	}

	/**
	 * This method returns the date timestamp when the cache was first initialized
	 *
	 * @return cache name
	 */
	@ManagedOperation(description = "This method returns the date timestamp when the cache was first initiliazed")
	@JsonView(JsonSummary.class)
	@JsonFormat(pattern = "MM/dd/yyyy HH:ss:SSS z")
	public Date initializedTime() {
		Assert.state(this.initialized, "Batch not initialized");

		return new Date(this.cacheInitializedTime);
	}

	/**
	 * This method returns the date timestamp when the cache was last refreshed
	 *
	 * @return cache name
	 */
	@ManagedOperation(description = "This method returns the date timestamp when the cache was last refreshed")
	@JsonView(JsonSummary.class)
	@JsonFormat(pattern = "MM/dd/yyyy HH:ss:SSS z")
	public Date lastRefreshedTime() {
		Assert.state(this.initialized, "Batch not initialized");

		return new Date(this.cacheRefreshedTime);
	}

	/**
	 * This method fetches the data to be loaded into the cache, from the data
	 * source.
	 * 
	 * @param aCacheMap
	 *
	 * @throws AccelerateException
	 *             Allowing implementations to wrap exceptions in one class
	 */
	protected abstract void loadCache(Map<K, V> aCacheMap) throws AccelerateException;

	/**
	 * A scheduled cron to run every 5 mins to check if the cache needs to be
	 * refreshed
	 * 
	 * @throws AccelerateException
	 *             thrown by {@link #refresh()}
	 */
	@Scheduled(fixedDelay = 5 * 60 * 1000)
	@Async
	private void checkRefresh() throws AccelerateException {
		/*
		 * if cache has not been initialized or is not refreshable or is currently
		 * refreshing, return
		 */
		if (!this.initialized || !this.refreshable || this.refreshMonitor) {
			return;
		}

		if ((System.currentTimeMillis() - this.cacheRefreshedTime) > this.cacheDuration) {
			refresh();
		}
	}

	/**
	 * This method calculates the cache duration to determine when the cache is due
	 * to be refreshed from the data store
	 */
	private void calculateCacheDuration() {
		if (this.cacheAge != null) {
			String[] tokens = StringUtils.split(this.cacheAge, AccelerateConstants.SPACE_CHAR);
			this.cacheDuration = TimeUnit.valueOf(tokens[1]).toMillis(Long.parseLong(tokens[0]));
		} else {
			this.cacheDuration = -1;
		}

		if (this.cacheDuration > 0) {
			this.refreshable = true;
		}
	}

	@SuppressWarnings("javadoc")
	private interface JsonSummary {
		// marker interface
	}

	@SuppressWarnings("javadoc")
	private interface JsonDetails {
		// marker interface
	}
}