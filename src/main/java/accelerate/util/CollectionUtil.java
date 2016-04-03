package accelerate.util;

import static accelerate.util.AppUtil.compare;
import static accelerate.util.AppUtil.isEmpty;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import accelerate.databean.AccelerateDataBean;
import accelerate.exception.AccelerateException;

/**
 * This class provides utility methods for the application
 *
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since Mar 10, 2016
 */
public final class CollectionUtil {

	/**
	 * hidden constructor
	 */
	private CollectionUtil() {
	}

	/**
	 * @param aProperties
	 *            - Properties Object
	 * @param type
	 *            - Class to be used for converting value
	 * @param <T>
	 *            - Type of value
	 * @return Map Object
	 */
	public static <T> Map<String, T> convertToMap(Properties aProperties, Class<T> type) {
		Map<String, T> propMap = new HashMap<>();

		if (isEmpty(aProperties)) {
			return propMap;
		}

		aProperties.forEach((key, value) -> propMap.put(StringUtil.toString(key), type.cast(value)));

		return propMap;
	}

	/**
	 * @param mapInstanceA
	 * @param mapInstanceB
	 * @return result string
	 */
	public static AccelerateDataBean compareMaps(Map<?, ?> mapInstanceA, Map<?, ?> mapInstanceB) {
		AccelerateDataBean extraA = new AccelerateDataBean();
		AccelerateDataBean extraB = new AccelerateDataBean();
		AccelerateDataBean conflict = new AccelerateDataBean();

		mapInstanceA.forEach((key, value) -> {
			if (mapInstanceB.get(key) == null) {
				extraA.put(StringUtil.toString(key), value);
			}
		});

		mapInstanceB.forEach((key, value) -> {
			if (mapInstanceA.get(key) == null) {
				extraB.put(StringUtil.toString(key), value);
			}
		});

		mapInstanceA.forEach((key, value) -> {
			Object valueB = mapInstanceB.get(key);
			if ((valueB != null) && !compare(valueB, value)) {
				conflict.put(StringUtil.toString(key), new Object[] { value, valueB });
			}
		});

		AccelerateDataBean dataMap = new AccelerateDataBean();
		dataMap.addAllAttributes("extraA", extraA, "extraB", extraB, "conflict", conflict);
		return dataMap;
	}

	/**
	 * @param <E>
	 * @param aCollection
	 * @return first element in the collection
	 */
	public static <E> E firstNotNullElement(Collection<E> aCollection) {
		if (AppUtil.isEmpty(aCollection)) {
			return null;
		}

		for (E element : aCollection) {
			if (element == null) {
				continue;
			}

			return element;
		}

		return null;
	}

	/**
	 * @param <E>
	 * @param aCollection
	 * @return array of elements
	 */
	public static <E> List<E> toList(E[] aCollection) {
		if (isEmpty(aCollection)) {
			return new ArrayList<>();
		}

		return Arrays.asList(aCollection);
	}

	/**
	 * @param <E>
	 * @param aCollection
	 * @param aComponentType
	 * @return array of elements
	 */
	public static <E> E[] toArray(Collection<? extends E> aCollection, Class<E> aComponentType) {
		if (isEmpty(aCollection)) {
			return null;
		}

		@SuppressWarnings("unchecked")
		E[] elements = (E[]) Array.newInstance(aComponentType, 0);
		return aCollection.toArray(elements);
	}

	/**
	 * Shortcut method to extract a sublist from the middle.
	 *
	 * @param <E>
	 * @param aElementList
	 * @param aStartIndex
	 * @param aFromEndIndex
	 * @return extracted sub list
	 * @throws AccelerateException
	 */
	public static <E> List<E> extractUpto(List<E> aElementList, int aStartIndex, int aFromEndIndex)
			throws AccelerateException {
		if (isEmpty(aElementList)) {
			throw new AccelerateException("Empty List");
		}

		int length = aElementList.size();
		int start = aStartIndex;
		int end = length - aFromEndIndex;

		if ((aStartIndex < 0) || (aFromEndIndex < 0) || (aStartIndex >= length) || (aFromEndIndex >= length)
				|| (end < start)) {
			throw new AccelerateException("Invalid/Incompatible indexes");
		}

		return aElementList.subList(start, end);
	}

	/**
	 * Shortcut method to extract a sub-array from the middle.
	 *
	 * @param <E>
	 * @param aElementArray
	 * @param aStartIndex
	 * @param aFromEndIndex
	 * @return extracted sub array
	 * @throws AccelerateException
	 */
	public static <E> E[] extractUpto(E[] aElementArray, int aStartIndex, int aFromEndIndex)
			throws AccelerateException {
		List<E> subList = extractUpto(toList(aElementArray), aStartIndex, aFromEndIndex);
		@SuppressWarnings("unchecked")
		Class<E> type = (Class<E>) subList.get(0).getClass();
		return toArray(subList, type);
	}

	/**
	 * @param <E>
	 * @param aList
	 * @return true, if Collection was sorted
	 */
	public static <E> boolean sort(List<E> aList) {
		if (isEmpty(aList)) {
			return false;
		}

		if (!(aList.get(0) instanceof Comparable<?>)) {
			return false;
		}

		@SuppressWarnings("unchecked")
		Comparator<E> comparator = (aElement1, aElement2) -> ((Comparable<E>) aElement1).compareTo(aElement2);
		Collections.sort(aList, comparator);
		return true;
	}

	/**
	 * @param <T>
	 * @param aMap
	 * @param aKey
	 * @return New Count Value
	 */
	public static <T> int incrementCount(Map<T, Integer> aMap, T aKey) {
		return incrementCount(aMap, aKey, 1);
	}

	/**
	 * @param <T>
	 * @param aMap
	 * @param aKey
	 * @param aCount
	 * @return New Count Value
	 */
	public static <T> int incrementCount(Map<T, Integer> aMap, T aKey, Integer aCount) {
		Integer count = aMap.get(aKey);
		if (count == null) {
			count = 0;
		}

		int newCount = count + aCount;
		aMap.put(aKey, newCount);
		return newCount;
	}

	/**
	 * @param <K>
	 * @param <V>
	 * @param aMap
	 * @param aKey
	 * @param aValue
	 * @return {@link List} to which value was added
	 */
	public static <K, V> List<V> addToValueList(Map<K, List<V>> aMap, K aKey, V aValue) {
		List<V> valueList = aMap.get(aKey);
		if (valueList == null) {
			valueList = new ArrayList<>();
			aMap.put(aKey, valueList);
		}

		valueList.add(aValue);
		return valueList;
	}
}