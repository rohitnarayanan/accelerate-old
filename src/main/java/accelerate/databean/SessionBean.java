package accelerate.databean;

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import accelerate.logging.AccelerateLogger;
import accelerate.util.AccelerateConstants;

/**
 * Generic Data Bean to store in the user's session data
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jul 30, 2009
 */
public class SessionBean extends AccelerateDataBean implements HttpSessionBindingListener, UserDetails {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5969087461063440103L;

	/**
	 * Default alive time for the session
	 */
	public static long defaultAge = 30 * 60 * 1000;

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
	 * Session Id
	 */
	private Date initTime = null;

	/**
	 * Default Constructor
	 */
	public SessionBean() {
		// blank impl
	}

	/**
	 * Constructor with session id parameter
	 *
	 * @param aUserName
	 */
	public SessionBean(String aUserName) {
		setUsername(aUserName);
		initialize(aUserName);
	}

	/**
	 * This method intializes the session bean
	 *
	 * @param aSessionId
	 */
	private void initialize(String aSessionId) {
		this.initTime = new Date();
		this.sessionId = aSessionId;
		setIdField("sessionId");
		AccelerateLogger.info(this.getClass(), AccelerateConstants.REQUEST_LOGGER,
				"Session initialized for '{}' with id '{}' at '{}'", getUsername(), aSessionId, getInitTime());
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
		return null;
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
		return false;
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
		return false;
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
		return false;
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
		return false;
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
	 * Getter method for "sessionId" property
	 * 
	 * @return sessionId
	 */
	public String getSessionId() {
		return this.sessionId;
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
	 * Getter method for "initTime" property
	 * 
	 * @return initTime
	 */
	public Date getInitTime() {
		return this.initTime;
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
}