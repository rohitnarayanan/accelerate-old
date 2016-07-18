package accelerate.databean;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import accelerate.exception.AccelerateException;
import accelerate.util.AccelerateConstants;
import accelerate.util.JSONUtil;

/**
 * {@link HashMap} extension with overloaded methods for easy loading, method
 * chaining and type-casted getters
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since 06-Jun-2016
 */
public class DataMap extends HashMap<String, Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param aArgs
	 * @return
	 */
	public static DataMap buildMap(Object... aArgs) {
		Assert.notNull(aArgs, "Arguments are required");
		Assert.isTrue(((aArgs.length % 2) == 0), "Incorrect number of arguments");

		return new DataMap().putAllAnd(aArgs);
	}

	/**
	 * @param aKey
	 * @param aValue
	 * @return
	 */
	public DataMap putAnd(String aKey, Object aValue) {
		put(aKey, aValue);
		return this;
	}

	/**
	 * @param aArgs
	 * @return
	 */
	public DataMap putAllAnd(Object... aArgs) {
		for (int idx = 0; idx < aArgs.length; idx += 2) {
			this.put((String) aArgs[idx], aArgs[idx + 1]);
		}

		return this;
	}

	/**
	 * @param <T>
	 * @param aKey
	 * @return
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String aKey) {
		return (T) super.get(aKey);
	}

	/**
	 * @param aKey
	 * @return
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	public String getString(String aKey) {
		return StringUtils.defaultString((String) get(aKey), AccelerateConstants.EMPTY_STRING);
	}

	/**
	 * @param aKey
	 * @return
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	public Integer getInt(String aKey) {
		Object value = get(aKey);
		if (value == null) {
			return null;
		} else if (value instanceof Integer) {
			return (Integer) value;
		}

		return Integer.parseInt(value.toString());
	}

	/**
	 * @param aKey
	 * @return
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	public boolean is(String aKey) {
		Object value = get(aKey);
		if (value == null) {
			return false;
		} else if (value instanceof Boolean) {
			return (Boolean) value;
		}

		return Boolean.parseBoolean(value.toString());
	}

	/**
	 * @param <T>
	 * @param aKey
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T remove(String aKey) {
		return (T) super.remove(aKey);
	}

	/**
	 * This methods returns a JSON representation of this Map
	 * 
	 * @return
	 * @throws AccelerateException
	 *             thrown due to {@link JSONUtil#serialize(Object)}
	 */
	public String toJSON() throws AccelerateException {
		return JSONUtil.serialize(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	/**
	 * @return
	 * @throws AccelerateException
	 *             thrown due to {@link #toJSON()}
	 */
	@Override
	public String toString() throws AccelerateException {
		return toJSON();
	}
}
