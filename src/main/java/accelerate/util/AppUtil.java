package accelerate.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import accelerate.exception.AccelerateException;

/**
 * This class provides utility methods for the application
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jun 12, 2009
 */
public final class AppUtil {
	/**
	 * 
	 */
	static final Logger LOGGER = LoggerFactory.getLogger(AppUtil.class);

	/**
	 * hidden constructor
	 */
	private AppUtil() {
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
			if (ObjectUtils.isEmpty(value)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param aValueList
	 * @return
	 */
	public static boolean isEmptyAll(Object... aValueList) {
		if (aValueList == null) {
			return true;
		}

		for (Object value : aValueList) {
			if (!ObjectUtils.isEmpty(value)) {
				return false;
			}
		}

		return true;
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
		if (ObjectUtils.isEmpty(aCompareValue) || ObjectUtils.isEmpty(aCompareValueList)) {
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
	public static String getErrorMessage(Throwable aError) {
		if (aError == null) {
			return AccelerateConstants.EMPTY_STRING;
		}

		String message = aError.getMessage();
		return StringUtils.isEmpty(message) ? aError.getClass().getName() : message;
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

	/**
	 * @param aCommand
	 * @param aEnvSettings
	 * @param aExecuteDir
	 * @return Command Output
	 */
	public static String executeOSCommand(String aCommand, String[] aEnvSettings, File aExecuteDir) {
		Assert.state(StringUtils.isNotEmpty(aCommand), "Invalid Input. Command cannot be empty");
		LOGGER.debug("OSCommand [{}]", aCommand);

		BufferedReader reader = null;
		String outputLine = null;
		StringBuilder outputBuffer = new StringBuilder();

		try {
			Process process = Runtime.getRuntime().exec(aCommand, aEnvSettings,
					(aExecuteDir != null) ? aExecuteDir : SystemUtils.getJavaIoTmpDir());
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			while ((outputLine = reader.readLine()) != null) {
				outputBuffer.append(outputLine);
			}

			LOGGER.debug("OSCommand Output =>\n{}", outputBuffer);
		} catch (IOException error) {
			throw new AccelerateException(error);
		} finally {
			IOUtils.closeQuietly(reader);
		}

		return outputBuffer.toString();
	}
}