package me.syncwrld.booter.libs.unnamed.inject.error;

public class InjectionException extends RuntimeException {
  public InjectionException() {}
  
  public InjectionException(String message) {
    super(message);
  }
  
  public InjectionException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public InjectionException(Throwable cause) {
    super(cause);
  }
}
