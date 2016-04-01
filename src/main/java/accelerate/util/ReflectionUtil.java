package accelerate.util;

import static accelerate.util.AppUtil.compare;
import static accelerate.util.StringUtil.toCamelCase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import accelerate.exception.AccelerateException;

/**
 * This class provides utility methods for the application
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jun 12, 2009
 */
public class ReflectionUtil {
	/**
	 * static class
	 */
	private ReflectionUtil() {
	}

	/**
	 * @param aClassType
	 * @param aFieldName
	 * @param aFieldType
	 * @return String
	 */
	public static Field getField(Class<?> aClassType, String aFieldName, Class<?> aFieldType) {
		Class<?> searchType = aClassType;
		while (searchType != null) {
			Field[] fields = searchType.getDeclaredFields();
			for (Field field : fields) {
				if (compare(field.getName(), aFieldName)) {
					if ((aFieldType == null) || compare(field.getClass(), aFieldType)) {
						return field;
					}
				}
			}

			searchType = searchType.getSuperclass();
		}

		return null;
	}

	/**
	 * @param aTargetClass
	 * @param aTargetInstance
	 * @param aTargetField
	 * @return
	 * @throws AccelerateException
	 */
	public static Object getFieldValue(Class<?> aTargetClass, Object aTargetInstance, String aTargetField)
			throws AccelerateException {
		if (AppUtil.isEmptyAny(aTargetClass, aTargetInstance, aTargetField)) {
			throw new AccelerateException("Invalid Call. All arguments are required");
		}

		try {
			Field field = getField(aTargetClass, aTargetField, null);
			boolean accessible = field.isAccessible();
			field.setAccessible(true);
			Object value = field.get(aTargetInstance);
			field.setAccessible(accessible);

			return value;
		} catch (Exception error) {
			throw new AccelerateException(error);
		}
	}

	/**
	 * @param aTargetClass
	 * @param aTargetInstance
	 * @param aTargetField
	 * @param aFieldValue
	 * @throws AccelerateException
	 */
	public static void setFieldValue(Class<?> aTargetClass, Object aTargetInstance, String aTargetField,
			Object aFieldValue) throws AccelerateException {
		if (AppUtil.isEmptyAny(aTargetClass, aTargetInstance, aTargetField, aFieldValue)) {
			throw new AccelerateException("Invalid Call. All arguments are required");
		}

		try {
			Field field = getField(aTargetClass, aTargetField, null);
			boolean accessible = field.isAccessible();
			field.setAccessible(true);
			field.set(aTargetInstance, aFieldValue);
			field.setAccessible(accessible);
		} catch (Exception error) {
			throw new AccelerateException(error);
		}
	}

	/**
	 * @param aTargetClass
	 * @param aTargetInstance
	 * @param aTargetMethod
	 * @param aMethodArgTypes
	 * @param aMethodArgs
	 * @return
	 * @throws AccelerateException
	 */
	public static Object invokeMethod(Class<?> aTargetClass, Object aTargetInstance, String aTargetMethod,
			Class<?>[] aMethodArgTypes, Object[] aMethodArgs) throws AccelerateException {
		if (AppUtil.isEmptyAny(aTargetClass, aTargetMethod)) {
			throw new AccelerateException("Invalid Call. Target Class and Method arguments are required");
		}

		try {
			Method method = aTargetClass.getMethod(aTargetMethod, aMethodArgTypes);
			boolean accessible = method.isAccessible();
			method.setAccessible(true);
			Object value = method.invoke(aTargetInstance, aMethodArgs);
			method.setAccessible(accessible);

			return value;
		} catch (Exception error) {
			throw new AccelerateException(error);
		}
	}

	/**
	 * @param aTargetInstance
	 * @param aTargetField
	 * @return String
	 * @throws AccelerateException
	 */
	public static Object invokeGetter(Object aTargetInstance, String aTargetField) throws AccelerateException {
		if (AppUtil.isEmptyAny(aTargetInstance, aTargetField)) {
			throw new AccelerateException("Invalid Call. All arguments are required");
		}

		String getterName = toCamelCase("get", aTargetField);
		return invokeMethod(aTargetInstance.getClass(), aTargetInstance, getterName, (Class<?>[]) null,
				(Object[]) null);
	}

	/**
	 * @param aTargetInstance
	 * @param aTargetField
	 * @param aFieldValue
	 * @throws AccelerateException
	 */
	public static void invokeSetter(Object aTargetInstance, String aTargetField, Object aFieldValue)
			throws AccelerateException {
		if (AppUtil.isEmptyAny(aTargetInstance, aTargetField, aFieldValue)) {
			throw new AccelerateException("Invalid Call. All arguments are required");
		}

		String setterName = toCamelCase("set", aTargetField);
		invokeMethod(aTargetInstance.getClass(), aTargetInstance, setterName, new Class<?>[] { aFieldValue.getClass() },
				new Object[] { aFieldValue });

	}
}