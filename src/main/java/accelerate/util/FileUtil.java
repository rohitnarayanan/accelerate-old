package accelerate.util;

import static accelerate.util.AccelerateConstants.DOT_CHAR;
import static accelerate.util.AccelerateConstants.EMPTY_STRING;
import static accelerate.util.AccelerateConstants.NEW_LINE;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import accelerate.exception.AccelerateException;

/**
 * Utility class with methods for {@link File} and {@link Path} operations
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Oct 15, 2009
 */
public final class FileUtil {
	/**
	 * 
	 */
	static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * hidden constructor
	 */
	private FileUtil() {
	}

	/**
	 * Method to consistenly return path in unix format with '/' separator
	 * instead of '\\' used in windows.
	 * 
	 * @param aPath
	 * @return file name
	 */
	public static String getFilePath(Path aPath) {
		if (aPath == null) {
			return EMPTY_STRING;
		}

		return org.springframework.util.StringUtils.cleanPath(aPath.toString());
	}

	/**
	 * Method to consistenly return path of the file in unix format with '/'
	 * separator instead of '\\' used in windows.
	 * 
	 * @param aTargetFile
	 * @return file name
	 */
	public static String getFilePath(File aTargetFile) {
		if (aTargetFile == null) {
			return EMPTY_STRING;
		}

		return org.springframework.util.StringUtils.cleanPath(aTargetFile.getPath());
	}

	/**
	 * @param aTargetFile
	 * @return file name
	 */
	public static String getFileNameWithExtn(Path aTargetFile) {
		if (aTargetFile == null) {
			return EMPTY_STRING;
		}

		return aTargetFile.toFile().getName();
	}

	/**
	 * @param aTargetFile
	 * @return file name
	 */
	public static String getFileName(Path aTargetFile) {
		if (aTargetFile == null) {
			return EMPTY_STRING;
		}

		return getFileName(aTargetFile.toFile());
	}

	/**
	 * @param aTargetFile
	 * @return file name
	 */
	public static String getFileName(File aTargetFile) {
		if (aTargetFile == null) {
			return EMPTY_STRING;
		}

		int index = aTargetFile.getName().lastIndexOf(DOT_CHAR);
		if (index < 0) {
			return aTargetFile.getName();
		}

		return aTargetFile.getName().substring(0, index);
	}

	/**
	 * @param aTargetFile
	 * @return file extension
	 */
	public static String getFileExtn(File aTargetFile) {
		if ((aTargetFile == null) || aTargetFile.isDirectory()) {
			return EMPTY_STRING;
		}

		int index = aTargetFile.getName().lastIndexOf(DOT_CHAR);
		if (index < 0) {
			return EMPTY_STRING;
		}

		return aTargetFile.getName().substring(index + 1);
	}

	/**
	 * @param aTargetFile
	 * @param aLevel
	 * @return Parent File
	 */
	public static File getParent(File aTargetFile, int aLevel) {
		if (aTargetFile == null) {
			return null;
		}

		if (aLevel < 1) {
			return aTargetFile;
		}

		int parentLevel = 1;
		File parentFile = aTargetFile.getParentFile();

		while (parentFile != null) {
			if (aLevel == parentLevel) {
				return parentFile;
			}

			parentLevel++;
			parentFile = parentFile.getParentFile();
		}

		return null;
	}

	/**
	 * @param aTargetFile
	 * @param aLevel
	 * @return Parent File Name
	 */
	public static String getParentName(File aTargetFile, int aLevel) {
		return getParent(aTargetFile, aLevel).getName();
	}

	/**
	 * @param aFile
	 * @param aRoot
	 * @return short path
	 */
	public static String getRelativePath(Path aFile, Path aRoot) {
		if (aFile == null) {
			return EMPTY_STRING;
		}

		int rootNameCount = (aRoot == null) ? 0 : aRoot.getNameCount();
		return getFilePath(aFile.subpath(rootNameCount, aFile.getNameCount()));
	}

	/**
	 * Overloaded {@link #getRelativePath(Path, Path)} for file arguments
	 * 
	 * @param aRelativePath
	 * @param aBasePath
	 * @return
	 */
	public static String getRelativePath(File aRelativePath, File aBasePath) {
		return getRelativePath(Paths.get(aRelativePath.toURI()), Paths.get(aBasePath.toURI()));
	}

	/**
	 * @param aSource
	 * @param aDestination
	 * @param aOverwrite
	 * @return copy status
	 */
	public static boolean copyViaOS(File aSource, File aDestination, Boolean aOverwrite) {
		Assert.noNullElements(new Object[] { aSource, aDestination }, "Invalid Input. All arguments are required");

		Assert.state(aSource.exists(), "Source does not exist");
		Assert.state((aOverwrite || !aDestination.exists()), "Destination exists");

		if (aDestination.exists()) {
			if (aSource.isFile()) {
				Assert.state(aDestination.isFile(), "Source is a file but destination exists as a directory");
			} else {
				Assert.state(aDestination.isDirectory(), "Source is a directory but destination exists as a file");
			}
		}

		String copyCommand = null;
		if (SystemUtils.IS_OS_WINDOWS) {
			copyCommand = StringUtils.join("cmd /C ", (aSource.isDirectory() ? "XCOPY \"" : "COPY \""),
					aSource.getPath(), "\" \"", aDestination.getPath(),
					(aSource.isDirectory() ? "\\\" /E /Q /R /Y" : "\""));
		} else {
			copyCommand = StringUtils.join("cp -fr '", getFilePath(aSource), "' '", getFilePath(aDestination), "'");
		}

		aDestination.getParentFile().mkdirs();
		AppUtil.executeOSCommand(copyCommand, null, null);
		return aDestination.exists();
	}

	/**
	 * This method returns a sorted list of files in the given Directory.
	 *
	 * @param aFolder
	 *            - Path to the directory
	 * @return array of file names
	 */
	public static List<File> listFiles(File aFolder) {
		if (aFolder.exists() && aFolder.isDirectory()) {
			return Arrays.stream(aFolder.listFiles())
					.sorted((aFile1, aFile2) -> aFile1.getName().compareTo(aFile2.getName()))
					.collect(Collectors.toList());
		}

		return Collections.emptyList();
	}

	/**
	 * @param aRootPath
	 * @param aSelector
	 * @param aPreVisitDirectory
	 * @param aPostVisitDirectory
	 * @param aVisitFile
	 * @return
	 * @throws AccelerateException
	 *             {@link IOException} thrown by
	 *             {@link Files#walkFileTree(Path, FileVisitor)}
	 */
	public static Map<String, File> walkFileTree(String aRootPath,
			final Function<File, FileVisitResult> aPreVisitDirectory,
			final Function<File, FileVisitResult> aPostVisitDirectory, final Function<File, FileVisitResult> aVisitFile,
			final BiFunction<File, FileVisitResult, Boolean> aSelector) throws AccelerateException {
		final Map<String, File> fileMap = new TreeMap<>();
		final Path sourcePath = new File(aRootPath).toPath();

		try {
			Files.walkFileTree(sourcePath, new FileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path aDir, BasicFileAttributes aAttrs) {
					if (AppUtil.compare(FileUtil.getFilePath(aDir), FileUtil.getFilePath(sourcePath))) {
						return FileVisitResult.CONTINUE;
					}

					FileVisitResult visitResult = FileVisitResult.CONTINUE;
					if (aPreVisitDirectory != null) {
						visitResult = aPreVisitDirectory.apply(aDir.toFile());
					}

					if (aSelector != null && aSelector.apply(aDir.toFile(), visitResult)) {
						fileMap.put(getRelativePath(aDir, sourcePath), aDir.toFile());
					}

					return visitResult;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path aDir, IOException aError) throws IOException {
					if (aError != null) {
						throw aError;
					}

					if (aPostVisitDirectory != null) {
						return aPostVisitDirectory.apply(aDir.toFile());
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path aFile, BasicFileAttributes aAttrs) {
					FileVisitResult visitResult = FileVisitResult.CONTINUE;
					if (aVisitFile != null) {
						visitResult = aVisitFile.apply(aFile.toFile());
					}

					if (aSelector != null && aSelector.apply(aFile.toFile(), visitResult)) {
						fileMap.put(getRelativePath(aFile, sourcePath), aFile.toFile());
					}

					return visitResult;
				}

				@Override
				public FileVisitResult visitFileFailed(Path aFile, IOException aError) throws IOException {
					if (aError != null) {
						throw aError;
					}

					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException error) {
			throw new AccelerateException(error);
		}

		return fileMap;
	}

	/**
	 * @param aRootPath
	 *            path to the file or folder of files
	 * @param aNamePattern
	 *            text to be searched in the filename
	 * @return {@link Map} of {@link File} that match the search criteria
	 * @throws AccelerateException
	 *             thrown by
	 *             {@link #walkFileTree(String, Function, Function, Function, BiFunction)}
	 */
	public static Map<String, File> findFilesByName(String aRootPath, String aNamePattern) throws AccelerateException {
		return walkFileTree(aRootPath, null, null, null,
				(aFile, aFileVisitResult) -> StringUtil.grepCheck(aNamePattern, aFile.getName()));
	}

	/**
	 * @param aRootPath
	 *            path to the file or folder of files
	 * @param aSearchExtn
	 *            extension of the file
	 * @return {@link Map} of {@link File} that match the search criteria
	 * @throws AccelerateException
	 *             thrown by
	 *             {@link #walkFileTree(String, Function, Function, Function, BiFunction)}
	 */
	public static Map<String, File> findFilesByExtn(String aRootPath, String aSearchExtn) throws AccelerateException {
		return walkFileTree(aRootPath, null, null, null,
				(aFile, aFileVisitResult) -> AppUtil.compare(accelerate.util.FileUtil.getFileExtn(aFile), aSearchExtn));
	}

	/**
	 * This method renames the given file to new name preserving the extension
	 *
	 * @param aFile
	 *            - File to be renamed
	 * @param aName
	 *            - New Name to be given 'without the extension'.
	 * @return array of file names
	 */
	public static File renameFile(File aFile, String aName) {
		return renameFileWithExtn(aFile, aName, getFileExtn(aFile));
	}

	/**
	 * This method renames the given file to new name with the provided
	 * extension
	 *
	 * @param aFile
	 *            - File to be renamed
	 * @param aName
	 *            - New Name to be given.
	 * @param aExtn
	 *            - New Extn to be given. It is ignored in case of directory
	 *            rename
	 * @return array of file names
	 * 
	 * @throws AccelerateException
	 *             {@link IOException} thrown by
	 *             {@link Files#move(Path, Path, CopyOption...)}
	 */
	public static File renameFileWithExtn(File aFile, String aName, String aExtn) throws AccelerateException {
		String extn = aFile.isDirectory() ? EMPTY_STRING : DOT_CHAR + aExtn;
		File target = new File(aFile.getParent(), aName + extn);

		try {
			Files.move(aFile.toPath(), target.toPath());
		} catch (IOException error) {
			throw new AccelerateException(error);
		}

		return target;
	}

	/**
	 * @param aRootPath
	 * @param aFileFilter
	 * @param aFindPattern
	 * @param aReplacement
	 * @return
	 */
	public static Map<String, String> renameFiles(String aRootPath, final Predicate<File> aFileFilter,
			final String aFindPattern, final String aReplacement) {
		Assert.noNullElements(new Object[] { aRootPath, aFindPattern, aReplacement },
				"Invalid Input. Required arguments are missing");

		final Path rootPath = new File(aRootPath).toPath();
		final Map<String, String> fileMap = new TreeMap<>();
		final Function<File, FileVisitResult> renameFunction = (aFile -> {
			if (!aFileFilter.test(aFile)) {
				return FileVisitResult.CONTINUE;
			}

			String currentName = aFile.getName();
			String newName = StringUtils.replacePattern(currentName, aFindPattern, aReplacement);

			if (!AppUtil.compare(newName, currentName)) {
				File newFile = new File(aFile.getParentFile(), newName);
				if (newFile.exists()) {
					LOGGER.debug("Cannot rename file [{}] to as file [{}] already exists", aFile, newName);
				}

				LOGGER.debug("Renaming file [{}] to [{}]", aFile, newName);
				FileUtil.renameFile(aFile, newName);
			}

			fileMap.put(getRelativePath(aFile.toPath(), rootPath), newName);
			return FileVisitResult.CONTINUE;
		});

		walkFileTree(aRootPath, null, renameFunction, renameFunction, null);
		return fileMap;
	}

	/**
	 * This method deletes the given files.
	 *
	 * @param aFileList
	 *            - list of files
	 * @return {@link Map} with result of delete operation for each file
	 */
	public static Map<String, Boolean> deleteFiles(File... aFileList) {
		Assert.notNull(aFileList, "Invalid Call. File list cannot be emtpy");
		Assert.noNullElements(aFileList, "Name of files to be deleted are required");

		return Arrays.stream(aFileList).collect(Collectors.toMap(aFile -> aFile.getName(), aFile -> aFile.delete()));
	}

	/**
	 * This method deletes files from a directory
	 *
	 * @param aDirFile
	 *            - directory file
	 * @param aFileNames
	 *            - list of file names
	 * @return {@link Map} with result of delete operation for each file
	 */
	public static Map<String, Boolean> deleteFilesFromDir(File aDirFile, String... aFileNames) {
		Assert.noNullElements(new Object[] { aDirFile, aFileNames }, "Invalid Call. All arguments are required");
		Assert.noNullElements(aFileNames, "Name of files to be deleted are required");

		return Arrays.stream(aFileNames).map(fileName -> new File(aDirFile, fileName))
				.collect(Collectors.toMap(aFile -> aFile.getName(), aFile -> aFile.delete()));
	}

	/**
	 * @param aTargetFile
	 * @param aSplitSize
	 * @param aSplitEqually
	 * @return message
	 * @throws AccelerateException
	 */
	public static String splitFile(File aTargetFile, int aSplitSize, boolean aSplitEqually) throws AccelerateException {
		StringBuilder message = new StringBuilder();

		try (BufferedReader errorLogReader = new BufferedReader(new FileReader(aTargetFile));) {
			long l = System.currentTimeMillis();
			String name = getFileName(aTargetFile);
			String extn = getFileExtn(aTargetFile);

			File outputDir = new File(aTargetFile.getParentFile(), aTargetFile.getName() + ".split");
			outputDir.mkdirs();
			message.append("Clearing output folder").append(JSONUtil.serialize(deleteFiles(outputDir.listFiles())))
					.append(NEW_LINE);

			String inputLine = null;
			long byteCount = 0;
			long sectionCount = 0;
			long sectionSize = aSplitSize;
			if (aSplitEqually) {
				sectionSize = aTargetFile.length() / aSplitSize;
			}

			File splitFile = new File(outputDir, StringUtils.join(new Object[] { name, sectionCount, extn }, DOT_CHAR));

			@SuppressWarnings("resource")
			BufferedWriter writer = new BufferedWriter(new FileWriter(splitFile));

			try {
				while ((inputLine = errorLogReader.readLine()) != null) {
					if (isEmpty(inputLine)) {
						continue;
					}

					byteCount += inputLine.getBytes().length;
					if (byteCount >= sectionSize) {
						IOUtils.closeQuietly(writer);

						message.append("file part completed: ");
						message.append(splitFile.getName()).append(NEW_LINE);

						sectionCount++;
						byteCount = 0;
						splitFile = new File(outputDir,
								StringUtils.join(new Object[] { name, sectionCount, extn }, DOT_CHAR));

						writer = new BufferedWriter(new FileWriter(splitFile));
					}

					writer.write(inputLine);
					writer.newLine();
				}
			} finally {
				IOUtils.closeQuietly(writer);
			}

			message.append("file part completed: ");
			message.append(splitFile.getName()).append(NEW_LINE).append(NEW_LINE);
			message.append("process finished in ").append(System.currentTimeMillis() - l).append("ms").append(NEW_LINE);
			return message.toString();
		} catch (Exception error) {
			return AccelerateException.checkAndThrow(error);
		}
	}

	/**
	 * @param aFiles
	 * @return byte array containing the compressed data
	 * @throws AccelerateException
	 */
	public static byte[] zipFiles(File... aFiles) throws AccelerateException {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ZipOutputStream zipStream = new ZipOutputStream(outputStream);) {
			zipStream.setLevel(9);

			for (File file : aFiles) {
				try (FileInputStream inputStream = new FileInputStream(file);) {
					zipStream.putNextEntry(new ZipEntry(file.getName()));
					IOUtils.copy(inputStream, zipStream);
					zipStream.closeEntry();
				}
			}

			zipStream.finish();
			return outputStream.toByteArray();
		} catch (Exception error) {
			return AccelerateException.checkAndThrow(error);
		}
	}

	/**
	 * @param aZipFile
	 * @param aFiles
	 * @throws AccelerateException
	 */
	public static void zipFilesToDisk(File aZipFile, File... aFiles) throws AccelerateException {
		try (FileOutputStream fos = new FileOutputStream(aZipFile);) {
			fos.write(zipFiles(aFiles));
		} catch (Exception error) {
			AccelerateException.checkAndThrow(error);
		}
	}

	/**
	 * @param aZipName
	 * @param aFiles
	 * @return Zip File
	 * @throws AccelerateException
	 */
	public static File zipFilesToDisk(String aZipName, File... aFiles) throws AccelerateException {
		File targetFile = new File(System.getProperty("java.io.tmpdir"), aZipName);
		zipFilesToDisk(targetFile, aFiles);

		return targetFile;
	}

	/**
	 * @param aTargetFile
	 * @param aData
	 * @throws AccelerateException
	 */
	public static void writeStream(File aTargetFile, byte[] aData) throws AccelerateException {
		try (FileOutputStream outputStream = new FileOutputStream(aTargetFile);) {
			outputStream.write(aData);
		} catch (Exception error) {
			AccelerateException.checkAndThrow(error);
		}
	}

	/**
	 * @param aTargetFile
	 * @return {@link ByteArrayOutputStream} instance, the data was read into
	 * @throws AccelerateException
	 */
	public static byte[] readStream(File aTargetFile) throws AccelerateException {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				FileInputStream inputStream = new FileInputStream(aTargetFile);) {
			IOUtils.copy(inputStream, outputStream);
			return outputStream.toByteArray();
		} catch (Exception error) {
			return AccelerateException.checkAndThrow(error);
		}
	}

	/**
	 * @param aFile
	 * @return {@link BufferedReader}
	 * @throws AccelerateException
	 */
	public static BufferedReader getBufferedReader(File aFile) throws AccelerateException {
		try {
			return new BufferedReader(new FileReader(aFile));
		} catch (Exception error) {
			return AccelerateException.checkAndThrow(error);
		}
	}

	/**
	 * @param aFile
	 * @return {@link BufferedWriter}
	 * @throws AccelerateException
	 */
	public static BufferedWriter getBufferedWriter(File aFile) throws AccelerateException {
		try {
			return new BufferedWriter(new FileWriter(aFile));
		} catch (Exception error) {
			return AccelerateException.checkAndThrow(error);
		}
	}
}