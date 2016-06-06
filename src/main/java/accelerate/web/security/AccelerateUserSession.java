package accelerate.web.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import accelerate.web.AccelerateWebSession;

/**
 * Generic Data Bean to store in the user's session data
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jul 30, 2009
 */
public class AccelerateUserSession extends AccelerateWebSession implements UserDetails {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

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
	public AccelerateUserSession() {
		this(null);
	}

	/**
	 * Constructor with session id parameter
	 *
	 * @param aUserName
	 */
	public AccelerateUserSession(String aUserName) {
		super(aUserName);
		this.authorities = new ArrayList<>();
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
		return super.getPassword();
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
		return super.getUsername();
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