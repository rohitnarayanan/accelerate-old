package test.accelerate;

/**
 * Basic class to quick test code
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 25-May-2015
 */
@SuppressWarnings("all")
public class QuickTest {
	public static void main(String[] args) {
		try {
			System.out.println(System.getProperty("user.home"));
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}
