package accelerate.spring;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Main {@link Configuration} class for Accelerate
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jul 20, 2014
 */
@Component
@ConfigurationProperties(prefix = "accelerate", ignoreUnknownFields = false)
public class AccelerateProperties implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -8074174684206912932L;

	/**
	 * Id of the Application
	 */
	private String appId = null;

	/**
	 * Name of the Application to be displayed
	 */
	private String appName = null;

	/**
	 * URL to be redirected to for the Application HomePage
	 */
	private String homePage = null;

	/**
	 * Base package of the Application
	 */
	private String appBasePackage = null;

	/**
	 * Base package of the Application
	 */
	private Map<String, String> web = new HashMap<>();

	/**
	 * Getter method for "appId" property
	 * 
	 * @return appId
	 */
	public String getAppId() {
		return this.appId;
	}

	/**
	 * Setter method for "appId" property
	 * 
	 * @param aAppId
	 */
	public void setAppId(String aAppId) {
		this.appId = aAppId;
	}

	/**
	 * Getter method for "appName" property
	 *
	 * @return appName
	 */
	public String getAppName() {
		return this.appName;
	}

	/**
	 * Setter method for "appName" property
	 *
	 * @param aAppName
	 */
	public void setAppName(String aAppName) {
		this.appName = aAppName;
	}

	/**
	 * Getter method for "homePage" property
	 *
	 * @return homePage
	 */
	public String getHomePage() {
		return this.homePage;
	}

	/**
	 * Setter method for "homePage" property
	 *
	 * @param aHomePage
	 */
	public void setHomePage(String aHomePage) {
		this.homePage = aHomePage;
	}

	/**
	 * Getter method for "appBasePackage" property
	 *
	 * @return appBasePackage
	 */
	public String getAppBasePackage() {
		return this.appBasePackage;
	}

	/**
	 * Setter method for "appBasePackage" property
	 *
	 * @param aAppBasePackage
	 */
	public void setAppBasePackage(String aAppBasePackage) {
		this.appBasePackage = aAppBasePackage;
	}

	/**
	 * Getter method for "web" property
	 * 
	 * @return web
	 */
	public Map<String, String> getWeb() {
		return this.web;
	}

	/**
	 * Setter method for "web" property
	 * 
	 * @param aWeb
	 */
	public void setWeb(Map<String, String> aWeb) {
		this.web = aWeb;
	}
}