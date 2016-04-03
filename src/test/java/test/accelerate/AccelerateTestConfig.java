package test.accelerate;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * PUT DESCRIPTION HERE
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since Mar 24, 2016
 */
@Configuration
@ComponentScan(basePackages = { "accelerate" })
public class AccelerateTestConfig {
	/**
	 * Path to home directory of the user
	 */
	public static final String userHome = System.getProperty("user.home") + "/.tmp";
}
