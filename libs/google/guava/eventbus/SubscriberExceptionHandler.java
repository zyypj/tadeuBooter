package me.syncwrld.booter.libs.google.guava.eventbus;

@ElementTypesAreNonnullByDefault
public interface SubscriberExceptionHandler {
  void handleException(Throwable paramThrowable, SubscriberExceptionContext paramSubscriberExceptionContext);
}
