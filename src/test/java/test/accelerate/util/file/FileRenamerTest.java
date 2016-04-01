package test.accelerate.util.file;

import static org.junit.Assert.assertEquals;
import static test.accelerate.AccelerateTest.userHome;

import org.junit.Test;

import accelerate.exception.AccelerateException;
import accelerate.util.file.FileRenamer;

/**
 * Junit test for {@link FileRenamer}
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 21-May-2015
 */
@SuppressWarnings("static-method")
public class FileRenamerTest {
	/**
	 * Test method for
	 * {@link accelerate.util.file.FileRenamer#rename(accelerate.util.file.FileRenamer.FileRenamerInput)}
	 * .
	 *
	 * @throws AccelerateException
	 */
	@Test
	public void testRename() throws AccelerateException {
		FileRenamer.FileRenamerInput input = new FileRenamer.FileRenamerInput();
		input.filePath = userHome;
		input.enablePatternReplace = true;
		input.findPattern = "ok";
		input.replaceString = "ok";

		FileRenamer.FileRenamerOutput output = FileRenamer.rename(input);
		assertEquals("FileRenamer.rename failed !", output.executeCount, 1);
		assertEquals("FileRenamer.rename failed !", output.renameCount, 0);
	}
}
