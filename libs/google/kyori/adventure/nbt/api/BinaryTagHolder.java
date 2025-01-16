package me.syncwrld.booter.libs.google.kyori.adventure.nbt.api;

import me.syncwrld.booter.libs.google.kyori.adventure.util.Codec;
import me.syncwrld.booter.libs.jtann.ApiStatus.ScheduledForRemoval;
import me.syncwrld.booter.libs.jtann.NotNull;

public interface BinaryTagHolder {
  @NotNull
  static <T, EX extends Exception> BinaryTagHolder encode(@NotNull T nbt, @NotNull Codec<? super T, String, ?, EX> codec) throws EX {
    return new BinaryTagHolderImpl((String)codec.encode(nbt));
  }
  
  @NotNull
  static BinaryTagHolder binaryTagHolder(@NotNull String string) {
    return new BinaryTagHolderImpl(string);
  }
  
  @Deprecated
  @ScheduledForRemoval(inVersion = "5.0.0")
  @NotNull
  static BinaryTagHolder of(@NotNull String string) {
    return new BinaryTagHolderImpl(string);
  }
  
  @NotNull
  String string();
  
  @NotNull
  <T, DX extends Exception> T get(@NotNull Codec<T, String, DX, ?> paramCodec) throws DX;
}
