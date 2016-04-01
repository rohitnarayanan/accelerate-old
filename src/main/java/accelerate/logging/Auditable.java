package accelerate.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to configure {@link AuditLoggerAspect} to audit
 * only methods marked by this annotation
 * 
 * @version 1.0 Initial Version
 * @author TCS
 * @since Apr 22, 2014
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auditable {
	// Marker Annotation
}
