package accelerate.web.security;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import accelerate.util.AppUtil;
import accelerate.util.ReflectionUtil;

/**
 * {@link org.springframework.web.servlet.mvc.Controller} providing basic pages
 * like index, error and debug.
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 25-May-2015
 */
@RestController
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "accelerate.web.security")
@RequestMapping("/acl/security")
public class AccelerateSecurityController {
	/**
	 *
	 */
	@Autowired
	private ApplicationContext applicationContext = null;

	/**
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/info/session")
	public List<Object> sessionList() {
		return this.applicationContext.getBean(SessionRegistry.class).getAllPrincipals();
	}

	/**
	 * @param aAuthId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/logout/{authId}")
	public Map<String, Boolean> logoutUser(@PathVariable("authId") String aAuthId) {
		final SessionRegistry sessionRegistry = this.applicationContext.getBean(SessionRegistry.class);
		return sessionRegistry.getAllPrincipals().stream()
				.filter(principal -> AppUtil
						.compare(ReflectionUtil.getFieldValue(principal.getClass(), principal, "username"), aAuthId))
				.flatMap(principal -> sessionRegistry.getAllSessions(principal, false).stream())
				.collect(Collectors.toMap(session -> session.getSessionId(), session -> {
					session.expireNow();
					return session.isExpired();
				}));
	}
}
