package me.syncwrld.booter.libs.google.kyori.adventure.pointer;

import java.util.Optional;
import java.util.function.Supplier;
import me.syncwrld.booter.libs.google.kyori.adventure.builder.AbstractBuilder;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface Pointers extends Buildable<Pointers, Pointers.Builder> {
  @Contract(pure = true)
  @NotNull
  static Pointers empty() {
    return PointersImpl.EMPTY;
  }
  
  @Contract(pure = true)
  @NotNull
  static Builder builder() {
    return new PointersImpl.BuilderImpl();
  }
  
  @NotNull
  <T> Optional<T> get(@NotNull Pointer<T> paramPointer);
  
  @Contract("_, null -> _; _, !null -> !null")
  @Nullable
  default <T> T getOrDefault(@NotNull Pointer<T> pointer, @Nullable T defaultValue) {
    return get(pointer).orElse(defaultValue);
  }
  
  default <T> T getOrDefaultFrom(@NotNull Pointer<T> pointer, @NotNull Supplier<? extends T> defaultValue) {
    return get(pointer).orElseGet(defaultValue);
  }
  
  <T> boolean supports(@NotNull Pointer<T> paramPointer);
  
  public static interface Builder extends AbstractBuilder<Pointers>, Buildable.Builder<Pointers> {
    @Contract("_, _ -> this")
    @NotNull
    default <T> Builder withStatic(@NotNull Pointer<T> pointer, @Nullable T value) {
      return withDynamic(pointer, () -> value);
    }
    
    @Contract("_, _ -> this")
    @NotNull
    <T> Builder withDynamic(@NotNull Pointer<T> param1Pointer, @NotNull Supplier<T> param1Supplier);
  }
}
