package me.syncwrld.booter.libs.google.kyori.adventure.text.flattener;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import me.syncwrld.booter.libs.google.kyori.adventure.builder.AbstractBuilder;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface ComponentFlattener extends Buildable<ComponentFlattener, ComponentFlattener.Builder> {
  @NotNull
  static Builder builder() {
    return new ComponentFlattenerImpl.BuilderImpl();
  }
  
  @NotNull
  static ComponentFlattener basic() {
    return ComponentFlattenerImpl.BASIC;
  }
  
  @NotNull
  static ComponentFlattener textOnly() {
    return ComponentFlattenerImpl.TEXT_ONLY;
  }
  
  void flatten(@NotNull Component paramComponent, @NotNull FlattenerListener paramFlattenerListener);
  
  public static interface Builder extends AbstractBuilder<ComponentFlattener>, Buildable.Builder<ComponentFlattener> {
    @NotNull
    <T extends Component> Builder mapper(@NotNull Class<T> param1Class, @NotNull Function<T, String> param1Function);
    
    @NotNull
    <T extends Component> Builder complexMapper(@NotNull Class<T> param1Class, @NotNull BiConsumer<T, Consumer<Component>> param1BiConsumer);
    
    @NotNull
    Builder unknownMapper(@Nullable Function<Component, String> param1Function);
  }
}
