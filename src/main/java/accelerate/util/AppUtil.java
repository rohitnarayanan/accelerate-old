package accelerate.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

/**
 * This class provides utility methods for the application
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jun 12, 2009
 */
public final class AppUtil {

	/**
	 * hidden constructor
	 */
	private AppUtil() {
	}

	/**
	 * @param value
	 * @return true if object is empty
	 */
	public static boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		} else if (value.getClass().isArray()) {
			return ArrayUtils.getLength(value) == 0;
		} else if (value instanceof Collection<?>) {
			return ((Collection<?>) value).size() == 0;
		} else if (value instanceof Map<?, ?>) {
			return ((Map<?, ?>) value).isEmpty();
		} else if (value instanceof String) {
			return ((String) value).trim().length() == 0;
		} else if (value instanceof CharSequence) {
			return ((CharSequence) value).length() == 0;
		}

		return false;
	}

	/**
	 * @param aValueList
	 * @return
	 */
	public static boolean isEmptyAny(Object... aValueList) {
		if (aValueList == null) {
			return true;
		}

		for (Object value : aValueList) {
			if (isEmpty(value)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param aValue1
	 *            Left side value
	 * @param aValue2
	 *            Right side value
	 * @return true if values are true mutually exclusive
	 */
	public static boolean xor(boolean aValue1, boolean aValue2) {
		if (aValue1) {
			return !aValue2;
		}

		return aValue2;
	}

	/**
	 * @param <T>
	 * @param value1
	 *            Left side value
	 * @param value2
	 *            Right side value
	 * @return true if objects are equal
	 */
	@SuppressWarnings("unchecked")
	public static <T> boolean compare(T value1, T value2) {
		if ((value1 == null) || (value2 == null)) {
			return false;
		}

		if (value1 instanceof Comparable<?>) {
			return ((Comparable<T>) value1).compareTo(value2) == 0;
		}

		return value1.equals(value2);
	}

	/**
	 * @param <T>
	 * @param aCompareValue
	 *            Left side value
	 * @param aCompareValueList
	 *            List of values to be compared with
	 * @return true if any of the compare values matches the leftValue
	 */
	public static <T> boolean compareAny(T aCompareValue, List<T> aCompareValueList) {
		if (isEmpty(aCompareValue) || isEmpty(aCompareValueList)) {
			return false;
		}

		for (T compareValue : aCompareValueList) {
			if (compare(aCompareValue, compareValue)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param <T>
	 * @param aCompareValue
	 *            Left side value
	 * @param aCompareValueList
	 *            List of values to be compared with
	 * @return true if any of the compare values matches the leftValue
	 */
	@SafeVarargs
	public static <T> boolean compareAny(T aCompareValue, T... aCompareValueList) {
		return compareAny(aCompareValue, CollectionUtil.toList(aCompareValueList));
	}

	/**
	 * @param aError
	 * @return error log
	 */
	public static String getErrorLog(Throwable aError) {
		if (aError == null) {
			return AccelerateConstants.EMPTY_STRING;
		}

		StringWriter writer = new StringWriter();
		aError.printStackTrace(new PrintWriter(writer));
		writer.flush();

		return writer.getBuffer().toString();
	}
}