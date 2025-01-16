package me.syncwrld.booter.libs.google.guava.util.concurrent.internal;

public abstract class InternalFutureFailureAccess {
  protected abstract Throwable tryInternalFastPathGetFailure();
}
