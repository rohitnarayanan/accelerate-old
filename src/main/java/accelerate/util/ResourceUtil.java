package accelerate.util;

import static accelerate.util.AccelerateConstants.POUND_CHAR;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;

import accelerate.exception.AccelerateException;

/**
 * This class provides utility methods to handle resource operations
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 14-May-2015
 */
public final class ResourceUtil {

	/**
	 * hidden constructor
	 */
	private ResourceUtil() {
	}

	/**
	 * @param aApplicationContext
	 * @param aConfigPath
	 *            URL/Path to the config file.
	 * @return Property Map
	 * @throws AccelerateException
	 */
	public static Document loadXMLDOM(ApplicationContext aApplicationContext, String aConfigPath)
			throws AccelerateException {
		try {
			return loadXMLDOM(aApplicationContext.getResource(aConfigPath).getInputStream());
		} catch (IOException error) {
			throw new AccelerateException(error);
		}
	}

	/**
	 * @param aInputStream
	 * @return {@link Document}
	 * @throws AccelerateException
	 */
	public static Document loadXMLDOM(InputStream aInputStream) throws AccelerateException {
		try {
			return UtilCache.getDocumentBuilder().parse(aInputStream);
		} catch (Exception error) {
			throw new AccelerateException(error);
		}
	}

	/**
	 * @param aApplicationContext
	 * @param aConfigPath
	 *            URL/Path to the config file.
	 * @return Property Map
	 * @throws AccelerateException
	 */
	public static Properties LoadProperties(ApplicationContext aApplicationContext, String aConfigPath)
			throws AccelerateException {
		try {
			return LoadProperties(aApplicationContext.getResource(aConfigPath).getInputStream());
		} catch (IOException error) {
			throw new AccelerateException(error);
		}
	}

	/**
	 * @param aInputStream
	 *            {@link InputStream} instance to be loaded
	 * @return {@link Properties} instance
	 * @throws AccelerateException
	 */
	public static Properties LoadProperties(InputStream aInputStream) throws AccelerateException {
		try {
			Properties properties = new Properties();
			properties.load(aInputStream);
			return properties;
		} catch (IOException error) {
			throw new AccelerateException(error);
		} finally {
			IOUtils.closeQuietly(aInputStream);
		}
	}

	/**
	 * @param aApplicationContext
	 * @param aConfigPath
	 *            URL/Path to the config file.
	 * @return Property Map
	 * @throws AccelerateException
	 */
	public static Map<String, String> LoadPropertyMap(ApplicationContext aApplicationContext, String aConfigPath)
			throws AccelerateException {
		return LoadPropertyMap(LoadProperties(aApplicationContext, aConfigPath));
	}

	/**
	 * @param aInputStream
	 *            {@link InputStream} instance to be loaded
	 * @return {@link Properties} instance
	 * @throws AccelerateException
	 */
	public static Map<String, String> LoadPropertyMap(InputStream aInputStream) throws AccelerateException {
		return LoadPropertyMap(LoadProperties(aInputStream));
	}

	/**
	 * @param aProperties
	 *            {@link Properties} instance to be loaded
	 * @return {@link Properties} instance
	 */
	public static Map<String, String> LoadPropertyMap(Properties aProperties) {
		Map<String, String> propertyMap = new HashMap<>();
		for (Entry<Object, Object> entry : aProperties.entrySet()) {
			propertyMap.put(StringUtil.toString(entry.getKey()), StringUtil.toString(entry.getValue()));
		}

		return propertyMap;
	}

	/**
	 * This method gets the {@link InputStream} from the given config path and
	 * passes it to LoadCustomPropertyMap(InputStream, aDelimiter) method.
	 * 
	 * @param aApplicationContext
	 *
	 * @param aConfigPath
	 *            URL/Path to the config file
	 * @param aDelimiter
	 *            delimiter for key value pairs
	 * @return Property {@link Map}
	 * @throws AccelerateException
	 */
	public static Map<String, String> LoadCustomPropertyMap(ApplicationContext aApplicationContext, String aConfigPath,
			String aDelimiter) throws AccelerateException {
		try {
			return LoadCustomPropertyMap(aApplicationContext.getResource(aConfigPath).getInputStream(), aDelimiter);
		} catch (Exception error) {
			throw new AccelerateException(error);
		}
	}

	/**
	 * This method helps overcome the limitation of java {@link Properties}
	 * class, by parsing a property file with special characters that will break
	 * the process otherwise.
	 *
	 * @param aInputStream
	 *            {@link InputStream} instance to be loaded
	 * @param aDelimiter
	 *            delimiter for key value pairs
	 * @return PropertyMap
	 * @throws AccelerateException
	 */
	public static Map<String, String> LoadCustomPropertyMap(InputStream aInputStream, String aDelimiter)
			throws AccelerateException {
		Map<String, String> propertyMap = new HashMap<>();
		BufferedReader reader = null;
		String inputLine = null;

		try {
			reader = new BufferedReader(new InputStreamReader(aInputStream));

			while ((inputLine = reader.readLine()) != null) {
				int index = inputLine.indexOf(aDelimiter);
				if (index < 0) {
					propertyMap.put(inputLine, POUND_CHAR);
				} else {
					propertyMap.put(StringUtil.extract(inputLine, 0, index++),
							StringUtil.extract(inputLine, index, -1));
				}
			}
		} catch (Exception error) {
			throw new AccelerateException(error);
		} finally {
			IOUtils.closeQuietly(reader);
		}

		return propertyMap;
	}
}