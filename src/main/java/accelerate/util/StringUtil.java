package accelerate.util;

import static accelerate.util.AccelerateConstants.DOT_CHAR;
import static accelerate.util.AccelerateConstants.EMPTY_STRING;
import static accelerate.util.AppUtil.isEmpty;
import static accelerate.util.CollectionUtil.toList;
import static accelerate.util.UtilCache.getPattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import accelerate.exception.AccelerateException;

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
	public static String createKey(List<? extends CharSequence> aKeys) {
		return createKey(aKeys, DOT_CHAR);
	}

	/**
	 * @param aKeys
	 *            - List of keys
	 * @return dot delimited key string
	 */
	public static String createKey(CharSequence... aKeys) {
		return createKey(CollectionUtil.toList(aKeys), DOT_CHAR);
	}

	/**
	 * @param aKeys
	 *            - List of keys
	 * @param aDelim
	 *            - Delimiting character
	 * @return key string delimited by given character
	 */
	public static String createKey(List<? extends CharSequence> aKeys, CharSequence aDelim) {
		if (isEmpty(aKeys)) {
			return EMPTY_STRING;
		}

		CharSequence delim = (aDelim != null) ? aDelim : EMPTY_STRING;

		StringBuilder keyBuffer = new StringBuilder();
		aKeys.forEach(key -> {
			if (AppUtil.isEmpty(key)) {
				keyBuffer.append(key).append(delim);
			}
		});

		return extractUpto(keyBuffer, 0, delim.length());
	}

	/**
	 * @param aKeys
	 *            - List of keys
	 * @param aDelim
	 *            - Delimiting character
	 * @return key string delimited by given character
	 */
	public static String createKey(CharSequence[] aKeys, CharSequence aDelim) {
		return createKey(CollectionUtil.toList(aKeys), aDelim);
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
	 * @param aPrefix
	 *            - prefix to be searched
	 * @param aSearchList
	 *            - List of string inputs to search
	 * @return true if match found
	 */
	public static boolean prefixesAny(CharSequence aPrefix, List<? extends CharSequence> aSearchList) {
		return searchForPrefixSuffix(aPrefix, false, aSearchList);

	}

	/**
	 * @param aPrefix
	 *            - prefix to be searched
	 * @param aSearchList
	 *            - Variable number of string inputs to search
	 * @return true if match found
	 */
	public static boolean prefixesAny(CharSequence aPrefix, CharSequence... aSearchList) {
		return prefixesAny(aPrefix, toList(aSearchList));
	}

	/**
	 * @param aSuffix
	 *            - prefix to be searched
	 * @param aSearchList
	 *            - List of string inputs to search
	 * @return true if match found
	 */
	public static boolean suffixesAny(CharSequence aSuffix, List<? extends CharSequence> aSearchList) {
		return searchForPrefixSuffix(aSuffix, true, aSearchList);
	}

	/**
	 * @param aSuffix
	 *            - prefix to be searched
	 * @param aSearchList
	 *            - Variable number of string inputs to search
	 * @return true if match found
	 */
	public static boolean suffixesAny(CharSequence aSuffix, CharSequence... aSearchList) {
		return suffixesAny(aSuffix, toList(aSearchList));
	}

	/**
	 * @param aMatch
	 *            - prefix to be searched
	 * @param aSearchList
	 *            - List of string inputs to search
	 * @param aSuffix
	 *            - Flag to indicate suffix is to be matched
	 * @return true if match found
	 */
	public static boolean searchForPrefixSuffix(CharSequence aMatch, boolean aSuffix,
			List<? extends CharSequence> aSearchList) {
		if ((aMatch == null) || (aSearchList == null)) {
			return false;
		}

		String match = aMatch.toString();
		for (CharSequence source : aSearchList) {
			if (source == null) {
				continue;
			}

			if (aSuffix) {
				if (source.toString().endsWith(match)) {
					return true;
				}
			} else {
				if (source.toString().startsWith(match)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * @param aSearchString
	 *            - prefix to be searched
	 * @param aPrefixList
	 *            - List of string inputs to search
	 * @return true if match found
	 */
	public static boolean prefixedByAny(CharSequence aSearchString, List<? extends CharSequence> aPrefixList) {
		return matchForPrefixSuffix(aSearchString, false, aPrefixList);
	}

	/**
	 * @param aSearchString
	 *            - prefix to be searched
	 * @param aPrefixList
	 *            - Variable number of string inputs to search
	 * @return true if match found
	 */
	public static boolean prefixedByAny(CharSequence aSearchString, CharSequence... aPrefixList) {
		return prefixedByAny(aSearchString, toList(aPrefixList));
	}

	/**
	 * @param aSearchString
	 *            - prefix to be searched
	 * @param aSuffixList
	 *            - List of string inputs to search
	 * @return true if match found
	 */
	public static boolean suffixedByAny(CharSequence aSearchString, List<? extends CharSequence> aSuffixList) {
		return matchForPrefixSuffix(aSearchString, true, aSuffixList);
	}

	/**
	 * @param aSearchString
	 *            - prefix to be searched
	 * @param aSuffixList
	 *            - Variable number of string inputs to search
	 * @return true if match found
	 */
	public static boolean suffixedByAny(CharSequence aSearchString, CharSequence... aSuffixList) {
		return suffixedByAny(aSearchString, toList(aSuffixList));
	}

	/**
	 * @param aSearchPattern
	 *            - pattern to be searched
	 * @param aMatchList
	 *            - List of string inputs to search
	 * @param aSuffix
	 *            - Flag to indicate suffix is to be matched
	 * @return true if match found
	 */
	public static boolean matchForPrefixSuffix(CharSequence aSearchPattern, boolean aSuffix,
			List<? extends CharSequence> aMatchList) {
		if (isEmpty(aSearchPattern) || isEmpty(aMatchList)) {
			return false;
		}

		String searchString = aSearchPattern.toString();
		for (CharSequence match : aMatchList) {
			if (match == null) {
				continue;
			}

			String matchString = match.toString();
			if (aSuffix) {
				if (searchString.endsWith(matchString)) {
					return true;
				}
			} else {
				if (searchString.startsWith(matchString)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * @param aPattern
	 *            - Pattern to be searched
	 * @param aCompareList
	 *            - List of string inputs to search
	 * @return true if match found
	 */
	public static boolean matchesAny(CharSequence aPattern, CharSequence... aCompareList) {
		return matchesAny(aPattern, toList(aCompareList));
	}

	/**
	 * @param aPattern
	 *            - Pattern to be searched
	 * @param aCompareList
	 *            - Variable number of string inputs to search
	 * @return true if match found
	 */
	public static boolean matchesAny(CharSequence aPattern, List<? extends CharSequence> aCompareList) {
		if ((aPattern == null) || isEmpty((aCompareList))) {
			return false;
		}

		Matcher matcher = getPattern(aPattern.toString()).matcher(EMPTY_STRING);
		for (CharSequence compareValue : aCompareList) {
			if (compareValue == null) {
				continue;
			}

			matcher.reset(compareValue);
			if (matcher.find()) {
				return true;
			}
		}

		return false;
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
	 * @param aSplitLine
	 * @param aRecordDelim
	 * @param aFieldDelim
	 * @return array of delimited values
	 */
	public static Map<String, String> multiSplit(CharSequence aSplitLine, CharSequence aRecordDelim,
			CharSequence aFieldDelim) {
		Map<String, String> tokenMap = new HashMap<>();

		if (isEmpty(aSplitLine)) {
			return tokenMap;
		}

		String[] records = getPattern(aRecordDelim.toString()).split(aSplitLine);
		for (String record : records) {
			String[] fields = getPattern(aFieldDelim.toString()).split(record);
			if (fields.length == 1) {
				tokenMap.put(fields[0], null);
			} else if (fields.length == 2) {
				tokenMap.put(fields[0], fields[1]);
			} else {
				tokenMap.put(JSONUtil.serialize(fields), null);
			}
		}

		return tokenMap;
	}

	/**
	 * This is a convenience method which breaks down the given input string
	 * separated by the given delimiter and creates a {@link List} of
	 * {@link Integer} values.
	 *
	 * @param aInputString
	 *            input string following the format '{value1}{aDelim}{value2}'
	 * @param aDelim
	 *            the delimiter separating the long values
	 * @return {@link List} of {@link Integer} values
	 */
	public static List<Integer> convertToList(String aInputString, String aDelim) {
		List<Integer> valueList = new ArrayList<>();

		Arrays.stream(StringUtil.split(aInputString, aDelim)).forEach(v -> valueList.add(Integer.valueOf(v)));

		return valueList;
	}

	/**
	 * This is a convenience method which breaks down the given input string
	 * separated by the given delimiter and creates a {@link List} of
	 * {@link Long} values.
	 *
	 * @param aInputString
	 *            input string following the format '{value1}{aDelim}{value2}'
	 * @param aDelim
	 *            the delimiter separating the long values
	 * @return {@link List} of {@link Long} values
	 */
	public static List<Long> convertToLongList(String aInputString, String aDelim) {
		List<Long> valueList = new ArrayList<>();
		Arrays.stream(StringUtil.split(aInputString, aDelim)).forEach(v -> valueList.add(Long.valueOf(v)));

		return valueList;
	}

	/**
	 * This is a convenience method which breaks down the given input string
	 * separated by the given delimiter and creates a {@link List} of
	 * {@link Double} values.
	 *
	 * @param aInputString
	 *            input string following the format '{value1}{aDelim}{value2}'
	 * @param aDelim
	 *            the delimiter separating the long values
	 * @return {@link List} of {@link Double} values
	 */
	public static List<Double> convertToDoubleList(String aInputString, String aDelim) {
		List<Double> valueList = new ArrayList<>();
		Arrays.stream(StringUtil.split(aInputString, aDelim)).forEach(v -> valueList.add(Double.valueOf(v)));

		return valueList;
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

		return extractNoCheck(aValue, aStartIndex, aEndIndex);
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

		return extractNoCheck(aValue, aStartIndex, end);
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

		return extractNoCheck(aValue, start, length);
	}

	/**
	 * @param aValue
	 * @param aStartIndex
	 * @param aEndIndex
	 * @return Clipped String
	 */
	private static String extractNoCheck(CharSequence aValue, int aStartIndex, int aEndIndex) {
		int length = aValue.length();
		int start = 0;
		String value = aValue.toString();

		if ((aStartIndex >= length) || ((aStartIndex >= aEndIndex) && (aEndIndex >= 0))) {
			return EMPTY_STRING;
		} else if (aStartIndex >= 0) {
			start = aStartIndex;
		}

		if ((aEndIndex < 0) || (aEndIndex > length)) {
			return value.substring(start);
		}

		return value.substring(start, aEndIndex);
	}

	/**
	 * This method splits the given string backwards by the provided length
	 *
	 * @param aInput
	 * @param aLength
	 * @return split values
	 */
	public static String[] splitFromEnd(CharSequence aInput, int aLength) {
		if (aInput == null) {
			return null;
		}

		String[] splitValues = new String[2];
		int length = aInput.length();

		splitValues[0] = extract(aInput, 0, length - aLength);
		splitValues[1] = extractFromEnd(aInput, aLength);

		return splitValues;
	}

	/**
	 * This method returns a string having desired length prepended with the
	 * given character
	 *
	 * @param aInputString
	 *            string to format
	 * @param aLength
	 *            length of the string to return
	 * @param aPadChar
	 *            character to use to pad the string
	 * @param aPadFlag
	 *            Flag to indicate pad direction. true for left pad and false
	 *            for right pad
	 * @return Formatted String
	 * @throws AccelerateException
	 */
	public static String padString(String aInputString, int aLength, Character aPadChar, boolean aPadFlag)
			throws AccelerateException {
		StringBuilder buffer = new StringBuilder();

		int padLength = isEmpty(aInputString) ? aLength : aInputString.length() - aLength;
		if (padLength < 0) {
			throw new AccelerateException("Invalid arguments");
		}

		if (!aPadFlag) {
			buffer.append(aInputString);
		}

		for (int count = 0; count < padLength; count++) {
			buffer.append(aPadChar);
		}

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
	public static String toCamelCase(String... aInputList) {
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

	/**
	 * This method searches for the pattern within the given String based on
	 * Boyer-Moore algorithm
	 *
	 * @param aSearchPattern
	 *            Pattern to be looked up
	 * @param aTargetString
	 *            Text to be searched
	 * @return {@link List} of Indexes where the pattern was found
	 */
	public static List<Integer> search(String aSearchPattern, String aTargetString) {
		List<Integer> matches = new ArrayList<>();
		int m = aTargetString.length();
		int n = aSearchPattern.length();

		Map<Character, Integer> rightMostIndexes = new HashMap<>();
		for (int i = aSearchPattern.length() - 1; i >= 0; i--) {
			char c = aSearchPattern.charAt(i);
			if (!rightMostIndexes.containsKey(c)) {
				rightMostIndexes.put(c, i);
			}
		}

		int alignedAt = 0;
		while ((alignedAt + (n - 1)) < m) {
			for (int indexInPattern = n - 1; indexInPattern >= 0; indexInPattern--) {
				int indexInText = alignedAt + indexInPattern;
				char x = aTargetString.charAt(indexInText);
				char y = aSearchPattern.charAt(indexInPattern);

				if (indexInText >= m) {
					break;
				}

				if (x != y) {

					Integer r = rightMostIndexes.get(x);

					if (r == null) {
						alignedAt = indexInText + 1;
					} else {
						int shift = indexInText - (alignedAt + r);
						alignedAt += shift > 0 ? shift : 1;
					}

					break;
				} else if (indexInPattern == 0) {
					matches.add(alignedAt);
					alignedAt++;
				}

			}
		}
		return matches;
	}
}