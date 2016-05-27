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
			System.out.println(FileUtil.deleteFilesFromDir(null, null));
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}
