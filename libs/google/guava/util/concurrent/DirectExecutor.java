package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.concurrent.Executor;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
enum DirectExecutor implements Executor {
  INSTANCE;
  
  public void execute(Runnable command) {
    command.run();
  }
  
  public String toString() {
    return "MoreExecutors.directExecutor()";
  }
}
