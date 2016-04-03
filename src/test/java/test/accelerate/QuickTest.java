package test.accelerate;

import accelerate.databean.AccelerateDataBean;
import accelerate.databean.AccelerateModel;

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
			AccelerateModel bean = new AccelerateModel();
			System.out.println(bean instanceof AccelerateDataBean);
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}
