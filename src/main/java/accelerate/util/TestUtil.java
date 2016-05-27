package accelerate.util;

import java.util.Arrays;

import org.springframework.util.Assert;

/**
 * This class provides utility methods for the application
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jun 12, 2009
 */
public final class TestUtil {

	/**
	 * hidden constructor
	 */
	private TestUtil() {
	}

	/**
	 * @param aMessage
	 * @param aValueList
	 */
	public static void assertNoNEmpty2(String aMessage, Object... aValueList) {
		Assert.notNull(aValueList, aMessage);
		Arrays.stream(aValueList).forEach(aValue -> Assert.notNull(aValue, aMessage));
	}

	/**
	 * @param aMessage
	 * @param aValue1
	 * @param aValue2
	 */
	public static void assertEqual2(String aMessage, Object aValue1, Object aValue2) {
		Assert.isTrue(AppUtil.compare(aValue1, aValue2), aMessage);
	}

	/**
	 * @param aMessage
	 * @param aValue1
	 * @param aValue2
	 */
	public static void assertNotEqual2(String aMessage, Object aValue1, Object aValue2) {
		Assert.isTrue(!AppUtil.compare(aValue1, aValue2), aMessage);
	}
}