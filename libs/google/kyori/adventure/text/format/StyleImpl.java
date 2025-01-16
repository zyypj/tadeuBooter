package me.syncwrld.booter.libs.google.kyori.adventure.text.format;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.ClickEvent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.HoverEvent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.HoverEventSource;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class StyleImpl implements Style {
  static final StyleImpl EMPTY = new StyleImpl(null, null, DecorationMap.EMPTY, null, null, null);
  
  @Nullable
  final Key font;
  
  @Nullable
  final TextColor color;
  
  @NotNull
  final DecorationMap decorations;
  
  @Nullable
  final ClickEvent clickEvent;
  
  @Nullable
  final HoverEvent<?> hoverEvent;
  
  @Nullable
  final String insertion;
  
  StyleImpl(@Nullable Key font, @Nullable TextColor color, @NotNull Map<TextDecoration, TextDecoration.State> decorations, @Nullable ClickEvent clickEvent, @Nullable HoverEvent<?> hoverEvent, @Nullable String insertion) {
    this.font = font;
    this.color = color;
    this.decorations = DecorationMap.fromMap(decorations);
    this.clickEvent = clickEvent;
    this.hoverEvent = hoverEvent;
    this.insertion = insertion;
  }
  
  @Nullable
  public Key font() {
    return this.font;
  }
  
  @NotNull
  public Style font(@Nullable Key font) {
    if (Objects.equals(this.font, font))
      return this; 
    return new StyleImpl(font, this.color, this.decorations, this.clickEvent, this.hoverEvent, this.insertion);
  }
  
  @Nullable
  public TextColor color() {
    return this.color;
  }
  
  @NotNull
  public Style color(@Nullable TextColor color) {
    if (Objects.equals(this.color, color))
      return this; 
    return new StyleImpl(this.font, color, this.decorations, this.clickEvent, this.hoverEvent, this.insertion);
  }
  
  @NotNull
  public Style colorIfAbsent(@Nullable TextColor color) {
    if (this.color == null)
      return color(color); 
    return this;
  }
  
  public TextDecoration.State decoration(@NotNull TextDecoration decoration) {
    TextDecoration.State state = this.decorations.get(decoration);
    if (state != null)
      return state; 
    throw new IllegalArgumentException(String.format("unknown decoration '%s'", new Object[] { decoration }));
  }
  
  @NotNull
  public Style decoration(@NotNull TextDecoration decoration, TextDecoration.State state) {
    Objects.requireNonNull(state, "state");
    if (decoration(decoration) == state)
      return this; 
    return new StyleImpl(this.font, this.color, this.decorations.with(decoration, state), this.clickEvent, this.hoverEvent, this.insertion);
  }
  
  @NotNull
  public Style decorationIfAbsent(@NotNull TextDecoration decoration, TextDecoration.State state) {
    Objects.requireNonNull(state, "state");
    TextDecoration.State oldState = this.decorations.get(decoration);
    if (oldState == TextDecoration.State.NOT_SET)
      return new StyleImpl(this.font, this.color, this.decorations.with(decoration, state), this.clickEvent, this.hoverEvent, this.insertion); 
    if (oldState != null)
      return this; 
    throw new IllegalArgumentException(String.format("unknown decoration '%s'", new Object[] { decoration }));
  }
  
  @NotNull
  public Map<TextDecoration, TextDecoration.State> decorations() {
    return this.decorations;
  }
  
  @NotNull
  public Style decorations(@NotNull Map<TextDecoration, TextDecoration.State> decorations) {
    return new StyleImpl(this.font, this.color, DecorationMap.merge(decorations, this.decorations), this.clickEvent, this.hoverEvent, this.insertion);
  }
  
  @Nullable
  public ClickEvent clickEvent() {
    return this.clickEvent;
  }
  
  @NotNull
  public Style clickEvent(@Nullable ClickEvent event) {
    return new StyleImpl(this.font, this.color, this.decorations, event, this.hoverEvent, this.insertion);
  }
  
  @Nullable
  public HoverEvent<?> hoverEvent() {
    return this.hoverEvent;
  }
  
  @NotNull
  public Style hoverEvent(@Nullable HoverEventSource<?> source) {
    return new StyleImpl(this.font, this.color, this.decorations, this.clickEvent, HoverEventSource.unbox(source), this.insertion);
  }
  
  @Nullable
  public String insertion() {
    return this.insertion;
  }
  
  @NotNull
  public Style insertion(@Nullable String insertion) {
    if (Objects.equals(this.insertion, insertion))
      return this; 
    return new StyleImpl(this.font, this.color, this.decorations, this.clickEvent, this.hoverEvent, insertion);
  }
  
  @NotNull
  public Style merge(@NotNull Style that, Style.Merge.Strategy strategy, @NotNull Set<Style.Merge> merges) {
    if (nothingToMerge(that, strategy, merges))
      return this; 
    if (isEmpty() && Style.Merge.hasAll(merges))
      return that; 
    Style.Builder builder = toBuilder();
    builder.merge(that, strategy, merges);
    return builder.build();
  }
  
  @NotNull
  public Style unmerge(@NotNull Style that) {
    if (isEmpty())
      return this; 
    Style.Builder builder = new BuilderImpl(this);
    if (Objects.equals(font(), that.font()))
      builder.font((Key)null); 
    if (Objects.equals(color(), that.color()))
      builder.color((TextColor)null); 
    for (int i = 0, length = DecorationMap.DECORATIONS.length; i < length; i++) {
      TextDecoration decoration = DecorationMap.DECORATIONS[i];
      if (decoration(decoration) == that.decoration(decoration))
        builder.decoration(decoration, TextDecoration.State.NOT_SET); 
    } 
    if (Objects.equals(clickEvent(), that.clickEvent()))
      builder.clickEvent((ClickEvent)null); 
    if (Objects.equals(hoverEvent(), that.hoverEvent()))
      builder.hoverEvent((HoverEventSource<?>)null); 
    if (Objects.equals(insertion(), that.insertion()))
      builder.insertion((String)null); 
    return builder.build();
  }
  
  static boolean nothingToMerge(@NotNull Style mergeFrom, Style.Merge.Strategy strategy, @NotNull Set<Style.Merge> merges) {
    if (strategy == Style.Merge.Strategy.NEVER)
      return true; 
    if (mergeFrom.isEmpty())
      return true; 
    if (merges.isEmpty())
      return true; 
    return false;
  }
  
  public boolean isEmpty() {
    return (this == EMPTY);
  }
  
  @NotNull
  public Style.Builder toBuilder() {
    return new BuilderImpl(this);
  }
  
  @NotNull
  public Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(this.decorations
        .examinableProperties(), 
        Stream.of(new ExaminableProperty[] { ExaminableProperty.of("color", this.color), 
            ExaminableProperty.of("clickEvent", this.clickEvent), 
            ExaminableProperty.of("hoverEvent", this.hoverEvent), 
            ExaminableProperty.of("insertion", this.insertion), 
            ExaminableProperty.of("font", this.font) }));
  }
  
  @NotNull
  public String toString() {
    return Internals.toString(this);
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof StyleImpl))
      return false; 
    StyleImpl that = (StyleImpl)other;
    return (Objects.equals(this.color, that.color) && this.decorations
      .equals(that.decorations) && 
      Objects.equals(this.clickEvent, that.clickEvent) && 
      Objects.equals(this.hoverEvent, that.hoverEvent) && 
      Objects.equals(this.insertion, that.insertion) && 
      Objects.equals(this.font, that.font));
  }
  
  public int hashCode() {
    int result = Objects.hashCode(this.color);
    result = 31 * result + this.decorations.hashCode();
    result = 31 * result + Objects.hashCode(this.clickEvent);
    result = 31 * result + Objects.hashCode(this.hoverEvent);
    result = 31 * result + Objects.hashCode(this.insertion);
    result = 31 * result + Objects.hashCode(this.font);
    return result;
  }
  
  static final class BuilderImpl implements Style.Builder {
    @Nullable
    Key font;
    
    @Nullable
    TextColor color;
    
    final Map<TextDecoration, TextDecoration.State> decorations;
    
    @Nullable
    ClickEvent clickEvent;
    
    @Nullable
    HoverEvent<?> hoverEvent;
    
    @Nullable
    String insertion;
    
    BuilderImpl() {
      this.decorations = new EnumMap<>(DecorationMap.EMPTY);
    }
    
    BuilderImpl(@NotNull StyleImpl style) {
      this.color = style.color;
      this.decorations = new EnumMap<>(style.decorations);
      this.clickEvent = style.clickEvent;
      this.hoverEvent = style.hoverEvent;
      this.insertion = style.insertion;
      this.font = style.font;
    }
    
    @NotNull
    public Style.Builder font(@Nullable Key font) {
      this.font = font;
      return this;
    }
    
    @NotNull
    public Style.Builder color(@Nullable TextColor color) {
      this.color = color;
      return this;
    }
    
    @NotNull
    public Style.Builder colorIfAbsent(@Nullable TextColor color) {
      if (this.color == null)
        this.color = color; 
      return this;
    }
    
    @NotNull
    public Style.Builder decoration(@NotNull TextDecoration decoration, TextDecoration.State state) {
      Objects.requireNonNull(state, "state");
      Objects.requireNonNull(decoration, "decoration");
      this.decorations.put(decoration, state);
      return this;
    }
    
    @NotNull
    public Style.Builder decorationIfAbsent(@NotNull TextDecoration decoration, TextDecoration.State state) {
      Objects.requireNonNull(state, "state");
      TextDecoration.State oldState = this.decorations.get(decoration);
      if (oldState == TextDecoration.State.NOT_SET)
        this.decorations.put(decoration, state); 
      if (oldState != null)
        return this; 
      throw new IllegalArgumentException(String.format("unknown decoration '%s'", new Object[] { decoration }));
    }
    
    @NotNull
    public Style.Builder clickEvent(@Nullable ClickEvent event) {
      this.clickEvent = event;
      return this;
    }
    
    @NotNull
    public Style.Builder hoverEvent(@Nullable HoverEventSource<?> source) {
      this.hoverEvent = HoverEventSource.unbox(source);
      return this;
    }
    
    @NotNull
    public Style.Builder insertion(@Nullable String insertion) {
      this.insertion = insertion;
      return this;
    }
    
    @NotNull
    public Style.Builder merge(@NotNull Style that, Style.Merge.Strategy strategy, @NotNull Set<Style.Merge> merges) {
      Objects.requireNonNull(that, "style");
      Objects.requireNonNull(strategy, "strategy");
      Objects.requireNonNull(merges, "merges");
      if (StyleImpl.nothingToMerge(that, strategy, merges))
        return this; 
      if (merges.contains(Style.Merge.COLOR)) {
        TextColor color = that.color();
        if (color != null && (
          strategy == Style.Merge.Strategy.ALWAYS || (strategy == Style.Merge.Strategy.IF_ABSENT_ON_TARGET && this.color == null)))
          color(color); 
      } 
      if (merges.contains(Style.Merge.DECORATIONS))
        for (int i = 0, length = DecorationMap.DECORATIONS.length; i < length; i++) {
          TextDecoration decoration = DecorationMap.DECORATIONS[i];
          TextDecoration.State state = that.decoration(decoration);
          if (state != TextDecoration.State.NOT_SET)
            if (strategy == Style.Merge.Strategy.ALWAYS) {
              decoration(decoration, state);
            } else if (strategy == Style.Merge.Strategy.IF_ABSENT_ON_TARGET) {
              decorationIfAbsent(decoration, state);
            }  
        }  
      if (merges.contains(Style.Merge.EVENTS)) {
        ClickEvent clickEvent = that.clickEvent();
        if (clickEvent != null && (
          strategy == Style.Merge.Strategy.ALWAYS || (strategy == Style.Merge.Strategy.IF_ABSENT_ON_TARGET && this.clickEvent == null)))
          clickEvent(clickEvent); 
        HoverEvent<?> hoverEvent = that.hoverEvent();
        if (hoverEvent != null && (
          strategy == Style.Merge.Strategy.ALWAYS || (strategy == Style.Merge.Strategy.IF_ABSENT_ON_TARGET && this.hoverEvent == null)))
          hoverEvent((HoverEventSource<?>)hoverEvent); 
      } 
      if (merges.contains(Style.Merge.INSERTION)) {
        String insertion = that.insertion();
        if (insertion != null && (
          strategy == Style.Merge.Strategy.ALWAYS || (strategy == Style.Merge.Strategy.IF_ABSENT_ON_TARGET && this.insertion == null)))
          insertion(insertion); 
      } 
      if (merges.contains(Style.Merge.FONT)) {
        Key font = that.font();
        if (font != null && (
          strategy == Style.Merge.Strategy.ALWAYS || (strategy == Style.Merge.Strategy.IF_ABSENT_ON_TARGET && this.font == null)))
          font(font); 
      } 
      return this;
    }
    
    @NotNull
    public StyleImpl build() {
      if (isEmpty())
        return StyleImpl.EMPTY; 
      return new StyleImpl(this.font, this.color, this.decorations, this.clickEvent, this.hoverEvent, this.insertion);
    }
    
    private boolean isEmpty() {
      return (this.color == null && this.decorations
        .values().stream().allMatch(state -> (state == TextDecoration.State.NOT_SET)) && this.clickEvent == null && this.hoverEvent == null && this.insertion == null && this.font == null);
    }
  }
}
