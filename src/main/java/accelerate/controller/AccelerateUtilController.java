package accelerate.controller;

import static accelerate.util.AccelerateConstants.COMMA_CHAR;
import static accelerate.util.AccelerateConstants.EMPTY_STRING;
import static accelerate.util.AppUtil.getErrorLog;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import accelerate.databean.AccelerateDataBean;
import accelerate.util.AccelerateConstants;
import accelerate.util.CollectionUtil;
import accelerate.util.StringUtil;
import accelerate.util.WebUtil;

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
@ConditionalOnProperty(name = "accelerate.web.util")
@RequestMapping("/acl/util")
public class AccelerateUtilController implements EnvironmentAware {
	/**
	 * 
	 */
	private Environment enviroment = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.context.EnvironmentAware#setEnvironment(org.
	 * springframework.core.env.Environment)
	 */
	/**
	 * @param aEnvironment
	 */
	@Override
	public void setEnvironment(Environment aEnvironment) {
		this.enviroment = aEnvironment;
	}

	/**
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/info/jvm")
	public static AccelerateDataBean jvmInfo() {
		Runtime runtime = Runtime.getRuntime();
		return AccelerateDataBean.build("JVM Max Size", Math.round((runtime.maxMemory() / 1024) / 1024) + "mb",
				"Current JVM Size", Math.round((runtime.totalMemory() / 1024) / 1024) + "mb", "Used Memory",
				Math.round(((runtime.totalMemory() - runtime.freeMemory()) / 1024) / 1024) + "mb", "Free Memory",
				Math.round((runtime.freeMemory() / 1024) / 1024) + "mb");
	}

	/**
	 * @param aRequest
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/info/request")
	public static AccelerateDataBean requestInfo(HttpServletRequest aRequest) {
		return AccelerateDataBean.build("getContextPath", aRequest.getContextPath(), "getLocalAddr",
				aRequest.getLocalAddr(), "getLocalName", aRequest.getLocalName(), "getLocalPort",
				aRequest.getLocalPort(), "getPathInfo", aRequest.getPathInfo(), "getPathTranslated",
				aRequest.getPathTranslated(), "getQueryString", aRequest.getQueryString(), "getRemoteAddr",
				aRequest.getRemoteAddr(), "getRemoteHost", aRequest.getRemoteHost(), "getRemotePort",
				aRequest.getRemotePort(), "getRemoteUser", aRequest.getRemoteUser(), "getRequestURI",
				aRequest.getRequestURI(), "getRequestURL", aRequest.getRequestURL(), "getServerName",
				aRequest.getServerName(), "getServerPort", aRequest.getServerPort(), "getServletPath",
				aRequest.getServletPath());
	}

	/**
	 * @param aKeys
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/info/env")
	public Map<String, String> envInfo(@RequestParam(name = "keys", defaultValue = "") String aKeys) {
		return StringUtil.split(aKeys, COMMA_CHAR).stream()
				.map(key -> new String[] { key, this.enviroment.getProperty(key, EMPTY_STRING) })
				.collect(Collectors.toMap(values -> values[0], values -> values[1]));
	}

	/**
	 * @param aRequest
	 *            {@link HttpServletRequest} instance
	 * @return thymeleaf view name for the error page
	 */
	@RequestMapping("/web/error")
	public static AccelerateDataBean error(HttpServletRequest aRequest) {
		AccelerateDataBean dataBean = requestInfo(aRequest);
		dataBean.put("requestURI", StringUtil.toString(aRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)));
		dataBean.put("errorStatusCode",
				StringUtil.toString(aRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)));
		dataBean.put("errorMessage", StringUtil.toString(aRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE)));
		dataBean.put("errorType", StringUtil.toString(aRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE)));
		dataBean.put("errorStackTrace",
				getErrorLog((Throwable) aRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION)));
		return dataBean;
	}

	/**
	 * @param aRequest
	 * @param aResponse
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/web/deleteCookies")
	public static void deleteCookies(HttpServletRequest aRequest, HttpServletResponse aResponse) {
		WebUtil.deleteCookies(aRequest, aResponse, CollectionUtil.toArray(
				StringUtil.split(aRequest.getParameter("list"), AccelerateConstants.COMMA_CHAR), String.class));
	}

	/**
	 * @param aLogPath
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/logs/list")
	public static AccelerateDataBean listLogs(@RequestParam(name = "dir") String aLogPath) {
		AccelerateDataBean dataBean = AccelerateDataBean.build("directory", aLogPath);
		File logDir = new File(aLogPath);
		if (logDir.exists()) {
			if (logDir.isDirectory()) {
				dataBean.put("logs",
						Arrays.stream(logDir.listFiles()).map(aFile -> aFile.getName()).collect(Collectors.toList()));
			} else {
				dataBean.put("msg", "directory does not exist");
			}
		} else {
			dataBean.put("msg", "directory does not exist");
		}

		return dataBean;
	}

	/**
	 * @param aLogPath
	 * @param aFileNames
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/logs/delete")
	public static Map<String, Boolean> deleteLogs(@RequestParam(name = "dir") String aLogPath,
			@RequestParam(name = "files", required = false) String aFileNames) {
		return StringUtil.split(aFileNames, AccelerateConstants.COMMA_CHAR).stream()
				.map(aFileName -> new File(aLogPath, aFileName))
				.collect(Collectors.toMap(aFile -> aFile.getName(), aFile -> aFile.delete()));
	}
}
