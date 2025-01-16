package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface ScoreComponent extends BuildableComponent<ScoreComponent, ScoreComponent.Builder>, ScopedComponent<ScoreComponent> {
  @NotNull
  String name();
  
  @Contract(pure = true)
  @NotNull
  ScoreComponent name(@NotNull String paramString);
  
  @NotNull
  String objective();
  
  @Contract(pure = true)
  @NotNull
  ScoreComponent objective(@NotNull String paramString);
  
  @Deprecated
  @Nullable
  String value();
  
  @Deprecated
  @Contract(pure = true)
  @NotNull
  ScoreComponent value(@Nullable String paramString);
  
  @NotNull
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
        Stream.of(new ExaminableProperty[] { ExaminableProperty.of("name", name()), 
            ExaminableProperty.of("objective", objective()), 
            ExaminableProperty.of("value", value()) }), super.examinableProperties());
  }
  
  public static interface Builder extends ComponentBuilder<ScoreComponent, Builder> {
    @Contract("_ -> this")
    @NotNull
    Builder name(@NotNull String param1String);
    
    @Contract("_ -> this")
    @NotNull
    Builder objective(@NotNull String param1String);
    
    @Deprecated
    @Contract("_ -> this")
    @NotNull
    Builder value(@Nullable String param1String);
  }
}
