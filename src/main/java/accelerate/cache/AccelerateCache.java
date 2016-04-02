package accelerate.cache;

import static accelerate.util.AccelerateConstants.AUDIT_LOGGER;
import static accelerate.util.AccelerateConstants.ERROR_LOGGER;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;

import accelerate.exception.AccelerateException;
import accelerate.logging.AccelerateLogger;
import accelerate.spring.StaticListenerHelper;
import accelerate.util.JSONUtil;
import accelerate.util.StringUtil;

/**
 * This is a generic {@link Map} based cache stored on the JVM heap. It has no
 * persistence mechanism. It is designed to be loaded at startup and provide
 * quick lookup to small data sets. Accelerate also provides JMX operations to
 * manage the cache and a web UI to view the cache.
 * <p>
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
	 * {@link Class} instance for the cache key type
	 */
	protected Class<K> keyClass = null;

	/**
	 * {@link Class} instance for the cache value type
	 */
	protected Class<V> valueClass = null;

	/**
	 * {@link ApplicationContext} instance
	 */
	@Autowired
	protected ApplicationContext applicationContext = null;

	/**
	 * {@link StaticListenerHelper} instance
	 */
	@Autowired
	protected StaticListenerHelper staticListenerHelper = null;

	/**
	 * {@link CacheManager} instance that manages the cache
	 */
	protected CacheManager cacheManagerBean = null;

	/**
	 * Name of the cache manager
	 */
	protected String cacheManager = "accelerateCacheManager";

	/**
	 * {@link Cache} instance to store
	 */
	protected Cache cache = null;

	/**
	 * Cache Key List
	 */
	@JsonView(JsonDetails.class)
	protected Set<K> keyList = null;

	/**
	 * Cache Duration
	 */
	protected long cacheDuration = -1;

	/**
	 * Init time of Cache
	 */
	protected long cacheInitializedTime = -1;

	/**
	 * Refresh Time of Cache
	 */
	protected long cacheRefreshedTime = -1;

	/**
	 * Is the cache refreshable or permanent
	 */
	protected boolean refreshable = false;

	/**
	 * Is the cache being refreshed
	 */
	protected boolean refreshing = false;

	/**
	 * Is the cache initialized
	 */
	protected boolean initialized = false;

	/**
	 * Name of the cache
	 */
	private String cacheName = null;

	/**
	 * Cache Age
	 */
	private String cacheAge = null;

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
		this.cacheName = aCacheName;
		this.keyClass = aKeyClass;
		this.valueClass = aValueClass;
	}

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
	 * This method sets the age of the cache
	 *
	 * @param aCacheAge
	 * @throws AccelerateException
	 */
	@ManagedOperation(description = "This method sets the age of the cache")
	public void age(String aCacheAge) throws AccelerateException {
		if (!isLoadedAtStartup()) {
			throw new AccelerateException("Cache Not LoadedAtStartup");
		}

		this.cacheAge = aCacheAge;
		calculateCacheDuration();
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
	 * This method returns the date timestamp when the cache was first
	 * initialized
	 *
	 * @return cache name
	 * @throws AccelerateException
	 */
	@ManagedOperation(description = "This method returns the date timestamp when the cache was first initiliazed")
	@JsonView(JsonSummary.class)
	@JsonFormat(pattern = "MM/dd/yyyy HH:ss:SSS z")
	public Date initializedTime() throws AccelerateException {
		if (this.cacheInitializedTime > 0) {
			return new Date(this.cacheInitializedTime);
		}

		throw new AccelerateException("Not Initialized");
	}

	/**
	 * This method returns the date timestamp when the cache was last refreshed
	 *
	 * @return cache name
	 * @throws AccelerateException
	 */
	@ManagedOperation(description = "This method returns the date timestamp when the cache was last refreshed")
	@JsonView(JsonSummary.class)
	@JsonFormat(pattern = "MM/dd/yyyy HH:ss:SSS z")
	public Date lastRefreshedTime() throws AccelerateException {
		if (this.cacheRefreshedTime > 0) {
			return new Date(this.cacheRefreshedTime);
		}

		throw new AccelerateException("Never Refreshed");
	}

	/**
	 * This method returns the base map which stores the cache
	 *
	 * @return {@link Cache} instance
	 * @throws AccelerateException
	 */
	public Cache cache() throws AccelerateException {
		if (!this.initialized) {
			throw new AccelerateException("Not Initialized");
		}

		return this.cache;
	}

	/**
	 * This method returns the number of keys stored in cache
	 *
	 * @return cache size
	 * @throws AccelerateException
	 */
	@ManagedOperation(description = "This method returns the number of keys stored in cache")
	@JsonView(JsonSummary.class)
	public int size() throws AccelerateException {
		if (this.initialized) {
			return this.keyList.size();
		}

		throw new AccelerateException("Not Initialized");
	}

	/**
	 * This method returns all the keys stored in cache
	 *
	 * @return {@link List} instance
	 * @throws AccelerateException
	 */
	@ManagedOperation(description = "This method returns all the keys stored in cache")
	public Set<K> keys() throws AccelerateException {
		if (this.initialized) {
			return this.keyList;
		}

		throw new AccelerateException("Not Initialized");
	}

	/**
	 * This method sets the name of the cache manager. It re-initializes the
	 * cache too.
	 *
	 * @param aCacheManager
	 * @throws AccelerateException
	 */
	@ManagedOperation(description = "This method sets the name of the cache manager")
	public void cacheManager(String aCacheManager) throws AccelerateException {
		this.cacheManager = aCacheManager;
		this.cache.clear();
		initialize();
	}

	/**
	 * This method returns the name of the cache manager
	 *
	 * @return cache name
	 */
	@ManagedOperation(description = "This method returns the name of the cache manager")
	public String cacheManager() {
		return this.cacheManager;
	}

	/**
	 * This method returns the element stored in cache against the given key
	 *
	 * @param aKey
	 *            key to be looked up in the cache
	 * @return value instance stored against the key
	 * @throws AccelerateException
	 */
	@SuppressWarnings("unchecked")
	public V get(K aKey) throws AccelerateException {
		if (!this.initialized) {
			throw new AccelerateException("Not Initialized");
		}

		V value = null;
		ValueWrapper valueWrapper = this.cache.get(aKey);
		if (valueWrapper != null) {
			value = (V) valueWrapper.get();
		} else if (!isLoadedAtStartup()) {
			value = fetch(aKey);
			this.cache.put(aKey, value);
			manageKeyList(aKey, 0);
		}

		return value;
	}

	/**
	 * This method returns the JSON form of value stored in cache against the
	 * given key
	 *
	 * @param aKeyString
	 *            key string to fetch value stored in the cache
	 * @return value instance stored against the key
	 * @throws AccelerateException
	 */
	@ManagedOperation(description = "This method returns the JSON form of value stored in cache against the given key")
	public String getSerialized(String aKeyString) throws AccelerateException {
		if (!this.initialized) {
			throw new AccelerateException("Not Initialized");
		}

		K key = JSONUtil.deserialize(aKeyString, this.keyClass);
		V value = get(key);

		return JSONUtil.serialize(value, Include.NON_EMPTY, false, true, true);
	}

	/**
	 * This method stores the given key-value pair in cache
	 *
	 * @param aKey
	 *            key against which the value should be stored
	 * @param aValue
	 *            value which has to be added to the cache
	 * @throws AccelerateException
	 */
	public void put(K aKey, V aValue) throws AccelerateException {
		if (!this.initialized) {
			throw new AccelerateException("Not Initialized");
		}

		this.cache.put(aKey, aValue);
		manageKeyList(aKey, 0);
	}

	/**
	 * This method stores the given key-value pair in cache after converting
	 * them
	 *
	 * @param aKeyString
	 *            Key to be added to the cache
	 * @param aValueString
	 *            Value to be stored in the cache. It can be a simple String or
	 *            JSON representation
	 * @throws AccelerateException
	 */
	@SuppressWarnings("unchecked")
	@ManagedOperation(description = "This method stores the given key-value pair in cache after converting them")
	public void putSerialized(String aKeyString, String aValueString) throws AccelerateException {
		if (!this.initialized) {
			throw new AccelerateException("Not Initialized");
		}

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
	 * @throws AccelerateException
	 */
	@SuppressWarnings("unchecked")
	public V delete(K aKey) throws AccelerateException {
		if (!this.initialized) {
			throw new AccelerateException("Not Initialized");
		}

		V value = null;
		ValueWrapper valueWrapper = this.cache.get(aKey);
		if (valueWrapper != null) {
			value = (V) valueWrapper.get();
		}

		this.cache.evict(aKey);
		manageKeyList(aKey, 1);
		return value;
	}

	/**
	 * This method refreshes the cache
	 *
	 * @return flag to indicate success of operation
	 * @throws AccelerateException
	 */
	@ManagedOperation(description = "This method refreshes the cache")
	public boolean refresh() throws AccelerateException {
		if (!this.initialized) {
			throw new AccelerateException("Not Initialized");
		}

		if (!this.refreshable) {
			throw new AccelerateException("Not Refeshable");
		}

		AccelerateLogger.info(this.getClass(), AUDIT_LOGGER, "{}: Refreshing Cache", name());

		this.refreshing = true;
		boolean returnFlag = true;

		try {
			Cache tmpCache = this.cacheManagerBean.getCache("tmp_" + name());

			if (tmpCache == null) {
				throw new AccelerateException("Temp Cache Not Available: tmp_" + name());
			}

			Set<K> tmpKeyList = loadCache(this.cache);
			for (K key : this.keyList) {
				if (!tmpKeyList.contains(key)) {
					this.cache.evict(key);
				}
			}

			this.cacheRefreshedTime = System.currentTimeMillis();
			this.staticListenerHelper.notifyCacheLoad(AccelerateCache.this);
		} catch (Exception error) {
			AccelerateLogger.exception(AccelerateCache.class, ERROR_LOGGER, AccelerateLogger.LogLevel.ERROR, error,
					"{}: Cache Refresh Error", name());

			returnFlag = false;
		}

		this.refreshing = false;

		AccelerateLogger.info(AccelerateCache.class, AUDIT_LOGGER, "{}: Cache Refreshed", name());

		return returnFlag;
	}

	/**
	 * This method initializes the cache. This is the first method that should
	 * be called before the cache can be used. It also should be registered as
	 * the "init-method" method in case the Cache class is going to be managed
	 * by Spring Framework.
	 *
	 * @return cache instance to allow chained calls to API
	 * @throws AccelerateException
	 */
	@PostConstruct
	public AccelerateCache<K, V> initialize() throws AccelerateException {
		try {
			this.cacheManagerBean = this.applicationContext.getBean(cacheManager(), CacheManager.class);
			this.cache = this.cacheManagerBean.getCache(name());

			if (this.cache == null) {
				throw new AccelerateException("Cache Not Available");
			}

			this.cache.clear();
			if (isLoadedAtStartup()) {
				this.keyList = loadCache(this.cache);
				this.cacheInitializedTime = System.currentTimeMillis();
				this.cacheRefreshedTime = System.currentTimeMillis();

				calculateCacheDuration();
				this.initialized = true;
				this.staticListenerHelper.notifyCacheLoad(AccelerateCache.this);
			}

			this.initialized = true;
			AccelerateLogger.info(this.getClass(), AUDIT_LOGGER, "{}: Cache Initialized", name());
		} catch (Exception error) {
			AccelerateLogger.exception(this.getClass(), ERROR_LOGGER, AccelerateLogger.LogLevel.ERROR, error,
					"{}: Cache Initialize Error", name());
			throw new AccelerateException("Initialize Error", error);
		}

		return this;
	}

	/**
	 * This method manages the list keys stored in the cache
	 *
	 * @param aKey
	 * @param aAddRemoveFlag
	 */
	protected synchronized void manageKeyList(K aKey, int aAddRemoveFlag) {
		if (aAddRemoveFlag == 0) {
			this.keyList.add(aKey);
		} else {
			this.keyList.remove(aKey);
		}
	}

	/**
	 * @throws AccelerateException
	 */
	@Scheduled(fixedDelay = 60000)
	@Async
	protected void scheduleRefresh() throws AccelerateException {
		if (!this.initialized) {
			throw new AccelerateException("Not Initialized");
		}

		if (!this.refreshable) {
			return;
		}

		if ((System.currentTimeMillis() - this.cacheRefreshedTime) > this.cacheDuration) {
			refresh();
		}
	}

	/**
	 * This method returns true if the data for this cache is loaded at once at
	 * startup, else false
	 *
	 * @return boolean value
	 */
	public abstract boolean isLoadedAtStartup();

	/**
	 * This method fetches the data to be loaded into the cache, from the data
	 * source.
	 *
	 * @param aCache
	 *            {@link Cache} instance to load initial data
	 * @return {@link Set} of keys added to cache
	 * @throws AccelerateException
	 */
	protected abstract Set<K> loadCache(Cache aCache) throws AccelerateException;

	/**
	 * This method fetches the value for the given key from the data source.
	 *
	 * @param aKey
	 *            Key for which the value is to be fetched
	 * @return Value retrieved from the data source
	 * @throws AccelerateException
	 */
	protected abstract V fetch(K aKey) throws AccelerateException;

	/**
	 * This method calculates the cache duration to determine when the cache is
	 * due to be refreshed from the data store
	 */
	private void calculateCacheDuration() {
		if (this.cacheAge != null) {
			char timeframe = StringUtil.extractFromEnd(this.cacheAge, 1).charAt(0);
			Float value = Float.parseFloat(StringUtil.extractUpto(this.cacheAge, 0, 1));

			switch (timeframe) {
			case 'S':
				this.cacheDuration = value.longValue();
				break;
			case 's':
				this.cacheDuration = value.longValue() * 1000;
				break;
			case 'm':
				this.cacheDuration = value.longValue() * 60 * 1000;
				break;
			case 'h':
				this.cacheDuration = value.longValue() * 60 * 60 * 1000;
				break;
			case 'd':
				this.cacheDuration = value.longValue() * 24 * 60 * 60 * 1000;
				break;
			}
		} else {
			this.cacheDuration = -1;
		}

		if (this.cacheDuration > 0) {
			this.refreshable = true;
		}
	}

	@SuppressWarnings("javadoc")
	public interface JsonSummary {
		// marker interface
	}

	@SuppressWarnings("javadoc")
	public interface JsonDetails {
		// marker interface
	}
}