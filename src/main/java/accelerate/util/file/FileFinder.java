package accelerate.util.file;

import static accelerate.util.FileUtil.getFileName;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import accelerate.exception.AccelerateException;
import accelerate.util.CollectionUtil;
import accelerate.util.StringUtil;

/**
 * This utility class provides the functionality to search for a file. It allows
 * the user to search by exact name, based on a pattern or by extension.
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Oct 18, 2010
 */
public class FileFinder {
	/**
	 * @param aRootPath
	 *            path to the file or folder of files
	 * @param aNamePattern
	 *            text to be searched in the filename
	 * @return {@link List} of {@link File} that match the search criteria
	 * @throws AccelerateException
	 */
	public static List<File> find(String aRootPath, String aNamePattern) throws AccelerateException {
		FileFinderHandler handler = new FileFinderHandler(aNamePattern, false);
		DirectoryParser.execute(aRootPath, handler);

		return handler.searchResults;
	}

	/**
	 * @param aRootPath
	 *            path to the file or folder of files
	 * @param aSearchExtn
	 *            extension of the file
	 * @return {@link List} of {@link File} that match the search criteria
	 * @throws AccelerateException
	 */
	public static List<File> findByExtn(String aRootPath, String aSearchExtn) throws AccelerateException {
		FileFinderHandler handler = new FileFinderHandler(aSearchExtn, true);
		DirectoryParser.execute(aRootPath, handler);

		return handler.searchResults;
	}

	/**
	 * A custom {@link DirectoryParser.FileHandler} implementation to find files
	 *
	 * @author Rohit Narayanan
	 * @version 1.0 Initial Version
	 * @since 21-May-2015
	 */
	private static class FileFinderHandler implements DirectoryParser.FileHandler {
		/**
		 * {@link List} of files matching the search criteria
		 */
		protected List<File> searchResults = new ArrayList<>();
		/**
		 * Flag to indicate whether string should be matched against the file's
		 * extn(true) or name(false)
		 */
		private boolean searchExtn = false;
		/**
		 * pattern to be matched
		 */
		private String searchString = null;

		/**
		 * @param aSearchString
		 * @param aSearchExtn
		 */
		public FileFinderHandler(String aSearchString, boolean aSearchExtn) {
			this.searchString = StringUtil.lower(aSearchString);
			this.searchExtn = aSearchExtn;
		}

		/**
		 * @return
		 */
		@Override
		public String getExtnFilter() {
			if (this.searchExtn) {
				return this.searchString;
			}

			return null;
		}

		/**
		 * @param aFile
		 * @return {@link File} instance
		 */
		@Override
		public File handleFile(File aFile) {
			if (this.searchExtn) {
				this.searchResults.add(aFile);
			} else if (StringUtil.grepCheck(this.searchString,
					CollectionUtil.toList(getFileName(aFile).toLowerCase()))) {
				this.searchResults.add(aFile);
			}

			return aFile;
		}

		/**
		 * @param aFolder
		 * @return {@link File} instance
		 */
		@Override
		public File handleDirectory(File aFolder) {
			return handleFile(aFolder);
		}
	}
}