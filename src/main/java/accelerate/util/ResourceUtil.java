package accelerate.util;

import static accelerate.util.AccelerateConstants.EMPTY_STRING;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
	 * {@link DocumentBuilderFactory} singleton instance for SAX parsing
	 */
	private static DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

	/**
	 * {@link DocumentBuilder} singleton instance for SAX parsing
	 */
	private static DocumentBuilder builder = null;

	/**
	 * hidden constructor
	 */
	private ResourceUtil() {
	}

	/**
	 * @return {@link DocumentBuilder} instance
	 * @throws ParserConfigurationException
	 */
	public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		if (builder == null) {
			synchronized (domFactory) {
				if (builder == null) {
					builder = domFactory.newDocumentBuilder();
				}
			}
		}

		return builder;
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
			return getDocumentBuilder().parse(aInputStream);
		} catch (SAXException | IOException | ParserConfigurationException error) {
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
			propertyMap.put(Objects.toString(entry.getKey(), EMPTY_STRING),
					Objects.toString(entry.getValue(), EMPTY_STRING));
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
		} catch (IOException error) {
			throw new AccelerateException(error);
		}
	}

	/**
	 * This method helps overcome the limitation of java {@link Properties}
	 * class, by parsing a property file with special characters that will break
	 * the process otherwise. It assumes the delimiter appears only once per
	 * line and maps the key value to tokens 0 and 1. Any additional tokens
	 * created due to multiple declarations of the delimiter on the same line
	 * will be lost.
	 * 
	 * <p>
	 * Assuming the delimiter is '#', here are a few examples:
	 * </p>
	 * 
	 * <pre>
	 * a#b will be mapped to a=b
	 * </pre>
	 * 
	 * <pre>
	 * a will be mapped to a=''
	 * </pre>
	 * 
	 * <pre>
	 * a#b#c will be mapped to a=b
	 * </pre>
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
				String[] tokens = StringUtils.split(inputLine, aDelimiter);
				if (tokens.length == 1) {
					propertyMap.put(tokens[0], AccelerateConstants.EMPTY_STRING);
				} else {
					propertyMap.put(tokens[0], tokens[1]);
				}
			}
		} catch (IOException error) {
			throw new AccelerateException(error);
		} finally {
			IOUtils.closeQuietly(reader);
		}

		return propertyMap;
	}
}