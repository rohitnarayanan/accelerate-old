package accelerate.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.core.session.SessionInformation;
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
	public List<Object> logoutUser(@PathVariable("authId") String aAuthId) {
		List<Object> sessionList = new ArrayList<Object>();
		List<Object> principalList = this.sessionRegistry.getAllPrincipals();
		for (Object principal : principalList) {
			if (!AppUtil.compare(ReflectionUtil.getFieldValue(principal.getClass(), principal, "username"), aAuthId)) {
				continue;
			}

			for (SessionInformation session : this.sessionRegistry.getAllSessions(principal, false)) {
				sessionList.add(session);
				session.expireNow();
			}
		}

		return sessionList;
	}
}
