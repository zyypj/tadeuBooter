package me.syncwrld.booter.libs.unnamed.inject.error;

public class BindingException extends RuntimeException {
  public BindingException() {}
  
  public BindingException(String message) {
    super(message);
  }
  
  public BindingException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public BindingException(Throwable cause) {
    super(cause);
  }
}
