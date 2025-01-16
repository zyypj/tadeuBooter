package me.syncwrld.booter.libs.google.kyori.adventure.util;

import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface ComponentMessageThrowable {
  @Nullable
  static Component getMessage(@Nullable Throwable throwable) {
    if (throwable instanceof ComponentMessageThrowable)
      return ((ComponentMessageThrowable)throwable).componentMessage(); 
    return null;
  }
  
  @Nullable
  static Component getOrConvertMessage(@Nullable Throwable throwable) {
    if (throwable instanceof ComponentMessageThrowable)
      return ((ComponentMessageThrowable)throwable).componentMessage(); 
    if (throwable != null) {
      String message = throwable.getMessage();
      if (message != null)
        return (Component)Component.text(message); 
    } 
    return null;
  }
  
  @Nullable
  Component componentMessage();
}
