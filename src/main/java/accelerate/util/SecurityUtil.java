package accelerate.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.CsrfException;

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
	 * {@link Logger} instance
	 */
	static final Logger LOGGER = LoggerFactory.getLogger(SecurityUtil.class);

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

	/**
	 * @param aAnglularJSFlag
	 * @return
	 */
	public static final AuthenticationEntryPoint configureAuthenticationEntryPoint(final boolean aAnglularJSFlag) {
		return new AuthenticationEntryPoint() {
			@Override
			public void commence(HttpServletRequest aRequest, HttpServletResponse aResponse,
					AuthenticationException aAuthException) throws IOException, ServletException {
				LOGGER.info("AuthError:{}#@#URL:{}", aAuthException.getClass().getName(), aRequest.getRequestURL());
				if (aAnglularJSFlag && AngularJSUtil.handleAuthEntry(aRequest, aResponse, aAuthException)) {
					return;
				}

				WebUtil.REDIRECT_STRATEGY.sendRedirect(aRequest, aResponse, getAuthErrorParam(aAuthException));
			}
		};
	}

	/**
	 * @param aLoginURL
	 * @param aLogoutURL
	 * @param aAnglularJSFlag
	 * @return
	 */
	public static final AccessDeniedHandler configureAccessDeniedHandler(final String aLoginURL,
			final String aLogoutURL, final boolean aAnglularJSFlag) {
		return new AccessDeniedHandlerImpl() {
			@Override
			public void handle(HttpServletRequest aRequest, HttpServletResponse aResponse,
					AccessDeniedException aAccessDeniedException) throws IOException, ServletException {
				if (aAccessDeniedException instanceof CsrfException) {
					if (StringUtils.startsWithAny(WebUtil.URL_PATH_HELPER.getPathWithinApplication(aRequest), aLoginURL,
							aLogoutURL)) {
						LOGGER.info("Ignoring Login/Logout failure due to invalid CSRF token, "
								+ "User will be logged out and sent to index(/) page");
						WebUtil.logout(aRequest, aResponse);
						WebUtil.REDIRECT_STRATEGY.sendRedirect(aRequest, aResponse, "/");
						return;
					}
				}

				if (aAnglularJSFlag && AngularJSUtil.handleAccessDenied(aRequest, aResponse, aAccessDeniedException)) {
					return;
				}

				super.handle(aRequest, aResponse, aAccessDeniedException);
			}
		};
	}
}
