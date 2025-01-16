package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface SelectorComponent extends BuildableComponent<SelectorComponent, SelectorComponent.Builder>, ScopedComponent<SelectorComponent> {
  @NotNull
  String pattern();
  
  @Contract(pure = true)
  @NotNull
  SelectorComponent pattern(@NotNull String paramString);
  
  @Nullable
  Component separator();
  
  @NotNull
  SelectorComponent separator(@Nullable ComponentLike paramComponentLike);
  
  @NotNull
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
        Stream.of(new ExaminableProperty[] { ExaminableProperty.of("pattern", pattern()), 
            ExaminableProperty.of("separator", separator()) }), super.examinableProperties());
  }
  
  public static interface Builder extends ComponentBuilder<SelectorComponent, Builder> {
    @Contract("_ -> this")
    @NotNull
    Builder pattern(@NotNull String param1String);
    
    @Contract("_ -> this")
    @NotNull
    Builder separator(@Nullable ComponentLike param1ComponentLike);
  }
}
