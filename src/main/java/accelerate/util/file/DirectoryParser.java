package accelerate.util.file;

import static accelerate.util.FileUtil.listFiles;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

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
	 *             {@link #parseDirectory(File, Predicate, FileHandler, Map)}
	 */
	public static Map<String, File> execute(File aFolder, Predicate<File> aFileFilter, FileHandler aHandler)
			throws AccelerateException {
		Assert.isTrue((aFolder != null) && aFolder.exists() && aFolder.isDirectory(),
				"Invalid folder - " + ((aFolder != null) ? aFolder.getPath() : "NULL"));
		Map<String, File> fileMap = new ConcurrentHashMap<>();
		parseDirectory(aFolder, aFileFilter, aHandler, fileMap);
		return fileMap;
	}

	/**
	 * @param aFolder
	 * @param aFileFilter
	 * @param aHandler
	 * @param aFileMap
	 * @throws AccelerateException
	 *             thrown by {@link FileHandler#handleDirectory(File)} or
	 *             {@link FileHandler#handleFile(File)}
	 */
	private static void parseDirectory(File aFolder, Predicate<File> aFileFilter, FileHandler aHandler,
			final Map<String, File> aFileMap) throws AccelerateException {
		listFiles(aFolder).parallelStream().filter(aFile -> (aFileFilter == null) ? true : aFileFilter.test(aFile))
				.forEach(aFile -> {
					File newFile = aFile;
					try {
						aFileMap.put(FileUtil.getFilePath(newFile), newFile);
					} catch (@SuppressWarnings("unused") NullPointerException error) {
						System.err.println(aFileMap + "@@" + newFile + "@@" + FileUtil.getFilePath(newFile));
					}

					if (aFile.isDirectory()) {
						if (aHandler != null) {
							newFile = aHandler.handleDirectory(aFile);
						}
						if (newFile != null) {
							parseDirectory(newFile, aFileFilter, aHandler, aFileMap);
						}
					} else if (aHandler != null) {
						newFile = aHandler.handleFile(aFile);
					}
				});
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