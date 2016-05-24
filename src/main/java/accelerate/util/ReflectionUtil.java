package accelerate.util;

import static accelerate.util.StringUtil.camelCase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import accelerate.exception.AccelerateException;
import accelerate.exception.AccelerateRuntimeException;

/**
 * This class provides utility methods for the application
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Jun 12, 2009
 */
public final class ReflectionUtil {

	/**
	 * hidden constructor
	 */
	private ReflectionUtil() {
	}

	/**
	 * @param aTargetClass
	 * @param aTargetInstance
	 * @param aTargetField
	 * @return
	 * @throws AccelerateException
	 *             on {@link Field} operations
	 */
	public static Object getFieldValue(Class<?> aTargetClass, Object aTargetInstance, String aTargetField)
			throws AccelerateException {
		if (AppUtil.isEmptyAny(aTargetClass, aTargetInstance, aTargetField)) {
			throw new AccelerateException("Invalid Call. All arguments are required");
		}

		try {
			Field field = aTargetClass.getField(aTargetField);
			boolean accessible = field.isAccessible();
			field.setAccessible(true);
			Object value = field.get(aTargetInstance);
			field.setAccessible(accessible);

			return value;
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException error) {
			throw new AccelerateException(error);
		}
	}

	/**
	 * @param aTargetClass
	 * @param aTargetInstance
	 * @param aTargetField
	 * @param aFieldValue
	 * @throws AccelerateException
	 *             on {@link Field} operations
	 */
	public static void setFieldValue(Class<?> aTargetClass, Object aTargetInstance, String aTargetField,
			Object aFieldValue) throws AccelerateException {
		if (AppUtil.isEmptyAny(aTargetClass, aTargetInstance, aTargetField, aFieldValue)) {
			throw new AccelerateRuntimeException("Invalid Call. All arguments are required");
		}

		try {
			Field field = aTargetClass.getField(aTargetField);
			boolean accessible = field.isAccessible();
			field.setAccessible(true);
			field.set(aTargetInstance, aFieldValue);
			field.setAccessible(accessible);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException error) {
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
	 */
	public static Object invokeMethod(Class<?> aTargetClass, Object aTargetInstance, String aTargetMethod,
			Class<?>[] aMethodArgTypes, Object[] aMethodArgs) {
		if (AppUtil.isEmptyAny(aTargetClass, aTargetMethod)) {
			throw new AccelerateRuntimeException("Invalid Call. Target Class and Method arguments are required");
		}

		try {
			Method method = aTargetClass.getMethod(aTargetMethod, aMethodArgTypes);
			boolean accessible = method.isAccessible();
			method.setAccessible(true);
			Object value = method.invoke(aTargetInstance, aMethodArgs);
			method.setAccessible(accessible);

			return value;
		} catch (Exception error) {
			throw new AccelerateRuntimeException(error);
		}
	}

	/**
	 * @param aTargetInstance
	 * @param aTargetField
	 * @return String
	 */
	public static Object invokeGetter(Object aTargetInstance, String aTargetField) {
		if (AppUtil.isEmptyAny(aTargetInstance, aTargetField)) {
			throw new AccelerateRuntimeException("Invalid Call. All arguments are required");
		}

		String getterName = camelCase("get", aTargetField);
		return invokeMethod(aTargetInstance.getClass(), aTargetInstance, getterName, (Class<?>[]) null,
				(Object[]) null);
	}

	/**
	 * @param aTargetInstance
	 * @param aTargetField
	 * @param aFieldValue
	 */
	public static void invokeSetter(Object aTargetInstance, String aTargetField, Object aFieldValue) {
		if (AppUtil.isEmptyAny(aTargetInstance, aTargetField, aFieldValue)) {
			throw new AccelerateRuntimeException("Invalid Call. All arguments are required");
		}

		String setterName = camelCase("set", aTargetField);
		invokeMethod(aTargetInstance.getClass(), aTargetInstance, setterName, new Class<?>[] { aFieldValue.getClass() },
				new Object[] { aFieldValue });

	}
}