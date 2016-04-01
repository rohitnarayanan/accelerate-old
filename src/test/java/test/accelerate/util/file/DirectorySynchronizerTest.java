package test.accelerate.util.file;

import static org.junit.Assert.assertEquals;
import static test.accelerate.AccelerateTest.userHome;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import accelerate.exception.AccelerateException;
import accelerate.util.file.DirectorySynchronizer;

/**
 * Junit test for {@link DirectorySynchronizer}
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 21-May-2015
 */
@SuppressWarnings("static-method")
public class DirectorySynchronizerTest {
	/**
	 * Test method for
	 * {@link accelerate.util.file.DirectorySynchronizer#compare(accelerate.util.file.DirectorySynchronizer.DirSyncInput)}
	 * .
	 *
	 * @throws AccelerateException
	 */
	@Test
	public void testCompare() throws AccelerateException {
		DirectorySynchronizer.DirSyncInput dirSyncInput = new DirectorySynchronizer.DirSyncInput();
		dirSyncInput.sourceDir = new File(userHome);
		dirSyncInput.targetDir = new File(userHome);

		DirectorySynchronizer.DirSyncOutput dirSyncOutput = DirectorySynchronizer.compare(dirSyncInput);
		assertEquals("Found new source files", dirSyncOutput.newSourceFiles.size(), 0);
		assertEquals("Found new target files", dirSyncOutput.newTargetFiles.size(), 0);
		assertEquals("Found mismatched files", dirSyncOutput.conflictedFiles.size(), 0);
	}

	/**
	 * Test method for
	 * {@link accelerate.util.file.DirectorySynchronizer#synchronize(accelerate.util.file.DirectorySynchronizer.DirSyncInput, accelerate.util.file.DirectorySynchronizer.DirSyncOutput, int)}
	 * .
	 *
	 * @throws AccelerateException
	 */
	@Test
	public void testSynchronize() throws AccelerateException {
		DirectorySynchronizer.DirSyncInput dirSyncInput = new DirectorySynchronizer.DirSyncInput();
		dirSyncInput.sourceDir = new File("C:/Temp");
		dirSyncInput.targetDir = new File("C:/Temp");

		DirectorySynchronizer.DirSyncOutput dirSyncOutput = DirectorySynchronizer.compare(dirSyncInput);
		DirectorySynchronizer.synchronize(dirSyncInput, dirSyncOutput, 10);
		Assert.assertTrue(dirSyncOutput.message.length() > 0);
		System.out.println(dirSyncOutput.message);
	}
}
