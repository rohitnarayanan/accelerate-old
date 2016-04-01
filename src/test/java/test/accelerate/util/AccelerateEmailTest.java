package test.accelerate.util;

import java.io.File;
import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import org.junit.Test;

import accelerate.util.AccelerateEmail;

/**
 * Junit test for {@link AccelerateEmail}
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 25-May-2015
 */
@SuppressWarnings("static-method")
public class AccelerateEmailTest {
	/**
	 * Test method for {@link accelerate.util.AccelerateEmail#AccelerateEmail()}
	 *
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	@Test
	public void testAccelerateEmail() throws MessagingException, UnsupportedEncodingException {
		AccelerateEmail mail = new AccelerateEmail();
		mail.setHost("smtp.gmail.com");
		mail.setPort(587);
		mail.setFrom("roggerdevel@gmail.com", "Rohit Narayanan");
		mail.addTo("rohit.nn@gmail.com");
		mail.setSubject("Accelerate Email");
		mail.addAttachment("Test Attachment", new File("temp.txt"));

		mail.getJavaMailProperties().put("mail.smtp.auth", true);
		mail.getJavaMailProperties().put("mail.smtp.starttls.enable", true);
		mail.setUsername("roggerdevel@gmail.com");
		mail.setPassword("roggerdevel123");
		mail.setHtmlMsg("<h2>Accelerate Email</h2>");
		// mail.send();
	}
}
