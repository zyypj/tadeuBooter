package me.syncwrld.booter.libs.google.gson.internal;

public final class $Gson$Preconditions {
  private $Gson$Preconditions() {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public static <T> T checkNotNull(T obj) {
    if (obj == null)
      throw new NullPointerException(); 
    return obj;
  }
  
  public static void checkArgument(boolean condition) {
    if (!condition)
      throw new IllegalArgumentException(); 
  }
}
