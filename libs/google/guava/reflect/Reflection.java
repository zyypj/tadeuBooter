package me.syncwrld.booter.libs.google.guava.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
public final class Reflection {
  public static String getPackageName(Class<?> clazz) {
    return getPackageName(clazz.getName());
  }
  
  public static String getPackageName(String classFullName) {
    int lastDot = classFullName.lastIndexOf('.');
    return (lastDot < 0) ? "" : classFullName.substring(0, lastDot);
  }
  
  public static void initialize(Class<?>... classes) {
    for (Class<?> clazz : classes) {
      try {
        Class.forName(clazz.getName(), true, clazz.getClassLoader());
      } catch (ClassNotFoundException e) {
        throw new AssertionError(e);
      } 
    } 
  }
  
  public static <T> T newProxy(Class<T> interfaceType, InvocationHandler handler) {
    Preconditions.checkNotNull(handler);
    Preconditions.checkArgument(interfaceType.isInterface(), "%s is not an interface", interfaceType);
    Object object = Proxy.newProxyInstance(interfaceType
        .getClassLoader(), new Class[] { interfaceType }, handler);
    return interfaceType.cast(object);
  }
}
