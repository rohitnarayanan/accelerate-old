package accelerate.util;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.commons.io.IOUtils;

/**
 * Utility class with helper methods to handle IO operations
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since Mar 10, 2016
 */
public class IOUtil {
	/**
	 * @param <T>
	 * @param bean
	 * @return object size
	 */
	public static <T extends Serializable> int getObjectSize(T bean) {
		return ToBytes(bean).length;
	}

	/**
	 * @param <T>
	 * @param bean
	 * @return object size
	 */
	public static <T extends Serializable> byte[] ToBytes(T bean) {
		ObjectOutputStream oos = null;

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(bean);
			oos.flush();

			return bos.toByteArray();
		} catch (Exception error) {
			error.printStackTrace();
		} finally {
			IOUtils.closeQuietly(oos);
		}

		return new byte[0];
	}
}
