package me.syncwrld.booter.libs.google.kyori.adventure.identity;

import java.util.UUID;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class IdentityImpl implements Examinable, Identity {
  private final UUID uuid;
  
  IdentityImpl(UUID uuid) {
    this.uuid = uuid;
  }
  
  @NotNull
  public UUID uuid() {
    return this.uuid;
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof Identity))
      return false; 
    Identity that = (Identity)other;
    return this.uuid.equals(that.uuid());
  }
  
  public int hashCode() {
    return this.uuid.hashCode();
  }
}
