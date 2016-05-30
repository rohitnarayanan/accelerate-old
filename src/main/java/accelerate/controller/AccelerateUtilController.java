package accelerate.controller;

import static accelerate.util.AccelerateConstants.COMMA_CHAR;
import static accelerate.util.AccelerateConstants.EMPTY_STRING;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import accelerate.databean.AccelerateDataBean;
import accelerate.util.AccelerateConstants;
import accelerate.util.AppUtil;
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
@Controller
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "accelerate.web.ui")
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
	 * Getter method for "enviroment" property
	 * 
	 * @return enviroment
	 */
	private Environment getEnviroment() {
		return this.enviroment;
	}

	/**
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/info/jvm", produces = MediaType.APPLICATION_JSON_VALUE)
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
	 * @param aResponse
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/temp/deleteCookies")
	public static void deleteCookies(HttpServletRequest aRequest, HttpServletResponse aResponse) {
		WebUtil.deleteCookies(aRequest, aResponse, CollectionUtil
				.toArray(StringUtil.split(aRequest.getParameter("list"), AccelerateConstants.COMMA_CHAR)));
	}

	/**
	 * @param aDirPath
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/logs/list")
	public static List<String> listLogs(@RequestParam(name = "dir", defaultValue = "") String aDirPath) {
		List<String> fileList = new ArrayList<String>();
		File logDir = new File(AppUtil.isEmpty(aDirPath) ? aLogPath : aDirPath);
		if (logDir.exists() && logDir.isDirectory()) {
			for (String fileName : logDir.list()) {
				fileList.add(fileName);
			}
		} else {
			fileList.add(logDir.getPath() + "is not a directory or does not exist");
		}

		return fileList;
	}

	/**
	 * @param aDirPath
	 * @param aFileNames
	 * @param aLogPath
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/logs/delete")
	public static int deleteLogs(@RequestParam(name = "dir", defaultValue = "") String aDirPath,
			@RequestParam(name = "files", defaultValue = "", required = false) String aFileNames,
			@Value("${lse.logs.logPath}") String aLogPath) {
		String[] fileList = StringUtil.tokenize(aFileNames, AccelerateConstants.COMMA_CHAR);
		int count = 0;
		File logDir = new File(AppUtil.isEmpty(aDirPath) ? aLogPath : aDirPath);
		for (String fileName : fileList) {
			File logFile = new File(logDir, fileName.trim());
			count = count + (logFile.delete() ? 1 : 0);
		}
		return count;
	}
}
