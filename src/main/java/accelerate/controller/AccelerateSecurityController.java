package accelerate.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
@Controller
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "accelerate.web.ui")
@RequestMapping("/acl/util")
public class AccelerateSecurityController {

	/**
	 *
	 */
	@Autowired
	private SessionRegistry sessionRegistry = null;

	/**
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/info/session")
	public List<Object> sessionList() {
		return this.sessionRegistry.getAllPrincipals();
	}

	/**
	 * @param aAuthId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/logout/{authId}")
	public Map<String, Boolean> logoutUser(@PathVariable("authId") String aAuthId) {
		return this.sessionRegistry.getAllPrincipals().stream()
				.filter(principal -> AppUtil
						.compare(ReflectionUtil.getFieldValue(principal.getClass(), principal, "username"), aAuthId))
				.flatMap(principal -> this.sessionRegistry.getAllSessions(principal, false).stream())
				.collect(Collectors.toMap(session -> session.getSessionId(), session -> {
					session.expireNow();
					return session.isExpired();
				}));
	}
}
