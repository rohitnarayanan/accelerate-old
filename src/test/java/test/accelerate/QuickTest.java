package test.accelerate;

import accelerate.web.AccelerateWebResponse;

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
			AccelerateWebResponse response = new AccelerateWebResponse();
			response.setServerError(true);
			response.setReturnCode(10);
			response.put("OK", "KO");
			System.out.println(response);
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}
