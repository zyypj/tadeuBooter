package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
@J2ktIncompatible
public abstract class AbstractExecutionThreadService implements Service {
  private static final LazyLogger logger = new LazyLogger(AbstractExecutionThreadService.class);
  
  private final Service delegate = new AbstractService() {
      protected final void doStart() {
        Executor executor = MoreExecutors.renamingDecorator(AbstractExecutionThreadService.this.executor(), () -> AbstractExecutionThreadService.this.serviceName());
        executor.execute(() -> {
              try {
                AbstractExecutionThreadService.this.startUp();
                notifyStarted();
                if (isRunning())
                  try {
                    AbstractExecutionThreadService.this.run();
                  } catch (Throwable t) {
                    Platform.restoreInterruptIfIsInterruptedException(t);
                    try {
                      AbstractExecutionThreadService.this.shutDown();
                    } catch (Exception ignored) {
                      Platform.restoreInterruptIfIsInterruptedException(ignored);
                      AbstractExecutionThreadService.logger.get().log(Level.WARNING, "Error while attempting to shut down the service after failure.", ignored);
                    } 
                    notifyFailed(t);
                    return;
                  }  
                AbstractExecutionThreadService.this.shutDown();
                notifyStopped();
              } catch (Throwable t) {
                Platform.restoreInterruptIfIsInterruptedException(t);
                notifyFailed(t);
              } 
            });
      }
      
      protected void doStop() {
        AbstractExecutionThreadService.this.triggerShutdown();
      }
      
      public String toString() {
        return AbstractExecutionThreadService.this.toString();
      }
    };
  
  protected void startUp() throws Exception {}
  
  protected void shutDown() throws Exception {}
  
  protected void triggerShutdown() {}
  
  protected Executor executor() {
    return command -> MoreExecutors.newThread(serviceName(), command).start();
  }
  
  public String toString() {
    return serviceName() + " [" + state() + "]";
  }
  
  public final boolean isRunning() {
    return this.delegate.isRunning();
  }
  
  public final Service.State state() {
    return this.delegate.state();
  }
  
  public final void addListener(Service.Listener listener, Executor executor) {
    this.delegate.addListener(listener, executor);
  }
  
  public final Throwable failureCause() {
    return this.delegate.failureCause();
  }
  
  @CanIgnoreReturnValue
  public final Service startAsync() {
    this.delegate.startAsync();
    return this;
  }
  
  @CanIgnoreReturnValue
  public final Service stopAsync() {
    this.delegate.stopAsync();
    return this;
  }
  
  public final void awaitRunning() {
    this.delegate.awaitRunning();
  }
  
  public final void awaitRunning(Duration timeout) throws TimeoutException {
    super.awaitRunning(timeout);
  }
  
  public final void awaitRunning(long timeout, TimeUnit unit) throws TimeoutException {
    this.delegate.awaitRunning(timeout, unit);
  }
  
  public final void awaitTerminated() {
    this.delegate.awaitTerminated();
  }
  
  public final void awaitTerminated(Duration timeout) throws TimeoutException {
    super.awaitTerminated(timeout);
  }
  
  public final void awaitTerminated(long timeout, TimeUnit unit) throws TimeoutException {
    this.delegate.awaitTerminated(timeout, unit);
  }
  
  protected String serviceName() {
    return getClass().getSimpleName();
  }
  
  protected abstract void run() throws Exception;
}
