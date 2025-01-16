package me.syncwrld.booter.libs.google.gson.internal;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class UnsafeAllocator {
  private static void assertInstantiable(Class<?> c) {
    String exceptionMessage = ConstructorConstructor.checkInstantiable(c);
    if (exceptionMessage != null)
      throw new AssertionError("UnsafeAllocator is used for non-instantiable type: " + exceptionMessage); 
  }
  
  public static final UnsafeAllocator INSTANCE = create();
  
  private static UnsafeAllocator create() {
    try {
      Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
      Field f = unsafeClass.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      final Object unsafe = f.get(null);
      final Method allocateInstance = unsafeClass.getMethod("allocateInstance", new Class[] { Class.class });
      return new UnsafeAllocator() {
          public <T> T newInstance(Class<T> c) throws Exception {
            UnsafeAllocator.assertInstantiable(c);
            return (T)allocateInstance.invoke(unsafe, new Object[] { c });
          }
        };
    } catch (Exception exception) {
      try {
        Method getConstructorId = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", new Class[] { Class.class });
        getConstructorId.setAccessible(true);
        final int constructorId = ((Integer)getConstructorId.invoke(null, new Object[] { Object.class })).intValue();
        final Method newInstance = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[] { Class.class, int.class });
        newInstance.setAccessible(true);
        return new UnsafeAllocator() {
            public <T> T newInstance(Class<T> c) throws Exception {
              UnsafeAllocator.assertInstantiable(c);
              return (T)newInstance.invoke(null, new Object[] { c, Integer.valueOf(this.val$constructorId) });
            }
          };
      } catch (Exception exception1) {
        try {
          final Method newInstance = ObjectInputStream.class.getDeclaredMethod("newInstance", new Class[] { Class.class, Class.class });
          newInstance.setAccessible(true);
          return new UnsafeAllocator() {
              public <T> T newInstance(Class<T> c) throws Exception {
                UnsafeAllocator.assertInstantiable(c);
                return (T)newInstance.invoke(null, new Object[] { c, Object.class });
              }
            };
        } catch (Exception exception2) {
          return new UnsafeAllocator() {
              public <T> T newInstance(Class<T> c) {
                throw new UnsupportedOperationException("Cannot allocate " + c + ". Usage of JDK sun.misc.Unsafe is enabled, but it could not be used. Make sure your runtime is configured correctly.");
              }
            };
        } 
      } 
    } 
  }
  
  public abstract <T> T newInstance(Class<T> paramClass) throws Exception;
}
