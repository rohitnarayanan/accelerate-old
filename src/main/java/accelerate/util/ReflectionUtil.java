package accelerate.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import accelerate.exception.AccelerateException;

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
	 *             - Wrapping the following exceptions thrown due to {@link Field}
	 *             operations - {@link IllegalArgumentException} |
	 *             {@link IllegalAccessException} | {@link NoSuchFieldException} |
	 *             {@link SecurityException}
	 */
	public static Object getFieldValue(Class<?> aTargetClass, Object aTargetInstance, String aTargetField)
			throws AccelerateException {
		Assert.noNullElements(new Object[] { aTargetClass, aTargetField },
				"Invalid Call. aTargetClass, aTargetField values are required");

		try {
			Field field = ReflectionUtils.findField(aTargetClass, aTargetField);
			if (field == null) {
				throw new NoSuchFieldException("Field " + aTargetField + " not found.");
			}

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
	 * @param aTargetInstance
	 * @param aTargetField
	 * @return
	 * @throws AccelerateException
	 *             - Wrapping the following exceptions thrown due to {@link Field}
	 *             operations - {@link IllegalArgumentException} |
	 *             {@link IllegalAccessException} | {@link SecurityException}
	 */
	public static Object getFieldValue(Object aTargetInstance, Field aTargetField) throws AccelerateException {
		Assert.noNullElements(new Object[] { aTargetInstance, aTargetField },
				"Invalid Call. All arguments are required");

		try {
			boolean accessible = aTargetField.isAccessible();
			aTargetField.setAccessible(true);
			Object value = aTargetField.get(aTargetInstance);
			aTargetField.setAccessible(accessible);

			return value;
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException error) {
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
	 * @throws AccelerateException
	 *             - Wrapping the following exceptions thrown due to {@link Field}
	 *             operations - {@link IllegalArgumentException} |
	 *             {@link IllegalAccessException} | {@link NoSuchFieldException} |
	 *             {@link SecurityException}
	 */
	public static void setFieldValue(Class<?> aTargetClass, Object aTargetInstance, String aTargetField,
			Object aFieldValue) throws AccelerateException {
		Assert.noNullElements(new Object[] { aTargetClass, aTargetField, aFieldValue },
				"Invalid Call. aTargetClass, aTargetField values are required");

		try {
			Field field = ReflectionUtils.findField(aTargetClass, aTargetField);
			if (field == null) {
				throw new NoSuchFieldException("Field " + aTargetField + " not found.");
			}

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
	 * @param aTargetMethodName
	 * @param aMethodArgTypes
	 * @param aMethodArgs
	 * @return
	 * @throws AccelerateException
	 *             - Wrapping the following exceptions thrown due to {@link Method}
	 *             operations - {@link NoSuchMethodException} |
	 *             {@link SecurityException} | {@link IllegalAccessException} |
	 *             {@link IllegalArgumentException} |
	 *             {@link InvocationTargetException}
	 * 
	 */
	public static Object invokeMethod(Class<?> aTargetClass, Object aTargetInstance, String aTargetMethodName,
			Class<?>[] aMethodArgTypes, Object[] aMethodArgs) throws AccelerateException {
		Assert.noNullElements(new Object[] { aTargetClass, aTargetMethodName },
				"Invalid Call. aTargetClass, aTargetMethodName values are required");

		try {
			Method method = ReflectionUtils.findMethod(aTargetClass, aTargetMethodName, aMethodArgTypes);
			if (aMethodArgTypes == null) {
				throw new NoSuchMethodException("Method " + aTargetMethodName + " not found.");
			}

			boolean accessible = method.isAccessible();
			method.setAccessible(true);
			Object value = method.invoke(aTargetInstance, aMethodArgs);
			method.setAccessible(accessible);

			return value;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException error) {
			throw new AccelerateException(error);
		}
	}

	/**
	 * @param aTargetInstance
	 * @param aTargetField
	 * @return String
	 * @throws AccelerateException
	 *             thrown by
	 *             {@link #invokeMethod(Class, Object, String, Class[], Object[])}
	 */
	public static Object invokeGetter(Object aTargetInstance, String aTargetField) throws AccelerateException {
		Assert.noNullElements(new Object[] { aTargetInstance, aTargetField },
				"Invalid Call. All arguments are required");

		String getterName = StringUtils.join("get", StringUtils.capitalize(aTargetField));
		return invokeMethod(aTargetInstance.getClass(), aTargetInstance, getterName, (Class<?>[]) null,
				(Object[]) null);
	}

	/**
	 * @param aTargetInstance
	 * @param aTargetField
	 * @param aFieldValue
	 * @throws AccelerateException
	 *             thrown by
	 *             {@link #invokeMethod(Class, Object, String, Class[], Object[])}
	 */
	public static void invokeSetter(Object aTargetInstance, String aTargetField, Object aFieldValue)
			throws AccelerateException {
		Assert.noNullElements(new Object[] { aTargetInstance, aTargetField },
				"Invalid Call. aTargetInstance, aTargetField values are required");

		String setterName = StringUtils.join("set", StringUtils.capitalize(aTargetField));
		invokeMethod(aTargetInstance.getClass(), aTargetInstance, setterName, new Class<?>[] { aFieldValue.getClass() },
				new Object[] { aFieldValue });
	}
}