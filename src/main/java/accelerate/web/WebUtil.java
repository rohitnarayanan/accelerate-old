package accelerate.web;

import static accelerate.util.AccelerateConstants.UNIX_PATH_CHAR;

import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

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
	 * 
	 */
	private WebUtil() {
	}

	/**
	 * @param aRequest
	 * @param aResponse
	 * @param aCookieList
	 * @throws ServletException
	 */
	public static final void logout(HttpServletRequest aRequest, HttpServletResponse aResponse, String... aCookieList)
			throws ServletException {
		HttpSession session = aRequest.getSession(false);
		if (session != null) {
			LOGGER.debug("Invalidating session[{}] for user [{}]", session.getId(), aRequest.getRemoteUser());
			session.invalidate();
		}

		aRequest.logout();
		deleteCookies(aRequest, aResponse, (aCookieList != null) ? aCookieList : new String[] { "JSESSIONID" });
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
			cookie.setPath(StringUtils.defaultString(aRequest.getContextPath(), UNIX_PATH_CHAR));
			cookie.setMaxAge(0);
			return cookie;
		}).forEach(cookie -> aResponse.addCookie(cookie));
	}
}
