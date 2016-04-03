package test.accelerate;

import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * PUT DESCRIPTION HERE
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since Mar 24, 2016
 */
@Configuration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration
@ComponentScan(basePackages = { "accelerate" })
public class AccelerateTestConfig {
	/**
	 * Path to home directory of the user
	 */
	public static final String userHome = System.getProperty("user.home") + "/.tmp";
}
