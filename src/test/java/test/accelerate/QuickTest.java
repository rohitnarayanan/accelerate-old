package test.accelerate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import accelerate.databean.AccelerateDataBean;
import accelerate.util.CollectionUtil;
import accelerate.util.FileUtil;
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
	public static void main(String[] args) {
		try {
			Object f = CollectionUtil.toArray(Arrays.asList(new String[] { "a", "b" }), Object.class);
			System.out.println(f);
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}
