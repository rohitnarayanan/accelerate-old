package accelerate.controller;

import static accelerate.util.AppUtil.getErrorLog;
import static accelerate.util.ReflectionUtil.invokeGetter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.context.WebContext;

import accelerate.cache.AccelerateCache;
import accelerate.exception.AccelerateException;
import accelerate.logging.AccelerateLogger;
import accelerate.logging.AccelerateLogger.LogLevel;
import accelerate.spring.AccelerateProperties;
import accelerate.util.AccelerateConstants;
import accelerate.util.StringUtil;

/**
 * {@link org.springframework.web.servlet.mvc.Controller} providing basic index,
 * error and debug pages, along with utility pages to view instances of
 * {@link AccelerateCache} configured in the application
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 25-May-2015
 */
@Controller
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "accelerate.web.ui")
@RequestMapping("/aclUtil")
public class UtilController {
	/**
	 * {@link List} of request attributes that are printed
	 */
	private static List<String> fieldList = new ArrayList<>();

	static {
		fieldList.add("serverName");
		fieldList.add("serverPort");
		fieldList.add("servletPath");
		fieldList.add("queryString");
		fieldList.add("requestURI");
		fieldList.add("requestURL");
		fieldList.add("remoteAddr");
		fieldList.add("remoteHost");
		fieldList.add("remotePort");
		fieldList.add("remoteUser");
		fieldList.add("localAddr");
		fieldList.add("localName");
		fieldList.add("localPort");
		fieldList.add("protocol");
	}

	/**
	 * {@link AccelerateProperties} instance
	 */
	@Autowired
	private AccelerateProperties accelerateProperties = null;

	/**
	 * @param aRequest
	 *            {@link HttpServletRequest} instance
	 * @param aResponse
	 *            {@link HttpServletResponse} instance
	 * @return {@link WebContext} instance
	 */
	protected WebContext populateContextAttributes(HttpServletRequest aRequest, HttpServletResponse aResponse) {
		/*
		 * Request Details
		 */
		List<String[]> requestFieldList = new ArrayList<>();
		for (String field : fieldList) {
			String fieldValue = null;

			try {
				fieldValue = StringUtil.toString(invokeGetter(aRequest, field));
			} catch (AccelerateException error) {
				fieldValue = error.getMessage();
			}

			requestFieldList.add(new String[] { field, fieldValue });
		}

		/*
		 * Request Params
		 */
		List<String[]> requestParamList = new ArrayList<>();
		Enumeration<String> paramNames = aRequest.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			requestParamList.add(new String[] { paramName, aRequest.getParameter(paramName) });
		}

		WebContext context = new WebContext(aRequest, aResponse, aRequest.getServletContext());
		context.setVariable("accelerateProperties", this.accelerateProperties);
		context.setVariable("requestFieldList", requestFieldList);
		context.setVariable("requestParamList", requestParamList);

		return context;
	}

	/**
	 * @param aRequest
	 *            {@link HttpServletRequest} instance
	 * @param aResponse
	 *            {@link HttpServletResponse} instance
	 * @return thymeleaf view name for the index page
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(HttpServletRequest aRequest, HttpServletResponse aResponse) {
		populateContextAttributes(aRequest, aResponse);

		return "acl#util/index";
	}

	/**
	 * @param aRequest
	 *            {@link HttpServletRequest} instance
	 * @param aResponse
	 *            {@link HttpServletResponse} instance
	 * @return thymeleaf view name for the debug page
	 */
	@RequestMapping(value = "/debug")
	public String debug(HttpServletRequest aRequest, HttpServletResponse aResponse) {
		populateContextAttributes(aRequest, aResponse);

		return "acl#util/debug";
	}

	/**
	 * @param aRequest
	 *            {@link HttpServletRequest} instance
	 * @param aResponse
	 *            {@link HttpServletResponse} instance
	 * @return thymeleaf view name for the error page
	 */
	@RequestMapping("/error")
	public String error(HttpServletRequest aRequest, HttpServletResponse aResponse) {
		try {
			WebContext context = populateContextAttributes(aRequest, aResponse);
			context.setVariable("requestURI",
					StringUtil.toString(aRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)));
			context.setVariable("errorStatusCode",
					StringUtil.toString(aRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)));
			context.setVariable("errorMessage",
					StringUtil.toString(aRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE)));
			context.setVariable("errorType",
					StringUtil.toString(aRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE)));
			context.setVariable("errorStackTrace",
					getErrorLog((Throwable) aRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION)));
		} catch (Exception error) {
			AccelerateLogger.exception(UtilController.class, AccelerateConstants.ERROR_LOGGER, LogLevel.ERROR, error,
					"Error compiling error information");
		}

		return "acl#util/error";
	}
}
