package accelerate.util.file;

import static accelerate.util.AccelerateConstants.HYPHEN_CHAR;
import static accelerate.util.AccelerateConstants.UNIX_PATH_CHAR;
import static accelerate.util.AppUtil.isEmpty;
import static accelerate.util.FileUtil.copyFile;
import static accelerate.util.FileUtil.getFileExtn;
import static accelerate.util.FileUtil.getFileName;
import static accelerate.util.FileUtil.getUnixPath;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import accelerate.batch.AccelerateBatch;
import accelerate.batch.AccelerateTask;
import accelerate.databean.AccelerateDataBean;
import accelerate.exception.AccelerateException;
import accelerate.exception.AccelerateRuntimeException;

/**
 * Utility class that provides methods to compare and synchronize 2 directories.
 * Can be used to maintain custom file system backups.
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 21-May-2015
 */
public class DirectorySynchronizer {
	/**
	 * @param aDirSyncInput
	 * @return {@link DirSyncOutput} instance
	 * @throws AccelerateException
	 */
	public static DirSyncOutput compare(DirSyncInput aDirSyncInput) throws AccelerateException {
		try {
			DirSyncOutput dirSyncOutput = new DirSyncOutput();

			validateInput(aDirSyncInput, dirSyncOutput);
			if (dirSyncOutput.errorFlag) {
				return dirSyncOutput;
			}

			DirSyncFileHandler sourceHandler = new DirSyncFileHandler(aDirSyncInput, true);
			DirectoryParser.execute(aDirSyncInput.sourceDir, sourceHandler);

			DirSyncFileHandler targetHandler = new DirSyncFileHandler(aDirSyncInput, false);
			DirectoryParser.execute(aDirSyncInput.targetDir, targetHandler);

			if (aDirSyncInput.ignoreExtensions) {
				dirSyncOutput.message
						.append("ignoreExtensions is active, disabling conflict comparison and synchronization !!");
				aDirSyncInput.copyToSource = false;
				aDirSyncInput.copyToTarget = false;
			}

			prepareOutput(aDirSyncInput, dirSyncOutput, sourceHandler, targetHandler);
			return dirSyncOutput;
		} catch (Exception error) {
			throw new AccelerateException(error);
		}
	}

	/**
	 * @param aDirSyncInput
	 *            {@link DirSyncInput} instance
	 * @param aDirSyncOutput
	 *            {@link DirSyncOutput} instance
	 * @param aThreadPoolCount
	 *            Number of concurrent tasks to run for copying files
	 * @throws AccelerateException
	 */
	public static void synchronize(DirSyncInput aDirSyncInput, DirSyncOutput aDirSyncOutput, int aThreadPoolCount)
			throws AccelerateException {
		if (aDirSyncOutput.errorFlag || !aDirSyncOutput.completedFlag) {
			return;
		}

		int copyCount = 0;

		try {
			AccelerateBatch<DirSyncFileCopyTask> dirSyncBatch = new AccelerateBatch<>("DirectorySynchronizer",
					aThreadPoolCount);
			dirSyncBatch.activate();

			List<DirSyncFileCopyTask> taskList = new ArrayList<>();
			if (aDirSyncInput.copyToTarget) {
				for (File file : aDirSyncOutput.newSourceFiles) {
					taskList.add(new DirSyncFileCopyTask(file, aDirSyncInput.sourceDir, aDirSyncInput.targetDir));
				}

				dirSyncBatch.submitTasks(taskList);
				for (DirSyncFileCopyTask task : taskList) {
					if (task.isCopyResult()) {
						copyCount++;
					}
				}
			}
			aDirSyncOutput.message.append("Missing files copied to Target @ ").append(aDirSyncInput.targetDir.getPath())
					.append(": ").append(copyCount);

			copyCount = 0;
			taskList = new ArrayList<>();
			if (aDirSyncInput.copyToSource) {
				for (File file : aDirSyncOutput.newTargetFiles) {
					taskList.add(new DirSyncFileCopyTask(file, aDirSyncInput.targetDir, aDirSyncInput.sourceDir));
				}

				dirSyncBatch.submitTasks(taskList);
				for (DirSyncFileCopyTask task : taskList) {
					if (task.isCopyResult()) {
						copyCount++;
					}
				}
			}
			aDirSyncOutput.message.append("Missing files copied to Source @ ").append(aDirSyncInput.sourceDir.getPath())
					.append(": ").append(copyCount);

			copyCount = 0;
			taskList = new ArrayList<>();
			if (aDirSyncInput.overwriteTarget) {
				for (Entry<String, ConflictResult> entry : aDirSyncOutput.conflictedFiles.entrySet()) {
					taskList.add(new DirSyncFileCopyTask(entry.getValue()));
				}

				dirSyncBatch.submitTasks(taskList);
				for (DirSyncFileCopyTask task : taskList) {
					if (task.isCopyResult()) {
						copyCount++;
					}
				}
			}
			aDirSyncOutput.message.append("Conflicted files copied to Target @ ")
					.append(aDirSyncInput.targetDir.getPath()).append(": ").append(copyCount);
			copyCount = 0;

			dirSyncBatch.shutdown("SECONDS", 60 * 60 * 2);
		} catch (Exception error) {
			aDirSyncOutput.message.append(error.getMessage());
			throw new AccelerateException(error);
		}
	}

	/**
	 * Method to validate input and create default {@link FileComparer} if
	 * custom implementation is not provided
	 *
	 * @param aDirSyncInput
	 * @param aDirSyncOutput
	 */
	private static void validateInput(DirSyncInput aDirSyncInput, DirSyncOutput aDirSyncOutput) {
		StringBuilder message = aDirSyncOutput.message;

		if (isEmpty(aDirSyncInput.sourceDir) || !aDirSyncInput.sourceDir.isDirectory()) {
			message.append("Source is not a valid directory !!");
			message.append(HYPHEN_CHAR);
			message.append(aDirSyncInput.sourceDir);
			aDirSyncOutput.errorFlag = true;
		}

		if (isEmpty(aDirSyncInput.targetDir) || !aDirSyncInput.targetDir.isDirectory()) {
			message.append("Target is not a valid directory !!");
			message.append(HYPHEN_CHAR);
			message.append(aDirSyncInput.targetDir);
			aDirSyncOutput.errorFlag = true;
		}

		if (aDirSyncOutput.errorFlag) {
			return;
		}

		if (aDirSyncInput.fileConflictChecker != null) {
			return;
		}

		/**
		 * Basic {@link FileComparer} implementation to compare file sizes only
		 */
		aDirSyncInput.fileConflictChecker = (aSourceFile, aTargetFile) -> {
			if (aSourceFile.length() == aTargetFile.length()) {
				return null;
			}

			ConflictResult conflictResult = new ConflictResult();
			conflictResult.sourceFile = aSourceFile;
			conflictResult.targetFile = aTargetFile;
			conflictResult.conflictReason = aSourceFile.length() + ":" + aTargetFile.length();
			return conflictResult;
		};
	}

	/**
	 * @param aDirSyncInput
	 * @param aDirSyncOutput
	 * @param aSourceHandler
	 * @param aTargetHandler
	 */
	private static void prepareOutput(DirSyncInput aDirSyncInput, DirSyncOutput aDirSyncOutput,
			DirSyncFileHandler aSourceHandler, DirSyncFileHandler aTargetHandler) {
		for (Entry<String, File> entry : aSourceHandler.fileMap.entrySet()) {
			if (aTargetHandler.fileMap.get(entry.getKey()) == null) {
				aDirSyncOutput.newSourceFiles.add(entry.getValue());
			}
		}

		for (Entry<String, File> entry : aTargetHandler.fileMap.entrySet()) {
			if (aSourceHandler.fileMap.get(entry.getKey()) == null) {
				aDirSyncOutput.newTargetFiles.add(entry.getValue());
			}
		}

		if (aDirSyncInput.ignoreExtensions) {
			aDirSyncOutput.completedFlag = true;
			return;
		}

		for (Entry<String, File> entry : aSourceHandler.fileMap.entrySet()) {
			File targetFile = aTargetHandler.fileMap.get(entry.getKey());
			if (targetFile == null) {
				continue;
			}

			ConflictResult conflictResult = aDirSyncInput.fileConflictChecker.compareFiles(entry.getValue(),
					targetFile);
			if (conflictResult != null) {
				aDirSyncOutput.conflictedFiles.put(entry.getKey(), conflictResult);
			}
		}

		aDirSyncOutput.completedFlag = true;
	}

	/**
	 * Interface to be implemented by calling methods to perform complex file
	 * comparison for synchronization
	 *
	 * @author Rohit Narayanan
	 * @version 1.0 Initial Version
	 * @since 21-May-2015
	 */
	public interface FileComparer {
		/**
		 * @param aSourceFile
		 * @param aTargetFile
		 * @return <code>null</code>, if files are identical, else
		 *         {@link ConflictResult} containing information on the conflict
		 */
		public ConflictResult compareFiles(File aSourceFile, File aTargetFile);
	}

	/**
	 * This class contains the input passed to {@link DirectorySynchronizer}
	 * class
	 *
	 * @author Rohit Narayanan
	 * @version 1.0 Initial Version
	 * @since 21-May-2015
	 */
	public static class DirSyncInput extends AccelerateDataBean {
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 5336645842239938872L;

		/**
		 * Source directory
		 */
		public File sourceDir = null;

		/**
		 * Target directory
		 */
		public File targetDir = null;

		/**
		 * {@link FileComparer} instance
		 */
		public FileComparer fileConflictChecker = null;

		/**
		 * {@link Set} of extensions that need to be ignored for comparison
		 */
		public Set<String> ignoreExtns = new HashSet<>();

		/**
		 * Flag enable comparison without file extension
		 */
		public boolean ignoreExtensions = false;

		/**
		 * Flag to enable syncing target direcrtory with missing source
		 * directory files
		 */
		public boolean copyToTarget = false;

		/**
		 * Flag to enable syncing source direcrtory with missing target
		 * directory files
		 */
		public boolean copyToSource = false;

		/**
		 * Flag to enable overwriting of target files with source files
		 */
		public boolean overwriteTarget = false;
	}

	/**
	 * This class contains the output received from
	 * {@link DirectorySynchronizer} class
	 *
	 * @author Rohit Narayanan
	 * @version 1.0 Initial Version
	 * @since 21-May-2015
	 */
	public static class DirSyncOutput extends AccelerateDataBean {
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = -8097285988368524955L;

		/**
		 * Flag to indicate whether the process encountered an erro
		 */
		public boolean errorFlag = false;

		/**
		 * Flag to indicate that the output has been prepared
		 */
		public boolean completedFlag = false;

		/**
		 * {@link List} of {@link File} in source directory that are missing in
		 * the target directory
		 */
		public List<File> newSourceFiles = new ArrayList<>();

		/**
		 * {@link List} of {@link File} in target directory that are missing in
		 * the source directory
		 */
		public List<File> newTargetFiles = new ArrayList<>();

		/**
		 * {@link Map} of {@link File} that are conflicting
		 */
		public Map<String, ConflictResult> conflictedFiles = new HashMap<>();

		/**
		 * Message Buffer
		 */
		public StringBuilder message = new StringBuilder();
	}

	/**
	 * A custom {@link DirectoryParser.FileHandler} implementation to handle
	 * file comparison
	 *
	 * @author Rohit Narayanan
	 * @version 1.0 Initial Version
	 * @since 21-May-2015
	 */
	private static class DirSyncFileHandler implements DirectoryParser.FileHandler {
		/**
		 * Root folder for comparison
		 */
		protected File compareRoot = null;

		/**
		 * Index for length of root
		 */
		protected int pathIndex = 0;
		/**
		 * {@link Map} of {@link File} instances read
		 */
		protected Map<String, File> fileMap = null;
		/**
		 * Index for length of root
		 */
		private DirSyncInput dirSyncInput = null;

		/**
		 * @param aDirSyncInput
		 *            {@link DirSyncInput} instance
		 * @param aSrcFlag
		 *            boolean value suggesting src handler if true or target
		 *            handler otherwise
		 */
		public DirSyncFileHandler(DirSyncInput aDirSyncInput, boolean aSrcFlag) {
			this.dirSyncInput = aDirSyncInput;
			this.fileMap = new TreeMap<>();

			if (aSrcFlag) {
				this.pathIndex = this.dirSyncInput.sourceDir.getPath().length();
				this.compareRoot = this.dirSyncInput.targetDir;
			} else {
				this.pathIndex = this.dirSyncInput.targetDir.getPath().length();
				this.compareRoot = this.dirSyncInput.sourceDir;
			}
		}

		/**
		 * @return
		 */
		@Override
		public String getExtnFilter() {
			return null;
		}

		/**
		 * @param aFile
		 * @return {@link File} instance
		 */
		@Override
		public File handleFile(File aFile) {
			if (this.dirSyncInput.ignoreExtns.contains(getFileExtn(aFile))) {
				return aFile;
			}

			StringBuilder key = new StringBuilder();
			key.append(getUnixPath(aFile.getParentFile()).substring(this.pathIndex));
			key.append(UNIX_PATH_CHAR);
			if (this.dirSyncInput.ignoreExtensions) {
				key.append(getFileName(aFile));
			} else {
				key.append(aFile.getName());
			}

			this.fileMap.put(key.toString(), aFile);
			return aFile;
		}

		/**
		 * @param aFolder
		 * @return {@link File} instance
		 */
		@Override
		public File handleDirectory(File aFolder) {
			String shortPath = getUnixPath(aFolder).substring(this.pathIndex);
			File targetFolder = new File(this.compareRoot, shortPath);
			if (!targetFolder.exists()) {
				this.fileMap.put(shortPath, aFolder);
				return null;
			}

			return aFolder;
		}
	}

	/**
	 * This {@link AccelerateDataBean} stores the result of conflict between 2
	 * files
	 *
	 * @author Rohit Narayanan
	 * @version 1.0 Initial Version
	 * @since 21-May-2015
	 */
	public static class ConflictResult extends AccelerateDataBean {
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 5577600876841621558L;

		/**
		 * Source {@link File}
		 */
		public File sourceFile = null;

		/**
		 * Target {@link File}
		 */
		public File targetFile = null;

		/**
		 * Reason for conflict
		 */
		public String conflictReason = null;
	}

	/**
	 * This is a custom extension of {@link AccelerateTask} to copy files in
	 * parallel. It is used in the
	 * {@link DirectorySynchronizer#synchronize(DirSyncInput, DirSyncOutput, int)}
	 * method
	 *
	 * @author Rohit Narayanan
	 * @version 1.0 Initial Version
	 * @since 21-May-2015
	 */
	@SuppressWarnings("javadoc")
	private static class DirSyncFileCopyTask extends AccelerateTask {
		private File sourceFile = null;
		private File sourceRoot = null;
		private File targetRoot = null;
		private ConflictResult conflictResult = null;
		private boolean copyResult = false;

		/**
		 * Constructor 1
		 *
		 * @param aSourceFile
		 * @param aSourceRoot
		 * @param aTargetRoot
		 */
		public DirSyncFileCopyTask(File aSourceFile, File aSourceRoot, File aTargetRoot) {
			this.sourceFile = aSourceFile;
			this.sourceRoot = aSourceRoot;
			this.targetRoot = aTargetRoot;
		}

		/**
		 * Constructor 2
		 *
		 * @param aConflictResult
		 */
		public DirSyncFileCopyTask(ConflictResult aConflictResult) {
			this.conflictResult = aConflictResult;
		}

		/**
		 */
		@Override
		public void execute() {
			int sourceRootIndex = this.sourceRoot.getPath().length();
			File destination = new File(this.targetRoot, getUnixPath(this.sourceFile).substring(sourceRootIndex));
			try {
				if (this.conflictResult != null) {
					this.copyResult = copyFile(this.conflictResult.sourceFile, this.conflictResult.targetFile, true);
				} else {
					this.copyResult = copyFile(this.sourceFile, destination);
				}
			} catch (AccelerateException error) {
				throw new AccelerateRuntimeException(error);
			}
		}

		/**
		 * Getter method for "copyResult" property
		 *
		 * @return copyResult
		 */
		public boolean isCopyResult() {
			return this.copyResult;
		}
	}
}