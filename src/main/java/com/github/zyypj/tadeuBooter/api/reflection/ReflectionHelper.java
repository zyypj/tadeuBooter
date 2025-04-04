package com.github.zyypj.tadeuBooter.api.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionHelper {

    /**
     * Obtém o valor de um campo (privado ou público) de um objeto.
     */
    public static Object getFieldValue(Object target, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = getDeclaredField(target.getClass(), fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    /**
     * Define o valor de um campo (privado ou público) de um objeto.
     */
    public static void setFieldValue(Object target, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = getDeclaredField(target.getClass(), fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    /**
     * Invoca um método (privado ou público) de um objeto.
     */
    public static Object invokeMethod(Object target, String methodName, Class<?>[] parameterTypes, Object... args) throws Exception {
        Method method = getDeclaredMethod(target.getClass(), methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    private static Field getDeclaredField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field " + fieldName + " not found in class " + clazz.getName());
    }

    private static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) throws NoSuchMethodException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchMethodException("Method " + methodName + " not found in class " + clazz.getName());
    }
}