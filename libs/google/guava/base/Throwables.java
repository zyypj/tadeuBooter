package me.syncwrld.booter.libs.google.guava.base;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
public final class Throwables {
  @J2ktIncompatible
  @GwtIncompatible
  private static final String JAVA_LANG_ACCESS_CLASSNAME = "sun.misc.JavaLangAccess";
  
  @J2ktIncompatible
  @GwtIncompatible
  @VisibleForTesting
  static final String SHARED_SECRETS_CLASSNAME = "sun.misc.SharedSecrets";
  
  @J2ktIncompatible
  @GwtIncompatible
  public static <X extends Throwable> void throwIfInstanceOf(Throwable throwable, Class<X> declaredType) throws X {
    Preconditions.checkNotNull(throwable);
    if (declaredType.isInstance(throwable))
      throw (X)declaredType.cast(throwable); 
  }
  
  @Deprecated
  @J2ktIncompatible
  @GwtIncompatible
  public static <X extends Throwable> void propagateIfInstanceOf(@CheckForNull Throwable throwable, Class<X> declaredType) throws X {
    if (throwable != null)
      throwIfInstanceOf(throwable, declaredType); 
  }
  
  public static void throwIfUnchecked(Throwable throwable) {
    Preconditions.checkNotNull(throwable);
    if (throwable instanceof RuntimeException)
      throw (RuntimeException)throwable; 
    if (throwable instanceof Error)
      throw (Error)throwable; 
  }
  
  @Deprecated
  @J2ktIncompatible
  @GwtIncompatible
  public static void propagateIfPossible(@CheckForNull Throwable throwable) {
    if (throwable != null)
      throwIfUnchecked(throwable); 
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static <X extends Throwable> void propagateIfPossible(@CheckForNull Throwable throwable, Class<X> declaredType) throws X {
    propagateIfInstanceOf(throwable, declaredType);
    propagateIfPossible(throwable);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static <X1 extends Throwable, X2 extends Throwable> void propagateIfPossible(@CheckForNull Throwable throwable, Class<X1> declaredType1, Class<X2> declaredType2) throws X1, X2 {
    Preconditions.checkNotNull(declaredType2);
    propagateIfInstanceOf(throwable, declaredType1);
    propagateIfPossible(throwable, declaredType2);
  }
  
  @Deprecated
  @CanIgnoreReturnValue
  @J2ktIncompatible
  @GwtIncompatible
  public static RuntimeException propagate(Throwable throwable) {
    throwIfUnchecked(throwable);
    throw new RuntimeException(throwable);
  }
  
  public static Throwable getRootCause(Throwable throwable) {
    Throwable slowPointer = throwable;
    boolean advanceSlowPointer = false;
    Throwable cause;
    while ((cause = throwable.getCause()) != null) {
      throwable = cause;
      if (throwable == slowPointer)
        throw new IllegalArgumentException("Loop in causal chain detected.", throwable); 
      if (advanceSlowPointer)
        slowPointer = slowPointer.getCause(); 
      advanceSlowPointer = !advanceSlowPointer;
    } 
    return throwable;
  }
  
  public static List<Throwable> getCausalChain(Throwable throwable) {
    Preconditions.checkNotNull(throwable);
    List<Throwable> causes = new ArrayList<>(4);
    causes.add(throwable);
    Throwable slowPointer = throwable;
    boolean advanceSlowPointer = false;
    Throwable cause;
    while ((cause = throwable.getCause()) != null) {
      throwable = cause;
      causes.add(throwable);
      if (throwable == slowPointer)
        throw new IllegalArgumentException("Loop in causal chain detected.", throwable); 
      if (advanceSlowPointer)
        slowPointer = slowPointer.getCause(); 
      advanceSlowPointer = !advanceSlowPointer;
    } 
    return Collections.unmodifiableList(causes);
  }
  
  @CheckForNull
  @J2ktIncompatible
  @GwtIncompatible
  public static <X extends Throwable> X getCauseAs(Throwable throwable, Class<X> expectedCauseType) {
    try {
      return expectedCauseType.cast(throwable.getCause());
    } catch (ClassCastException e) {
      e.initCause(throwable);
      throw e;
    } 
  }
  
  @GwtIncompatible
  public static String getStackTraceAsString(Throwable throwable) {
    StringWriter stringWriter = new StringWriter();
    throwable.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString();
  }
  
  @Deprecated
  @J2ktIncompatible
  @GwtIncompatible
  public static List<StackTraceElement> lazyStackTrace(Throwable throwable) {
    return lazyStackTraceIsLazy() ? 
      jlaStackTrace(throwable) : 
      Collections.<StackTraceElement>unmodifiableList(Arrays.asList(throwable.getStackTrace()));
  }
  
  @Deprecated
  @J2ktIncompatible
  @GwtIncompatible
  public static boolean lazyStackTraceIsLazy() {
    return (getStackTraceElementMethod != null && getStackTraceDepthMethod != null);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  private static List<StackTraceElement> jlaStackTrace(final Throwable t) {
    Preconditions.checkNotNull(t);
    return new AbstractList<StackTraceElement>() {
        public StackTraceElement get(int n) {
          return 
            (StackTraceElement)Throwables.invokeAccessibleNonThrowingMethod(
              Objects.<Method>requireNonNull(Throwables.getStackTraceElementMethod), Objects.requireNonNull(Throwables.jla), new Object[] { this.val$t, Integer.valueOf(n) });
        }
        
        public int size() {
          return (
            (Integer)Throwables.invokeAccessibleNonThrowingMethod(
              Objects.<Method>requireNonNull(Throwables.getStackTraceDepthMethod), Objects.requireNonNull(Throwables.jla), new Object[] { this.val$t })).intValue();
        }
      };
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  private static Object invokeAccessibleNonThrowingMethod(Method method, Object receiver, Object... params) {
    try {
      return method.invoke(receiver, params);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw propagate(e.getCause());
    } 
  }
  
  @CheckForNull
  @J2ktIncompatible
  @GwtIncompatible
  private static final Object jla = getJLA();
  
  @CheckForNull
  @J2ktIncompatible
  @GwtIncompatible
  private static final Method getStackTraceElementMethod = (jla == null) ? null : getGetMethod();
  
  @CheckForNull
  @J2ktIncompatible
  @GwtIncompatible
  private static final Method getStackTraceDepthMethod = (jla == null) ? null : getSizeMethod(jla);
  
  @CheckForNull
  @J2ktIncompatible
  @GwtIncompatible
  private static Object getJLA() {
    try {
      Class<?> sharedSecrets = Class.forName("sun.misc.SharedSecrets", false, (ClassLoader)null);
      Method langAccess = sharedSecrets.getMethod("getJavaLangAccess", new Class[0]);
      return langAccess.invoke(null, new Object[0]);
    } catch (ThreadDeath death) {
      throw death;
    } catch (Throwable t) {
      return null;
    } 
  }
  
  @CheckForNull
  @J2ktIncompatible
  @GwtIncompatible
  private static Method getGetMethod() {
    return getJlaMethod("getStackTraceElement", new Class[] { Throwable.class, int.class });
  }
  
  @CheckForNull
  @J2ktIncompatible
  @GwtIncompatible
  private static Method getSizeMethod(Object jla) {
    try {
      Method getStackTraceDepth = getJlaMethod("getStackTraceDepth", new Class[] { Throwable.class });
      if (getStackTraceDepth == null)
        return null; 
      getStackTraceDepth.invoke(jla, new Object[] { new Throwable() });
      return getStackTraceDepth;
    } catch (UnsupportedOperationException|IllegalAccessException|InvocationTargetException e) {
      return null;
    } 
  }
  
  @CheckForNull
  @J2ktIncompatible
  @GwtIncompatible
  private static Method getJlaMethod(String name, Class<?>... parameterTypes) throws ThreadDeath {
    try {
      return Class.forName("sun.misc.JavaLangAccess", false, (ClassLoader)null).getMethod(name, parameterTypes);
    } catch (ThreadDeath death) {
      throw death;
    } catch (Throwable t) {
      return null;
    } 
  }
}
