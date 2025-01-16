package me.syncwrld.booter.libs.google.yamlbeans;

import java.io.IOException;

public class YamlException extends IOException {
  public YamlException() {}
  
  public YamlException(String message, Throwable cause) {
    super(message);
    initCause(cause);
  }
  
  public YamlException(String message) {
    super(message);
  }
  
  public YamlException(Throwable cause) {
    initCause(cause);
  }
}
