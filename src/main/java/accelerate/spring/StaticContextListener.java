package accelerate.spring;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation should primarily be used by classes (mostly static) that are
 * not managed within Spring. It marks the annotated class as a listener for
 * Spring Context.
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 25-May-2015
 */
@Retention(RUNTIME)
@Target(value = { TYPE })
@Documented
public @interface StaticContextListener {
	/**
	 * This method will be called when the spring context is initialized
	 */
	public abstract String onContextStarted() default "onContextStarted";

	/**
	 * This method will be called when the spring context is destroyed
	 */
	public abstract String onContextClosed() default "onContextClosed";
}