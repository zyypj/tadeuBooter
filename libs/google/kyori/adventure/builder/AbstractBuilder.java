package me.syncwrld.booter.libs.google.kyori.adventure.builder;

import java.util.function.Consumer;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

@FunctionalInterface
public interface AbstractBuilder<R> {
  @Contract(mutates = "param1")
  @NotNull
  static <R, B extends AbstractBuilder<R>> R configureAndBuild(@NotNull B builder, @Nullable Consumer<? super B> consumer) {
    if (consumer != null)
      consumer.accept(builder); 
    return builder.build();
  }
  
  @Contract(value = "-> new", pure = true)
  @NotNull
  R build();
}
