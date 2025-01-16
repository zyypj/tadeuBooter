package me.syncwrld.booter.libs.google.kyori.adventure.util;

import java.util.function.Consumer;
import me.syncwrld.booter.libs.google.kyori.adventure.builder.AbstractBuilder;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface Buildable<R, B extends Buildable.Builder<R>> {
  @Deprecated
  @Contract(mutates = "param1")
  @NotNull
  static <R extends Buildable<R, B>, B extends Builder<R>> R configureAndBuild(@NotNull B builder, @Nullable Consumer<? super B> consumer) {
    return (R)AbstractBuilder.configureAndBuild((AbstractBuilder)builder, consumer);
  }
  
  @Contract(value = "-> new", pure = true)
  @NotNull
  B toBuilder();
  
  @Deprecated
  public static interface Builder<R> extends AbstractBuilder<R> {
    @Contract(value = "-> new", pure = true)
    @NotNull
    R build();
  }
}
