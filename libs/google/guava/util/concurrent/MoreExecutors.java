package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.GuardedBy;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.base.Supplier;
import me.syncwrld.booter.libs.google.guava.base.Throwables;
import me.syncwrld.booter.libs.google.guava.collect.Lists;
import me.syncwrld.booter.libs.google.guava.collect.Queues;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
public final class MoreExecutors {
  @J2ktIncompatible
  @GwtIncompatible
  public static ExecutorService getExitingExecutorService(ThreadPoolExecutor executor, Duration terminationTimeout) {
    return getExitingExecutorService(executor, 
        Internal.toNanosSaturated(terminationTimeout), TimeUnit.NANOSECONDS);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static ExecutorService getExitingExecutorService(ThreadPoolExecutor executor, long terminationTimeout, TimeUnit timeUnit) {
    return (new Application()).getExitingExecutorService(executor, terminationTimeout, timeUnit);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static ExecutorService getExitingExecutorService(ThreadPoolExecutor executor) {
    return (new Application()).getExitingExecutorService(executor);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor executor, Duration terminationTimeout) {
    return getExitingScheduledExecutorService(executor, 
        Internal.toNanosSaturated(terminationTimeout), TimeUnit.NANOSECONDS);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor executor, long terminationTimeout, TimeUnit timeUnit) {
    return (new Application())
      .getExitingScheduledExecutorService(executor, terminationTimeout, timeUnit);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor executor) {
    return (new Application()).getExitingScheduledExecutorService(executor);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static void addDelayedShutdownHook(ExecutorService service, Duration terminationTimeout) {
    addDelayedShutdownHook(service, Internal.toNanosSaturated(terminationTimeout), TimeUnit.NANOSECONDS);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static void addDelayedShutdownHook(ExecutorService service, long terminationTimeout, TimeUnit timeUnit) {
    (new Application()).addDelayedShutdownHook(service, terminationTimeout, timeUnit);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  @VisibleForTesting
  static class Application {
    final ExecutorService getExitingExecutorService(ThreadPoolExecutor executor, long terminationTimeout, TimeUnit timeUnit) {
      MoreExecutors.useDaemonThreadFactory(executor);
      ExecutorService service = Executors.unconfigurableExecutorService(executor);
      addDelayedShutdownHook(executor, terminationTimeout, timeUnit);
      return service;
    }
    
    final ExecutorService getExitingExecutorService(ThreadPoolExecutor executor) {
      return getExitingExecutorService(executor, 120L, TimeUnit.SECONDS);
    }
    
    final ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor executor, long terminationTimeout, TimeUnit timeUnit) {
      MoreExecutors.useDaemonThreadFactory(executor);
      ScheduledExecutorService service = Executors.unconfigurableScheduledExecutorService(executor);
      addDelayedShutdownHook(executor, terminationTimeout, timeUnit);
      return service;
    }
    
    final ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor executor) {
      return getExitingScheduledExecutorService(executor, 120L, TimeUnit.SECONDS);
    }
    
    final void addDelayedShutdownHook(final ExecutorService service, final long terminationTimeout, final TimeUnit timeUnit) {
      Preconditions.checkNotNull(service);
      Preconditions.checkNotNull(timeUnit);
      addShutdownHook(
          MoreExecutors.newThread("DelayedShutdownHook-for-" + service, new Runnable(this) {
              public void run() {
                try {
                  service.shutdown();
                  service.awaitTermination(terminationTimeout, timeUnit);
                } catch (InterruptedException interruptedException) {}
              }
            }));
    }
    
    @VisibleForTesting
    void addShutdownHook(Thread hook) {
      Runtime.getRuntime().addShutdownHook(hook);
    }
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  private static void useDaemonThreadFactory(ThreadPoolExecutor executor) {
    executor.setThreadFactory((new ThreadFactoryBuilder())
        
        .setDaemon(true)
        .setThreadFactory(executor.getThreadFactory())
        .build());
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  private static final class DirectExecutorService extends AbstractListeningExecutorService {
    private final Object lock = new Object();
    
    @GuardedBy("lock")
    private int runningTasks = 0;
    
    @GuardedBy("lock")
    private boolean shutdown = false;
    
    public void execute(Runnable command) {
      startTask();
      try {
        command.run();
      } finally {
        endTask();
      } 
    }
    
    public boolean isShutdown() {
      synchronized (this.lock) {
        return this.shutdown;
      } 
    }
    
    public void shutdown() {
      synchronized (this.lock) {
        this.shutdown = true;
        if (this.runningTasks == 0)
          this.lock.notifyAll(); 
      } 
    }
    
    public List<Runnable> shutdownNow() {
      shutdown();
      return Collections.emptyList();
    }
    
    public boolean isTerminated() {
      synchronized (this.lock) {
        return (this.shutdown && this.runningTasks == 0);
      } 
    }
    
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      long nanos = unit.toNanos(timeout);
      synchronized (this.lock) {
        while (true) {
          if (this.shutdown && this.runningTasks == 0)
            return true; 
          if (nanos <= 0L)
            return false; 
          long now = System.nanoTime();
          TimeUnit.NANOSECONDS.timedWait(this.lock, nanos);
          nanos -= System.nanoTime() - now;
        } 
      } 
    }
    
    private void startTask() {
      synchronized (this.lock) {
        if (this.shutdown)
          throw new RejectedExecutionException("Executor already shutdown"); 
        this.runningTasks++;
      } 
    }
    
    private void endTask() {
      synchronized (this.lock) {
        int numRunning = --this.runningTasks;
        if (numRunning == 0)
          this.lock.notifyAll(); 
      } 
    }
    
    private DirectExecutorService() {}
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static ListeningExecutorService newDirectExecutorService() {
    return new DirectExecutorService();
  }
  
  public static Executor directExecutor() {
    return DirectExecutor.INSTANCE;
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static Executor newSequentialExecutor(Executor delegate) {
    return new SequentialExecutor(delegate);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static ListeningExecutorService listeningDecorator(ExecutorService delegate) {
    return (delegate instanceof ListeningExecutorService) ? 
      (ListeningExecutorService)delegate : (
      (delegate instanceof ScheduledExecutorService) ? 
      new ScheduledListeningDecorator((ScheduledExecutorService)delegate) : 
      new ListeningDecorator(delegate));
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static ListeningScheduledExecutorService listeningDecorator(ScheduledExecutorService delegate) {
    return (delegate instanceof ListeningScheduledExecutorService) ? 
      (ListeningScheduledExecutorService)delegate : 
      new ScheduledListeningDecorator(delegate);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  private static class ListeningDecorator extends AbstractListeningExecutorService {
    private final ExecutorService delegate;
    
    ListeningDecorator(ExecutorService delegate) {
      this.delegate = (ExecutorService)Preconditions.checkNotNull(delegate);
    }
    
    public final boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      return this.delegate.awaitTermination(timeout, unit);
    }
    
    public final boolean isShutdown() {
      return this.delegate.isShutdown();
    }
    
    public final boolean isTerminated() {
      return this.delegate.isTerminated();
    }
    
    public final void shutdown() {
      this.delegate.shutdown();
    }
    
    public final List<Runnable> shutdownNow() {
      return this.delegate.shutdownNow();
    }
    
    public final void execute(Runnable command) {
      this.delegate.execute(command);
    }
    
    public final String toString() {
      return super.toString() + "[" + this.delegate + "]";
    }
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  private static final class ScheduledListeningDecorator extends ListeningDecorator implements ListeningScheduledExecutorService {
    final ScheduledExecutorService delegate;
    
    ScheduledListeningDecorator(ScheduledExecutorService delegate) {
      super(delegate);
      this.delegate = (ScheduledExecutorService)Preconditions.checkNotNull(delegate);
    }
    
    public ListenableScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
      TrustedListenableFutureTask<Void> task = TrustedListenableFutureTask.create(command, null);
      ScheduledFuture<?> scheduled = this.delegate.schedule(task, delay, unit);
      return new ListenableScheduledTask(task, scheduled);
    }
    
    public <V> ListenableScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
      TrustedListenableFutureTask<V> task = TrustedListenableFutureTask.create(callable);
      ScheduledFuture<?> scheduled = this.delegate.schedule(task, delay, unit);
      return new ListenableScheduledTask<>(task, scheduled);
    }
    
    public ListenableScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
      NeverSuccessfulListenableFutureTask task = new NeverSuccessfulListenableFutureTask(command);
      ScheduledFuture<?> scheduled = this.delegate.scheduleAtFixedRate(task, initialDelay, period, unit);
      return new ListenableScheduledTask(task, scheduled);
    }
    
    public ListenableScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
      NeverSuccessfulListenableFutureTask task = new NeverSuccessfulListenableFutureTask(command);
      ScheduledFuture<?> scheduled = this.delegate.scheduleWithFixedDelay(task, initialDelay, delay, unit);
      return new ListenableScheduledTask(task, scheduled);
    }
    
    private static final class ListenableScheduledTask<V> extends ForwardingListenableFuture.SimpleForwardingListenableFuture<V> implements ListenableScheduledFuture<V> {
      private final ScheduledFuture<?> scheduledDelegate;
      
      public ListenableScheduledTask(ListenableFuture<V> listenableDelegate, ScheduledFuture<?> scheduledDelegate) {
        super(listenableDelegate);
        this.scheduledDelegate = scheduledDelegate;
      }
      
      public boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancelled = super.cancel(mayInterruptIfRunning);
        if (cancelled)
          this.scheduledDelegate.cancel(mayInterruptIfRunning); 
        return cancelled;
      }
      
      public long getDelay(TimeUnit unit) {
        return this.scheduledDelegate.getDelay(unit);
      }
      
      public int compareTo(Delayed other) {
        return this.scheduledDelegate.compareTo(other);
      }
    }
    
    @J2ktIncompatible
    @GwtIncompatible
    private static final class NeverSuccessfulListenableFutureTask extends AbstractFuture.TrustedFuture<Void> implements Runnable {
      private final Runnable delegate;
      
      public NeverSuccessfulListenableFutureTask(Runnable delegate) {
        this.delegate = (Runnable)Preconditions.checkNotNull(delegate);
      }
      
      public void run() {
        try {
          this.delegate.run();
        } catch (Throwable t) {
          setException(t);
          throw t;
        } 
      }
      
      protected String pendingToString() {
        return "task=[" + this.delegate + "]";
      }
    }
  }
  
  @ParametricNullness
  @J2ktIncompatible
  @GwtIncompatible
  static <T> T invokeAnyImpl(ListeningExecutorService executorService, Collection<? extends Callable<T>> tasks, boolean timed, Duration timeout) throws InterruptedException, ExecutionException, TimeoutException {
    return invokeAnyImpl(executorService, tasks, timed, 
        Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  @ParametricNullness
  @J2ktIncompatible
  @GwtIncompatible
  static <T> T invokeAnyImpl(ListeningExecutorService executorService, Collection<? extends Callable<T>> tasks, boolean timed, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    Preconditions.checkNotNull(executorService);
    Preconditions.checkNotNull(unit);
    int ntasks = tasks.size();
    Preconditions.checkArgument((ntasks > 0));
    List<Future<T>> futures = Lists.newArrayListWithCapacity(ntasks);
    BlockingQueue<Future<T>> futureQueue = Queues.newLinkedBlockingQueue();
    long timeoutNanos = unit.toNanos(timeout);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  private static <T> ListenableFuture<T> submitAndAddQueueListener(ListeningExecutorService executorService, Callable<T> task, final BlockingQueue<Future<T>> queue) {
    final ListenableFuture<T> future = executorService.submit(task);
    future.addListener(new Runnable() {
          public void run() {
            queue.add(future);
          }
        },  directExecutor());
    return future;
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static ThreadFactory platformThreadFactory() {
    if (!isAppEngineWithApiClasses())
      return Executors.defaultThreadFactory(); 
    try {
      return 
        (ThreadFactory)Class.forName("com.google.appengine.api.ThreadManager")
        .getMethod("currentRequestThreadFactory", new Class[0])
        .invoke(null, new Object[0]);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", e);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", e);
    } catch (InvocationTargetException e) {
      throw Throwables.propagate(e.getCause());
    } 
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  private static boolean isAppEngineWithApiClasses() {
    if (System.getProperty("com.google.appengine.runtime.environment") == null)
      return false; 
    try {
      Class.forName("com.google.appengine.api.utils.SystemProperty");
    } catch (ClassNotFoundException e) {
      return false;
    } 
    try {
      return 
        
        (Class.forName("com.google.apphosting.api.ApiProxy").getMethod("getCurrentEnvironment", new Class[0]).invoke(null, new Object[0]) != null);
    } catch (ClassNotFoundException e) {
      return false;
    } catch (InvocationTargetException e) {
      return false;
    } catch (IllegalAccessException e) {
      return false;
    } catch (NoSuchMethodException e) {
      return false;
    } 
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  static Thread newThread(String name, Runnable runnable) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(runnable);
    Thread result = Objects.<Thread>requireNonNull(platformThreadFactory().newThread(runnable));
    try {
      result.setName(name);
    } catch (SecurityException securityException) {}
    return result;
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  static Executor renamingDecorator(final Executor executor, final Supplier<String> nameSupplier) {
    Preconditions.checkNotNull(executor);
    Preconditions.checkNotNull(nameSupplier);
    return new Executor() {
        public void execute(Runnable command) {
          executor.execute(Callables.threadRenaming(command, nameSupplier));
        }
      };
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  static ExecutorService renamingDecorator(ExecutorService service, final Supplier<String> nameSupplier) {
    Preconditions.checkNotNull(service);
    Preconditions.checkNotNull(nameSupplier);
    return new WrappingExecutorService(service) {
        protected <T> Callable<T> wrapTask(Callable<T> callable) {
          return Callables.threadRenaming(callable, nameSupplier);
        }
        
        protected Runnable wrapTask(Runnable command) {
          return Callables.threadRenaming(command, nameSupplier);
        }
      };
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  static ScheduledExecutorService renamingDecorator(ScheduledExecutorService service, final Supplier<String> nameSupplier) {
    Preconditions.checkNotNull(service);
    Preconditions.checkNotNull(nameSupplier);
    return new WrappingScheduledExecutorService(service) {
        protected <T> Callable<T> wrapTask(Callable<T> callable) {
          return Callables.threadRenaming(callable, nameSupplier);
        }
        
        protected Runnable wrapTask(Runnable command) {
          return Callables.threadRenaming(command, nameSupplier);
        }
      };
  }
  
  @CanIgnoreReturnValue
  @J2ktIncompatible
  @GwtIncompatible
  public static boolean shutdownAndAwaitTermination(ExecutorService service, Duration timeout) {
    return shutdownAndAwaitTermination(service, Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  @CanIgnoreReturnValue
  @J2ktIncompatible
  @GwtIncompatible
  public static boolean shutdownAndAwaitTermination(ExecutorService service, long timeout, TimeUnit unit) {
    long halfTimeoutNanos = unit.toNanos(timeout) / 2L;
    service.shutdown();
    try {
      if (!service.awaitTermination(halfTimeoutNanos, TimeUnit.NANOSECONDS)) {
        service.shutdownNow();
        service.awaitTermination(halfTimeoutNanos, TimeUnit.NANOSECONDS);
      } 
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      service.shutdownNow();
    } 
    return service.isTerminated();
  }
  
  static Executor rejectionPropagatingExecutor(final Executor delegate, final AbstractFuture<?> future) {
    Preconditions.checkNotNull(delegate);
    Preconditions.checkNotNull(future);
    if (delegate == directExecutor())
      return delegate; 
    return new Executor() {
        public void execute(Runnable command) {
          try {
            delegate.execute(command);
          } catch (RejectedExecutionException e) {
            future.setException(e);
          } 
        }
      };
  }
}
