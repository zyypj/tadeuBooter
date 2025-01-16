package me.syncwrld.booter.libs.google.kyori.adventure.pointer;

import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.NotNull;

public interface Pointer<V> extends Examinable {
  @NotNull
  static <V> Pointer<V> pointer(@NotNull Class<V> type, @NotNull Key key) {
    return new PointerImpl<>(type, key);
  }
  
  @NotNull
  Class<V> type();
  
  @NotNull
  Key key();
  
  @NotNull
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("type", type()), 
          ExaminableProperty.of("key", key()) });
  }
}
