package test.accelerate;

import accelerate.util.ReflectionUtil;

/**
 * Basic class to quick test code
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 25-May-2015
 */
@SuppressWarnings("all")
public class AccelerateQuickTest {
	private String field1 = "abc";
	private static String field2 = "def";

	public static void main(String[] args) {
		try {
			AccelerateQuickTest test = new AccelerateQuickTest();
			System.out.println(ReflectionUtil.getFieldValue(AccelerateQuickTest.class, test, "field1"));
			System.out.println(ReflectionUtil.getFieldValue(AccelerateQuickTest.class, test, "field2"));
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}
