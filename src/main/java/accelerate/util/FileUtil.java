package accelerate.util;

import static accelerate.util.AccelerateConstants.DOT_CHAR;
import static accelerate.util.AccelerateConstants.EMPTY_STRING;
import static accelerate.util.AccelerateConstants.NEW_LINE;
import static accelerate.util.StringUtil.join;
import static java.lang.String.valueOf;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import accelerate.exception.AccelerateException;
import accelerate.util.file.DirectoryParser;

/**
 * PUT DESCRIPTION HERE
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
	 * @return Unix Style File Path
	 */
	public static String getPath(File aTargetFile) {
		if (aTargetFile == null) {
			return EMPTY_STRING;
		}

		return StringUtils.cleanPath(aTargetFile.getPath());
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
	 * @param aTargetFile
	 * @param aLevel
	 * @return short path
	 */
	public static String getShortPath(File aTargetFile, int aLevel) {
		if (aTargetFile == null) {
			return EMPTY_STRING;
		}

		StringBuilder buffer = new StringBuilder();
		File tempFile = aTargetFile.getParentFile();

		for (int i = 0; i < aLevel; i++) {
			if (tempFile == null) {
				break;
			}

			buffer.insert(0, tempFile.getName() + "/");
			tempFile = tempFile.getParentFile();
		}

		buffer.append(aTargetFile.getName());
		return buffer.toString();
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
	 *            path to the file or folder of files
	 * @param aNamePattern
	 *            text to be searched in the filename
	 * @return {@link List} of {@link File} that match the search criteria
	 * @throws AccelerateException
	 *             throw by
	 *             {@link DirectoryParser#execute(File, java.util.function.Predicate, accelerate.util.file.DirectoryParser.FileHandler)}
	 */
	public static List<File> findFilesByName(String aRootPath, String aNamePattern) throws AccelerateException {
		return DirectoryParser.execute(new File(aRootPath),
				aFile -> StringUtil.grepCheck(aNamePattern, aFile.getName()), null);
	}

	/**
	 * @param aRootPath
	 *            path to the file or folder of files
	 * @param aSearchExtn
	 *            extension of the file
	 * @return {@link List} of {@link File} that match the search criteria
	 * @throws AccelerateException
	 *             throw by
	 *             {@link DirectoryParser#execute(File, java.util.function.Predicate, accelerate.util.file.DirectoryParser.FileHandler)}
	 */
	public static List<File> findFilesByExtn(String aRootPath, String aSearchExtn) throws AccelerateException {
		return DirectoryParser.execute(new File(aRootPath),
				aFile -> AppUtil.compare(accelerate.util.FileUtil.getFileExtn(aFile), aSearchExtn), null);
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
		return renameFileAndExtn(aFile, aName, getFileExtn(aFile));
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
	 */
	public static File renameFileAndExtn(File aFile, String aName, String aExtn) {
		String extn = aFile.isDirectory() ? EMPTY_STRING : DOT_CHAR + aExtn;
		File target = new File(aFile.getParent(), aName + extn);
		aFile.renameTo(target);

		return target;
	}

	/**
	 * @param aRootFolder
	 * @param aFileFilter
	 * @param aFindPattern
	 * @param aReplacement
	 * @param aGlobalReplace
	 * @return
	 * @throws AccelerateException
	 */
	public static List<File> renameFiles(File aRootFolder, Predicate<File> aFileFilter, final String aFindPattern,
			final String aReplacement, final boolean aGlobalReplace) throws AccelerateException {
		Assert.noNullElements(new Object[] { aRootFolder, aFindPattern, aReplacement, aGlobalReplace },
				"Invalid Input. Required arguments are missing");

		return DirectoryParser.execute(aRootFolder, aFileFilter, new DirectoryParser.FileHandler() {
			@Override
			public File handleFile(File aFile) throws AccelerateException {
				String currentName = aFile.getName();
				String newName = StringUtil.replace(currentName, aFindPattern, aReplacement, aGlobalReplace);

				if (AppUtil.compare(newName, currentName)) {
					return aFile;
				}

				File newFile = new File(aFile.getParentFile(), newName);
				if (newFile.exists()) {
					LOGGER.debug("Cannot rename file [{}] to as file [{}] already exists", aFile, newName);
					return aFile;
				}

				LOGGER.debug("Renaming file [{}] to [{}]", aFile, newName);
				return FileUtil.renameFile(aFile, newName);
			}

			@Override
			public File handleDirectory(File aFolder) throws AccelerateException {
				return handleFile(aFolder);
			}
		});
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

			File splitFile = new File(outputDir, join(name, valueOf(sectionCount), extn));

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
						splitFile = new File(outputDir, join(name, valueOf(sectionCount), extn));

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