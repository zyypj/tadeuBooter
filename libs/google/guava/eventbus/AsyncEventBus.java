package me.syncwrld.booter.libs.google.guava.eventbus;

import java.util.concurrent.Executor;

@ElementTypesAreNonnullByDefault
public class AsyncEventBus extends EventBus {
  public AsyncEventBus(String identifier, Executor executor) {
    super(identifier, executor, Dispatcher.legacyAsync(), EventBus.LoggingHandler.INSTANCE);
  }
  
  public AsyncEventBus(Executor executor, SubscriberExceptionHandler subscriberExceptionHandler) {
    super("default", executor, Dispatcher.legacyAsync(), subscriberExceptionHandler);
  }
  
  public AsyncEventBus(Executor executor) {
    super("default", executor, Dispatcher.legacyAsync(), EventBus.LoggingHandler.INSTANCE);
  }
}
