package accelerate.util;

import java.util.HashMap;

import accelerate.exception.AccelerateRuntimeException;

/**
 * Utility class to create data maps with different data type values PUT
 * DESCRIPTION HERE
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @param <T>
 * @since Mar 24, 2016
 */
public final class DataMap<T> extends HashMap<String, T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Method to add data to the map
	 * 
	 * @param aKey
	 * @param aValue
	 * @return instance of {@link DataMap} to chain calls
	 */
	public final DataMap<T> addData(String aKey, T aValue) {
		super.put(aKey, aValue);
		return this;
	}

	/**
	 * Shortcut method to add multiple key value pairs to the map. Though it
	 * accepts an Object array by definition, but it expects the arguments to be
	 * in the order of key1, value1, key2, value2.. and so on.
	 * 
	 * @param aArgs
	 *            Variable number of key value pairs
	 * @return instance of {@link DataMap} to chain calls
	 * @throws AccelerateRuntimeException
	 *             If key value pairs do not match, or keys are not of type
	 *             {@link String}
	 */
	@SafeVarargs
	public final DataMap<T> addData(T... aArgs) throws AccelerateRuntimeException {
		if (AppUtil.isEmpty(aArgs)) {
			throw new AccelerateRuntimeException("Empty arguments are not allowed");
		}

		if ((aArgs.length % 2) != 0) {
			throw new AccelerateRuntimeException(
					"Incorrect number of arguments or array size not correct:" + aArgs.length);
		}

		for (int idx = 0; idx < aArgs.length; idx += 2) {
			if (aArgs[idx] instanceof String) {
				put((String) aArgs[idx], aArgs[idx + 1]);
			} else {
				throw new AccelerateRuntimeException("Invalid key %s at position %d", aArgs[idx], idx);
			}
		}

		return this;
	}
}
