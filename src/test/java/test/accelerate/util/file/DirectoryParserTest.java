package test.accelerate.util.file;

import static test.accelerate.AccelerateTest.userHome;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import accelerate.exception.AccelerateException;
import accelerate.util.file.DirectoryParser;

/**
 * Junit test for {@link DirectoryParser}
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 21-May-2015
 */
@SuppressWarnings("static-method")
public class DirectoryParserTest {
	/**
	 * Test method for
	 * {@link accelerate.util.file.DirectoryParser#execute(java.lang.String, accelerate.util.file.DirectoryParser.FileHandler)}
	 * .
	 *
	 * @throws AccelerateException
	 */
	@Test
	public void testExecuteString() throws AccelerateException {
		final Map<String, String> fileMap = new HashMap<>();
		System.out.println("userHome:" + userHome);
		DirectoryParser.execute(userHome, new DirectoryParser.FileHandler() {
			@Override
			public File handleFile(File aFile) {
				fileMap.put(aFile.getName(), aFile.getName());
				return aFile;
			}

			@Override
			public File handleDirectory(File aFolder) {
				return aFolder;
			}

			@Override
			public String getExtnFilter() {
				return null;
			}
		});

		org.junit.Assert.assertSame("Files not counted correctly", fileMap.size(),
				new File(userHome).listFiles().length);
	}

	/**
	 * Test method for
	 * {@link accelerate.util.file.DirectoryParser#execute(java.io.File, accelerate.util.file.DirectoryParser.FileHandler)}
	 * .
	 *
	 * @throws AccelerateException
	 */
	@Test
	public void testExecuteFile() throws AccelerateException {
		final Map<String, String> fileMap = new HashMap<>();
		DirectoryParser.execute(new File(userHome), new DirectoryParser.FileHandler() {
			@Override
			public File handleFile(File aFile) {
				fileMap.put(aFile.getName(), aFile.getName());
				return aFile;
			}

			@Override
			public File handleDirectory(File aFolder) {
				return aFolder;
			}

			@Override
			public String getExtnFilter() {
				return null;
			}
		});

		org.junit.Assert.assertSame("Files not counted correctly", fileMap.size(),
				new File(userHome).listFiles().length);
	}
}
