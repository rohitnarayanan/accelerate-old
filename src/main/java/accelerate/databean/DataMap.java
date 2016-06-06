package accelerate.databean;

import java.util.HashMap;

import org.springframework.util.Assert;

/**
 * PUT DESCRIPTION HERE
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

		return new DataMap().putAllData(aArgs);
	}

	/**
	 * @param aKey
	 * @param aValue
	 * @return
	 */
	public DataMap putData(String aKey, Object aValue) {
		put(aKey, aValue);
		return this;
	}

	/**
	 * @param aArgs
	 * @return
	 */
	public DataMap putAllData(Object... aArgs) {
		for (int idx = 0; idx < aArgs.length; idx += 2) {
			this.put((String) aArgs[idx], aArgs[idx + 1]);
		}

		return this;
	}
}
