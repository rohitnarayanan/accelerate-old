package accelerate.util;

import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.util.Assert;
import org.springframework.web.util.UrlPathHelper;

/**
 * PUT DESCRIPTION HERE
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since May 29, 2016
 */
public class WebUtil {
	/**
	 * {@link Logger} instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(WebUtil.class);

	/**
	 * static instance
	 */
	public static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();

	/**
	 * 
	 */
	public static final RedirectStrategy REDIRECT_STRATEGY = new DefaultRedirectStrategy();

	/**
	 * 
	 */
	public static final SessionRegistry SESSION_REGISTRY = new SessionRegistryImpl();

	/**
	 * 
	 */
	private WebUtil() {
	}

	/**
	 * @param aRequest
	 * @param aResponse
	 * @throws ServletException
	 */
	public static final void logout(HttpServletRequest aRequest, HttpServletResponse aResponse)
			throws ServletException {
		HttpSession session = aRequest.getSession(false);
		if (session != null) {
			LOGGER.debug("Invalidating session[{}] for user [{}]", session.getId(), aRequest.getRemoteUser());
			session.invalidate();
		}

		aRequest.logout();
		deleteCookies(aRequest, aResponse, "JSESSIONID");
	}

	/**
	 * @param aRequest
	 * @param aResponse
	 * @param aCookieList
	 */
	public static final void deleteCookies(HttpServletRequest aRequest, HttpServletResponse aResponse,
			String... aCookieList) {
		Assert.noNullElements(new Object[] { aRequest, aResponse, aCookieList }, "All arguments are required");
		Assert.noNullElements(aCookieList, "List of cookies are required");

		Arrays.stream(aCookieList).map(cookieName -> {
			LOGGER.debug("Resetting cookie [{}]", cookieName);
			Cookie cookie = new Cookie(cookieName, null);
			cookie.setPath(StringUtil.toString(aRequest.getContextPath(), AccelerateConstants.UNIX_PATH_CHAR));
			cookie.setMaxAge(0);
			return cookie;
		}).forEach(cookie -> aResponse.addCookie(cookie));
	}
}
