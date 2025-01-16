package me.syncwrld.booter.libs.google.guava.base;

import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class CommonMatcher {
  public abstract boolean matches();
  
  public abstract boolean find();
  
  public abstract boolean find(int paramInt);
  
  public abstract String replaceAll(String paramString);
  
  public abstract int end();
  
  public abstract int start();
}
