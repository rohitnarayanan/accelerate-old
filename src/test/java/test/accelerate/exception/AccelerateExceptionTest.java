package test.accelerate.exception;

import org.junit.Assert;
import org.junit.Test;

import accelerate.exception.AccelerateException;

/**
 * Junit test for {@link AccelerateException}
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 27-May-2015
 */
@SuppressWarnings("static-method")
public class AccelerateExceptionTest {
	/**
	 * Test method for
	 * {@link accelerate.exception.AccelerateException#AccelerateException()}
	 */
	@Test
	public void testAccelerateException() {
		Assert.assertEquals(null, new AccelerateException().getMessage());
	}

	/**
	 * Test method for
	 * {@link accelerate.exception.AccelerateException#AccelerateException(java.lang.Throwable)}
	 */
	@Test
	public void testAccelerateExceptionThrowable() {
		Assert.assertTrue(new AccelerateException(new Throwable("testAccelerateExceptionThrowable")).getMessage()
				.equals("java.lang.Throwable: testAccelerateExceptionThrowable"));
	}

	/**
	 * Test method for
	 * {@link accelerate.exception.AccelerateException#AccelerateException(java.lang.String, java.lang.String[])}
	 */
	@Test
	public void testAccelerateExceptionStringStringArray() {
		Assert.assertTrue(new AccelerateException("testAccelerateExceptionStringStringArray %s", "message").getMessage()
				.equals("testAccelerateExceptionStringStringArray message"));
	}

	/**
	 * Test method for
	 * {@link accelerate.exception.AccelerateException#AccelerateException(java.lang.String, java.lang.Throwable, java.lang.String[])}
	 */
	@Test
	public void testAccelerateExceptionStringThrowable() {
		Assert.assertTrue(new AccelerateException("testAccelerateExceptionStringThrowable2",
				new Throwable("testAccelerateExceptionStringThrowable1")).getMessage()
						.equals("testAccelerateExceptionStringThrowable2"));
	}
}
