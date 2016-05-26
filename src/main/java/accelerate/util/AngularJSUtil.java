package accelerate.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

/**
 * Utility class with helper methods to handle IO operations
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since Mar 10, 2016
 */
public final class AngularJSUtil {

	/**
	 * hidden constructor
	 */
	private AngularJSUtil() {
	}

	/**
	 * Filter to handle csrf token sent by Angular JS
	 * 
	 * @return
	 */
	public static Filter csrfHeaderFilter() {
		return new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest aRequest, HttpServletResponse aResponse,
					FilterChain aFilterChain) throws ServletException, IOException {

				CsrfToken csrf = (CsrfToken) aRequest.getAttribute(CsrfToken.class.getName());
				if (csrf != null) {
					Cookie cookie = WebUtils.getCookie(aRequest, "XSRF-TOKEN");
					String token = csrf.getToken();
					if ((cookie == null) || ((token != null) && !token.equals(cookie.getValue()))) {
						cookie = new Cookie("XSRF-TOKEN", token);
						cookie.setPath(aRequest.getContextPath());
						aResponse.addCookie(cookie);
					}
				}

				aFilterChain.doFilter(aRequest, aResponse);
			}
		};
	}

	/**
	 * Token repository configuration to identify csrf request header snet by
	 * Angular JS
	 * 
	 * @return
	 */
	public static CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName("X-XSRF-TOKEN");
		return repository;
	}
}
