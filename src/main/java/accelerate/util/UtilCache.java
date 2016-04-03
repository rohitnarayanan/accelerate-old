package accelerate.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import accelerate.exception.AccelerateException;

/**
 * This class provides utility methods for the application
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since June 12, 2009
 */
public final class UtilCache {
	/**
	 * This map stores the compiled pattern for each type of delimiter
	 */
	private static Map<String, Pattern> patternMap = new HashMap<>();

	/**
	 * {@link DocumentBuilderFactory} singleton instance for SAX parsing
	 */
	private static DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

	/**
	 * {@link DocumentBuilder} singleton instance for SAX parsing
	 */
	private static DocumentBuilder builder = null;

	/**
	 * {@link XPathFactory} singleton instance for SAX parsing
	 */
	private static XPathFactory xPathFactory = XPathFactory.newInstance();

	/**
	 * {@link XPath} singleton instance for SAX parsing
	 */
	private static XPath xPath = xPathFactory.newXPath();

	/**
	 * hidden constructor
	 */
	private UtilCache() {
	}

	/**
	 * This method clears out all the {@link Pattern} instances cached
	 */
	public static void clearPatternCache() {
		patternMap = new HashMap<>();
	}

	/**
	 * @param aPaternString
	 * @return {@link Pattern} instance
	 */
	public static Pattern getPattern(String aPaternString) {
		Pattern pattern = patternMap.get(aPaternString);
		if (pattern == null) {
			pattern = Pattern.compile(aPaternString);
			patternMap.put(aPaternString, pattern);
		}

		return pattern;
	}

	/**
	 * @return {@link DocumentBuilder} instance
	 * @throws AccelerateException
	 */
	public static DocumentBuilder getDocumentBuilder() throws AccelerateException {
		if (builder == null) {
			synchronized (domFactory) {
				if (builder == null) {
					try {
						builder = domFactory.newDocumentBuilder();
					} catch (ParserConfigurationException error) {
						throw new AccelerateException(error);
					}
				}
			}
		}

		return builder;
	}

	/**
	 * @return {@link XPath} instance
	 */
	public static XPath getXPath() {
		return xPath;
	}
}