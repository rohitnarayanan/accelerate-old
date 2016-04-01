package accelerate.util;

/**
 * PUT DESCRIPTION HERE
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since May 19, 2009
 */
@SuppressWarnings("all")
public abstract class AccelerateConstants {
	// Bean Name Constants
	public static final String APP_CONFIG = "PropertyCache";
	public static final String APP_LOGGER = "AppLogger";
	public static final String SESSION_BEAN = "sessionBean";
	public static final String RESPONSE_BEAN = "responseBean";

	// Logger Names
	public static final String ROOT_LOGGER = "rootLogger";
	public static final String ERROR_LOGGER = "ErrorLogger";
	public static final String AUDIT_LOGGER = "AuditLogger";
	public static final String MONITOR_LOGGER = "MonitorLogger";
	public static final String REQUEST_LOGGER = "RequestLogger";

	// General Property Constants
	public static final String CACHE = "cache";
	public static final String DEBUG = "debug";
	public static final String DEFAULT = "default";
	public static final String DEV = "dev";
	public static final String DISABLED = "disabled";
	public static final String EMPTY_STRING = "";
	public static final String ENABLED = "enabled";
	public static final String ENVIRONMENT = "environment";
	public static final String FAILURE = "failure";
	public static final String FALSE = "false";
	public static final String FILE = "file";
	public static final String IMAGE = "image";
	public static final String INDENT = "indent";
	public static final String MESSAGE = "message";
	public static final String NAME = "name";
	public static final String NO = "no";
	public static final String NULL_STRING = "null";
	public static final String PAGE_BREAK = "<br />";
	public static final String PARAM = "param";
	public static final String PATH = "path";
	public static final String PREFIX = "prefix";
	public static final String PROD = "prod";
	public static final String REPORT = "report";
	public static final String SUCCESS = "success";
	public static final String SUFFIX = "suffix";
	public static final String SYSTEM = "system";
	public static final String TEMPLATE = "template";
	public static final String TRUE = "true";
	public static final String TYPE = "type";
	public static final String VALUE = "value";
	public static final String YES = "yes";
	public static final String WINDOW = "window";

	// Character Constants
	public static final String ASTERIX_CHAR = "*";
	public static final String BRACE_CHAR_CLOSE = "}";
	public static final String BRACE_CHAR_OPEN = "{";
	public static final String BRACKET_CHAR_CLOSE = ")";
	public static final String BRACKET_CHAR_OPEN = "(";
	public static final String COMPLEX_DELIMITER = "|#|";
	public static final String COMMA_CHAR = ",";
	public static final String COLON_CHAR = ":";
	public static final String DOT_CHAR = ".";
	public static final String DOUBLE_QUOTE_CHAR = "\"";
	public static final String EQUALS_CHAR = "=";
	public static final String HYPHEN_CHAR = "-";
	public static final String NEW_LINE = System.getProperty("line.separator");
	public static final String PERCENT_CHAR = "%";
	public static final String PIPE_CHAR = "|";
	public static final String POUND_CHAR = "#";
	public static final String QUESTION_CHAR = "?";
	public static final String SEMICOLON_CHAR = ";";
	public static final String SINGLE_QUOTE_CHAR = "'";
	public static final String SPACE_CHAR = " ";
	public static final String SQ_BRACKET_CHAR_CLOSE = "]";
	public static final String SQ_BRACKET_CHAR_OPEN = "[";
	public static final String UNDERSCORE_CHAR = "_";
	public static final String UNIX_PATH_CHAR = "/";
	public static final String WINDOWS_PATH_CHAR = "\\";
}