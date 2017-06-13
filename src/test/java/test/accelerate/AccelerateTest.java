package test.accelerate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import accelerate.cache.AccelerateCache;
import accelerate.cache.PropertyCache;
import accelerate.databean.AccelerateDataBean;
import accelerate.spring.StaticCacheListener;
import accelerate.spring.StaticContextListener;

/**
 * Junit test for accelerate spring context
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jul 20, 2014
 */
@SpringBootApplication(scanBasePackages = { "accelerate.*" }, exclude = { SecurityAutoConfiguration.class,
		ManagementWebSecurityAutoConfiguration.class, DataSourceAutoConfiguration.class })
@EnableAspectJAutoProxy(proxyTargetClass = true)
@RestController
@StaticContextListener(onContextStarted = "contextStarted", onContextClosed = "contextClosed")
@StaticCacheListener(name = "AccelerateConfig", handler = "testHandler")
public class AccelerateTest extends WebMvcConfigurerAdapter {
	/**
	 * 
	 */
	private static final Logger _logger = LoggerFactory.getLogger(AccelerateCache.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
	 * #addViewControllers(org.springframework.web.servlet.config.annotation.
	 * ViewControllerRegistry)
	 */
	/**
	 * @param aRegistry
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry aRegistry) {
		aRegistry.addViewController("/").setViewName("index");
	}

	/**
	 * @return
	 */
	@Bean
	public static EmbeddedServletContainerCustomizer customizeEmbeddedContainer() {
		return aContainer -> aContainer.addErrorPages(new ErrorPage("/acl/util/web/error"));
	}

	/**
	 * @return
	 */
	@Bean(name = "AccelerateConfig")
	public static PropertyCache accelerateConfig() {
		return new PropertyCache("AccelerateConfig", "classpath:config/application.properties");
	}

	/**
	 * Method to test spring jackson convertor and logbacks message formatting
	 * 
	 * @return
	 */
	@RequestMapping("/testJsonConverter")
	public static AccelerateDataBean testJsonConverter() {
		MDC.put("sessionId", "1234567890");
		_logger.error("This is test message to log the msg:{} and error too", "YEAH", new Exception());
		return AccelerateDataBean.build("testKey1", "testValue1", "testKey2", "testValue2");
	}

	/**
	 * @param aCache
	 */
	public static final void testHandler(PropertyCache aCache) {
		System.err.println("AccelerateTest: Injected Cache with size:" + aCache.size());
	}

	/**
	 * @param aContext
	 */
	public static final void contextStarted(@SuppressWarnings("unused") ApplicationContext aContext) {
		System.err.println("AccelerateTest: Context started");
	}

	/**
	 * @param aContext
	 */
	public static final void contextClosed(@SuppressWarnings("unused") ApplicationContext aContext) {
		System.err.println("AccelerateTest: Context closed");
	}

	/**
	 * Main method to start web context as spring boot application
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(AccelerateTest.class, args);
	}
}
