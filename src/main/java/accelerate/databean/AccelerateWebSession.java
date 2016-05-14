package accelerate.databean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Generic Data Bean to store in the user's session data
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jul 30, 2009
 */
public class AccelerateWebSession extends AccelerateDataBean implements HttpSessionBindingListener, UserDetails {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@link Logger} instance
	 */
	private static final Logger _logger = LoggerFactory.getLogger(AccelerateWebSession.class);

	/**
	 * Default alive time for the session
	 */
	public static long defaultAge = 30 * 60 * 1000;

	/**
	 * Session Id
	 */
	private Date initTime = null;

	/**
	 * Session Id
	 */
	private String sessionId = null;

	/**
	 * Login User Name
	 */
	private String username = null;

	/**
	 * Login Password
	 */
	private transient String password = null;

	/**
	 * 
	 */
	private List<? extends GrantedAuthority> authorities = null;

	/**
	 * 
	 */
	private boolean accountExpired = false;

	/**
	 * 
	 */
	private boolean accountLocked = false;

	/**
	 * 
	 */
	private boolean credentialsExpired = false;

	/**
	 * 
	 */
	private boolean accountDisabled = false;

	/**
	 * Default Constructor
	 */
	public AccelerateWebSession() {
		initialize(null);
	}

	/**
	 * Constructor with session id parameter
	 *
	 * @param aUserName
	 */
	public AccelerateWebSession(String aUserName) {
		initialize(aUserName);
	}

	/**
	 * This method intializes the session bean
	 *
	 * @param aUserName
	 */
	private void initialize(String aUserName) {
		this.initTime = new Date();
		this.sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		this.username = aUserName == null ? this.sessionId : aUserName;
		this.authorities = new ArrayList<>();

		setIdField("sessionId");
		_logger.info("Session initialized for '{}' with id '{}' at '{}'", this.username, this.sessionId, this.initTime);
	}

	/**
	 * This method checks if the session is stale
	 *
	 * @return true, if stale
	 */
	public boolean isStale() {
		return (System.currentTimeMillis() - this.initTime.getTime()) > defaultAge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#getAuthorities(
	 * )
	 */
	/**
	 * @return
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#getPassword()
	 */
	/**
	 * @return
	 */
	@Override
	public String getPassword() {
		return this.password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#getUsername()
	 */
	/**
	 * @return
	 */
	@Override
	public String getUsername() {
		return this.username;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetails#
	 * isAccountNonExpired()
	 */
	/**
	 * @return
	 */
	@Override
	public boolean isAccountNonExpired() {
		return !this.accountExpired;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetails#
	 * isAccountNonLocked()
	 */
	/**
	 * @return
	 */
	@Override
	public boolean isAccountNonLocked() {
		return !this.accountLocked;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetails#
	 * isCredentialsNonExpired()
	 */
	/**
	 * @return
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return !this.credentialsExpired;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#isEnabled()
	 */
	/**
	 * @return
	 */
	@Override
	public boolean isEnabled() {
		return !this.accountDisabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.
	 * http.HttpSessionBindingEvent)
	 */
	/**
	 * @param aEvent
	 */
	@Override
	public void valueBound(HttpSessionBindingEvent aEvent) {
		// blank impl
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.
	 * http.HttpSessionBindingEvent)
	 */
	/**
	 * @param aEvent
	 */
	@Override
	public void valueUnbound(HttpSessionBindingEvent aEvent) {
		// blank impl
	}

	/**
	 * Getter method for "initTime" property
	 * 
	 * @return initTime
	 */
	public Date getInitTime() {
		return this.initTime;
	}

	/**
	 * Getter method for "sessionId" property
	 * 
	 * @return sessionId
	 */
	public String getSessionId() {
		return this.sessionId;
	}

	/**
	 * Setter method for "initTime" property
	 * 
	 * @param aInitTime
	 */
	public void setInitTime(Date aInitTime) {
		this.initTime = aInitTime;
	}

	/**
	 * Setter method for "sessionId" property
	 * 
	 * @param aSessionId
	 */
	public void setSessionId(String aSessionId) {
		this.sessionId = aSessionId;
	}

	/**
	 * Setter method for "username" property
	 * 
	 * @param aUsername
	 */
	public void setUsername(String aUsername) {
		this.username = aUsername;
	}

	/**
	 * Setter method for "password" property
	 * 
	 * @param aPassword
	 */
	public void setPassword(String aPassword) {
		this.password = aPassword;
	}

	/**
	 * Setter method for "authorities" property
	 * 
	 * @param aAuthorities
	 */
	public void setAuthorities(List<? extends GrantedAuthority> aAuthorities) {
		this.authorities = aAuthorities;
	}

	/**
	 * Setter method for "accountExpired" property
	 * 
	 * @param aAccountExpired
	 */
	public void setAccountExpired(boolean aAccountExpired) {
		this.accountExpired = aAccountExpired;
	}

	/**
	 * Setter method for "accountLocked" property
	 * 
	 * @param aAccountLocked
	 */
	public void setAccountLocked(boolean aAccountLocked) {
		this.accountLocked = aAccountLocked;
	}

	/**
	 * Setter method for "credentialsExpired" property
	 * 
	 * @param aCredentialsExpired
	 */
	public void setCredentialsExpired(boolean aCredentialsExpired) {
		this.credentialsExpired = aCredentialsExpired;
	}

	/**
	 * Setter method for "accountDisabled" property
	 * 
	 * @param aAccountDisabled
	 */
	public void setAccountDisabled(boolean aAccountDisabled) {
		this.accountDisabled = aAccountDisabled;
	}
}