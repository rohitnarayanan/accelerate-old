package test.accelerate;

import java.io.File;

import accelerate.util.FileUtil;

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
			File source = new File("C:\\Users\\185710\\Desktop\\Temp");
			File destination = new File("C:\\Users\\185710\\Desktop\\2.txt");

			System.out.println(FileUtil.listFiles(new File("D:\\Rogger\\Docs\\Certificates\\Visa")));
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}
