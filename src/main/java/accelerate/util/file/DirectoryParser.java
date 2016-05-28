package accelerate.util.file;

import static accelerate.util.FileUtil.listFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import accelerate.exception.AccelerateException;
import accelerate.util.FileUtil;

/**
 * Utility class that provides methods to traverse a given directory. Users can
 * provide custom implementation of {@link FileHandler} to handle directories &
 * files
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 21-May-2015
 */
public class DirectoryParser {
	/**
	 * @param aFolder
	 * @param aFileFilter
	 * @param aHandler
	 * @return List of all files that were processed
	 * @throws AccelerateException
	 *             thrown by
	 *             {@link #parseDirectory(File, Predicate, FileHandler, List)}
	 */
	public static List<File> execute(File aFolder, Predicate<File> aFileFilter, FileHandler aHandler)
			throws AccelerateException {
		Assert.isTrue((aFolder != null) && aFolder.exists() && aFolder.isDirectory(),
				"Invalid folder - " + FileUtil.getPath(aFolder));
		List<File> fileList = new ArrayList<>();
		parseDirectory(aFolder, aFileFilter, aHandler, fileList);
		return fileList;
	}

	/**
	 * @param aFolder
	 * @param aFileFilter
	 * @param aHandler
	 * @param aFileList
	 * @throws AccelerateException
	 *             thrown by {@link FileHandler#handleDirectory(File)} or
	 *             {@link FileHandler#handleFile(File)}
	 */
	private static void parseDirectory(File aFolder, Predicate<File> aFileFilter, FileHandler aHandler,
			final List<File> aFileList) throws AccelerateException {
		aFileList.addAll(listFiles(aFolder).stream()
				.filter(aFile -> (aFileFilter == null) ? true : aFileFilter.test(aFile)).map(aFile -> {
					if (aHandler == null) {
						return aFile;
					}

					File newFile = null;
					if (aFile.isDirectory()) {
						newFile = aHandler.handleDirectory(aFile);
						parseDirectory(newFile, aFileFilter, aHandler, aFileList);
					} else {
						newFile = aHandler.handleFile(aFile);
					}

					return newFile;
				}).collect(Collectors.toList()));
	}

	/**
	 * Interface defining methods to be implemented by calling methods that need
	 * to use {@link DirectoryParser}
	 *
	 * @author Rohit Narayanan
	 * @version 1.0 Initial Version
	 * @since 21-May-2015
	 */
	public interface FileHandler {
		/**
		 * @param aFile
		 * @return {@link File} Instance, Will differ if path modifications were
		 *         made
		 * @throws AccelerateException
		 *             Allowing implementations to wrap exceptions in one class
		 */
		public File handleFile(File aFile) throws AccelerateException;

		/**
		 * @param aFolder
		 * @return {@link File} Instance, Will differ if path modifications were
		 *         made
		 * @throws AccelerateException
		 *             Allowing implementations to wrap exceptions in one class
		 */
		public File handleDirectory(File aFolder) throws AccelerateException;
	}
}