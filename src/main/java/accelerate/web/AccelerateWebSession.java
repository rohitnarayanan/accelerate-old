package accelerate.web;

import java.util.Date;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;

import accelerate.databean.AccelerateDataBean;

/**
 * Generic Data Bean to store in the user's session data
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jul 30, 2009
 */
public class AccelerateWebSession extends AccelerateDataBean implements HttpSessionBindingListener {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@link Logger} instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AccelerateWebSession.class);

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

		setIdField("sessionId");
		LOGGER.info("Session initialized for '{}' with id '{}' at '{}'", this.username, this.sessionId, this.initTime);
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
	 * Setter method for "initTime" property
	 * 
	 * @param aInitTime
	 */
	public void setInitTime(Date aInitTime) {
		this.initTime = aInitTime;
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
	 * Getter method for "username" property
	 * 
	 * @return username
	 */
	public String getUsername() {
		return this.username;
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
	 * Getter method for "password" property
	 * 
	 * @return password
	 */
	public String getPassword() {
		return this.password;
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