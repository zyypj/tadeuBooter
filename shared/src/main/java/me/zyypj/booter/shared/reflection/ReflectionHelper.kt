package me.zyypj.booter.shared.reflection

import java.lang.reflect.Field
import java.lang.reflect.Method

object ReflectionHelper {

    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun getFieldValue(target: Any, fieldName: String): Any? {
        val field = getDeclaredField(target.javaClass, fieldName)
        field.isAccessible = true
        return field.get(target)
    }

    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun setFieldValue(target: Any, fieldName: String, value: Any?) {
        val field = getDeclaredField(target.javaClass, fieldName)
        field.isAccessible = true
        field.set(target, value)
    }

    @Throws(Exception::class)
    fun invokeMethod(
        target: Any,
        methodName: String,
        parameterTypes: Array<Class<*>>,
        vararg args: Any?
    ): Any? {
        val method = getDeclaredMethod(target.javaClass, methodName, parameterTypes)
        method.isAccessible = true
        return method.invoke(target, *args)
    }

    @Throws(NoSuchFieldException::class)
    private fun getDeclaredField(clazz: Class<*>, fieldName: String): Field {
        var current: Class<*>? = clazz
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName)
            } catch (_: NoSuchFieldException) {
                current = current.superclass
            }
        }
        throw NoSuchFieldException("Field $fieldName não encontrado na classe ${clazz.name}")
    }

    @Throws(NoSuchMethodException::class)
    private fun getDeclaredMethod(
        clazz: Class<*>,
        methodName: String,
        parameterTypes: Array<Class<*>>
    ): Method {
        var current: Class<*>? = clazz
        while (current != null) {
            try {
                return current.getDeclaredMethod(methodName, *parameterTypes)
            } catch (_: NoSuchMethodException) {
                current = current.superclass
            }
        }
        throw NoSuchMethodException("Method $methodName não encontrado na classe ${clazz.name}")
    }
}