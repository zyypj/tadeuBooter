package me.syncwrld.booter.libs.google.kyori.adventure.permission;

import me.syncwrld.booter.libs.google.kyori.adventure.util.TriState;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class PermissionCheckers {
  static final PermissionChecker NOT_SET = new Always(TriState.NOT_SET);
  
  static final PermissionChecker FALSE = new Always(TriState.FALSE);
  
  static final PermissionChecker TRUE = new Always(TriState.TRUE);
  
  private static final class Always implements PermissionChecker {
    private final TriState value;
    
    private Always(TriState value) {
      this.value = value;
    }
    
    @NotNull
    public TriState value(@NotNull String permission) {
      return this.value;
    }
    
    public String toString() {
      return PermissionChecker.class.getSimpleName() + ".always(" + this.value + ")";
    }
    
    public boolean equals(@Nullable Object other) {
      if (this == other)
        return true; 
      if (other == null || getClass() != other.getClass())
        return false; 
      Always always = (Always)other;
      return (this.value == always.value);
    }
    
    public int hashCode() {
      return this.value.hashCode();
    }
  }
}
