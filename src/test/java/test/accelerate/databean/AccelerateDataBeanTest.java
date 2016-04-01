package test.accelerate.databean;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import accelerate.databean.AccelerateDataBean;

/**
 * Junit test for {@link AccelerateDataBean}
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 27-May-2015
 */
@SuppressWarnings("static-method")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccelerateDataBeanTest {
	/**
	 * @return
	 */
	private static AccelerateDataBean getDataBean() {
		AccelerateDataBean dataBean = new AccelerateDataBean();
		dataBean.putData("key1", "value1");
		return dataBean;
	}

	/**
	 * Test method for
	 * {@link accelerate.databean.AccelerateDataBean#addJsonIgnoreFields(java.lang.String[])}
	 */
	@Test
	public void test02RegisterLogExcludedFields() {
		AccelerateDataBean dataBean = getDataBean();
		dataBean.addJsonIgnoreFields("data");
		Assert.assertEquals("{}", dataBean.toJSON());
	}

	/**
	 * Test method for
	 * {@link accelerate.databean.AccelerateDataBean#removeJsonIgnoreFields(java.lang.String[])}
	 */
	@Test
	public void test03DeregisterLogExcludedFields() {
		AccelerateDataBean dataBean = getDataBean();
		dataBean.addJsonIgnoreFields("data");
		Assert.assertEquals("{}", dataBean.toJSON());
		dataBean.removeJsonIgnoreFields("data");
		Assert.assertEquals("{\"data\":{\"key1\":\"value1\"}}", dataBean.toJSON());
	}

	/**
	 * Test method for {@link accelerate.databean.AccelerateDataBean#toString()}
	 * .
	 */
	@Test
	public void test04ToString() {
		Assert.assertEquals("{\"data\":{\"key1\":\"value1\"}}", getDataBean().toString());
	}

	/**
	 * Test method for {@link accelerate.databean.AccelerateDataBean#toJSON()}.
	 */
	@Test
	public void test05ToJSON() {
		Assert.assertEquals("{\"data\":{\"key1\":\"value1\"}}", getDataBean().toJSON());
	}

	/**
	 * Test method for
	 * {@link accelerate.databean.AccelerateDataBean#toJSON(boolean)}.
	 */
	@Test
	public void test06ToJSON() {
		AccelerateDataBean dataBean = getDataBean();
		dataBean.setLargeDataset(true);
		Assert.assertTrue(getDataBean().toJSON().startsWith("accelerate.databean.AccelerateDataBean@"));
		Assert.assertEquals("{\"data\":{\"key1\":\"value1\"}}", getDataBean().toJSON(true));
	}

	/**
	 * Test method for
	 * {@link accelerate.databean.AccelerateDataBean#toShortJSON()} .
	 */
	@Test
	public void test07ToCollectionLog() {
		AccelerateDataBean dataBean = getDataBean();
		Assert.assertEquals(
				"{\"id\":" + dataBean.getClass().getName() + "@" + Integer.toHexString(dataBean.hashCode()) + "}",
				getDataBean().toShortJSON());
	}

	/**
	 * Test method for
	 * {@link accelerate.databean.AccelerateDataBean#getIdField()} .
	 */
	@Test
	public void test09GetIdField() {
		Assert.assertEquals(null, getDataBean().getIdField());
	}

	/**
	 * Test method for
	 * {@link accelerate.databean.AccelerateDataBean#setIdField(java.lang.String)}
	 * .
	 */
	@Test
	public void test10SetIdField() {
		AccelerateDataBean dataBean2 = new AccelerateDataBean() {
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("unused")
			private String beanId = "accelerateDataBean";
		};

		dataBean2.setIdField("beanId");
		Assert.assertEquals("{\"beanId\":\"accelerateDataBean\"}", dataBean2.toShortJSON());
	}
}