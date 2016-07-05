package test.accelerate;

import accelerate.databean.DataMap;
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
			AccelerateWebResponse bean = new AccelerateWebResponse();
			bean.put("Key1", "Value1");
			bean.setViewName("asdf");
			System.out.println(bean);

			System.out.println(true || false);
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}
