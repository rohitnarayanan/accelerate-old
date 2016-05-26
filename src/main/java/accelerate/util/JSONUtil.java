package accelerate.util;

import static accelerate.util.AccelerateConstants.EMPTY_STRING;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * Utility class for converting object to/from JSON string
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 25-May-2015
 */
public final class JSONUtil {
	/**
	 * 
	 */
	private static final Logger _logger = LoggerFactory.getLogger(JSONUtil.class);

	/**
	 * hidden constructor
	 */
	private JSONUtil() {
	}

	/**
	 * This method returns the default instance of {@link ObjectMapper}
	 *
	 * @return
	 */
	public static ObjectMapper objectMapper() {
		return objectMapper(Include.NON_NULL, false, true, true);
	}

	/**
	 * This method returns an instance of {@link ObjectMapper} based on the
	 * given flags.
	 *
	 * @param aInclude
	 *            {@link Boolean} value to indicated whether the JSON string
	 *            should be contain fields with empty/null values
	 * @param aIndent
	 *            {@link Boolean} value to indicated whether the JSON string
	 *            should be indented or not
	 * @param aQuoteFieldNames
	 *            {@link Boolean} value to indicated whether the field names in
	 *            the JSON string should be quoted(") or not
	 * @param aEscapeNonAscii
	 *            {@link Boolean} value to indicated whether the Non ASCII
	 *            characters should be escaped in the JSON string or not
	 * @return configured {@link ObjectMapper} instance
	 */
	public static ObjectMapper objectMapper(Include aInclude, boolean aIndent, boolean aQuoteFieldNames,
			boolean aEscapeNonAscii) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mapper.setSerializationInclusion(aInclude);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, aIndent);
		mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, aQuoteFieldNames);
		mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, aEscapeNonAscii);
		mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);

		SimpleFilterProvider filterProvider = new SimpleFilterProvider();
		filterProvider.setDefaultFilter(SimpleBeanPropertyFilter.serializeAllExcept());
		mapper.setConfig(mapper.getSerializationConfig().withFilters(filterProvider));

		return mapper;
	}

	/**
	 * This is the default method to convert the given object to JSON string
	 *
	 * @param aObject
	 *            Object to be converted to JSON string
	 * @return JSON string
	 */
	public static String serialize(Object aObject) {
		if (isEmpty(aObject)) {
			return EMPTY_STRING;
		}

		return serialize(aObject, objectMapper());
	}

	/**
	 * This is an overloaded method for {@link #serialize(Object)} that provides
	 * more options for resulting JSON string
	 *
	 * @param aObject
	 *            Object to be converted to JSON string
	 * @param aInclude
	 *            {@link Boolean} value to indicated whether the JSON string
	 *            should be contain fields with empty/null values
	 * @param aIndent
	 *            {@link Boolean} value to indicated whether the JSON string
	 *            should be indented or not
	 * @param aQuoteFieldNames
	 *            {@link Boolean} value to indicated whether the field names in
	 *            the JSON string should be quoted(") or not
	 * @param aEscapeNonAscii
	 *            {@link Boolean} value to indicated whether the Non ASCII
	 *            characters should be escaped in the JSON string or not
	 * @return JSON string
	 */
	public static String serialize(Object aObject, Include aInclude, boolean aIndent, boolean aQuoteFieldNames,
			boolean aEscapeNonAscii) {
		if (isEmpty(aObject)) {
			return EMPTY_STRING;
		}

		return serialize(aObject, objectMapper(aInclude, aIndent, aQuoteFieldNames, aEscapeNonAscii));
	}

	/**
	 * This is an overloaded method for {@link #serialize(Object)} that allows
	 * the caller to provide a preconfigured instance of {@link ObjectMapper}
	 *
	 * @param aObject
	 *            Object to be converted to JSON string
	 * @param aObjectMapper
	 *            preconfigured instance of {@link ObjectMapper}
	 * @return JSON string
	 */
	public static String serialize(Object aObject, ObjectMapper aObjectMapper) {
		if (isEmpty(aObject)) {
			return EMPTY_STRING;
		}

		String objectString;

		try {
			objectString = aObjectMapper.writeValueAsString(aObject);
		} catch (JsonProcessingException error) {
			_logger.warn("Error:[{}] while serializing:[{}]", error.getMessage(), aObject.getClass());
			objectString = "~" + aObject.getClass().getSimpleName();
		}

		return objectString;
	}

	/**
	 * This method converts the given object to JSON string, excluding all the
	 * given field names
	 *
	 * @param aObject
	 *            Object to be converted to JSON string
	 * @param aExcludedFields
	 *            Fields to be excluded from the JSON string
	 * @return JSON string
	 */
	public static String serializeExcept(Object aObject, String... aExcludedFields) {
		if (isEmpty(aObject)) {
			return EMPTY_STRING;
		}

		return serializeExcept(aObject, objectMapper(), aExcludedFields);
	}

	/**
	 * This is an overloaded method for
	 * {@link #serializeExcept(Object, String...)} that provides more options
	 * for resulting JSON string
	 *
	 * @param aObject
	 *            Object to be converted to JSON string
	 * @param aInclude
	 *            {@link Boolean} value to indicated whether the JSON string
	 *            should be contain fields with empty/null values
	 * @param aIndent
	 *            {@link Boolean} value to indicated whether the JSON string
	 *            should be indented or not
	 * @param aQuoteFieldNames
	 *            {@link Boolean} value to indicated whether the field names in
	 *            the JSON string should be quoted(") or not
	 * @param aEscapeNonAscii
	 *            {@link Boolean} value to indicated whether the Non ASCII
	 *            characters should be escaped in the JSON string or not
	 * @param aExcludedFields
	 *            Field names as required by
	 *            {@link #serializeExcept(Object, String...)}
	 * @return JSON string
	 */
	public static String serializeExcept(Object aObject, Include aInclude, boolean aIndent, boolean aQuoteFieldNames,
			boolean aEscapeNonAscii, String... aExcludedFields) {
		if (!isEmpty(aObject)) {
			return serializeExcept(aObject, objectMapper(aInclude, aIndent, aQuoteFieldNames, aEscapeNonAscii),
					aExcludedFields);
		}

		return EMPTY_STRING;
	}

	/**
	 * This is an overloaded method for
	 * {@link #serializeExcept(Object, String...)} that allows the caller to
	 * provide a preconfigured instance of {@link ObjectMapper}
	 *
	 * @param aObject
	 *            Object to be converted to JSON string
	 * @param aObjectMapper
	 *            preconfigured instance of {@link ObjectMapper}
	 * @param aExcludedFields
	 *            Field names as required by
	 *            {@link #serializeExcept(Object, String...)}
	 * @return JSON string
	 */
	public static String serializeExcept(Object aObject, ObjectMapper aObjectMapper, String... aExcludedFields) {
		if (isEmpty(aObject)) {
			return EMPTY_STRING;
		}

		String objectString = null;

		try {

			if (!isEmpty(aExcludedFields)) {
				SimpleFilterProvider filterProvider = new SimpleFilterProvider();
				filterProvider.setDefaultFilter(SimpleBeanPropertyFilter.serializeAllExcept(aExcludedFields));
				aObjectMapper.setConfig(aObjectMapper.getSerializationConfig().withFilters(filterProvider));
			}

			objectString = aObjectMapper.writeValueAsString(aObject);
		} catch (JsonProcessingException error) {
			_logger.warn(String.format("Error in serializing {}", aObject.getClass()), error);
			objectString = "~" + aObject.getClass().getSimpleName();
		}

		return objectString;
	}

	/**
	 * This method converts the given object to JSON string, including only the
	 * given field names
	 *
	 * @param aObject
	 *            Object to be converted to JSON string
	 * @param aIncludedFields
	 *            Field names that should be included in the JSON output
	 * @return JSON string
	 */
	public static String serializeOnly(Object aObject, String... aIncludedFields) {
		if (isEmpty(aObject)) {
			return EMPTY_STRING;
		}

		return serializeOnly(aObject, objectMapper(), aIncludedFields);
	}

	/**
	 * This is an overloaded method for
	 * {@link #serializeOnly(Object, String...)} that provides more options for
	 * resulting JSON string
	 *
	 * @param aObject
	 *            Object to be converted to JSON string
	 * @param aInclude
	 *            {@link Boolean} value to indicated whether the JSON string
	 *            should be contain fields with empty/null values
	 * @param aIndent
	 *            {@link Boolean} value to indicated whether the JSON string
	 *            should be indented or not
	 * @param aQuoteFieldNames
	 *            {@link Boolean} value to indicated whether the field names in
	 *            the JSON string should be quoted(") or not
	 * @param aEscapeNonAscii
	 *            {@link Boolean} value to indicated whether the Non ASCII
	 *            characters should be escaped in the JSON string or not
	 * @param aIncludedFields
	 *            Field names as required by
	 *            {@link #serializeOnly(Object, String...)}
	 * @return JSON string
	 */
	public static String serializeOnly(Object aObject, Include aInclude, boolean aIndent, boolean aQuoteFieldNames,
			boolean aEscapeNonAscii, String... aIncludedFields) {
		if (isEmpty(aObject)) {
			return EMPTY_STRING;
		}

		return serializeOnly(aObject, objectMapper(aInclude, aIndent, aQuoteFieldNames, aEscapeNonAscii),
				aIncludedFields);
	}

	/**
	 * This is an overloaded method for
	 * {@link #serializeOnly(Object, String...)} that allows the caller to
	 * provide a preconfigured instance of {@link ObjectMapper}
	 *
	 * @param aObject
	 *            Object to be converted to JSON string
	 * @param aObjectMapper
	 *            preconfigured instance of {@link ObjectMapper}
	 * @param aIncludedFields
	 *            Field names as required by
	 *            {@link #serializeOnly(Object, String...)}
	 * @return JSON string
	 */
	public static String serializeOnly(Object aObject, ObjectMapper aObjectMapper, String... aIncludedFields) {
		if (isEmpty(aObject)) {
			return EMPTY_STRING;
		}

		String objectString;

		try {
			if (!isEmpty(aIncludedFields)) {
				SimpleFilterProvider filterProvider = new SimpleFilterProvider();
				filterProvider.setDefaultFilter(SimpleBeanPropertyFilter.filterOutAllExcept(aIncludedFields));
				aObjectMapper.setConfig(aObjectMapper.getSerializationConfig().withFilters(filterProvider));
			}

			objectString = aObjectMapper.writeValueAsString(aObject);
		} catch (JsonProcessingException error) {
			_logger.warn("Error:[{}] while serializing:[{}]", error.getMessage(), aObject.getClass());
			objectString = "~" + aObject.getClass().getSimpleName();
		}

		return objectString;
	}

	/**
	 * This method parses the given JSON string and returns an instance of the
	 * given class loaded with data
	 *
	 * @param <T>
	 *            Any subclass of {@link Object}
	 * @param aJSONString
	 *            JSON string to be parsed
	 * @param aClass
	 *            {@link Class} which should be instantiated from the JSON
	 *            string
	 * @return loaded instance
	 * @throws IOException
	 */
	public static <T> T deserialize(String aJSONString, Class<T> aClass) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

		return deserialize(aJSONString, aClass, mapper);
	}

	/**
	 * This is an overloaded method for {@link #deserialize(String, Class)} that
	 * allows the caller to provide a preconfigured instance of
	 * {@link ObjectMapper}
	 *
	 * @param <T>
	 *            Any subclass of {@link Object}
	 * @param aJSONString
	 *            JSON string to be parsed
	 * @param aClass
	 *            {@link Class} which should be instantiated from the JSON
	 *            string
	 * @param aObjectMapper
	 *            preconfigured instance of {@link ObjectMapper}
	 * @return loaded instance
	 * @throws IOException
	 */
	public static <T extends Object> T deserialize(String aJSONString, Class<T> aClass, ObjectMapper aObjectMapper)
			throws IOException {
		return aObjectMapper.readValue(aJSONString, aClass);
	}
}
