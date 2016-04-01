package test.accelerate.cache;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import accelerate.cache.AccelerateCache;
import accelerate.exception.AccelerateException;
import accelerate.spring.AccelerateConfig;
import accelerate.util.AppUtil;

/**
 * Junit test for {@link AccelerateCache}
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 25-May-2015
 */
@Configuration
@Import(AccelerateConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AccelerateCacheTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccelerateCacheTest {
	/**
	 * {@link TempCache} instance
	 */
	@Autowired
	private TempCache tempCache = null;

	/**
	 * @return
	 */
	@Bean(name = "tempCache")
	public static TempCache tempCache() {
		return new TempCache();
	}

	/**
	 * @return
	 */
	@Bean
	public static CacheManager tmpCacheManager() {
		return new ConcurrentMapCacheManager();
	}

	/**
	 * Test method for
	 * {@link accelerate.cache.AccelerateCache#AccelerateCache(java.lang.String, java.lang.Class, java.lang.Class)}
	 *
	 * @throws AccelerateException
	 */
	@Test
	public synchronized void test01AccelerateCache() throws AccelerateException {
		Assert.assertTrue(3 <= this.tempCache.size());
	}

	/**
	 * Test method for {@link accelerate.cache.AccelerateCache#name()}
	 */
	@Test
	public synchronized void test02GetName() {
		Assert.assertEquals("tempCache", this.tempCache.name());
	}

	/**
	 * Test method for {@link accelerate.cache.AccelerateCache#age()}
	 */
	@Test
	public synchronized void test03GetAge() {
		Assert.assertEquals(null, this.tempCache.age());
	}

	/**
	 * Test method for
	 * {@link accelerate.cache.AccelerateCache#initializedTime()}
	 *
	 * @throws AccelerateException
	 */
	@Test
	public synchronized void test04InitializedTime() throws AccelerateException {
		Assert.assertTrue(this.tempCache.initializedTime().getTime() < System.currentTimeMillis());
	}

	/**
	 * Test method for
	 * {@link accelerate.cache.AccelerateCache#lastRefreshedTime()}
	 *
	 * @throws AccelerateException
	 */
	@Test
	public synchronized void test05LastRefreshedTime() throws AccelerateException {
		Assert.assertTrue(this.tempCache.lastRefreshedTime().getTime() < System.currentTimeMillis());
	}

	/**
	 * Test method for {@link accelerate.cache.AccelerateCache#cache()}
	 *
	 * @throws AccelerateException
	 */
	@Test
	public synchronized void test06Cache() throws AccelerateException {
		Assert.assertEquals("tempCache", this.tempCache.cache().getName());
	}

	/**
	 * Test method for {@link accelerate.cache.AccelerateCache#size()}.
	 *
	 * @throws AccelerateException
	 */
	@Test
	public synchronized void test07Size() throws AccelerateException {
		Assert.assertTrue(3 <= this.tempCache.size());
	}

	/**
	 * Test method for {@link accelerate.cache.AccelerateCache#keys()}
	 *
	 * @throws AccelerateException
	 */
	@Test
	public synchronized void test08Keys() throws AccelerateException {
		Assert.assertTrue(3 <= this.tempCache.keys().size());
	}

	/**
	 * Test method for {@link accelerate.cache.AccelerateCache#cacheManager()}
	 */
	@Test
	public synchronized void test09CacheManager() {
		Assert.assertTrue(
				AppUtil.compareAny(this.tempCache.cacheManager(), "accelerateCacheManager", "tmpCacheManager"));
	}

	/**
	 * Test method for
	 * {@link accelerate.cache.AccelerateCache#isLoadedAtStartup()}.
	 */
	@Test
	public synchronized void test10IsLoadedAtStartup() {
		Assert.assertTrue(this.tempCache.isLoadedAtStartup());
	}

	/**
	 * Test method for
	 * {@link accelerate.cache.AccelerateCache#age(java.lang.String)}
	 *
	 * @throws AccelerateException
	 */
	@Test
	public synchronized void test12SetAge() throws AccelerateException {
		this.tempCache.age("8h");
		Assert.assertEquals("8h", this.tempCache.age());
		this.tempCache.age(null);
	}

	/**
	 * Test method for {@link accelerate.cache.AccelerateCache#refresh()}.
	 *
	 * @throws AccelerateException
	 */
	@Test
	public synchronized void test13Refresh() throws AccelerateException {
		long refreshTime = this.tempCache.lastRefreshedTime().getTime();
		this.tempCache.refresh();
		Assert.assertTrue(this.tempCache.lastRefreshedTime().getTime() > refreshTime);
	}

	/**
	 * Test method for {@link accelerate.cache.AccelerateCache#initialize()}.
	 *
	 * @throws AccelerateException
	 */
	@Test
	public synchronized void test14Initialize() throws AccelerateException {
		long initializedTime = this.tempCache.initializedTime().getTime();
		this.tempCache.initialize();
		Assert.assertTrue(this.tempCache.initializedTime().getTime() > initializedTime);
	}

	/**
	 * Test method for
	 * {@link accelerate.cache.AccelerateCache#cacheManager(java.lang.String)}
	 *
	 * @throws AccelerateException
	 */
	@Test
	public synchronized void test15SetCacheManager() throws AccelerateException {
		System.out.println("test15SetCacheManager");
		this.tempCache.cacheManager("tmpCacheManager");
		Assert.assertEquals("tmpCacheManager", this.tempCache.cacheManager());
	}

	/**
	 * Test method for
	 * {@link accelerate.cache.AccelerateCache#get(java.lang.Object)}
	 *
	 * @throws AccelerateException
	 */
	@Test
	public synchronized void test16Get() throws AccelerateException {
		Assert.assertEquals("a", this.tempCache.get("a"));
	}

	/**
	 * Test method for
	 * {@link accelerate.cache.AccelerateCache#getSerialized(java.lang.String)}
	 * .
	 *
	 * @throws AccelerateException
	 */
	@Test
	public synchronized void test17GetSerialized() throws AccelerateException {
		Assert.assertEquals("\"b\"", this.tempCache.getSerialized("b"));
	}

	/**
	 * Test method for
	 * {@link accelerate.cache.AccelerateCache#put(java.lang.Object, java.lang.Object)}
	 * .
	 *
	 * @throws AccelerateException
	 */
	@Test
	public synchronized void test18Put() throws AccelerateException {
		this.tempCache.put("d", "d");
		Assert.assertEquals("d", this.tempCache.get("d"));
	}

	/**
	 * Test method for
	 * {@link accelerate.cache.AccelerateCache#putSerialized(java.lang.String, java.lang.String)}
	 * .
	 *
	 * @throws AccelerateException
	 */
	@Test
	public synchronized void test19PutSerialized() throws AccelerateException {
		this.tempCache.putSerialized("e", "e");
		Assert.assertEquals("e", this.tempCache.get("e"));
	}

	/**
	 * Test method for
	 * {@link accelerate.cache.AccelerateCache#delete(java.lang.Object)}.
	 *
	 * @throws AccelerateException
	 */
	@Test
	public synchronized void test20Delete() throws AccelerateException {
		this.tempCache.delete("e");
		Assert.assertEquals(null, this.tempCache.get("e"));
	}

	@SuppressWarnings("javadoc")
	public static class TempCache extends AccelerateCache<String, String> {
		private static final long serialVersionUID = 1L;

		public TempCache() {
			super("tempCache", String.class, String.class);
		}

		@Override
		protected Set<String> loadCache(Cache aCache) {
			this.cache.put("a", "a");
			this.cache.put("b", "b");
			this.cache.put("c", "c");

			Set<String> keySet = new HashSet<>();
			keySet.add("a");
			keySet.add("b");
			keySet.add("c");
			return keySet;
		}

		@Override
		public boolean isLoadedAtStartup() {
			return true;
		}

		@Override
		protected String fetch(String aKey) {
			return null;
		}
	}
}
