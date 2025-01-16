package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.ApiStatus.ScheduledForRemoval;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;

public interface TextComponent extends BuildableComponent<TextComponent, TextComponent.Builder>, ScopedComponent<TextComponent> {
  @Deprecated
  @ScheduledForRemoval(inVersion = "5.0.0")
  @NotNull
  static TextComponent ofChildren(@NotNull ComponentLike... components) {
    return Component.textOfChildren(components);
  }
  
  @NotNull
  String content();
  
  @Contract(pure = true)
  @NotNull
  TextComponent content(@NotNull String paramString);
  
  @NotNull
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
        Stream.of(
          ExaminableProperty.of("content", content())), super
        
        .examinableProperties());
  }
  
  public static interface Builder extends ComponentBuilder<TextComponent, Builder> {
    @NotNull
    String content();
    
    @Contract("_ -> this")
    @NotNull
    Builder content(@NotNull String param1String);
  }
}
