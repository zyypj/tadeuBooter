package me.syncwrld.booter.libs.google.kyori.adventure.text.format;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import me.syncwrld.booter.libs.google.kyori.adventure.builder.AbstractBuilder;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.ClickEvent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.HoverEvent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.HoverEventSource;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.google.kyori.adventure.util.MonkeyBars;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;
import me.syncwrld.booter.libs.jtann.ApiStatus.ScheduledForRemoval;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

@NonExtendable
public interface Style extends Buildable<Style, Style.Builder>, Examinable, StyleGetter, StyleSetter<Style> {
  public static final Key DEFAULT_FONT = Key.key("default");
  
  @NotNull
  static Style empty() {
    return StyleImpl.EMPTY;
  }
  
  @NotNull
  static Builder style() {
    return new StyleImpl.BuilderImpl();
  }
  
  @NotNull
  static Style style(@NotNull Consumer<Builder> consumer) {
    return (Style)AbstractBuilder.configureAndBuild(style(), consumer);
  }
  
  @NotNull
  static Style style(@Nullable TextColor color) {
    return empty().color(color);
  }
  
  @NotNull
  static Style style(@NotNull TextDecoration decoration) {
    return style().decoration(decoration, true).build();
  }
  
  @NotNull
  static Style style(@Nullable TextColor color, TextDecoration... decorations) {
    Builder builder = style();
    builder.color(color);
    builder.decorate(decorations);
    return builder.build();
  }
  
  @NotNull
  static Style style(@Nullable TextColor color, Set<TextDecoration> decorations) {
    Builder builder = style();
    builder.color(color);
    if (!decorations.isEmpty())
      for (TextDecoration decoration : decorations)
        builder.decoration(decoration, true);  
    return builder.build();
  }
  
  @NotNull
  static Style style(StyleBuilderApplicable... applicables) {
    int length = applicables.length;
    if (length == 0)
      return empty(); 
    Builder builder = style();
    for (int i = 0; i < length; i++) {
      StyleBuilderApplicable applicable = applicables[i];
      if (applicable != null)
        applicable.styleApply(builder); 
    } 
    return builder.build();
  }
  
  @NotNull
  static Style style(@NotNull Iterable<? extends StyleBuilderApplicable> applicables) {
    Builder builder = style();
    for (StyleBuilderApplicable applicable : applicables)
      applicable.styleApply(builder); 
    return builder.build();
  }
  
  @NotNull
  default Style edit(@NotNull Consumer<Builder> consumer) {
    return edit(consumer, Merge.Strategy.ALWAYS);
  }
  
  @NotNull
  default Style edit(@NotNull Consumer<Builder> consumer, Merge.Strategy strategy) {
    return style(style -> {
          if (strategy == Merge.Strategy.ALWAYS)
            style.merge(this, strategy); 
          consumer.accept(style);
          if (strategy == Merge.Strategy.IF_ABSENT_ON_TARGET)
            style.merge(this, strategy); 
        });
  }
  
  default boolean hasDecoration(@NotNull TextDecoration decoration) {
    return super.hasDecoration(decoration);
  }
  
  @NotNull
  default Style decorate(@NotNull TextDecoration decoration) {
    return super.decorate(decoration);
  }
  
  @NotNull
  default Style decoration(@NotNull TextDecoration decoration, boolean flag) {
    return super.decoration(decoration, flag);
  }
  
  @NotNull
  default Map<TextDecoration, TextDecoration.State> decorations() {
    return super.decorations();
  }
  
  @NotNull
  default Style merge(@NotNull Style that) {
    return merge(that, Merge.all());
  }
  
  @NotNull
  default Style merge(@NotNull Style that, Merge.Strategy strategy) {
    return merge(that, strategy, Merge.all());
  }
  
  @NotNull
  default Style merge(@NotNull Style that, @NotNull Merge merge) {
    return merge(that, Collections.singleton(merge));
  }
  
  @NotNull
  default Style merge(@NotNull Style that, Merge.Strategy strategy, @NotNull Merge merge) {
    return merge(that, strategy, Collections.singleton(merge));
  }
  
  @NotNull
  Style merge(@NotNull Style that, @NotNull Merge... merges) {
    return merge(that, Merge.merges(merges));
  }
  
  @NotNull
  Style merge(@NotNull Style that, Merge.Strategy strategy, @NotNull Merge... merges) {
    return merge(that, strategy, Merge.merges(merges));
  }
  
  @NotNull
  default Style merge(@NotNull Style that, @NotNull Set<Merge> merges) {
    return merge(that, Merge.Strategy.ALWAYS, merges);
  }
  
  @Nullable
  Key font();
  
  @NotNull
  Style font(@Nullable Key paramKey);
  
  @Nullable
  TextColor color();
  
  @NotNull
  Style color(@Nullable TextColor paramTextColor);
  
  @NotNull
  Style colorIfAbsent(@Nullable TextColor paramTextColor);
  
  TextDecoration.State decoration(@NotNull TextDecoration paramTextDecoration);
  
  @NotNull
  Style decoration(@NotNull TextDecoration paramTextDecoration, TextDecoration.State paramState);
  
  @NotNull
  Style decorationIfAbsent(@NotNull TextDecoration paramTextDecoration, TextDecoration.State paramState);
  
  @NotNull
  Style decorations(@NotNull Map<TextDecoration, TextDecoration.State> paramMap);
  
  @Nullable
  ClickEvent clickEvent();
  
  @NotNull
  Style clickEvent(@Nullable ClickEvent paramClickEvent);
  
  @Nullable
  HoverEvent<?> hoverEvent();
  
  @NotNull
  Style hoverEvent(@Nullable HoverEventSource<?> paramHoverEventSource);
  
  @Nullable
  String insertion();
  
  @NotNull
  Style insertion(@Nullable String paramString);
  
  @NotNull
  Style merge(@NotNull Style paramStyle, Merge.Strategy paramStrategy, @NotNull Set<Merge> paramSet);
  
  @NotNull
  Style unmerge(@NotNull Style paramStyle);
  
  boolean isEmpty();
  
  @NotNull
  Builder toBuilder();
  
  public enum Merge {
    COLOR, DECORATIONS, EVENTS, INSERTION, FONT;
    
    static final Set<Merge> ALL = merges(values());
    
    static final Set<Merge> COLOR_AND_DECORATIONS = merges(new Merge[] { COLOR, DECORATIONS });
    
    static {
    
    }
    
    @NotNull
    public static Set<Merge> all() {
      return ALL;
    }
    
    @NotNull
    public static Set<Merge> colorAndDecorations() {
      return COLOR_AND_DECORATIONS;
    }
    
    @NotNull
    public static Set<Merge> merges(Merge... merges) {
      return MonkeyBars.enumSet(Merge.class, (Enum[])merges);
    }
    
    @Deprecated
    @ScheduledForRemoval(inVersion = "5.0.0")
    @NotNull
    public static Set<Merge> of(Merge... merges) {
      return MonkeyBars.enumSet(Merge.class, (Enum[])merges);
    }
    
    static boolean hasAll(@NotNull Set<Merge> merges) {
      return (merges.size() == ALL.size());
    }
    
    public enum Strategy {
      ALWAYS, NEVER, IF_ABSENT_ON_TARGET;
    }
  }
  
  public enum Strategy {
    ALWAYS, NEVER, IF_ABSENT_ON_TARGET;
  }
  
  public static interface Builder extends AbstractBuilder<Style>, Buildable.Builder<Style>, MutableStyleSetter<Builder> {
    @Contract("_ -> this")
    @NotNull
    default Builder decorate(@NotNull TextDecoration decoration) {
      return super.decorate(decoration);
    }
    
    @Contract("_ -> this")
    @NotNull
    Builder decorate(@NotNull TextDecoration... decorations) {
      return super.decorate(decorations);
    }
    
    @Contract("_, _ -> this")
    @NotNull
    default Builder decoration(@NotNull TextDecoration decoration, boolean flag) {
      return super.decoration(decoration, flag);
    }
    
    @Contract("_ -> this")
    @NotNull
    default Builder decorations(@NotNull Map<TextDecoration, TextDecoration.State> decorations) {
      return super.decorations(decorations);
    }
    
    @Contract("_ -> this")
    @NotNull
    default Builder merge(@NotNull Style that) {
      return merge(that, Style.Merge.all());
    }
    
    @Contract("_, _ -> this")
    @NotNull
    default Builder merge(@NotNull Style that, Style.Merge.Strategy strategy) {
      return merge(that, strategy, Style.Merge.all());
    }
    
    @Contract("_, _ -> this")
    @NotNull
    Builder merge(@NotNull Style that, @NotNull Style.Merge... merges) {
      if (merges.length == 0)
        return this; 
      return merge(that, Style.Merge.merges(merges));
    }
    
    @Contract("_, _, _ -> this")
    @NotNull
    Builder merge(@NotNull Style that, Style.Merge.Strategy strategy, @NotNull Style.Merge... merges) {
      if (merges.length == 0)
        return this; 
      return merge(that, strategy, Style.Merge.merges(merges));
    }
    
    @Contract("_, _ -> this")
    @NotNull
    default Builder merge(@NotNull Style that, @NotNull Set<Style.Merge> merges) {
      return merge(that, Style.Merge.Strategy.ALWAYS, merges);
    }
    
    @Contract("_ -> this")
    @NotNull
    default Builder apply(@NotNull StyleBuilderApplicable applicable) {
      applicable.styleApply(this);
      return this;
    }
    
    @Contract("_ -> this")
    @NotNull
    Builder font(@Nullable Key param1Key);
    
    @Contract("_ -> this")
    @NotNull
    Builder color(@Nullable TextColor param1TextColor);
    
    @Contract("_ -> this")
    @NotNull
    Builder colorIfAbsent(@Nullable TextColor param1TextColor);
    
    @Contract("_, _ -> this")
    @NotNull
    Builder decoration(@NotNull TextDecoration param1TextDecoration, TextDecoration.State param1State);
    
    @Contract("_, _ -> this")
    @NotNull
    Builder decorationIfAbsent(@NotNull TextDecoration param1TextDecoration, TextDecoration.State param1State);
    
    @Contract("_ -> this")
    @NotNull
    Builder clickEvent(@Nullable ClickEvent param1ClickEvent);
    
    @Contract("_ -> this")
    @NotNull
    Builder hoverEvent(@Nullable HoverEventSource<?> param1HoverEventSource);
    
    @Contract("_ -> this")
    @NotNull
    Builder insertion(@Nullable String param1String);
    
    @Contract("_, _, _ -> this")
    @NotNull
    Builder merge(@NotNull Style param1Style, Style.Merge.Strategy param1Strategy, @NotNull Set<Style.Merge> param1Set);
    
    @NotNull
    Style build();
  }
}
