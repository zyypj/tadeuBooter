package me.syncwrld.booter.libs.unnamed.inject.scope;

import me.syncwrld.booter.libs.unnamed.inject.Provider;

public final class Scopes {
  public static final Scope SINGLETON = new LazySingletonScope();
  
  public static final Scope NONE = EmptyScope.INSTANCE;
  
  private static final ScopeScanner SCANNER = new ScopeScanner();
  
  public static ScopeScanner getScanner() {
    return SCANNER;
  }
  
  private enum EmptyScope implements Scope {
    INSTANCE;
    
    public <T> Provider<T> scope(Provider<T> unscoped) {
      return unscoped;
    }
    
    public String toString() {
      return "Emptá»³Scope";
    }
  }
}
