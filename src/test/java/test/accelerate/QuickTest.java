package test.accelerate;

import java.io.File;

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

			// Files.copy(Paths, out)
			File f = new File("\\\\192.168.1.46\\rohitnarayanan\\Documents\\Docs\\TCS Docs");
			// File f = new File("D:\\Rogger\\Docs\\TCS Docs");
			System.out.println(f.exists() + "===" + f.toURI());
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}
