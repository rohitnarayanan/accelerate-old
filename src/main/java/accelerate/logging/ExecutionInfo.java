package accelerate.logging;

import static accelerate.util.AccelerateConstants.EMPTY_STRING;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.SystemUtils;

/**
 * PUT DESCRIPTION HERE
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Sep 15, 2009
 */
public class ExecutionInfo {
	/**
	 *
	 */
	private String executionId = null;

	/**
	 *
	 */
	private String user = null;

	/**
	 *
	 */
	private String remoteIp = null;

	/**
	 *
	 */
	private String browserType = null;

	/**
	 *
	 */
	private String protocol = null;

	/**
	 *
	 */
	private String virtualHostName = null;

	/**
	 *
	 */
	private String contextPath = null;

	/**
	 *
	 */
	private String httpMethod = null;

	/**
	 *
	 */
	private String url = null;

	/**
	 * private constructor to disable instantiation
	 */
	private ExecutionInfo() {
	}

	/**
	 * @param aExecutionId
	 * @return
	 */
	public static ExecutionInfo init(String aExecutionId) {
		ExecutionInfo executionInfo = new ExecutionInfo();
		executionInfo.setExecutionId(aExecutionId);
		executionInfo.setUser(SystemUtils.USER_NAME);

		// MDC.put("executionInfo", executionInfo);

		return executionInfo;
	}

	/**
	 * @param aRequest
	 * @return
	 */
	public static ExecutionInfo init(HttpServletRequest aRequest) {
		StringBuilder executionId = new StringBuilder();
		executionId.append(Thread.currentThread().getName());
		executionId.append("-");
		executionId.append(System.currentTimeMillis());
		executionId.append("@");
		executionId.append(aRequest.getRemoteAddr());

		String virtualHost = EMPTY_STRING;
		try {
			URL url = new URL(aRequest.getRequestURI());
			virtualHost = url.getHost();
		} catch (Exception exp) {
			System.out.println(exp.getMessage());
		}

		ExecutionInfo executionInfo = new ExecutionInfo();
		executionInfo.setRemoteIp(aRequest.getRemoteAddr());
		executionInfo.setBrowserType(aRequest.getHeader("User-Agent"));
		executionInfo.setProtocol(aRequest.getProtocol());
		executionInfo.setVirtualHostName(virtualHost);
		executionInfo.setContextPath(aRequest.getContextPath());
		executionInfo.setHttpMethod(aRequest.getMethod());
		executionInfo.setUrl(aRequest.getRequestURI());
		executionInfo.setExecutionId(executionId.toString());
		executionInfo.setUser(SystemUtils.USER_NAME);

		return executionInfo;
	}

	/**
	 * Getter Method for executionId
	 *
	 * @return executionId
	 */
	public String getExecutionId() {
		return this.executionId;
	}

	/**
	 * Setter Method for executionId
	 *
	 * @param aExecutionId
	 *            - set executionId
	 */
	public void setExecutionId(String aExecutionId) {
		this.executionId = aExecutionId;
	}

	/**
	 * Getter Method for user
	 *
	 * @return user
	 */
	public String getUser() {
		return this.user;
	}

	/**
	 * Setter Method for user
	 *
	 * @param aUser
	 *            - set user
	 */
	public void setUser(String aUser) {
		this.user = aUser;
	}

	/**
	 * Getter Method for remoteIp
	 *
	 * @return remoteIp
	 */
	public String getRemoteIp() {
		return this.remoteIp;
	}

	/**
	 * Setter Method for remoteIp
	 *
	 * @param aRemoteIp
	 *            - set remoteIp
	 */
	public void setRemoteIp(String aRemoteIp) {
		this.remoteIp = aRemoteIp;
	}

	/**
	 * Getter Method for protocol
	 *
	 * @return protocol
	 */
	public String getProtocol() {
		return this.protocol;
	}

	/**
	 * Setter Method for protocol
	 *
	 * @param aProtocol
	 *            - set protocol
	 */
	public void setProtocol(String aProtocol) {
		this.protocol = aProtocol;
	}

	/**
	 * Getter Method for virtualHostName
	 *
	 * @return virtualHostName
	 */
	public String getVirtualHostName() {
		return this.virtualHostName;
	}

	/**
	 * Setter Method for virtualHostName
	 *
	 * @param aVirtualHostName
	 *            - set virtualHostName
	 */
	public void setVirtualHostName(String aVirtualHostName) {
		this.virtualHostName = aVirtualHostName;
	}

	/**
	 * Getter Method for contextPath
	 *
	 * @return contextPath
	 */
	public String getContextPath() {
		return this.contextPath;
	}

	/**
	 * Setter Method for contextPath
	 *
	 * @param aContextPath
	 *            - set contextPath
	 */
	public void setContextPath(String aContextPath) {
		this.contextPath = aContextPath;
	}

	/**
	 * Getter Method for browserType
	 *
	 * @return browserType
	 */
	public String getBrowserType() {
		return this.browserType;
	}

	/**
	 * Setter Method for browserType
	 *
	 * @param aBrowserType
	 *            - set browserType
	 */
	public void setBrowserType(String aBrowserType) {
		this.browserType = aBrowserType;
	}

	/**
	 * Getter Method for httpMethod
	 *
	 * @return httpMethod
	 */
	public String getHttpMethod() {
		return this.httpMethod;
	}

	/**
	 * Setter Method for httpMethod
	 *
	 * @param aHttpMethod
	 *            - set httpMethod
	 */
	public void setHttpMethod(String aHttpMethod) {
		this.httpMethod = aHttpMethod;
	}

	/**
	 * Getter Method for url
	 *
	 * @return url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Setter Method for url
	 *
	 * @param aUrl
	 *            - set url
	 */
	public void setUrl(String aUrl) {
		this.url = aUrl;
	}
}