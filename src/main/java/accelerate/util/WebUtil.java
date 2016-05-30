package accelerate.util;

import java.util.Arrays;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	 * @param aRequest
	 * @param aResponse
	 * @param aCookieList
	 */
	public static final void deleteCookies(HttpServletRequest aRequest, HttpServletResponse aResponse,
			String... aCookieList) {
		Assert.noNullElements(new Object[] { aRequest, aResponse, aCookieList }, "All arguments are required");
		Assert.noNullElements(aCookieList, "List of cookies are required");

		Arrays.stream(aCookieList).map(cookieName -> {
			Cookie cookie = new Cookie(cookieName, null);
			cookie.setPath(StringUtil.toString(aRequest.getContextPath(), AccelerateConstants.UNIX_PATH_CHAR));
			cookie.setMaxAge(0);
			return cookie;
		}).forEach(cookie -> aResponse.addCookie(cookie));
	}
}
