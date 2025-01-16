package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.function.Function;
import java.util.function.Predicate;
import me.syncwrld.booter.libs.google.kyori.adventure.builder.AbstractBuilder;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

@NonExtendable
public interface JoinConfiguration extends Buildable<JoinConfiguration, JoinConfiguration.Builder>, Examinable {
  @NotNull
  static Builder builder() {
    return new JoinConfigurationImpl.BuilderImpl();
  }
  
  @NotNull
  static JoinConfiguration noSeparators() {
    return JoinConfigurationImpl.NULL;
  }
  
  @NotNull
  static JoinConfiguration newlines() {
    return JoinConfigurationImpl.STANDARD_NEW_LINES;
  }
  
  @NotNull
  static JoinConfiguration spaces() {
    return JoinConfigurationImpl.STANDARD_SPACES;
  }
  
  @NotNull
  static JoinConfiguration commas(boolean spaces) {
    return spaces ? JoinConfigurationImpl.STANDARD_COMMA_SPACE_SEPARATED : JoinConfigurationImpl.STANDARD_COMMA_SEPARATED;
  }
  
  @NotNull
  static JoinConfiguration arrayLike() {
    return JoinConfigurationImpl.STANDARD_ARRAY_LIKE;
  }
  
  @NotNull
  static JoinConfiguration separator(@Nullable ComponentLike separator) {
    if (separator == null)
      return JoinConfigurationImpl.NULL; 
    return (JoinConfiguration)builder().separator(separator).build();
  }
  
  @NotNull
  static JoinConfiguration separators(@Nullable ComponentLike separator, @Nullable ComponentLike lastSeparator) {
    if (separator == null && lastSeparator == null)
      return JoinConfigurationImpl.NULL; 
    return (JoinConfiguration)builder().separator(separator).lastSeparator(lastSeparator).build();
  }
  
  @Nullable
  Component prefix();
  
  @Nullable
  Component suffix();
  
  @Nullable
  Component separator();
  
  @Nullable
  Component lastSeparator();
  
  @Nullable
  Component lastSeparatorIfSerial();
  
  @NotNull
  Function<ComponentLike, Component> convertor();
  
  @NotNull
  Predicate<ComponentLike> predicate();
  
  @NotNull
  Style parentStyle();
  
  public static interface Builder extends AbstractBuilder<JoinConfiguration>, Buildable.Builder<JoinConfiguration> {
    @Contract("_ -> this")
    @NotNull
    Builder prefix(@Nullable ComponentLike param1ComponentLike);
    
    @Contract("_ -> this")
    @NotNull
    Builder suffix(@Nullable ComponentLike param1ComponentLike);
    
    @Contract("_ -> this")
    @NotNull
    Builder separator(@Nullable ComponentLike param1ComponentLike);
    
    @Contract("_ -> this")
    @NotNull
    Builder lastSeparator(@Nullable ComponentLike param1ComponentLike);
    
    @Contract("_ -> this")
    @NotNull
    Builder lastSeparatorIfSerial(@Nullable ComponentLike param1ComponentLike);
    
    @Contract("_ -> this")
    @NotNull
    Builder convertor(@NotNull Function<ComponentLike, Component> param1Function);
    
    @Contract("_ -> this")
    @NotNull
    Builder predicate(@NotNull Predicate<ComponentLike> param1Predicate);
    
    @Contract("_ -> this")
    @NotNull
    Builder parentStyle(@NotNull Style param1Style);
  }
}
