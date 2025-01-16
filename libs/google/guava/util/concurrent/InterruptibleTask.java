package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.AbstractOwnableSynchronizer;
import java.util.concurrent.locks.LockSupport;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.j2objc.annotations.ReflectionSupport;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
@ReflectionSupport(ReflectionSupport.Level.FULL)
abstract class InterruptibleTask<T> extends AtomicReference<Runnable> implements Runnable {
  static {
    Class<LockSupport> clazz = LockSupport.class;
  }
  
  private static final class DoNothingRunnable implements Runnable {
    private DoNothingRunnable() {}
    
    public void run() {}
  }
  
  private static final Runnable DONE = new DoNothingRunnable();
  
  private static final Runnable PARKED = new DoNothingRunnable();
  
  private static final int MAX_BUSY_WAIT_SPINS = 1000;
  
  public final void run() {
    Thread currentThread = Thread.currentThread();
    if (!compareAndSet(null, currentThread))
      return; 
    boolean run = !isDone();
    T result = null;
    Throwable error = null;
    try {
      if (run)
        result = runInterruptibly(); 
    } catch (Throwable t) {
      Platform.restoreInterruptIfIsInterruptedException(t);
      error = t;
    } finally {
      if (!compareAndSet(currentThread, DONE))
        waitForInterrupt(currentThread); 
      if (run)
        if (error == null) {
          afterRanInterruptiblySuccess(NullnessCasts.uncheckedCastNullableTToT(result));
        } else {
          afterRanInterruptiblyFailure(error);
        }  
    } 
  }
  
  private void waitForInterrupt(Thread currentThread) {
    boolean restoreInterruptedBit = false;
    int spinCount = 0;
    Runnable state = get();
    Blocker blocker = null;
    while (state instanceof Blocker || state == PARKED) {
      if (state instanceof Blocker)
        blocker = (Blocker)state; 
      spinCount++;
      if (spinCount > 1000) {
        if (state == PARKED || compareAndSet(state, PARKED)) {
          restoreInterruptedBit = (Thread.interrupted() || restoreInterruptedBit);
          LockSupport.park(blocker);
        } 
      } else {
        Thread.yield();
      } 
      state = get();
    } 
    if (restoreInterruptedBit)
      currentThread.interrupt(); 
  }
  
  final void interruptTask() {
    Runnable currentRunner = get();
    if (currentRunner instanceof Thread) {
      Blocker blocker = new Blocker(this);
      blocker.setOwner(Thread.currentThread());
      if (compareAndSet(currentRunner, blocker))
        try {
          ((Thread)currentRunner).interrupt();
        } finally {
          Runnable prev = getAndSet(DONE);
          if (prev == PARKED)
            LockSupport.unpark((Thread)currentRunner); 
        }  
    } 
  }
  
  @VisibleForTesting
  static final class Blocker extends AbstractOwnableSynchronizer implements Runnable {
    private final InterruptibleTask<?> task;
    
    private Blocker(InterruptibleTask<?> task) {
      this.task = task;
    }
    
    public void run() {}
    
    private void setOwner(Thread thread) {
      setExclusiveOwnerThread(thread);
    }
    
    @CheckForNull
    @VisibleForTesting
    Thread getOwner() {
      return getExclusiveOwnerThread();
    }
    
    public String toString() {
      return this.task.toString();
    }
  }
  
  public final String toString() {
    String result;
    Runnable state = get();
    if (state == DONE) {
      result = "running=[DONE]";
    } else if (state instanceof Blocker) {
      result = "running=[INTERRUPTED]";
    } else if (state instanceof Thread) {
      result = "running=[RUNNING ON " + ((Thread)state).getName() + "]";
    } else {
      result = "running=[NOT STARTED YET]";
    } 
    return result + ", " + toPendingString();
  }
  
  abstract boolean isDone();
  
  @ParametricNullness
  abstract T runInterruptibly() throws Exception;
  
  abstract void afterRanInterruptiblySuccess(@ParametricNullness T paramT);
  
  abstract void afterRanInterruptiblyFailure(Throwable paramThrowable);
  
  abstract String toPendingString();
}
