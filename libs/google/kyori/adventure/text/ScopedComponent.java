package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.ClickEvent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.HoverEventSource;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.StyleSetter;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.TextColor;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.TextDecoration;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface ScopedComponent<C extends Component> extends Component {
  @NotNull
  default C style(@NotNull Consumer<Style.Builder> style) {
    return (C)super.style(style);
  }
  
  @NotNull
  default C style(Style.Builder style) {
    return (C)super.style(style);
  }
  
  @NotNull
  default C mergeStyle(@NotNull Component that) {
    return (C)super.mergeStyle(that);
  }
  
  @NotNull
  C mergeStyle(@NotNull Component that, Style.Merge... merges) {
    return (C)super.mergeStyle(that, merges);
  }
  
  @NotNull
  default C append(@NotNull Component component) {
    return (C)super.append(component);
  }
  
  @NotNull
  default C append(@NotNull ComponentLike like) {
    return (C)super.append(like);
  }
  
  @NotNull
  default C append(@NotNull ComponentBuilder<?, ?> builder) {
    return (C)super.append(builder);
  }
  
  @NotNull
  default C mergeStyle(@NotNull Component that, @NotNull Set<Style.Merge> merges) {
    return (C)super.mergeStyle(that, merges);
  }
  
  @NotNull
  default C color(@Nullable TextColor color) {
    return (C)super.color(color);
  }
  
  @NotNull
  default C colorIfAbsent(@Nullable TextColor color) {
    return (C)super.colorIfAbsent(color);
  }
  
  @NotNull
  default C decorate(@NotNull TextDecoration decoration) {
    return (C)super.decorate(decoration);
  }
  
  @NotNull
  default C decoration(@NotNull TextDecoration decoration, boolean flag) {
    return (C)super.decoration(decoration, flag);
  }
  
  @NotNull
  default C decoration(@NotNull TextDecoration decoration, TextDecoration.State state) {
    return (C)super.decoration(decoration, state);
  }
  
  @NotNull
  default C clickEvent(@Nullable ClickEvent event) {
    return (C)super.clickEvent(event);
  }
  
  @NotNull
  default C hoverEvent(@Nullable HoverEventSource<?> event) {
    return (C)super.hoverEvent(event);
  }
  
  @NotNull
  default C insertion(@Nullable String insertion) {
    return (C)super.insertion(insertion);
  }
  
  @NotNull
  C children(@NotNull List<? extends ComponentLike> paramList);
  
  @NotNull
  C style(@NotNull Style paramStyle);
}
