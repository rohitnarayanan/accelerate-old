package accelerate.spring;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation should primarily be used by classes (mostly static) that are
 * not managed within Spring. It marks the annotated class as a listener for
 * Accelerate Caches.
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jan 19, 2015
 */
@Retention(RUNTIME)
@Target(value = { TYPE })
@Documented
public @interface StaticCacheListener {
	/**
	 * Name of the cache the annotated class is listening to
	 *
	 * @return
	 */
	public abstract String name();

	/**
	 * Name of the method that will handle the callback
	 *
	 * @return
	 */
	public abstract String handler() default "handleCacheLoad";
}
