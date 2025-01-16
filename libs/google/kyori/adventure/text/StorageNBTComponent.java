package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;

public interface StorageNBTComponent extends NBTComponent<StorageNBTComponent, StorageNBTComponent.Builder>, ScopedComponent<StorageNBTComponent> {
  @NotNull
  Key storage();
  
  @Contract(pure = true)
  @NotNull
  StorageNBTComponent storage(@NotNull Key paramKey);
  
  @NotNull
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
        Stream.of(
          ExaminableProperty.of("storage", storage())), super
        
        .examinableProperties());
  }
  
  public static interface Builder extends NBTComponentBuilder<StorageNBTComponent, Builder> {
    @Contract("_ -> this")
    @NotNull
    Builder storage(@NotNull Key param1Key);
  }
}
