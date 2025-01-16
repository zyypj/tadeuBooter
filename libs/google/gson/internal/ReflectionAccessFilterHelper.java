package me.syncwrld.booter.libs.google.gson.internal;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;
import me.syncwrld.booter.libs.google.gson.ReflectionAccessFilter;

public class ReflectionAccessFilterHelper {
  public static boolean isJavaType(Class<?> c) {
    return isJavaType(c.getName());
  }
  
  private static boolean isJavaType(String className) {
    return (className.startsWith("java.") || className.startsWith("me.syncwrld.booter.libs.javax."));
  }
  
  public static boolean isAndroidType(Class<?> c) {
    return isAndroidType(c.getName());
  }
  
  private static boolean isAndroidType(String className) {
    return (className.startsWith("android.") || className
      .startsWith("androidx.") || 
      isJavaType(className));
  }
  
  public static boolean isAnyPlatformType(Class<?> c) {
    String className = c.getName();
    return (isAndroidType(className) || className
      .startsWith("kotlin.") || className
      .startsWith("kotlinx.") || className
      .startsWith("scala."));
  }
  
  public static ReflectionAccessFilter.FilterResult getFilterResult(List<ReflectionAccessFilter> reflectionFilters, Class<?> c) {
    for (ReflectionAccessFilter filter : reflectionFilters) {
      ReflectionAccessFilter.FilterResult result = filter.check(c);
      if (result != ReflectionAccessFilter.FilterResult.INDECISIVE)
        return result; 
    } 
    return ReflectionAccessFilter.FilterResult.ALLOW;
  }
  
  public static boolean canAccess(AccessibleObject accessibleObject, Object object) {
    return AccessChecker.INSTANCE.canAccess(accessibleObject, object);
  }
  
  private static abstract class AccessChecker {
    public static final AccessChecker INSTANCE;
    
    private AccessChecker() {}
    
    public abstract boolean canAccess(AccessibleObject param1AccessibleObject, Object param1Object);
    
    static {
      AccessChecker accessChecker = null;
      if (JavaVersion.isJava9OrLater())
        try {
          final Method canAccessMethod = AccessibleObject.class.getDeclaredMethod("canAccess", new Class[] { Object.class });
          accessChecker = new AccessChecker() {
              public boolean canAccess(AccessibleObject accessibleObject, Object object) {
                try {
                  return ((Boolean)canAccessMethod.invoke(accessibleObject, new Object[] { object })).booleanValue();
                } catch (Exception e) {
                  throw new RuntimeException("Failed invoking canAccess", e);
                } 
              }
            };
        } catch (NoSuchMethodException noSuchMethodException) {} 
      if (accessChecker == null)
        accessChecker = new AccessChecker() {
            public boolean canAccess(AccessibleObject accessibleObject, Object object) {
              return true;
            }
          }; 
      INSTANCE = accessChecker;
    }
  }
}
