package test.accelerate;

import java.io.File;

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
			File folder = new File("C:\\Temp\\root\\f2\\sf5");
			File root = new File("C:\\Temp\\root");
			System.out.println(FileUtil.getPathKey(folder.toPath(), root.toPath()));
			FileUtil.walkFileTree("C:\\Temp\\root", null, null, null,
					(aFile, aFileVisitResult) -> aFile.getName().startsWith("f"))
					.forEach((k, v) -> System.out.println(v));
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}
