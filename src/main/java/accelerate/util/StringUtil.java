package accelerate.util;

import static accelerate.util.AccelerateConstants.DOT_CHAR;
import static accelerate.util.AccelerateConstants.EMPTY_STRING;
import static accelerate.util.AppUtil.isEmpty;
import static accelerate.util.UtilCache.getPattern;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import accelerate.exception.AccelerateRuntimeException;

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
	 * @param aKeys
	 *            - List of keys
	 * @return dot delimited key string
	 */
	public static String join(CharSequence... aKeys) {
		return join(CollectionUtil.toList(aKeys), DOT_CHAR);
	}

	/**
	 * @param aKeys
	 *            - List of keys
	 * @param aDelim
	 *            - Delimiting character
	 * @return key string delimited by given character
	 */
	public static String join(List<? extends CharSequence> aKeys, CharSequence aDelim) {
		if (isEmpty(aKeys)) {
			return EMPTY_STRING;
		}

		return aKeys.stream().collect(Collectors.joining((aDelim != null) ? aDelim : EMPTY_STRING));
	}

	/**
	 * @param aTargetString
	 * @param aPattern
	 * @param aReplacement
	 * @param aGlobalReplace
	 * @return Modified String
	 */
	public static String replace(String aTargetString, String aPattern, String aReplacement, boolean aGlobalReplace) {
		String newName = aTargetString;
		Matcher patternMatcher = UtilCache.getPattern(aPattern).matcher(aTargetString);

		if (aGlobalReplace) {
			StringBuffer buffer = new StringBuffer();
			while (patternMatcher.find()) {
				patternMatcher.appendReplacement(buffer, aReplacement);
			}

			patternMatcher.appendTail(buffer);
			newName = buffer.toString();
		} else {
			if (patternMatcher.find()) {
				StringBuffer buffer = new StringBuffer();
				patternMatcher.appendReplacement(buffer, aReplacement);
				patternMatcher.appendTail(buffer);
				newName = buffer.toString();
			}
		}

		return newName;
	}

	/**
	 * @param aPattern
	 *            - Pattern to be searched
	 * @param aCompareList
	 *            - Variable number of string inputs to search
	 * @return true if match found
	 */
	public static List<String> grep(CharSequence aPattern, List<String> aCompareList) {
		if (isEmpty(aPattern) || isEmpty((aCompareList))) {
			return Collections.emptyList();
		}

		Matcher matcher = getPattern(aPattern.toString()).matcher(EMPTY_STRING);

		return aCompareList.stream().filter(aValue -> {
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
	public static boolean grepCheck(CharSequence aPattern, List<String> aCompareList) {
		return grep(aPattern, aCompareList).isEmpty();
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
	public static String[] split(CharSequence aSplitLine, CharSequence aDelim) {
		if (isEmpty(aSplitLine)) {
			return new String[] {};
		}

		return getPattern(aDelim.toString()).split(aSplitLine);
	}

	/**
	 * @param aInputString
	 * @param aRecordDelim
	 * @param aFieldDelim
	 * @return array of delimited values
	 */
	public static Map<String, String> multiSplit(CharSequence aInputString, CharSequence aRecordDelim,
			CharSequence aFieldDelim) {
		if (isEmpty(aInputString)) {
			return Collections.emptyMap();
		}

		Map<String, String> tokenMap = new HashMap<>();

		Arrays.stream(StringUtil.split(aInputString, aRecordDelim)).forEach(aLine -> {
			String[] fields = StringUtil.split(aLine, aFieldDelim);
			if (fields.length == 1) {
				tokenMap.put(fields[0], null);
			} else if (fields.length == 2) {
				tokenMap.put(fields[0], fields[1]);
			} else {
				throw new AccelerateRuntimeException("3 or more fields in record %s", aLine);
			}
		});

		return tokenMap;
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
		int length = aValue.length();
		int start = (aStartIndex < 0) ? 0 : aStartIndex;
		int end = (aEndIndex < 0) ? length : aEndIndex;
		String value = aValue.toString();

		if ((start < 0) || (end < 0) || (start >= length) || (end >= length) || (end < start)) {
			throw new AccelerateRuntimeException("Invalid/Incompatible indexes");
		}

		return value.substring(start, end);
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
		if (padLength < 0) {
			throw new AccelerateRuntimeException("Invalid arguments");
		}

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
		boolean flag = true;
		StringBuilder buffer = new StringBuilder();
		for (String token : aInputList) {
			if (flag) {
				buffer.append(Character.toLowerCase(token.charAt(0)));
				flag = false;
			} else {
				buffer.append(Character.toUpperCase(token.charAt(0)));
			}

			buffer.append(token.substring(1));
		}

		return buffer.toString();
	}
}