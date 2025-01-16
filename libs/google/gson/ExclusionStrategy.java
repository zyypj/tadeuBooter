package me.syncwrld.booter.libs.google.gson;

public interface ExclusionStrategy {
  boolean shouldSkipField(FieldAttributes paramFieldAttributes);
  
  boolean shouldSkipClass(Class<?> paramClass);
}
