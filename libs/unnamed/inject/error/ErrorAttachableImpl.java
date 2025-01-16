package me.syncwrld.booter.libs.unnamed.inject.error;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public class ErrorAttachableImpl implements ErrorAttachable {
  private final List<String> errorMessages = new LinkedList<>();
  
  public void attach(String... messages) {
    Validate.notNull(messages, "errorMessages", new Object[0]);
    Collections.addAll(this.errorMessages, messages);
  }
  
  public void attach(String header, Throwable error) {
    Validate.notNull(error, "error", new Object[0]);
    String stackTrace = Errors.getStackTrace(error);
    if (header != null)
      stackTrace = header + "\n" + stackTrace; 
    this.errorMessages.add(stackTrace);
  }
  
  public void attachAll(ErrorAttachable attachable) {
    this.errorMessages.addAll(attachable.getErrorMessages());
  }
  
  public boolean hasErrors() {
    return !this.errorMessages.isEmpty();
  }
  
  public List<String> getErrorMessages() {
    return new ArrayList<>(this.errorMessages);
  }
  
  public void applySnapshot(List<String> errorMessages) {
    this.errorMessages.clear();
    this.errorMessages.addAll(errorMessages);
  }
  
  public String formatMessages() {
    return Errors.formatErrorMessages(this.errorMessages);
  }
  
  public int errorCount() {
    return this.errorMessages.size();
  }
  
  public void reportAttachedErrors() {
    throw new UnsupportedOperationException("The attached errors cannot be reported here!");
  }
}
