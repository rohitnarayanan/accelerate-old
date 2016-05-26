package test.accelerate;

import java.util.Arrays;
import java.util.stream.Collectors;

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
			Object[] arr = new Object[] { "ok", null, "ko" };
			Arrays.stream(arr).map(val -> val.toString()).collect(Collectors.toList());
			System.out.println("QuickTest");
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}
