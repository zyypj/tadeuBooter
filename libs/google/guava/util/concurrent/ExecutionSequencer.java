package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
public final class ExecutionSequencer {
  public static ExecutionSequencer create() {
    return new ExecutionSequencer();
  }
  
  private final AtomicReference<ListenableFuture<Void>> ref = new AtomicReference<>(
      Futures.immediateVoidFuture());
  
  private ThreadConfinedTaskQueue latestTaskQueue = new ThreadConfinedTaskQueue();
  
  private static final class ThreadConfinedTaskQueue {
    @CheckForNull
    Thread thread;
    
    @CheckForNull
    Runnable nextTask;
    
    @CheckForNull
    Executor nextExecutor;
    
    private ThreadConfinedTaskQueue() {}
  }
  
  public <T> ListenableFuture<T> submit(final Callable<T> callable, Executor executor) {
    Preconditions.checkNotNull(callable);
    Preconditions.checkNotNull(executor);
    return submitAsync(new AsyncCallable<T>(this) {
          public ListenableFuture<T> call() throws Exception {
            return Futures.immediateFuture(callable.call());
          }
          
          public String toString() {
            return callable.toString();
          }
        }executor);
  }
  
  public <T> ListenableFuture<T> submitAsync(final AsyncCallable<T> callable, Executor executor) {
    Preconditions.checkNotNull(callable);
    Preconditions.checkNotNull(executor);
    final TaskNonReentrantExecutor taskExecutor = new TaskNonReentrantExecutor(executor, this);
    AsyncCallable<T> task = new AsyncCallable<T>(this) {
        public ListenableFuture<T> call() throws Exception {
          if (!taskExecutor.trySetStarted())
            return Futures.immediateCancelledFuture(); 
          return callable.call();
        }
        
        public String toString() {
          return callable.toString();
        }
      };
    SettableFuture<Void> newFuture = SettableFuture.create();
    ListenableFuture<Void> oldFuture = this.ref.getAndSet(newFuture);
    TrustedListenableFutureTask<T> taskFuture = TrustedListenableFutureTask.create(task);
    oldFuture.addListener(taskFuture, taskExecutor);
    ListenableFuture<T> outputFuture = Futures.nonCancellationPropagating(taskFuture);
    Runnable listener = () -> {
        if (taskFuture.isDone()) {
          newFuture.setFuture(oldFuture);
        } else if (outputFuture.isCancelled() && taskExecutor.trySetCancelled()) {
          taskFuture.cancel(false);
        } 
      };
    outputFuture.addListener(listener, MoreExecutors.directExecutor());
    taskFuture.addListener(listener, MoreExecutors.directExecutor());
    return outputFuture;
  }
  
  enum RunningState {
    NOT_RUN, CANCELLED, STARTED;
  }
  
  private static final class TaskNonReentrantExecutor extends AtomicReference<RunningState> implements Executor, Runnable {
    @CheckForNull
    ExecutionSequencer sequencer;
    
    @CheckForNull
    Executor delegate;
    
    @CheckForNull
    Runnable task;
    
    @CheckForNull
    Thread submitting;
    
    private TaskNonReentrantExecutor(Executor delegate, ExecutionSequencer sequencer) {
      super(ExecutionSequencer.RunningState.NOT_RUN);
      this.delegate = delegate;
      this.sequencer = sequencer;
    }
    
    public void execute(Runnable task) {
      if (get() == ExecutionSequencer.RunningState.CANCELLED) {
        this.delegate = null;
        this.sequencer = null;
        return;
      } 
      this.submitting = Thread.currentThread();
      try {
        ExecutionSequencer.ThreadConfinedTaskQueue submittingTaskQueue = (Objects.<ExecutionSequencer>requireNonNull(this.sequencer)).latestTaskQueue;
        if (submittingTaskQueue.thread == this.submitting) {
          this.sequencer = null;
          Preconditions.checkState((submittingTaskQueue.nextTask == null));
          submittingTaskQueue.nextTask = task;
          submittingTaskQueue.nextExecutor = Objects.<Executor>requireNonNull(this.delegate);
          this.delegate = null;
        } else {
          Executor localDelegate = Objects.<Executor>requireNonNull(this.delegate);
          this.delegate = null;
          this.task = task;
          localDelegate.execute(this);
        } 
      } finally {
        this.submitting = null;
      } 
    }
    
    public void run() {
      Thread currentThread = Thread.currentThread();
      if (currentThread != this.submitting) {
        Runnable localTask = Objects.<Runnable>requireNonNull(this.task);
        this.task = null;
        localTask.run();
        return;
      } 
      ExecutionSequencer.ThreadConfinedTaskQueue executingTaskQueue = new ExecutionSequencer.ThreadConfinedTaskQueue();
      executingTaskQueue.thread = currentThread;
      (Objects.<ExecutionSequencer>requireNonNull(this.sequencer)).latestTaskQueue = executingTaskQueue;
      this.sequencer = null;
      try {
        Runnable localTask = Objects.<Runnable>requireNonNull(this.task);
        this.task = null;
        localTask.run();
        Runnable queuedTask;
        Executor queuedExecutor;
        while ((queuedTask = executingTaskQueue.nextTask) != null && (queuedExecutor = executingTaskQueue.nextExecutor) != null) {
          executingTaskQueue.nextTask = null;
          executingTaskQueue.nextExecutor = null;
          queuedExecutor.execute(queuedTask);
        } 
      } finally {
        executingTaskQueue.thread = null;
      } 
    }
    
    private boolean trySetStarted() {
      return compareAndSet(ExecutionSequencer.RunningState.NOT_RUN, ExecutionSequencer.RunningState.STARTED);
    }
    
    private boolean trySetCancelled() {
      return compareAndSet(ExecutionSequencer.RunningState.NOT_RUN, ExecutionSequencer.RunningState.CANCELLED);
    }
  }
}
