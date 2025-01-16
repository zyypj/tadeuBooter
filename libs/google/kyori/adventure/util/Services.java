package me.syncwrld.booter.libs.google.kyori.adventure.util;

import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceLoader;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.properties.AdventureProperties;
import me.syncwrld.booter.libs.jtann.NotNull;

public final class Services {
  private static final boolean SERVICE_LOAD_FAILURES_ARE_FATAL = Boolean.TRUE.equals(AdventureProperties.SERVICE_LOAD_FAILURES_ARE_FATAL.value());
  
  @NotNull
  public static <P> Optional<P> service(@NotNull Class<P> type) {
    ServiceLoader<P> loader = Services0.loader(type);
    Iterator<P> it = loader.iterator();
    while (it.hasNext()) {
      P instance;
      try {
        instance = it.next();
      } catch (Throwable t) {
        if (SERVICE_LOAD_FAILURES_ARE_FATAL)
          throw new IllegalStateException("Encountered an exception loading service " + type, t); 
        continue;
      } 
      if (it.hasNext())
        throw new IllegalStateException("Expected to find one service " + type + ", found multiple"); 
      return Optional.of(instance);
    } 
    return Optional.empty();
  }
  
  @NotNull
  public static <P> Optional<P> serviceWithFallback(@NotNull Class<P> type) {
    ServiceLoader<P> loader = Services0.loader(type);
    Iterator<P> it = loader.iterator();
    P firstFallback = null;
    while (it.hasNext()) {
      P instance;
      try {
        instance = it.next();
      } catch (Throwable t) {
        if (SERVICE_LOAD_FAILURES_ARE_FATAL)
          throw new IllegalStateException("Encountered an exception loading service " + type, t); 
        continue;
      } 
      if (instance instanceof Fallback) {
        if (firstFallback == null)
          firstFallback = instance; 
        continue;
      } 
      return Optional.of(instance);
    } 
    return Optional.ofNullable(firstFallback);
  }
  
  public static interface Fallback {}
}
