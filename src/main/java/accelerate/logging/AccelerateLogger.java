package accelerate.logging;

import static accelerate.util.JSONUtil.serialize;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import accelerate.databean.AccelerateDataBean;
import accelerate.util.AppUtil;

/**
 * Logger Utility Class for the application. Current implementation is based on
 * Logback
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Sep 15, 2009
 */
public class AccelerateLogger {
	/**
	 * @param aCaller
	 * @param aLoggerName
	 * @param aMessage
	 * @param aArgs
	 */
	public static void trace(Class<?> aCaller, String aLoggerName, String aMessage, Object... aArgs) {
		getLogger(aCaller, aLoggerName).trace(aMessage, aArgs);
	}

	/**
	 * @param aCaller
	 * @param aLoggerName
	 * @param aMessage
	 * @param aArgs
	 */
	public static void debug(Class<?> aCaller, String aLoggerName, String aMessage, Object... aArgs) {
		getLogger(aCaller, aLoggerName).debug(aMessage, aArgs);
	}

	/**
	 * @param aCaller
	 * @param aLoggerName
	 * @param aMessage
	 * @param aArgs
	 */
	public static void info(Class<?> aCaller, String aLoggerName, String aMessage, Object... aArgs) {
		getLogger(aCaller, aLoggerName).info(aMessage, aArgs);
	}

	/**
	 * @param aCaller
	 * @param aLoggerName
	 * @param aMessage
	 * @param aArgs
	 */
	public static void warn(Class<?> aCaller, String aLoggerName, String aMessage, Object... aArgs) {
		getLogger(aCaller, aLoggerName).warn(aMessage, aArgs);
	}

	/**
	 * @param aCaller
	 * @param aLoggerName
	 * @param aMessage
	 * @param aArgs
	 */
	public static void error(Class<?> aCaller, String aLoggerName, String aMessage, Object... aArgs) {
		getLogger(aCaller, aLoggerName).error(aMessage, aArgs);
	}

	/**
	 * @param aCaller
	 * @param aLoggerName
	 * @param aLogLevel
	 * @param aError
	 * @param aMessage
	 * @param aArgs
	 */
	public static void exception(Class<?> aCaller, String aLoggerName, LogLevel aLogLevel, Throwable aError,
			String aMessage, Object... aArgs) {
		// Append the throwable object to argument array for Logback to pick it
		ArrayUtils.add(aArgs, aError);
		log(aCaller, aLoggerName, aLogLevel, aMessage, aArgs);
	}

	/**
	 * @param aCaller
	 * @param aLoggerName
	 * @param aLogLevel
	 * @param aRequest
	 */
	public static void logRequest(Class<?> aCaller, String aLoggerName, LogLevel aLogLevel,
			HttpServletRequest aRequest) {
		Logger logger = getLogger(aCaller, aLoggerName);
		if (checkLogger(logger, aLogLevel)) {
			logger.debug(serialize(aRequest.getParameterMap()));
		}
	}

	/**
	 * @param aCaller
	 * @param aLoggerName
	 * @param aLogLevel
	 * @param aBean
	 */
	public static void logBean(Class<?> aCaller, String aLoggerName, LogLevel aLogLevel, Object aBean) {
		Logger logger = getLogger(aCaller, aLoggerName);

		if (checkLogger(logger, aLogLevel)) {
			if (aBean instanceof AccelerateDataBean) {
				logger.debug(((AccelerateDataBean) aBean).toJSON());
			} else {
				logger.debug(serialize(aBean));
			}
		}
	}

	/**
	 * @param aCaller
	 * @param aLoggerName
	 * @param aLogLevel
	 * @param aMessage
	 * @param aArgs
	 */
	public static void log(Class<?> aCaller, String aLoggerName, LogLevel aLogLevel, String aMessage, Object... aArgs) {
		Logger logger = getLogger(aCaller, aLoggerName);
		if (!checkLogger(logger, aLogLevel)) {
			return;
		}

		switch (aLogLevel) {
		case TRACE:
			logger.trace(aMessage, aArgs);
			break;
		case DEBUG:
			logger.debug(aMessage, aArgs);
			break;
		case INFO:
			logger.info(aMessage, aArgs);
			break;
		case WARN:
			logger.warn(aMessage, aArgs);
			break;
		case ERROR:
			logger.error(aMessage, aArgs);
			break;
		}
	}

	/**
	 * @param aCaller
	 * @param aLoggerName
	 * @return
	 */
	public static Logger getLogger(Class<?> aCaller, String aLoggerName) {
		return AppUtil.isEmpty(aLoggerName) ? LoggerFactory.getLogger(aCaller) : LoggerFactory.getLogger(aLoggerName);
	}

	/**
	 * @param aLogger
	 * @param aLogLevel
	 * @return
	 */
	public static boolean checkLogger(Logger aLogger, LogLevel aLogLevel) {
		switch (aLogLevel) {
		case TRACE:
			return aLogger.isTraceEnabled();
		case DEBUG:
			return aLogger.isDebugEnabled();
		case INFO:
			return aLogger.isInfoEnabled();
		case WARN:
			return aLogger.isWarnEnabled();
		case ERROR:
			return aLogger.isErrorEnabled();
		}

		return false;
	}

	/**
	 * Log Level
	 *
	 * @author Rohit Narayanan
	 * @version 1.0 Initial Version
	 * @since Jan 17, 2015
	 */
	public static enum LogLevel {
		/**
		 *
		 */
		TRACE,

		/**
		 *
		 */
		DEBUG,

		/**
		 *
		 */
		INFO,

		/**
		 *
		 */
		WARN,

		/**
		 *
		 */
		ERROR
	}
}