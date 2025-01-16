package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.ObjectArrays;
import me.syncwrld.booter.libs.google.guava.collect.Sets;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public final class SimpleTimeLimiter implements TimeLimiter {
  private final ExecutorService executor;
  
  private SimpleTimeLimiter(ExecutorService executor) {
    this.executor = (ExecutorService)Preconditions.checkNotNull(executor);
  }
  
  public static SimpleTimeLimiter create(ExecutorService executor) {
    return new SimpleTimeLimiter(executor);
  }
  
  public <T> T newProxy(final T target, Class<T> interfaceType, final long timeoutDuration, final TimeUnit timeoutUnit) {
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(interfaceType);
    Preconditions.checkNotNull(timeoutUnit);
    checkPositiveTimeout(timeoutDuration);
    Preconditions.checkArgument(interfaceType.isInterface(), "interfaceType must be an interface type");
    final Set<Method> interruptibleMethods = findInterruptibleMethods(interfaceType);
    InvocationHandler handler = new InvocationHandler() {
        @CheckForNull
        public Object invoke(Object obj, Method method, @CheckForNull Object[] args) throws Throwable {
          Callable<Object> callable = () -> {
              try {
                return method.invoke(target, args);
              } catch (InvocationTargetException e) {
                throw SimpleTimeLimiter.throwCause(e, false);
              } 
            };
          return SimpleTimeLimiter.this.callWithTimeout((Callable)callable, timeoutDuration, timeoutUnit, interruptibleMethods
              .contains(method));
        }
      };
    return newProxy(interfaceType, handler);
  }
  
  private static <T> T newProxy(Class<T> interfaceType, InvocationHandler handler) {
    Object object = Proxy.newProxyInstance(interfaceType
        .getClassLoader(), new Class[] { interfaceType }, handler);
    return interfaceType.cast(object);
  }
  
  @ParametricNullness
  private <T> T callWithTimeout(Callable<T> callable, long timeoutDuration, TimeUnit timeoutUnit, boolean amInterruptible) throws Exception {
    Preconditions.checkNotNull(callable);
    Preconditions.checkNotNull(timeoutUnit);
    checkPositiveTimeout(timeoutDuration);
    Future<T> future = this.executor.submit(callable);
    try {
      return amInterruptible ? 
        future.get(timeoutDuration, timeoutUnit) : 
        Uninterruptibles.<T>getUninterruptibly(future, timeoutDuration, timeoutUnit);
    } catch (InterruptedException e) {
      future.cancel(true);
      throw e;
    } catch (ExecutionException e) {
      throw throwCause(e, true);
    } catch (TimeoutException e) {
      future.cancel(true);
      throw new UncheckedTimeoutException(e);
    } 
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  public <T> T callWithTimeout(Callable<T> callable, long timeoutDuration, TimeUnit timeoutUnit) throws TimeoutException, InterruptedException, ExecutionException {
    Preconditions.checkNotNull(callable);
    Preconditions.checkNotNull(timeoutUnit);
    checkPositiveTimeout(timeoutDuration);
    Future<T> future = this.executor.submit(callable);
    try {
      return future.get(timeoutDuration, timeoutUnit);
    } catch (InterruptedException|TimeoutException e) {
      future.cancel(true);
      throw e;
    } catch (ExecutionException e) {
      wrapAndThrowExecutionExceptionOrError(e.getCause());
      throw new AssertionError();
    } 
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  public <T> T callUninterruptiblyWithTimeout(Callable<T> callable, long timeoutDuration, TimeUnit timeoutUnit) throws TimeoutException, ExecutionException {
    Preconditions.checkNotNull(callable);
    Preconditions.checkNotNull(timeoutUnit);
    checkPositiveTimeout(timeoutDuration);
    Future<T> future = this.executor.submit(callable);
    try {
      return Uninterruptibles.getUninterruptibly(future, timeoutDuration, timeoutUnit);
    } catch (TimeoutException e) {
      future.cancel(true);
      throw e;
    } catch (ExecutionException e) {
      wrapAndThrowExecutionExceptionOrError(e.getCause());
      throw new AssertionError();
    } 
  }
  
  public void runWithTimeout(Runnable runnable, long timeoutDuration, TimeUnit timeoutUnit) throws TimeoutException, InterruptedException {
    Preconditions.checkNotNull(runnable);
    Preconditions.checkNotNull(timeoutUnit);
    checkPositiveTimeout(timeoutDuration);
    Future<?> future = this.executor.submit(runnable);
    try {
      future.get(timeoutDuration, timeoutUnit);
    } catch (InterruptedException|TimeoutException e) {
      future.cancel(true);
      throw e;
    } catch (ExecutionException e) {
      wrapAndThrowRuntimeExecutionExceptionOrError(e.getCause());
      throw new AssertionError();
    } 
  }
  
  public void runUninterruptiblyWithTimeout(Runnable runnable, long timeoutDuration, TimeUnit timeoutUnit) throws TimeoutException {
    Preconditions.checkNotNull(runnable);
    Preconditions.checkNotNull(timeoutUnit);
    checkPositiveTimeout(timeoutDuration);
    Future<?> future = this.executor.submit(runnable);
    try {
      Uninterruptibles.getUninterruptibly(future, timeoutDuration, timeoutUnit);
    } catch (TimeoutException e) {
      future.cancel(true);
      throw e;
    } catch (ExecutionException e) {
      wrapAndThrowRuntimeExecutionExceptionOrError(e.getCause());
      throw new AssertionError();
    } 
  }
  
  private static Exception throwCause(Exception e, boolean combineStackTraces) throws Exception {
    Throwable cause = e.getCause();
    if (cause == null)
      throw e; 
    if (combineStackTraces) {
      StackTraceElement[] combined = (StackTraceElement[])ObjectArrays.concat((Object[])cause.getStackTrace(), (Object[])e.getStackTrace(), StackTraceElement.class);
      cause.setStackTrace(combined);
    } 
    if (cause instanceof Exception)
      throw (Exception)cause; 
    if (cause instanceof Error)
      throw (Error)cause; 
    throw e;
  }
  
  private static Set<Method> findInterruptibleMethods(Class<?> interfaceType) {
    Set<Method> set = Sets.newHashSet();
    for (Method m : interfaceType.getMethods()) {
      if (declaresInterruptedEx(m))
        set.add(m); 
    } 
    return set;
  }
  
  private static boolean declaresInterruptedEx(Method method) {
    for (Class<?> exType : method.getExceptionTypes()) {
      if (exType == InterruptedException.class)
        return true; 
    } 
    return false;
  }
  
  private void wrapAndThrowExecutionExceptionOrError(Throwable cause) throws ExecutionException {
    if (cause instanceof Error)
      throw new ExecutionError((Error)cause); 
    if (cause instanceof RuntimeException)
      throw new UncheckedExecutionException(cause); 
    throw new ExecutionException(cause);
  }
  
  private void wrapAndThrowRuntimeExecutionExceptionOrError(Throwable cause) {
    if (cause instanceof Error)
      throw new ExecutionError((Error)cause); 
    throw new UncheckedExecutionException(cause);
  }
  
  private static void checkPositiveTimeout(long timeoutDuration) {
    Preconditions.checkArgument((timeoutDuration > 0L), "timeout must be positive: %s", timeoutDuration);
  }
}
