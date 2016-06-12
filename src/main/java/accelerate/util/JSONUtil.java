package accelerate.util;

import static accelerate.util.AccelerateConstants.EMPTY_STRING;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.io.IOException;

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

import accelerate.exception.AccelerateException;

/**
 * Utility class for converting object to/from JSON string
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 25-May-2015
 */
public final class JSONUtil {
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
		return objectMapper(Include.NON_NULL, false, true, true, false);
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
	 * @param aIncludeDefaultView
	 *            {@link Boolean} value to indicated whether the defaut json
	 *            view should be included or not
	 * @return configured {@link ObjectMapper} instance
	 */
	public static ObjectMapper objectMapper(Include aInclude, boolean aIndent, boolean aQuoteFieldNames,
			boolean aEscapeNonAscii, boolean aIncludeDefaultView) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mapper.setSerializationInclusion(aInclude);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, aIndent);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, aQuoteFieldNames);
		mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, aEscapeNonAscii);
		mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, aIncludeDefaultView);

		return mapper;
	}

	/**
	 * This is the default method to convert the given object to JSON string
	 *
	 * @param aObject
	 *            Object to be converted to JSON string
	 * @return JSON string
	 * @throws AccelerateException
	 */
	public static String serialize(Object aObject) throws AccelerateException {
		// if (isEmpty(aObject)) {
		// return EMPTY_STRING;
		// }

		return serialize(aObject, objectMapper());
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
	 * @throws AccelerateException
	 */
	public static String serialize(Object aObject, ObjectMapper aObjectMapper) throws AccelerateException {
		// if (isEmpty(aObject)) {
		// return EMPTY_STRING;
		// }

		try {
			return aObjectMapper.writeValueAsString(aObject);
		} catch (JsonProcessingException error) {
			throw new AccelerateException(error);
		}
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
	 * @throws AccelerateException
	 */
	public static String serializeExcept(Object aObject, String... aExcludedFields) throws AccelerateException {
		if (isEmpty(aObject)) {
			return EMPTY_STRING;
		}

		return serializeExcept(aObject, objectMapper(), aExcludedFields);
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
	 * @throws AccelerateException
	 */
	public static String serializeExcept(Object aObject, ObjectMapper aObjectMapper, String... aExcludedFields)
			throws AccelerateException {
		if (isEmpty(aObject)) {
			return EMPTY_STRING;
		}

		try {
			return aObjectMapper
					.writer(new SimpleFilterProvider()
							.setDefaultFilter(SimpleBeanPropertyFilter.serializeAllExcept(aExcludedFields)))
					.writeValueAsString(aObject);
		} catch (JsonProcessingException error) {
			throw new AccelerateException(error);
		}
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
	 * @throws AccelerateException
	 */
	public static String serializeOnly(Object aObject, String... aIncludedFields) throws AccelerateException {
		if (isEmpty(aObject)) {
			return EMPTY_STRING;
		}

		return serializeOnly(aObject, objectMapper(), aIncludedFields);
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
	 * @throws AccelerateException
	 */
	public static String serializeOnly(Object aObject, ObjectMapper aObjectMapper, String... aIncludedFields)
			throws AccelerateException {
		if (isEmpty(aObject)) {
			return EMPTY_STRING;
		}

		try {
			return aObjectMapper
					.writer(new SimpleFilterProvider()
							.setDefaultFilter(SimpleBeanPropertyFilter.filterOutAllExcept(aIncludedFields)))
					.writeValueAsString(aObject);
		} catch (JsonProcessingException error) {
			throw new AccelerateException(error);
		}
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
	 * @throws AccelerateException
	 */
	public static <T> T deserialize(String aJSONString, Class<T> aClass) throws AccelerateException {
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
	 * @throws AccelerateException
	 */
	public static <T extends Object> T deserialize(String aJSONString, Class<T> aClass, ObjectMapper aObjectMapper)
			throws AccelerateException {
		try {
			return aObjectMapper.readValue(aJSONString, aClass);
		} catch (IOException error) {
			throw new AccelerateException(error);
		}
	}
}
