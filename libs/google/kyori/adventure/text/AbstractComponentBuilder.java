package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.ClickEvent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.HoverEventSource;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.StyleSetter;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.TextColor;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.TextDecoration;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

abstract class AbstractComponentBuilder<C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>> implements ComponentBuilder<C, B> {
  protected List<Component> children = Collections.emptyList();
  
  @Nullable
  private Style style;
  
  private Style.Builder styleBuilder;
  
  protected AbstractComponentBuilder(@NotNull C component) {
    List<Component> children = component.children();
    if (!children.isEmpty())
      this.children = new ArrayList<>(children); 
    if (component.hasStyling())
      this.style = component.style(); 
  }
  
  @NotNull
  public B append(@NotNull Component component) {
    if (component == Component.empty())
      return (B)this; 
    prepareChildren();
    this.children.add(Objects.<Component>requireNonNull(component, "component"));
    return (B)this;
  }
  
  @NotNull
  public B append(@NotNull Component... components) {
    return append((ComponentLike[])components);
  }
  
  @NotNull
  public B append(@NotNull ComponentLike... components) {
    Objects.requireNonNull(components, "components");
    boolean prepared = false;
    for (int i = 0, length = components.length; i < length; i++) {
      Component component = ((ComponentLike)Objects.<ComponentLike>requireNonNull(components[i], "components[?]")).asComponent();
      if (component != Component.empty()) {
        if (!prepared) {
          prepareChildren();
          prepared = true;
        } 
        this.children.add(Objects.<Component>requireNonNull(component, "components[?]"));
      } 
    } 
    return (B)this;
  }
  
  @NotNull
  public B append(@NotNull Iterable<? extends ComponentLike> components) {
    Objects.requireNonNull(components, "components");
    boolean prepared = false;
    for (ComponentLike like : components) {
      Component component = ((ComponentLike)Objects.<ComponentLike>requireNonNull(like, "components[?]")).asComponent();
      if (component != Component.empty()) {
        if (!prepared) {
          prepareChildren();
          prepared = true;
        } 
        this.children.add(Objects.<Component>requireNonNull(component, "components[?]"));
      } 
    } 
    return (B)this;
  }
  
  private void prepareChildren() {
    if (this.children == Collections.emptyList())
      this.children = new ArrayList<>(); 
  }
  
  @NotNull
  public B applyDeep(@NotNull Consumer<? super ComponentBuilder<?, ?>> consumer) {
    apply(consumer);
    if (this.children == Collections.emptyList())
      return (B)this; 
    ListIterator<Component> it = this.children.listIterator();
    while (it.hasNext()) {
      Component child = it.next();
      if (!(child instanceof BuildableComponent))
        continue; 
      ComponentBuilder<?, ?> childBuilder = (ComponentBuilder<?, ?>)((BuildableComponent)child).toBuilder();
      childBuilder.applyDeep(consumer);
      it.set((Component)childBuilder.build());
    } 
    return (B)this;
  }
  
  @NotNull
  public B mapChildren(@NotNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> function) {
    if (this.children == Collections.emptyList())
      return (B)this; 
    ListIterator<Component> it = this.children.listIterator();
    while (it.hasNext()) {
      Component child = it.next();
      if (!(child instanceof BuildableComponent))
        continue; 
      BuildableComponent<?, ?> mappedChild = Objects.<BuildableComponent<?, ?>>requireNonNull(function.apply((BuildableComponent<?, ?>)child), "mappedChild");
      if (child == mappedChild)
        continue; 
      it.set(mappedChild);
    } 
    return (B)this;
  }
  
  @NotNull
  public B mapChildrenDeep(@NotNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> function) {
    if (this.children == Collections.emptyList())
      return (B)this; 
    ListIterator<Component> it = this.children.listIterator();
    while (it.hasNext()) {
      Component child = it.next();
      if (!(child instanceof BuildableComponent))
        continue; 
      BuildableComponent<?, ?> mappedChild = Objects.<BuildableComponent<?, ?>>requireNonNull(function.apply((BuildableComponent<?, ?>)child), "mappedChild");
      if (mappedChild.children().isEmpty()) {
        if (child == mappedChild)
          continue; 
        it.set(mappedChild);
        continue;
      } 
      ComponentBuilder<?, ?> builder = (ComponentBuilder<?, ?>)mappedChild.toBuilder();
      builder.mapChildrenDeep(function);
      it.set((Component)builder.build());
    } 
    return (B)this;
  }
  
  @NotNull
  public List<Component> children() {
    return Collections.unmodifiableList(this.children);
  }
  
  @NotNull
  public B style(@NotNull Style style) {
    this.style = style;
    this.styleBuilder = null;
    return (B)this;
  }
  
  @NotNull
  public B style(@NotNull Consumer<Style.Builder> consumer) {
    consumer.accept(styleBuilder());
    return (B)this;
  }
  
  @NotNull
  public B font(@Nullable Key font) {
    styleBuilder().font(font);
    return (B)this;
  }
  
  @NotNull
  public B color(@Nullable TextColor color) {
    styleBuilder().color(color);
    return (B)this;
  }
  
  @NotNull
  public B colorIfAbsent(@Nullable TextColor color) {
    styleBuilder().colorIfAbsent(color);
    return (B)this;
  }
  
  @NotNull
  public B decoration(@NotNull TextDecoration decoration, TextDecoration.State state) {
    styleBuilder().decoration(decoration, state);
    return (B)this;
  }
  
  @NotNull
  public B decorationIfAbsent(@NotNull TextDecoration decoration, TextDecoration.State state) {
    styleBuilder().decorationIfAbsent(decoration, state);
    return (B)this;
  }
  
  @NotNull
  public B clickEvent(@Nullable ClickEvent event) {
    styleBuilder().clickEvent(event);
    return (B)this;
  }
  
  @NotNull
  public B hoverEvent(@Nullable HoverEventSource<?> source) {
    styleBuilder().hoverEvent(source);
    return (B)this;
  }
  
  @NotNull
  public B insertion(@Nullable String insertion) {
    styleBuilder().insertion(insertion);
    return (B)this;
  }
  
  @NotNull
  public B mergeStyle(@NotNull Component that, @NotNull Set<Style.Merge> merges) {
    styleBuilder().merge(((Component)Objects.<Component>requireNonNull(that, "component")).style(), merges);
    return (B)this;
  }
  
  @NotNull
  public B resetStyle() {
    this.style = null;
    this.styleBuilder = null;
    return (B)this;
  }
  
  private Style.Builder styleBuilder() {
    if (this.styleBuilder == null)
      if (this.style != null) {
        this.styleBuilder = this.style.toBuilder();
        this.style = null;
      } else {
        this.styleBuilder = Style.style();
      }  
    return this.styleBuilder;
  }
  
  protected final boolean hasStyle() {
    return (this.styleBuilder != null || this.style != null);
  }
  
  @NotNull
  protected Style buildStyle() {
    if (this.styleBuilder != null)
      return this.styleBuilder.build(); 
    if (this.style != null)
      return this.style; 
    return Style.empty();
  }
  
  protected AbstractComponentBuilder() {}
}
