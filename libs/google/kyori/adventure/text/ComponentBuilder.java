package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import me.syncwrld.booter.libs.google.kyori.adventure.builder.AbstractBuilder;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.ClickEvent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.HoverEventSource;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.MutableStyleSetter;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.StyleSetter;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.TextColor;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.TextDecoration;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

@NonExtendable
public interface ComponentBuilder<C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>> extends AbstractBuilder<C>, Buildable.Builder<C>, ComponentBuilderApplicable, ComponentLike, MutableStyleSetter<B> {
  @Contract("_ -> this")
  @NotNull
  default B append(@NotNull ComponentLike component) {
    return append(component.asComponent());
  }
  
  @Contract("_ -> this")
  @NotNull
  default B append(@NotNull ComponentBuilder<?, ?> builder) {
    return append((Component)builder.build());
  }
  
  @NotNull
  default B appendNewline() {
    return append(Component.newline());
  }
  
  @NotNull
  default B appendSpace() {
    return append(Component.space());
  }
  
  @Contract("_ -> this")
  @NotNull
  default B apply(@NotNull Consumer<? super ComponentBuilder<?, ?>> consumer) {
    consumer.accept(this);
    return (B)this;
  }
  
  @Contract("_, _ -> this")
  @NotNull
  default B decorations(@NotNull Set<TextDecoration> decorations, boolean flag) {
    return (B)super.decorations(decorations, flag);
  }
  
  @Contract("_ -> this")
  @NotNull
  default B decorate(@NotNull TextDecoration decoration) {
    return decoration(decoration, TextDecoration.State.TRUE);
  }
  
  @Contract("_ -> this")
  @NotNull
  B decorate(@NotNull TextDecoration... decorations) {
    return (B)super.decorate(decorations);
  }
  
  @Contract("_, _ -> this")
  @NotNull
  default B decoration(@NotNull TextDecoration decoration, boolean flag) {
    return decoration(decoration, TextDecoration.State.byBoolean(flag));
  }
  
  @Contract("_ -> this")
  @NotNull
  default B decorations(@NotNull Map<TextDecoration, TextDecoration.State> decorations) {
    return (B)super.decorations(decorations);
  }
  
  @Contract("_ -> this")
  @NotNull
  default B mergeStyle(@NotNull Component that) {
    return mergeStyle(that, Style.Merge.all());
  }
  
  @Contract("_, _ -> this")
  @NotNull
  B mergeStyle(@NotNull Component that, Style.Merge... merges) {
    return mergeStyle(that, Style.Merge.merges(merges));
  }
  
  @Contract("_ -> this")
  @NotNull
  default B applicableApply(@NotNull ComponentBuilderApplicable applicable) {
    applicable.componentBuilderApply(this);
    return (B)this;
  }
  
  default void componentBuilderApply(@NotNull ComponentBuilder<?, ?> component) {
    component.append(this);
  }
  
  @NotNull
  default Component asComponent() {
    return (Component)build();
  }
  
  @Contract("_ -> this")
  @NotNull
  B append(@NotNull Component paramComponent);
  
  @Contract("_ -> this")
  @NotNull
  B append(@NotNull Component... paramVarArgs);
  
  @Contract("_ -> this")
  @NotNull
  B append(@NotNull ComponentLike... paramVarArgs);
  
  @Contract("_ -> this")
  @NotNull
  B append(@NotNull Iterable<? extends ComponentLike> paramIterable);
  
  @Contract("_ -> this")
  @NotNull
  B applyDeep(@NotNull Consumer<? super ComponentBuilder<?, ?>> paramConsumer);
  
  @Contract("_ -> this")
  @NotNull
  B mapChildren(@NotNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> paramFunction);
  
  @Contract("_ -> this")
  @NotNull
  B mapChildrenDeep(@NotNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> paramFunction);
  
  @NotNull
  List<Component> children();
  
  @Contract("_ -> this")
  @NotNull
  B style(@NotNull Style paramStyle);
  
  @Contract("_ -> this")
  @NotNull
  B style(@NotNull Consumer<Style.Builder> paramConsumer);
  
  @Contract("_ -> this")
  @NotNull
  B font(@Nullable Key paramKey);
  
  @Contract("_ -> this")
  @NotNull
  B color(@Nullable TextColor paramTextColor);
  
  @Contract("_ -> this")
  @NotNull
  B colorIfAbsent(@Nullable TextColor paramTextColor);
  
  @Contract("_, _ -> this")
  @NotNull
  B decoration(@NotNull TextDecoration paramTextDecoration, TextDecoration.State paramState);
  
  @Contract("_, _ -> this")
  @NotNull
  B decorationIfAbsent(@NotNull TextDecoration paramTextDecoration, TextDecoration.State paramState);
  
  @Contract("_ -> this")
  @NotNull
  B clickEvent(@Nullable ClickEvent paramClickEvent);
  
  @Contract("_ -> this")
  @NotNull
  B hoverEvent(@Nullable HoverEventSource<?> paramHoverEventSource);
  
  @Contract("_ -> this")
  @NotNull
  B insertion(@Nullable String paramString);
  
  @Contract("_, _ -> this")
  @NotNull
  B mergeStyle(@NotNull Component paramComponent, @NotNull Set<Style.Merge> paramSet);
  
  @NotNull
  B resetStyle();
  
  @NotNull
  C build();
}
