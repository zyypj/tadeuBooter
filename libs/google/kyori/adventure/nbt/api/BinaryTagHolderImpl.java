package me.syncwrld.booter.libs.google.kyori.adventure.nbt.api;

import java.util.Objects;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Codec;
import me.syncwrld.booter.libs.jtann.NotNull;

final class BinaryTagHolderImpl implements BinaryTagHolder {
  private final String string;
  
  BinaryTagHolderImpl(String string) {
    this.string = Objects.<String>requireNonNull(string, "string");
  }
  
  @NotNull
  public String string() {
    return this.string;
  }
  
  @NotNull
  public <T, DX extends Exception> T get(@NotNull Codec<T, String, DX, ?> codec) throws DX {
    return (T)codec.decode(this.string);
  }
  
  public int hashCode() {
    return 31 * this.string.hashCode();
  }
  
  public boolean equals(Object that) {
    if (!(that instanceof BinaryTagHolderImpl))
      return false; 
    return this.string.equals(((BinaryTagHolderImpl)that).string);
  }
  
  public String toString() {
    return this.string;
  }
}
