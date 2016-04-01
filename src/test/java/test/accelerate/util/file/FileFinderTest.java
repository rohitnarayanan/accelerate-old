package test.accelerate.util.file;

import static org.junit.Assert.assertEquals;
import static test.accelerate.AccelerateTest.userHome;

import java.io.File;
import java.util.List;

import org.junit.Test;

import accelerate.exception.AccelerateException;
import accelerate.util.file.FileFinder;

/**
 * Junit test for {@link FileFinder}
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 21-May-2015
 */
@SuppressWarnings("static-method")
public class FileFinderTest {
	/**
	 * Test method for
	 * {@link accelerate.util.file.FileFinder#find(java.lang.String, java.lang.String)}
	 * .
	 *
	 * @throws AccelerateException
	 */
	@Test
	public void testFind() throws AccelerateException {
		List<File> files = FileFinder.find(userHome, "test");
		assertEquals("FileFinder.find failed", files.size(), 1);
	}

	/**
	 * Test method for
	 * {@link accelerate.util.file.FileFinder#findByExtn(java.lang.String, java.lang.String)}
	 * .
	 *
	 * @throws AccelerateException
	 */
	@Test
	public void testFindByExtn() throws AccelerateException {
		List<File> files = FileFinder.findByExtn(userHome, "txt");
		assertEquals("FileFinder.findByExtn failed", 1, files.size());
	}
}
