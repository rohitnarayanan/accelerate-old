package accelerate.util;

import static accelerate.util.AccelerateConstants.DOT_CHAR;
import static accelerate.util.AccelerateConstants.EMPTY_STRING;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * This class provides utility methods for the application
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jun 12, 2009
 */
public final class StringUtil {

	/**
	 * hidden constructor
	 */
	private StringUtil() {
	}

	/**
	 * This function safely invokes the toString method on the Object. It return
	 * an empty string for null value
	 *
	 * @param aObject
	 *            - Object to be converted to String
	 * @return {@link Object#toString()}
	 */
	public static String toString(Object aObject) {
		return toString(aObject, EMPTY_STRING);
	}

	/**
	 * This function safely invokes the toString method on the Object. It return
	 * an empty string for null value
	 *
	 * @param aObject
	 *            - Object to be converted to String
	 * @param aDefaultValue
	 *            - Default Value to be returned
	 * @return {@link Object#toString()}
	 */
	public static String toString(Object aObject, String aDefaultValue) {
		if (aObject != null) {
			String objectString = aObject.toString();
			if (objectString != null) {
				return objectString;
			}
		}

		return aDefaultValue;
	}

	/**
	 * @param aString
	 * @return toString()
	 */
	public static String trim(CharSequence aString) {
		if (aString != null) {
			return aString.toString().trim();
		}

		return EMPTY_STRING;
	}

	/**
	 * @param aString
	 * @return toString()
	 */
	public static String lower(CharSequence aString) {
		if (aString != null) {
			return aString.toString().toLowerCase();
		}

		return EMPTY_STRING;
	}

	/**
	 * @param aString
	 * @return toString()
	 */
	public static String upper(CharSequence aString) {
		if (aString != null) {
			return aString.toString().toUpperCase();
		}

		return EMPTY_STRING;
	}

	/**
	 * @param aTokens
	 *            - List of keys
	 * @return dot delimited key string
	 */
	public static String join(CharSequence... aTokens) {
		Assert.notEmpty(aTokens, "No tokens provided");
		return join(CollectionUtil.toList(aTokens), DOT_CHAR);
	}

	/**
	 * @param aTokens
	 *            - List of keys
	 * @param aDelim
	 *            - Delimiting character
	 * @return key string delimited by given character
	 */
	public static String join(List<? extends CharSequence> aTokens, CharSequence aDelim) {
		Assert.notEmpty(aTokens, "No tokens provided");
		Assert.notNull(aDelim, "No delimiter provided");

		return aTokens.stream().collect(Collectors.joining(aDelim));
	}

	/**
	 * @param aTargetString
	 * @param aPattern
	 * @param aReplacement
	 * @param aGlobalReplace
	 * @return Modified String
	 */
	public static String replace(CharSequence aTargetString, String aPattern, String aReplacement,
			boolean aGlobalReplace) {
		Matcher patternMatcher = Pattern.compile(aPattern).matcher(aTargetString);

		if (aGlobalReplace) {
			StringBuffer buffer = new StringBuffer();
			while (patternMatcher.find()) {
				patternMatcher.appendReplacement(buffer, aReplacement);
			}

			patternMatcher.appendTail(buffer);
			return buffer.toString();
		}

		if (patternMatcher.find()) {
			StringBuffer buffer = new StringBuffer();
			patternMatcher.appendReplacement(buffer, aReplacement);
			patternMatcher.appendTail(buffer);
			return buffer.toString();
		}

		return aTargetString.toString();
	}

	/**
	 * @param aPattern
	 *            - Pattern to be searched
	 * @param aCompareList
	 *            - Variable number of string inputs to search
	 * @return true if match found
	 */
	public static List<String> grep(String aPattern, String... aCompareList) {
		if (isEmpty(aPattern) || isEmpty((aCompareList))) {
			return Collections.emptyList();
		}

		Matcher matcher = Pattern.compile(aPattern.toString()).matcher(EMPTY_STRING);

		return Arrays.stream(aCompareList).filter(aValue -> {
			matcher.reset(aValue);
			return matcher.find();
		}).collect(Collectors.toList());
	}

	/**
	 * @param aPattern
	 *            - Pattern to be searched
	 * @param aCompareList
	 *            - Variable number of string inputs to search
	 * @return true if match found
	 */
	public static boolean grepCheck(String aPattern, String... aCompareList) {
		return !grep(aPattern, aCompareList).isEmpty();
	}

	/**
	 * @param aTargetString
	 *            - Target string to search
	 * @param aPatternList
	 *            - List of patterns to be searched
	 * @return true if match found
	 */
	public static boolean search(CharSequence aTargetString, List<String> aPatternList) {
		if (isEmpty(aTargetString) || isEmpty((aPatternList))) {
			return false;
		}

		return aPatternList.stream().filter(aPattern -> {
			Matcher matcher = Pattern.compile(aPattern.toString()).matcher(aTargetString);
			return matcher.find();
		}).collect(Collectors.toList()).isEmpty();
	}

	/**
	 * @param aSplitLine
	 * @param aDelim
	 * @return array of delimited values
	 */
	public static List<String> split(CharSequence aSplitLine, String aDelim) {
		if (isEmpty(aSplitLine)) {
			return Collections.emptyList();
		}

		return Arrays.stream(StringUtils.delimitedListToStringArray(aSplitLine.toString(), aDelim))
				.filter(token -> StringUtils.hasLength(token)).collect(Collectors.toList());
	}

	/**
	 * @param aInputString
	 * @param aRecordDelim
	 * @param aFieldDelim
	 * @return array of delimited values
	 */
	public static Map<String, String> multiSplit(CharSequence aInputString, String aRecordDelim, String aFieldDelim) {
		if (isEmpty(aInputString)) {
			return Collections.emptyMap();
		}

		return StringUtil.split(aInputString, aRecordDelim).stream().map(aLine -> StringUtil.split(aLine, aFieldDelim))
				.collect(Collectors.toMap(aValues -> aValues.get(0),
						aValues -> (aValues.size() == 1) ? aValues.get(0) : aValues.get(1)));
	}

	/**
	 * @param aValue
	 * @param aStartIndex
	 * @param aEndIndex
	 * @return Clipped String
	 */
	public static String extract(CharSequence aValue, int aStartIndex, int aEndIndex) {
		if (isEmpty(aValue)) {
			return EMPTY_STRING;
		}

		return _extract(aValue, aStartIndex, aEndIndex);
	}

	/**
	 * @param aValue
	 * @param aStartIndex
	 * @param aFromEndIndex
	 * @return Clipped String
	 */
	public static String extractUpto(CharSequence aValue, int aStartIndex, int aFromEndIndex) {
		if (isEmpty(aValue)) {
			return EMPTY_STRING;
		}

		int length = aValue.length();
		int end = length - aFromEndIndex;

		return _extract(aValue, aStartIndex, end);
	}

	/**
	 * @param aValue
	 * @param aFromEndIndex
	 * @return Clipped String
	 */
	public static String extractFromEnd(CharSequence aValue, int aFromEndIndex) {
		if (isEmpty(aValue)) {
			return EMPTY_STRING;
		}

		int length = aValue.length();
		int start = length - aFromEndIndex;

		return _extract(aValue, start, length);
	}

	/**
	 * @param aValue
	 * @param aStartIndex
	 * @param aEndIndex
	 * @return Clipped String
	 */
	private static String _extract(CharSequence aValue, int aStartIndex, int aEndIndex) {
		Assert.notNull(aValue, "String value is required");

		int length = aValue.length();
		int start = (aStartIndex < 0) ? 0 : aStartIndex;
		int end = (aEndIndex < 0) ? length : aEndIndex;

		Assert.isTrue((start >= 0), "start index cannot be less than 0");
		Assert.isTrue((end >= 0), "end index cannot be less than 0");
		Assert.isTrue((start < length), "start index cannot be beyond the string length");
		Assert.isTrue((end <= length), "end index cannot be beyond the string length");
		Assert.isTrue((end > length), "end index cannot be before start index");

		return aValue.subSequence(start, end).toString();
	}

	/**
	 * This method returns a string having desired length prepended with the
	 * given character
	 *
	 * @param aInputString
	 *            string to format
	 * @param aTargetLength
	 *            length of the string to return
	 * @param aPadChar
	 *            character to use to pad the string
	 * @param aPadFlag
	 *            Flag to indicate pad direction. true for left pad and false
	 *            for right pad
	 * @return Formatted String
	 */
	public static String padString(String aInputString, int aTargetLength, Character aPadChar, boolean aPadFlag) {
		StringBuilder buffer = new StringBuilder();
		int padLength = isEmpty(aInputString) ? aTargetLength : aInputString.length() - aTargetLength;
		Assert.isTrue((padLength > 0), "Invalid target length");

		if (!aPadFlag) {
			buffer.append(aInputString);
		}

		IntStream.range(0, padLength).forEach(value -> buffer.append(aPadChar));

		if (aPadFlag) {
			buffer.append(aInputString);
		}

		return buffer.toString();
	}

	/**
	 * This method returns string after converting it to CamelCase
	 *
	 * @param aInputList
	 * @return
	 */
	public static String camelCase(String... aInputList) {
		final Boolean[] flag = new Boolean[] { true };

		return Arrays.stream(aInputList).map(token -> {
			Character firstChar = null;
			if (flag[0]) {
				firstChar = Character.toLowerCase(token.charAt(0));
				flag[0] = false;
			} else {
				firstChar = Character.toUpperCase(token.charAt(0));
			}
			return firstChar + token.substring(1);
		}).collect(Collectors.joining());
	}
}