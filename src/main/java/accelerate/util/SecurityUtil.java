package accelerate.util;

import org.springframework.security.core.context.SecurityContextHolder;

import accelerate.databean.AccelerateSession;

/**
 * Utility class with helper methods to handle IO operations
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since Mar 10, 2016
 */
public final class SecurityUtil {

	/**
	 * hidden constructor
	 */
	private SecurityUtil() {
	}

	/**
	 * @return
	 */
	public static final AccelerateSession getUserSession() {
		return (AccelerateSession) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
}
