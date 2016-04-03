package accelerate.logging;

import static accelerate.util.JSONUtil.serialize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import accelerate.databean.AccelerateDataBean;

/**
 * Logger Utility Class for the application. Current implementation is based on
 * Logback
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Sep 15, 2009
 */
public class AccelerateLogUtil {
	/**
	 * @param aLogger
	 * @param aLogLevel
	 * @param aRequest
	 */
	public static void logRequest(Logger aLogger, LogLevel aLogLevel, HttpServletRequest aRequest) {
		if (!checkLogger(aLogger, aLogLevel)) {
			return;
		}

		log(aLogger, aLogLevel, null, serialize(aRequest.getParameterMap()));
	}

	/**
	 * @param aLogger
	 * @param aLogLevel
	 * @param aBean
	 */
	public static void logBean(Logger aLogger, LogLevel aLogLevel, Object aBean) {
		if (!checkLogger(aLogger, aLogLevel)) {
			return;
		}
		String message = null;
		if (aBean instanceof AccelerateDataBean) {
			message = ((AccelerateDataBean) aBean).toJSON();
		} else {
			message = serialize(aBean);
		}

		log(aLogger, aLogLevel, null, message);
	}

	/**
	 * @param aLogger
	 * @param aLogLevel
	 * @param aError
	 * @param aMessage
	 * @param aArgs
	 */
	public static void log(Logger aLogger, LogLevel aLogLevel, Throwable aError, String aMessage, Object... aArgs) {
		if (!checkLogger(aLogger, aLogLevel)) {
			return;
		}

		Object args = aArgs;

		// if provided add exception to arg list
		if (aError != null) {
			List<Object> argList = (args == null) ? new ArrayList<>() : Arrays.asList(aArgs);
			argList.add(aError);
			args = argList.toArray();
		}

		switch (aLogLevel) {
		case TRACE:
			aLogger.trace(aMessage, args);
			break;
		case DEBUG:
			aLogger.debug(aMessage, args);
			break;
		case INFO:
			aLogger.info(aMessage, args);
			break;
		case WARN:
			aLogger.warn(aMessage, args);
			break;
		case ERROR:
			aLogger.error(aMessage, args);
			break;
		}
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