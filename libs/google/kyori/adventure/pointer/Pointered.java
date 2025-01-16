package me.syncwrld.booter.libs.google.kyori.adventure.pointer;

import java.util.Optional;
import java.util.function.Supplier;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface Pointered {
  @NotNull
  default <T> Optional<T> get(@NotNull Pointer<T> pointer) {
    return pointers().get(pointer);
  }
  
  @Contract("_, null -> _; _, !null -> !null")
  @Nullable
  default <T> T getOrDefault(@NotNull Pointer<T> pointer, @Nullable T defaultValue) {
    return pointers().getOrDefault(pointer, defaultValue);
  }
  
  default <T> T getOrDefaultFrom(@NotNull Pointer<T> pointer, @NotNull Supplier<? extends T> defaultValue) {
    return pointers().getOrDefaultFrom(pointer, defaultValue);
  }
  
  @NotNull
  default Pointers pointers() {
    return Pointers.empty();
  }
}
