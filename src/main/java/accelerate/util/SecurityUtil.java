package accelerate.util;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import accelerate.databean.AccelerateWebSession;

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
	public static final AccelerateWebSession getUserSession() {
		return (AccelerateWebSession) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	/**
	 * @param aAuthenticationException
	 * @return
	 */
	public static final String getAuthErrorParam(AuthenticationException aAuthenticationException) {
		StringBuilder errorURL = new StringBuilder();

		if (aAuthenticationException instanceof InsufficientAuthenticationException) {
			errorURL.append("errorType=notLoggedIn");
		} else if (aAuthenticationException instanceof BadCredentialsException) {
			errorURL.append("errorType=incorrectLogin");
		} else if (aAuthenticationException instanceof DisabledException) {
			errorURL.append("errorType=userDisabled");
		} else if (aAuthenticationException instanceof AccountExpiredException) {
			errorURL.append("errorType=userAccountExpired");
		} else if (aAuthenticationException instanceof CredentialsExpiredException) {
			errorURL.append("errorType=userCredentialsAccount");
		} else if (aAuthenticationException instanceof LockedException) {
			errorURL.append("errorType=userAccountLocked");
		} else {
			errorURL.append("errorType=other");
		}

		return errorURL.toString();
	}
}
