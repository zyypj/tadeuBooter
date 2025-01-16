package me.syncwrld.booter.libs.google.guava.cache;

import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public enum RemovalCause {
  EXPLICIT {
    boolean wasEvicted() {
      return false;
    }
  },
  REPLACED {
    boolean wasEvicted() {
      return false;
    }
  },
  COLLECTED {
    boolean wasEvicted() {
      return true;
    }
  },
  EXPIRED {
    boolean wasEvicted() {
      return true;
    }
  },
  SIZE {
    boolean wasEvicted() {
      return true;
    }
  };
  
  abstract boolean wasEvicted();
}
