package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;

public interface EntityNBTComponent extends NBTComponent<EntityNBTComponent, EntityNBTComponent.Builder>, ScopedComponent<EntityNBTComponent> {
  @NotNull
  String selector();
  
  @Contract(pure = true)
  @NotNull
  EntityNBTComponent selector(@NotNull String paramString);
  
  @NotNull
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
        Stream.of(
          ExaminableProperty.of("selector", selector())), super
        
        .examinableProperties());
  }
  
  public static interface Builder extends NBTComponentBuilder<EntityNBTComponent, Builder> {
    @Contract("_ -> this")
    @NotNull
    Builder selector(@NotNull String param1String);
  }
}
