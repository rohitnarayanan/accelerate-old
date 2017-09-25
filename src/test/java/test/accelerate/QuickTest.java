package test.accelerate;

import java.io.File;

import org.springframework.util.ReflectionUtils;

import accelerate.util.FileUtil;
import accelerate.util.ReflectionUtil;
import accelerate.util.StringUtil;

/**
 * Basic class to quick test code
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 25-May-2015
 */
@SuppressWarnings("all")
public class QuickTest {
	private String field1 = "abc";
	private static String field2 = "def";

	public static void main(String[] args) {
		try {
			QuickTest test = new QuickTest();
			System.out.println(ReflectionUtil.getFieldValue(QuickTest.class, null, "field1"));
			 System.out.println(ReflectionUtil.getFieldValue(QuickTest.class, null,
			 "field2"));

//			ReflectionUtils.getField(ReflectionUtils.findField(QuickTest.class, "field2"), test);
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}
