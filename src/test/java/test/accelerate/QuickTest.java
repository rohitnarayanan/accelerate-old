package test.accelerate;

import java.io.File;

import accelerate.util.FileUtil;
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
			File file = new File("C:\\Users\\185710\\Documents\\OneNote Notebooks");
			System.out.println(FileUtil.getFilePath(file));
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}
