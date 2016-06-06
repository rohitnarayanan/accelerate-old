package accelerate.web;

import static accelerate.util.AccelerateConstants.COMMA_CHAR;
import static accelerate.util.AccelerateConstants.EMPTY_STRING;
import static accelerate.util.AppUtil.getErrorLog;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import accelerate.databean.DataMap;

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
	public static DataMap jvmInfo() {
		Runtime runtime = Runtime.getRuntime();
		return DataMap.buildMap("JVM Max Size", Math.round((runtime.maxMemory() / 1024) / 1024) + "mb",
				"Current JVM Size", Math.round((runtime.totalMemory() / 1024) / 1024) + "mb", "Used Memory",
				Math.round(((runtime.totalMemory() - runtime.freeMemory()) / 1024) / 1024) + "mb", "Free Memory",
				Math.round((runtime.freeMemory() / 1024) / 1024) + "mb");
	}

	/**
	 * @param aRequest
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/info/request")
	public static DataMap requestInfo(HttpServletRequest aRequest) {
		return DataMap.buildMap("getContextPath", aRequest.getContextPath(), "getLocalAddr", aRequest.getLocalAddr(),
				"getLocalName", aRequest.getLocalName(), "getLocalPort", aRequest.getLocalPort(), "getPathInfo",
				aRequest.getPathInfo(), "getPathTranslated", aRequest.getPathTranslated(), "getQueryString",
				aRequest.getQueryString(), "getRemoteAddr", aRequest.getRemoteAddr(), "getRemoteHost",
				aRequest.getRemoteHost(), "getRemotePort", aRequest.getRemotePort(), "getRemoteUser",
				aRequest.getRemoteUser(), "getRequestURI", aRequest.getRequestURI(), "getRequestURL",
				aRequest.getRequestURL(), "getServerName", aRequest.getServerName(), "getServerPort",
				aRequest.getServerPort(), "getServletPath", aRequest.getServletPath());
	}

	/**
	 * @param aKeys
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/info/env")
	public Map<String, String> envInfo(@RequestParam(name = "keys", defaultValue = "") String aKeys) {
		return Arrays.stream(StringUtils.split(aKeys, COMMA_CHAR))
				.map(key -> new String[] { key, this.enviroment.getProperty(key, EMPTY_STRING) })
				.collect(Collectors.toMap(values -> values[0], values -> values[1]));
	}

	/**
	 * @param aRequest
	 *            {@link HttpServletRequest} instance
	 * @return thymeleaf view name for the error page
	 */
	@RequestMapping("/web/error")
	public static DataMap error(HttpServletRequest aRequest) {
		DataMap dataMap = requestInfo(aRequest);
		dataMap.put("requestURI",
				Objects.toString(aRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI), EMPTY_STRING));
		dataMap.put("errorStatusCode",
				Objects.toString(aRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE), EMPTY_STRING));
		dataMap.put("errorMessage",
				Objects.toString(aRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE), EMPTY_STRING));
		dataMap.put("errorType",
				Objects.toString(aRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE), EMPTY_STRING));
		dataMap.put("errorStackTrace",
				getErrorLog((Throwable) aRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION)));
		return dataMap;
	}

	/**
	 * @param aRequest
	 * @param aResponse
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/web/deleteCookies")
	public static void deleteCookies(HttpServletRequest aRequest, HttpServletResponse aResponse) {
		WebUtil.deleteCookies(aRequest, aResponse, StringUtils.split(aRequest.getParameter("list"), COMMA_CHAR));
	}

	/**
	 * @param aLogPath
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/logs/list")
	public static DataMap listLogs(@RequestParam(name = "dir") String aLogPath) {
		DataMap dataMap = DataMap.buildMap("directory", aLogPath);
		File logDir = new File(aLogPath);
		if (logDir.exists()) {
			if (logDir.isDirectory()) {
				dataMap.put("logs",
						Arrays.stream(logDir.listFiles()).map(aFile -> aFile.getName()).collect(Collectors.toList()));
			} else {
				dataMap.put("msg", "directory does not exist");
			}
		} else {
			dataMap.put("msg", "directory does not exist");
		}

		return dataMap;
	}

	/**
	 * @param aLogPath
	 * @param aFileNames
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/logs/delete")
	public static Map<String, Boolean> deleteLogs(@RequestParam(name = "dir") String aLogPath,
			@RequestParam(name = "files", required = false) String aFileNames) {
		return Arrays.stream(StringUtils.split(aFileNames, COMMA_CHAR)).map(aFileName -> new File(aLogPath, aFileName))
				.collect(Collectors.toMap(aFile -> aFile.getName(), aFile -> aFile.delete()));
	}
}
