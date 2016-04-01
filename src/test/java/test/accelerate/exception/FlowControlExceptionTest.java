package test.accelerate.exception;

import org.junit.Assert;
import org.junit.Test;

import accelerate.exception.FlowControlException;

/**
 * Junit test for {@link FlowControlException}
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 27-May-2015
 */
@SuppressWarnings("static-method")
public class FlowControlExceptionTest {
	/**
	 * Test method for
	 * {@link accelerate.exception.FlowControlException#FlowControlException(java.lang.String)}
	 */
	@Test
	public void testFlowControlException() {
		int i = 0;
		try {
			while (true) {
				if (i++ == 5) {
					throw new FlowControlException("val" + i);
				}
			}
		} catch (FlowControlException error) {
			Assert.assertTrue(error.getMessage().equals("val6"));
		}
	}
}
