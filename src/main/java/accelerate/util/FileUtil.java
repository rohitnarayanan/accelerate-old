package accelerate.util;

import static accelerate.util.AccelerateConstants.DOT_CHAR;
import static accelerate.util.AccelerateConstants.EMPTY_STRING;
import static accelerate.util.AccelerateConstants.NEW_LINE;
import static accelerate.util.AccelerateConstants.UNIX_PATH_CHAR;
import static accelerate.util.AppUtil.compare;
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
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import accelerate.exception.AccelerateException;

/**
 * PUT DESCRIPTION HERE
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Oct 15, 2009
 */
public final class FileUtil {

	/**
	 * hidden constructor
	 */
	private FileUtil() {
	}

	/**
	 * This method deletes the given directory.
	 *
	 * @param aDirPath
	 *            - directory path
	 * @return message string
	 * @throws IOException
	 */
	public static String deleteDirectory(String aDirPath) throws IOException {
		return deleteDirectory(new File(aDirPath));
	}

	/**
	 * This method deletes the given directory.
	 *
	 * @param aDirFile
	 *            - directory file
	 * @return message string
	 * @throws IOException
	 */
	public static String deleteDirectory(File aDirFile) throws IOException {
		StringBuilder message = new StringBuilder();
		if (aDirFile == null) {
			message.append("Null Directory !!");
		} else if (!aDirFile.exists()) {
			message.append("Directory Not Found: ").append(aDirFile);
		} else {
			FileUtils.deleteDirectory(aDirFile);
			message.append("Directory Deleted: ").append(aDirFile);
		}

		return message.toString();
	}

	/**
	 * This method deletes files from a directory.
	 *
	 * @param aDirPath
	 *            - directory path
	 * @return message string
	 * @throws IOException
	 */
	public static String cleanDirectory(String aDirPath) throws IOException {
		return cleanDirectory(new File(aDirPath));
	}

	/**
	 * This method deletes files from a directory.
	 *
	 * @param aDirFile
	 *            - directory file
	 * @return message string
	 * @throws IOException
	 */
	public static String cleanDirectory(File aDirFile) throws IOException {
		StringBuilder message = new StringBuilder();
		message.append("Cleaning directory - ").append(aDirFile);
		message.append(deleteDirectory(aDirFile));
		message.append("Creating directory - ").append(aDirFile.mkdir());

		return message.toString();
	}

	/**
	 * This method deletes the given file.
	 *
	 * @param aFilePath
	 *            - path of the file to be deleted
	 * @return message string
	 */
	public static String deleteFile(String aFilePath) {
		return deleteFiles(new File(aFilePath));
	}

	/**
	 * This method deletes files from a directory.
	 *
	 * @param aDirPath
	 *            - directory path
	 * @param aFileNames
	 *            - list of file names
	 * @return message string
	 */
	public static String deleteFilesFromDir(String aDirPath, String... aFileNames) {
		return deleteFilesFromDir(new File(aDirPath), aFileNames);
	}

	/**
	 * This method deletes files from a directory
	 *
	 * @param aDirFile
	 *            - directory file
	 * @param aFileNames
	 *            - list of file names
	 * @return message string
	 */
	public static String deleteFilesFromDir(File aDirFile, String... aFileNames) {
		if (aFileNames == null) {
			return deleteFiles(new File[] {});
		}

		File[] fileList = new File[aFileNames.length];
		for (int idx = 0; idx < aFileNames.length; idx++) {
			fileList[idx] = new File(aDirFile, aFileNames[idx]);
		}

		return deleteFiles(fileList);
	}

	/**
	 * This method deletes the given files.
	 *
	 * @param aFilePaths
	 *            - full path of files
	 * @return message string
	 */
	public static String deleteFiles(String... aFilePaths) {
		if (aFilePaths == null) {
			return deleteFiles(new File[] {});
		}

		File[] fileList = new File[aFilePaths.length];
		for (int idx = 0; idx < aFilePaths.length; idx++) {
			fileList[idx] = new File(aFilePaths[idx]);
		}

		return deleteFiles(fileList);
	}

	/**
	 * This method deletes the given files.
	 *
	 * @param aFiles
	 *            - list of files
	 * @return message string
	 */
	public static String deleteFiles(File... aFiles) {
		if (isEmpty(aFiles)) {
			return "Empty File List !! Nothing Deleted.";
		}

		StringBuilder message = new StringBuilder();
		int count = 0;
		for (File file : aFiles) {
			if (file.delete()) {
				count++;
			}
		}

		message.append("Deleted ").append(count).append(" of ").append(aFiles.length).append(" files !!");

		return message.toString();
	}

	/**
	 * This method renames the given file to new name.
	 *
	 * @param aFile
	 *            - File to be renamed
	 * @param aName
	 *            - New Name to be given 'without the extension'.
	 * @return array of file names
	 */
	public static File renameFile(File aFile, String aName) {
		return renameFile(aFile, aName, getFileExtn(aFile));
	}

	/**
	 * This method renames the given file to new name.
	 *
	 * @param aFile
	 *            - File to be renamed
	 * @param aName
	 *            - New Name to be given.
	 * @param aExtn
	 *            - New Extn to be given.
	 * @return array of file names
	 */
	public static File renameFile(File aFile, String aName, String aExtn) {
		String extn = aFile.isDirectory() ? EMPTY_STRING : DOT_CHAR + aExtn;
		File target = new File(aFile.getParent(), aName + extn);
		aFile.renameTo(target);

		return target;
	}

	/**
	 * This method returns a sorted list of files present at the given path.
	 *
	 * @param aDirPath
	 *            - Path to the directory
	 * @return array of file names
	 */
	public static File[] getFileList(String aDirPath) {
		return getFileList(new File(aDirPath));
	}

	/**
	 * This method returns a sorted list of files in the given Directory.
	 *
	 * @param aFolder
	 *            - Path to the directory
	 * @return array of file names
	 */
	public static File[] getFileList(File aFolder) {
		if (aFolder.exists() && aFolder.isDirectory()) {
			File[] fileList = aFolder.listFiles();
			Arrays.sort(fileList, (aFile1, aFile2) -> aFile1.getName().compareTo(aFile2.getName()));

			return fileList;
		}

		return new File[] {};
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
	public static String getUnixPath(File aTargetFile) {
		Pattern pattern = UtilCache.getPattern("\\\\");
		return pattern.matcher(aTargetFile.getPath()).replaceAll(UNIX_PATH_CHAR);
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
	 * @param source
	 * @param destination
	 * @return true, if copied successfully
	 * @throws IOException
	 */
	public static boolean copyFile(File source, File destination) throws IOException {
		return copyFile(source, destination, false);
	}

	/**
	 * @param source
	 * @param destination
	 * @param overwrite
	 * @return true, if copied successfully
	 * @throws IOException
	 */
	public static boolean copyFile(File source, File destination, boolean overwrite) throws IOException {
		if (destination.exists() && !overwrite) {
			String message = "Destination (" + destination.getPath() + ") exists and overwrite is disabled !!";
			throw new AccelerateException(message);
		}

		if (!source.exists()) {
			String message = "Source (" + source.getPath() + ") does not exist !!";
			throw new AccelerateException(message);
		}

		if (source.isDirectory() && destination.isFile()) {
			String message = "Cannot copy source folder (" + source.getPath() + "), destination ("
					+ destination.getPath() + ") is a file !!";
			throw new AccelerateException(message);
		}

		File targetFile = destination;
		if (destination.isDirectory()) {
			if (source.isFile() || !compare(source.getName(), destination.getName())) {
				targetFile = new File(destination, source.getName());
			}
		}

		destination.getParentFile().mkdirs();
		if (source.isDirectory()) {
			FileUtils.copyDirectory(source, targetFile);
		} else if (source.isFile()) {
			FileUtils.copyFile(source, targetFile);
		}

		return targetFile.exists();
	}

	/**
	 * @param aTargetFile
	 * @param aSplitSize
	 * @param aSplitEqually
	 * @return message
	 * @throws AccelerateException
	 * @throws IOException
	 */
	public static String splitFile(File aTargetFile, int aSplitSize, boolean aSplitEqually) throws IOException {
		StringBuilder message = new StringBuilder();

		try (BufferedReader errorLogReader = new BufferedReader(new FileReader(aTargetFile));) {
			long l = System.currentTimeMillis();
			String name = getFileName(aTargetFile);
			String extn = getFileExtn(aTargetFile);

			File outputDir = new File(aTargetFile.getParentFile(), aTargetFile.getName() + ".split");
			outputDir.mkdirs();
			message.append(cleanDirectory(outputDir)).append(NEW_LINE);

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
		}
	}

	/**
	 * @param aZipName
	 * @param aFiles
	 * @return Zip File
	 * @throws IOException
	 */
	public static File zipFiles(String aZipName, File... aFiles) throws IOException {
		File targetFile = new File(System.getProperty("java.io.tmpdir"), aZipName);
		zipFiles(targetFile, aFiles);

		return targetFile;
	}

	/**
	 * @param aZipFile
	 * @param aFiles
	 * @throws IOException
	 */
	public static void zipFiles(File aZipFile, File... aFiles) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(aZipFile);) {
			fos.write(getZipData(aFiles));
		}
	}

	/**
	 * @param aFiles
	 * @return byte array containing the compressed data
	 * @throws IOException
	 */
	public static byte[] getZipData(File... aFiles) throws IOException {
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
		}
	}

	/**
	 * @param aFilePath
	 * @param aData
	 * @return {@link File} instance, the data was written to
	 * @throws IOException
	 */
	public static File writeStream(String aFilePath, byte[] aData) throws IOException {
		File targetFile = new File(aFilePath);
		writeStream(targetFile, aData);

		return targetFile;
	}

	/**
	 * @param aTargetFile
	 * @param aData
	 * @throws IOException
	 */
	public static void writeStream(File aTargetFile, byte[] aData) throws IOException {
		try (FileOutputStream outputStream = new FileOutputStream(aTargetFile);) {
			outputStream.write(aData);
		}
	}

	/**
	 * @param aTargetPath
	 * @return {@link ByteArrayOutputStream} instance, the data was read into
	 * @throws IOException
	 */
	public static byte[] readStream(String aTargetPath) throws IOException {
		return readStream(new File(aTargetPath));
	}

	/**
	 * @param aTargetFile
	 * @return {@link ByteArrayOutputStream} instance, the data was read into
	 * @throws IOException
	 */
	public static byte[] readStream(File aTargetFile) throws IOException {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				FileInputStream inputStream = new FileInputStream(aTargetFile);) {
			IOUtils.copy(inputStream, outputStream);
			return outputStream.toByteArray();
		}
	}

	/**
	 * @param aFilePath
	 * @return {@link BufferedReader}
	 * @throws IOException
	 */
	public static BufferedReader getBufferedReader(String aFilePath) throws IOException {
		return getBufferedReader(new File(aFilePath));
	}

	/**
	 * @param aFile
	 * @return {@link BufferedReader}
	 * @throws IOException
	 */
	public static BufferedReader getBufferedReader(File aFile) throws IOException {
		return new BufferedReader(new FileReader(aFile));
	}

	/**
	 * @param aFilePath
	 * @return {@link BufferedWriter}
	 * @throws IOException
	 */
	public static BufferedWriter getBufferedWriter(String aFilePath) throws IOException {
		return getBufferedWriter(new File(aFilePath));
	}

	/**
	 * @param aFile
	 * @return {@link BufferedWriter}
	 * @throws IOException
	 */
	public static BufferedWriter getBufferedWriter(File aFile) throws IOException {
		return new BufferedWriter(new FileWriter(aFile));
	}
}