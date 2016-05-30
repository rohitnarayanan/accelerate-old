package accelerate.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

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
			resolver.setDefaultErrorView("forward:/aclUtil/error");
			return resolver;
		}
	}

	/**
	 * Configuration for Accelerate UI templates
	 * 
	 * @version 1.0 Initial Version
	 * @author Rohit Narayanan
	 * @since Feb 20, 2016
	 */
	@Configuration
	@ConditionalOnProperty(name = "accelerate.web.ui")
	public static class AccelerateUIConfig extends WebMvcConfigurerAdapter {
		/**
		 * 
		 */
		@Autowired
		private ApplicationContext applicationContext = null;

		/**
		 * 
		 */
		@Autowired
		private ThymeleafProperties thymeleafProperties = null;

		/**
		 * Method to register custom template resolver to handle path of
		 * accelerate view templates
		 * 
		 * @return
		 */
		@Bean
		public SpringResourceTemplateResolver templateResolver() {
			SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver() {
				@Override
				protected String computeResourceName(TemplateProcessingParameters aTemplateProcessingParameters) {
					if (aTemplateProcessingParameters.getTemplateName().startsWith("acl#")) {
						String[] tokens = aTemplateProcessingParameters.getTemplateName().split("#");
						final StringBuilder resourceName = new StringBuilder();
						resourceName.append("classpath:/templates/acl/");
						resourceName.append(tokens[1]);
						resourceName.append(getSuffix());
						return resourceName.toString();
					}

					return super.computeResourceName(aTemplateProcessingParameters);
				}
			};

			resolver.setApplicationContext(this.applicationContext);
			resolver.setCacheable(this.thymeleafProperties.isCache());
			resolver.setPrefix(this.thymeleafProperties.getPrefix());
			resolver.setSuffix(this.thymeleafProperties.getSuffix());
			resolver.setTemplateMode(this.thymeleafProperties.getMode());
			resolver.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
			return resolver;
		}

		/**
		 * @param aTemplateResolver
		 * @return
		 */
		@Bean
		public static SpringTemplateEngine templateEngine(SpringResourceTemplateResolver aTemplateResolver) {
			SpringTemplateEngine engine = new SpringTemplateEngine();
			engine.addTemplateResolver(aTemplateResolver);
			return engine;
		}

		/**
		 * @param aSpringTemplateEngine
		 * @return
		 */
		@Bean
		public static ThymeleafViewResolver thymeleafViewResolver(SpringTemplateEngine aSpringTemplateEngine) {
			ThymeleafViewResolver resolver = new ThymeleafViewResolver();
			resolver.setTemplateEngine(aSpringTemplateEngine);
			return resolver;
		}
	}
}