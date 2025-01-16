package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.concurrent.Callable;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.base.Supplier;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
public final class Callables {
  public static <T> Callable<T> returning(@ParametricNullness T value) {
    return () -> value;
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static <T> AsyncCallable<T> asAsyncCallable(Callable<T> callable, ListeningExecutorService listeningExecutorService) {
    Preconditions.checkNotNull(callable);
    Preconditions.checkNotNull(listeningExecutorService);
    return () -> listeningExecutorService.submit(callable);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  static <T> Callable<T> threadRenaming(Callable<T> callable, Supplier<String> nameSupplier) {
    Preconditions.checkNotNull(nameSupplier);
    Preconditions.checkNotNull(callable);
    return () -> {
        Thread currentThread = Thread.currentThread();
        String oldName = currentThread.getName();
        boolean restoreName = trySetName((String)nameSupplier.get(), currentThread);
        try {
          return callable.call();
        } finally {
          if (restoreName)
            boolean bool = trySetName(oldName, currentThread); 
        } 
      };
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  static Runnable threadRenaming(Runnable task, Supplier<String> nameSupplier) {
    Preconditions.checkNotNull(nameSupplier);
    Preconditions.checkNotNull(task);
    return () -> {
        Thread currentThread = Thread.currentThread();
        String oldName = currentThread.getName();
        boolean restoreName = trySetName((String)nameSupplier.get(), currentThread);
        try {
          task.run();
        } finally {
          if (restoreName)
            boolean bool = trySetName(oldName, currentThread); 
        } 
      };
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  private static boolean trySetName(String threadName, Thread currentThread) {
    try {
      currentThread.setName(threadName);
      return true;
    } catch (SecurityException e) {
      return false;
    } 
  }
}
