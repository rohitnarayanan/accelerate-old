package test.accelerate.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import accelerate.databean.RequestBean;
import accelerate.databean.SessionBean;
import accelerate.exception.AccelerateException;
import accelerate.util.JSONUtil;

/**
 * Junit test for {@link JSONUtil}
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 25-May-2015
 */
@SuppressWarnings("static-method")
public class JSONUtilTest {
	/**
	 * Test method for
	 * {@link accelerate.util.JSONUtil#serialize(java.lang.Object)} .
	 */
	@Test
	public void testSerializePerformance() {
		long l1 = System.currentTimeMillis();

		Map<String, String> map1 = new HashMap<>();
		map1.put("key1", "value1");
		map1.put("key2", "value2");
		map1.put("key3", "value3");

		SessionBean[] array = new SessionBean[3];
		array[0] = new SessionBean("MySessionBean1");
		array[0].setUsername("rohit");
		array[0].setPassword("password");
		array[1] = new SessionBean("MySessionBean2");
		array[1].setUsername("rohit");
		array[1].setPassword("password");
		array[2] = new SessionBean("MySessionBean3");
		array[2].setUsername("rohit");
		array[2].setPassword("password");

		Map<String, SessionBean> map2 = new HashMap<>();
		map2.put("key1", array[0]);
		map2.put("key2", array[1]);
		map2.put("key3", array[2]);

		long l2 = System.currentTimeMillis();
		System.out.println("Prepare input -----------" + (l2 - l1));
		l1 = l2;

		System.out.println(JSONUtil.serialize(map1));
		l2 = System.currentTimeMillis();
		System.out.println("JSONUtil.serialize(map1) -----------" + (l2 - l1));
		l1 = l2;

		System.out.println(JSONUtil.serialize(map2));
		l2 = System.currentTimeMillis();
		System.out.println("JSONUtil.serialize(map2) -----------" + (l2 - l1));
		l1 = l2;

		System.out.println(JSONUtil.serialize(map2));
		l2 = System.currentTimeMillis();
		System.out.println("JSONUtil.serialize(map2) -----------" + (l2 - l1));
		l1 = l2;

		System.out.println(JSONUtil.serialize(array[0]));
		l2 = System.currentTimeMillis();
		System.out.println("JSONUtil.serialize(array[0]) -----------" + (l2 - l1));
	}

	/**
	 * Test method for
	 * {@link accelerate.util.JSONUtil#serialize(Object, Include, boolean, boolean, boolean)}
	 * .
	 */
	@Test
	public void testSerializeAll() {
		RequestBean bean = new RequestBean();
		bean.setActionId("asdfgh");
		bean.setQueryString("qwerty");
		Assert.assertEquals("Invalid JSON",
				"{\"actionId\":\"asdfgh\",\"queryString\":\"qwerty\",\"paramsPopulated\":false}",
				JSONUtil.serialize(bean, Include.NON_EMPTY, false, true, true));
	}

	/**
	 * Test method for
	 * {@link accelerate.util.JSONUtil#serializeOnly(Object, String...)} .
	 */
	@Test
	public void testSerializeOnly() {
		SessionBean sessionBean = new SessionBean("MySessionBean5");
		sessionBean.setUsername("asdfgh");
		Assert.assertEquals("Invalid JSON", "{\"sessionId\":\"MySessionBean5\"}",
				JSONUtil.serializeOnly(sessionBean, "sessionId"));
	}

	/**
	 * Test method for
	 * {@link accelerate.util.JSONUtil#serializeExcept(Object, String...)} .
	 */
	@Test
	public void testSerializeExcept() {
		SessionBean sessionBean = new SessionBean("MySessionBean6");
		sessionBean.setUsername("asdfgh");
		sessionBean.putData("key", "value");
		Assert.assertEquals("Invalid JSON", "{\"data\":{\"key\":\"value\"},\"sessionId\":\"MySessionBean6\"}",
				JSONUtil.serializeExcept(sessionBean, "username", "initTime"));
	}

	/**
	 * Test method for
	 * {@link accelerate.util.JSONUtil#deserialize(String, Class)} .
	 *
	 * @throws AccelerateException
	 */
	@Test
	public void testDeserialize() throws AccelerateException {
		Assert.assertEquals("Invalid JSON",
				"{\"actionId\":\"asdfgh\",\"queryString\":\"qwerty\",\"paramsPopulated\":false}",
				JSONUtil.serialize(JSONUtil.deserialize(
						"{\"actionId\":\"asdfgh\",\"queryString\":\"qwerty\",\"paramsPopulated\":false}",
						RequestBean.class)));
	}
}
