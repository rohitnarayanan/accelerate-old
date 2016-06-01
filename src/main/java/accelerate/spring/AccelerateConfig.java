package accelerate.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * Main {@link Configuration} class for accelerate
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jul 20, 2014
 */
@Configuration
@EnableCaching
@Profile("accelerate")
public class AccelerateConfig {
	/**
	 * @return {@link PropertySourcesPlaceholderConfigurer} instance to inject
	 *         properties into beans
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	/**
	 * {@link Configuration} class to handle web context configuration
	 * 
	 * @version 1.0 Initial Version
	 * @author Rohit Narayanan
	 * @since Mar 10, 2016
	 */
	@Configuration
	@ConditionalOnWebApplication
	public static class AccelerateWebConfig {
		/**
		 * @return
		 */
		@Bean(name = "exceptionResolver")
		@ConditionalOnProperty(name = "accelerate.web.errorhandler")
		public static SimpleMappingExceptionResolver defaultExceptionResolver() {
			SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
			resolver.setDefaultErrorView("forward:/acl/util/web/error");
			return resolver;
		}
	}
}