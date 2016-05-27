package accelerate.util;

import static accelerate.util.AccelerateConstants.DOT_CHAR;
import static accelerate.util.AccelerateConstants.EMPTY_STRING;
import static accelerate.util.AccelerateConstants.NEW_LINE;
import static accelerate.util.AccelerateConstants.UNIX_PATH_CHAR;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.util.Assert;

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
	 * @throws IOException
	 *             thrown due to multiple IO operations
	 */
	public static String splitFile(File aTargetFile, int aSplitSize, boolean aSplitEqually) throws IOException {
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
		}
	}

	/**
	 * @param aFiles
	 * @return byte array containing the compressed data
	 * @throws IOException
	 */
	public static byte[] zipFiles(File... aFiles) throws IOException {
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
	 * @param aZipFile
	 * @param aFiles
	 * @throws IOException
	 */
	public static void zipFilesToDisk(File aZipFile, File... aFiles) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(aZipFile);) {
			fos.write(zipFiles(aFiles));
		}
	}

	/**
	 * @param aZipName
	 * @param aFiles
	 * @return Zip File
	 * @throws IOException
	 */
	public static File zipFilesToDisk(String aZipName, File... aFiles) throws IOException {
		File targetFile = new File(System.getProperty("java.io.tmpdir"), aZipName);
		zipFilesToDisk(targetFile, aFiles);

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
	 * @param aFile
	 * @return {@link BufferedReader}
	 * @throws IOException
	 */
	public static BufferedReader getBufferedReader(File aFile) throws IOException {
		return new BufferedReader(new FileReader(aFile));
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