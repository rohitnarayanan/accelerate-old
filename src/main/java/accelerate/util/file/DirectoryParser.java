package accelerate.util.file;

import static accelerate.util.AppUtil.compare;
import static accelerate.util.FileUtil.getFileExtn;
import static accelerate.util.FileUtil.getFileList;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.io.File;

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
	 * @param aFolderPath
	 * @param aHandler
	 */
	public static void execute(String aFolderPath, FileHandler aHandler) {
		execute(new File(aFolderPath), aHandler);
	}

	/**
	 * @param aFolder
	 * @param aHandler
	 */
	public static void execute(File aFolder, FileHandler aHandler) {
		File rootDirectory = aFolder;
		FileHandler handler = aHandler;

		if ((rootDirectory == null) || !rootDirectory.exists()) {
			System.out.println("Invalid Root: " + rootDirectory);
			return;
		}

		if (rootDirectory.isFile()) {
			if (isEmpty(handler.getExtnFilter()) || compare(getFileExtn(rootDirectory), handler.getExtnFilter())) {
				handler.handleFile(rootDirectory);
			}
			return;
		}

		parseDirectory(rootDirectory, aHandler);
	}

	/**
	 * @param aFolder
	 * @param aHandler
	 */
	private static void parseDirectory(File aFolder, FileHandler aHandler) {
		if (aFolder == null) {
			return;
		}

		if (!aFolder.exists()) {
			System.out.println("Missing Folder: " + aFolder);
			return;
		}

		for (File file : getFileList(aFolder)) {
			if (file.isDirectory()) {
				File folder = aHandler.handleDirectory(file);
				parseDirectory(folder, aHandler);
			} else {
				if (isEmpty(aHandler.getExtnFilter()) || compare(getFileExtn(file), aHandler.getExtnFilter())) {
					aHandler.handleFile(file);
				}
			}
		}
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
		 * @return File Extn that should be targeted. null if all files should
		 *         be processed
		 */
		public String getExtnFilter();

		/**
		 * @param aFile
		 * @return {@link File} Instance, Will differ if path modifications were
		 *         made
		 */
		public File handleFile(File aFile);

		/**
		 * @param aFolder
		 * @return {@link File} Instance, Will differ if path modifications were
		 *         made
		 */
		public File handleDirectory(File aFolder);
	}
}