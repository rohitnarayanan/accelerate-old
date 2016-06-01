package accelerate.logging;

import static accelerate.util.AccelerateConstants.SPACE_CHAR;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import accelerate.util.StringUtil;

/**
 * This class is setup as a spring {@link Aspect}, to allow applications to
 * profile methods by specifying {@link Around} pointcuts.
 * 
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Apr 18, 2012
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class AuditLoggerAspect {
	/**
	 * 
	 */
	protected static final Logger LOGGER = LoggerFactory.getLogger(AuditLoggerAspect.class);

	/**
	 * @return
	 */
	public static boolean isDebugEnabled() {
		return LOGGER.isDebugEnabled();
	}

	/**
	 * This method profiles the execution of the method that has been captured
	 * by the pointcut defined. It logs the entry and exit of every method and
	 * also logs the execution time.
	 * 
	 * NOTE: This call is only applied to call by Spring or call on Spring
	 * configured objects. It does not handle private or class-internal methods
	 * calls.
	 * 
	 * @param aJoinPoint
	 *            the {@link JoinPoint} caught by the {@link Around} advice
	 * @return the object returned by the target method
	 * @throws Throwable
	 */
	@Around("execution(* *(..)) and @annotation(accelerate.logging.Auditable)")
	public static Object audit(ProceedingJoinPoint aJoinPoint) throws Throwable {
		Throwable error = null;
		Object returnObject = null;
		String signature = null;
		StopWatch stopWatch = null;

		if (LOGGER.isDebugEnabled()) {
			signature = StringUtil.split(aJoinPoint.getSignature().toString(), SPACE_CHAR).get(1);
			stopWatch = logMethodStart(signature);
		}

		try {
			returnObject = aJoinPoint.proceed();
		} catch (Throwable throwable) {
			error = throwable;
		}

		if (LOGGER.isDebugEnabled()) {
			logMethodEnd(signature, error, stopWatch);
		}

		if (error != null) {
			throw error;
		}

		return returnObject;
	}

	/**
	 * @param aSignature
	 * @return
	 */
	public static StopWatch logMethodStart(String aSignature) {
		LOGGER.debug("{},{},{}", "Start", aSignature, System.currentTimeMillis());
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		return stopWatch;

	}

	/**
	 * @param aSignature
	 * @param aError
	 * @param aStopWatch
	 */
	public static void logMethodEnd(String aSignature, Throwable aError, StopWatch aStopWatch) {
		aStopWatch.stop();

		LOGGER.debug("{},{},{}", "Time", aSignature, aStopWatch.getTotalTimeMillis());
		LOGGER.debug("{},{},{}", (aError != null) ? "ErrorExit" : "End", aSignature, System.currentTimeMillis());
	}
}