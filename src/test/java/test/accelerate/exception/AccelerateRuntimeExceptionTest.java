package test.accelerate.exception;

import org.junit.Assert;
import org.junit.Test;

import accelerate.exception.AccelerateRuntimeException;

/**
 * Junit test for {@link AccelerateRuntimeException}
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 27-May-2015
 */
@SuppressWarnings("static-method")
public class AccelerateRuntimeExceptionTest {
	/**
	 * Test method for
	 * {@link accelerate.exception.AccelerateRuntimeException#AccelerateRuntimeException(java.lang.Throwable)}
	 * .
	 */
	@Test
	public void testAccelerateRuntimeExceptionThrowable() {
		Assert.assertTrue(new AccelerateRuntimeException(new Throwable("testAccelerateRuntimeExceptionThrowable"))
				.getMessage().equals("java.lang.Throwable: testAccelerateRuntimeExceptionThrowable"));
	}

	/**
	 * Test method for
	 * {@link accelerate.exception.AccelerateRuntimeException#AccelerateRuntimeException(java.lang.String)}
	 * .
	 */
	@Test
	public void testAccelerateRuntimeExceptionString() {
		Assert.assertTrue(new AccelerateRuntimeException("testAccelerateRuntimeExceptionString").getMessage()
				.equals("testAccelerateRuntimeExceptionString"));
	}

	/**
	 * Test method for
	 * {@link accelerate.exception.AccelerateRuntimeException#AccelerateRuntimeException(Throwable, String, Object...)}
	 * .
	 */
	@Test
	public void testAccelerateRuntimeExceptionStringThrowable() {
		Assert.assertTrue(new AccelerateRuntimeException("testAccelerateRuntimeExceptionStringThrowable1",
				new Throwable("testAccelerateRuntimeExceptionStringThrowable2")).getMessage()
						.equals("testAccelerateRuntimeExceptionStringThrowable1"));
	}

	/**
	 * Test method for
	 * {@link accelerate.exception.AccelerateRuntimeException#AccelerateRuntimeException(java.lang.String, java.lang.Throwable)}
	 * .
	 */
	@Test(expected = AccelerateRuntimeException.class)
	public void testUncheckedThrow() {
		throw new AccelerateRuntimeException("testUncheckedThrow");
	}
}
