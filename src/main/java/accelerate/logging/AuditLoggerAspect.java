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
@Component("AuditLoggerAspect")
public class AuditLoggerAspect {
	/**
	 * 
	 */
	protected static final Logger _logger = LoggerFactory.getLogger(AuditLoggerAspect.class);

	/**
	 * This method profiles the execution of the method that has been captured
	 * by the pointcut defined. It logs the entry and exit of every method and
	 * also logs the execution time.
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
		String signature = StringUtil.split(aJoinPoint.getSignature().toString(), SPACE_CHAR)[1];
		StopWatch stopWatch = new StopWatch();

		_logger.debug("{},{},{}", "Start", signature, System.currentTimeMillis());

		stopWatch.start();
		try {
			returnObject = aJoinPoint.proceed();
		} catch (Throwable throwable) {
			error = throwable;
		}
		stopWatch.stop();

		_logger.debug("{},{},{}", "Time", signature, stopWatch.getTotalTimeMillis());
		_logger.debug("{},{},{}", (error != null) ? "ErrorExit" : "End", signature, System.currentTimeMillis());

		if (error != null) {
			throw error;
		}

		return returnObject;
	}
}