package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.concurrent.Executor;
import java.util.logging.Level;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.GuardedBy;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public final class ExecutionList {
  private static final LazyLogger log = new LazyLogger(ExecutionList.class);
  
  @CheckForNull
  @GuardedBy("this")
  private RunnableExecutorPair runnables;
  
  @GuardedBy("this")
  private boolean executed;
  
  public void add(Runnable runnable, Executor executor) {
    Preconditions.checkNotNull(runnable, "Runnable was null.");
    Preconditions.checkNotNull(executor, "Executor was null.");
    synchronized (this) {
      if (!this.executed) {
        this.runnables = new RunnableExecutorPair(runnable, executor, this.runnables);
        return;
      } 
    } 
    executeListener(runnable, executor);
  }
  
  public void execute() {
    RunnableExecutorPair list;
    synchronized (this) {
      if (this.executed)
        return; 
      this.executed = true;
      list = this.runnables;
      this.runnables = null;
    } 
    RunnableExecutorPair reversedList = null;
    while (list != null) {
      RunnableExecutorPair tmp = list;
      list = list.next;
      tmp.next = reversedList;
      reversedList = tmp;
    } 
    while (reversedList != null) {
      executeListener(reversedList.runnable, reversedList.executor);
      reversedList = reversedList.next;
    } 
  }
  
  private static void executeListener(Runnable runnable, Executor executor) {
    try {
      executor.execute(runnable);
    } catch (Exception e) {
      log.get()
        .log(Level.SEVERE, "RuntimeException while executing runnable " + runnable + " with executor " + executor, e);
    } 
  }
  
  private static final class RunnableExecutorPair {
    final Runnable runnable;
    
    final Executor executor;
    
    @CheckForNull
    RunnableExecutorPair next;
    
    RunnableExecutorPair(Runnable runnable, Executor executor, @CheckForNull RunnableExecutorPair next) {
      this.runnable = runnable;
      this.executor = executor;
      this.next = next;
    }
  }
}
