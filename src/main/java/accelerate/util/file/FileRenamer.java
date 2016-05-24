package accelerate.util.file;

import static accelerate.util.AccelerateConstants.COMMA_CHAR;
import static accelerate.util.AccelerateConstants.EMPTY_STRING;
import static accelerate.util.AccelerateConstants.HYPHEN_CHAR;
import static accelerate.util.AccelerateConstants.SPACE_CHAR;
import static accelerate.util.AccelerateConstants.UNDERSCORE_CHAR;
import static accelerate.util.AppUtil.compare;
import static accelerate.util.AppUtil.compareAny;
import static accelerate.util.AppUtil.isEmpty;
import static accelerate.util.FileUtil.getFileName;
import static accelerate.util.StringUtil.extractFromEnd;
import static accelerate.util.StringUtil.extractUpto;
import static accelerate.util.UtilCache.getPattern;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import accelerate.cache.PropertyCache;
import accelerate.exception.AccelerateException;
import accelerate.util.FileUtil;
import accelerate.util.StringUtil;

/**
 * This class provides utlity methods to rename files
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 14-May-2015
 */
public class FileRenamer {

	/**
	 * 
	 */
	protected static final Logger _logger = LoggerFactory.getLogger(FileRenamer.class);

	/**
	 * @param aInput
	 * @return
	 * @throws AccelerateException
	 */
	public static FileRenamerOutput rename(FileRenamerInput aInput) throws AccelerateException {
		if ((aInput == null) || isEmpty(aInput.filePath)) {
			throw new AccelerateException("Invalid Input");
		}

		if (aInput.enablePatternReplace) {
			if (compareAny(true, isEmpty(aInput.findPattern), isEmpty(aInput.replaceString))) {
				throw new AccelerateException(
						"For pattern replacem findPattern{%s} and replaceString{%s} are required !!",
						aInput.findPattern, aInput.replaceString);
			}
		}

		if (aInput.configProps == null) {
			aInput.configProps = new HashMap<>();
		}

		FileRenamerOutput output = new FileRenamerOutput();
		DirectoryParser.execute(aInput.filePath, new FileRenameHandler(aInput, output));

		return output;
	}

	/**
	 * This class contains the input passed to {@link FileRenamer} class
	 *
	 * @author Rohit Narayanan
	 * @version 1.0 Initial Version
	 * @since 21-May-2015
	 */
	public class FileRenamerInput implements Serializable {
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Path to file / directory
		 */
		public String filePath = EMPTY_STRING;

		/**
		 * String pattern to search
		 */
		public String findPattern = EMPTY_STRING;

		/**
		 * String to replace the match with
		 */
		public String replaceString = EMPTY_STRING;

		/**
		 * String to filter specific file extn. Empty signifies folder renaming
		 */
		public String fileTypeFilter = null;

		/**
		 * Flag to enable/disable file renaming
		 */
		public boolean rename = false;

		/**
		 * Flag to indicate that file name should be cleaned up/disable file
		 * renaming
		 */
		public boolean cleanupFileName = false;

		/**
		 * Flag to indicate that {@link #findPattern} should be replaced with
		 * {@link #replaceString}
		 */
		public boolean enablePatternReplace = false;

		/**
		 * Flag to enable/disable global replacement of pattern
		 */
		public boolean globalReplace = false;

		/**
		 * Flag to indicate roman literrals should be converted to numbers
		 */
		public boolean convertRomanToNumber = false;

		/**
		 * Flag to indicate Numeric Strings should be converted into numbers
		 */
		public boolean convertStringToNumber = false;

		/**
		 * {@link PropertyCache} instance to manage properties
		 */
		public Map<String, String> configProps = null;
	}

	/**
	 * This class contains the output received from {@link FileRenamer} class
	 *
	 * @author Rohit Narayanan
	 * @version 1.0 Initial Version
	 * @since 21-May-2015
	 */
	public static class FileRenamerOutput implements Serializable {
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Count for the number of files executed by this instance
		 */
		public int executeCount = 0;

		/**
		 * Count for the number of files skipped by the fileTypeFilter
		 */
		public int skipCount = 0;

		/**
		 * Count for the number of files renamed by this instance
		 */
		public int renameCount = 0;
	}

	/**
	 * A custom {@link DirectoryParser.FileHandler} implementation to rename
	 * files
	 *
	 * @author Rohit Narayanan
	 * @version 1.0 Initial Version
	 * @since 21-May-2015
	 */
	private static class FileRenameHandler implements DirectoryParser.FileHandler {
		/**
		 * {@link Matcher} instance to look for All CAPS words
		 */
		private static Matcher allCapsMatcher = getPattern("[A-Z][A-Z]+").matcher("");

		/**
		 *
		 */
		private FileRenamerInput input = null;

		/**
		 *
		 */
		private FileRenamerOutput output = null;

		/**
		 * @param aInput
		 * @param aOutput
		 */
		public FileRenameHandler(FileRenamerInput aInput, FileRenamerOutput aOutput) {
			this.input = aInput;
			this.output = aOutput;
		}

		/**
		 * @param aFile
		 * @return
		 * @throws AccelerateException
		 */
		@Override
		public File handleFile(File aFile) throws AccelerateException {
			return rename(aFile);
		}

		/**
		 * @param aFolder
		 * @return
		 * @throws AccelerateException
		 */
		@Override
		public File handleDirectory(File aFolder) throws AccelerateException {
			if (isEmpty(this.input.fileTypeFilter)) {
				return rename(aFolder);
			}

			return aFolder;
		}

		/**
		 * @return
		 */
		@Override
		public String getExtnFilter() {
			return this.input.fileTypeFilter;
		}

		/**
		 * @param aFile
		 * @return
		 * @throws AccelerateException
		 */
		private File rename(File aFile) throws AccelerateException {
			this.output.executeCount++;

			if (!compare(FileUtil.getFileExtn(aFile), this.input.fileTypeFilter)) {
				this.output.skipCount++;
				return aFile;
			}

			String fileName = getFileName(aFile);
			if (checkIgnoreToken(fileName)) {
				return aFile;
			}

			String newName = this.input.enablePatternReplace ? StringUtil.replace(fileName, this.input.findPattern,
					this.input.replaceString, this.input.globalReplace) : getCleanedUpName(fileName);

			if (!compare(newName, fileName)) {
				if (this.input.rename) {
					File newFile = new File(aFile.getParentFile(), newName);
					if (newFile.exists()) {
						_logger.debug("Exists -> {} ::: {} ::: {}", fileName, newName, aFile.getParent());
						return aFile;
					}

					newFile = FileUtil.renameFile(aFile, newName);
					this.output.renameCount++;
				}

				_logger.debug("Completed -> {} ::: {} ::: {}", fileName, newName, aFile.getParent());
			}

			return aFile;
		}

		/**
		 * @param aToken
		 * @return
		 */
		private boolean checkIgnoreToken(String aToken) {
			for (String pattern : this.input.configProps.keySet()) {
				if (getPattern(pattern).matcher(aToken).matches()) {
					return true;
				}
			}

			return false;
		}

		/**
		 * @param aFileName
		 * @return File name after performing the required modifications
		 * @throws AccelerateException
		 */
		private String getCleanedUpName(String aFileName) throws AccelerateException {
			String[] skipTokens = StringUtil.split(this.input.configProps.get("skipTokens"), COMMA_CHAR);
			for (String skipToken : skipTokens) {
				if (aFileName.indexOf(skipToken) >= 0) {
					return aFileName;
				}
			}

			List<String> tokens = new ArrayList<>();
			String[] spaceTokens = StringUtil.split(aFileName, SPACE_CHAR);
			for (String spaceToken : spaceTokens) {
				if (isEmpty(spaceToken)) {
					continue;
				}

				String[] underScoreTokens = StringUtil.split(spaceToken, UNDERSCORE_CHAR);
				for (String underScoreToken : underScoreTokens) {
					if (isEmpty(underScoreToken)) {
						continue;
					}

					if (checkIgnoreToken(underScoreToken)) {
						tokens.add(underScoreToken);
						continue;
					}

					String[] hyphenTokens = StringUtil.split(underScoreToken, HYPHEN_CHAR);
					int size = hyphenTokens.length;
					if (size == 0) {
						tokens.add(HYPHEN_CHAR);
						continue;
					}

					int index = 0;
					for (String hyphenToken : hyphenTokens) {
						if (isEmpty(hyphenToken)) {
							index++;
							continue;
						}

						tokens.add(hyphenToken);
						if (index++ < (size - 1)) {
							tokens.add(HYPHEN_CHAR);
						}
					}
				}
			}

			StringBuilder newName = new StringBuilder();
			String prevToken = null;
			int size = tokens.size();

			for (int index = 0; index < size; index++) {
				String token = tokens.get(index);
				if (isEmpty(token)) {
					continue;
				}

				if (token.equals(prevToken) && compareAny(token, HYPHEN_CHAR, UNDERSCORE_CHAR)) {
					continue;
				}

				if (checkIgnoreToken(token)) {
					newName.append(token);
				} else {
					newName.append(capitalizeToken(token));
				}

				if (index < (size - 1)) {
					newName.append(SPACE_CHAR);
				}

				prevToken = token;
			}

			return newName.toString();
		}

		/**
		 * @param aToken
		 * @return
		 * @throws AccelerateException
		 */
		private String capitalizeToken(String aToken) throws AccelerateException {
			if (aToken.startsWith("(") || aToken.startsWith("[")) {
				return aToken.substring(0, 1) + capitalizeToken(aToken.substring(1));
			}

			if (aToken.endsWith(")") || aToken.endsWith("]")) {
				return capitalizeToken(extractUpto(aToken, 0, 1)) + extractFromEnd(aToken, 1);
			}

			if (checkIgnoreToken(aToken)) {
				return aToken;
			}

			String upperCase = aToken.toUpperCase();
			if (allCapsMatcher.reset(aToken).matches()) {
				_logger.debug("*********Skipping all Caps Token: {}", aToken);
				return aToken;
			}

			if (this.input.convertRomanToNumber) {
				String val = this.input.configProps.get(StringUtil.join("Roman", upperCase));
				if (val != null) {
					_logger.debug("*********Returning '{}' for '{}'", val, aToken);
					return val;
				}
			}

			if (this.input.convertStringToNumber) {
				String val = this.input.configProps.get(StringUtil.join("Name", upperCase));
				if (val != null) {
					_logger.debug("*********Returning '{}' for '{}'", val, aToken);
					return val;
				}
			}

			int index = 0;
			boolean letterFlag = false;
			boolean breakFlag = false;
			StringBuilder buffer = new StringBuilder();
			String token = aToken.toLowerCase();

			for (int j = 0; j < token.length(); j++) {
				char charValue = token.charAt(j);
				if (Character.isLetter(charValue)) {
					index = j;

					if (letterFlag) {
						breakFlag = true;
						break;
					}

					letterFlag = true;
				} else {
					letterFlag = false;
				}

				buffer.append(Character.toUpperCase(charValue));
			}

			if (breakFlag) {
				buffer.append(token.substring(index));
			}

			return buffer.toString();
		}
	}
}