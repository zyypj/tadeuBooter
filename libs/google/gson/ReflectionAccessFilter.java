package me.syncwrld.booter.libs.google.gson;

import me.syncwrld.booter.libs.google.gson.internal.ReflectionAccessFilterHelper;

public interface ReflectionAccessFilter {
  public enum FilterResult {
    ALLOW, INDECISIVE, BLOCK_INACCESSIBLE, BLOCK_ALL;
  }
  
  public static final ReflectionAccessFilter BLOCK_INACCESSIBLE_JAVA = new ReflectionAccessFilter() {
      public ReflectionAccessFilter.FilterResult check(Class<?> rawClass) {
        return ReflectionAccessFilterHelper.isJavaType(rawClass) ? 
          ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE : 
          ReflectionAccessFilter.FilterResult.INDECISIVE;
      }
    };
  
  public static final ReflectionAccessFilter BLOCK_ALL_JAVA = new ReflectionAccessFilter() {
      public ReflectionAccessFilter.FilterResult check(Class<?> rawClass) {
        return ReflectionAccessFilterHelper.isJavaType(rawClass) ? 
          ReflectionAccessFilter.FilterResult.BLOCK_ALL : 
          ReflectionAccessFilter.FilterResult.INDECISIVE;
      }
    };
  
  public static final ReflectionAccessFilter BLOCK_ALL_ANDROID = new ReflectionAccessFilter() {
      public ReflectionAccessFilter.FilterResult check(Class<?> rawClass) {
        return ReflectionAccessFilterHelper.isAndroidType(rawClass) ? 
          ReflectionAccessFilter.FilterResult.BLOCK_ALL : 
          ReflectionAccessFilter.FilterResult.INDECISIVE;
      }
    };
  
  public static final ReflectionAccessFilter BLOCK_ALL_PLATFORM = new ReflectionAccessFilter() {
      public ReflectionAccessFilter.FilterResult check(Class<?> rawClass) {
        return ReflectionAccessFilterHelper.isAnyPlatformType(rawClass) ? 
          ReflectionAccessFilter.FilterResult.BLOCK_ALL : 
          ReflectionAccessFilter.FilterResult.INDECISIVE;
      }
    };
  
  FilterResult check(Class<?> paramClass);
}
