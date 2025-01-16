package me.syncwrld.booter.libs.unnamed.inject.error;

import java.util.List;

public interface ErrorAttachable {
  void attach(String... paramVarArgs);
  
  void attach(String paramString, Throwable paramThrowable);
  
  void attachAll(ErrorAttachable paramErrorAttachable);
  
  List<String> getErrorMessages();
  
  void applySnapshot(List<String> paramList);
  
  String formatMessages();
  
  int errorCount();
  
  boolean hasErrors();
  
  void reportAttachedErrors();
}
