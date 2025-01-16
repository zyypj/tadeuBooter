package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class TextReplacementConfigImpl implements TextReplacementConfig {
  private final Pattern matchPattern;
  
  private final BiFunction<MatchResult, TextComponent.Builder, ComponentLike> replacement;
  
  private final TextReplacementConfig.Condition continuer;
  
  TextReplacementConfigImpl(Builder builder) {
    this.matchPattern = builder.matchPattern;
    this.replacement = builder.replacement;
    this.continuer = builder.continuer;
  }
  
  @NotNull
  public Pattern matchPattern() {
    return this.matchPattern;
  }
  
  TextReplacementRenderer.State createState() {
    return new TextReplacementRenderer.State(this.matchPattern, this.replacement, this.continuer);
  }
  
  public TextReplacementConfig.Builder toBuilder() {
    return new Builder(this);
  }
  
  @NotNull
  public Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("matchPattern", this.matchPattern), 
          ExaminableProperty.of("replacement", this.replacement), 
          ExaminableProperty.of("continuer", this.continuer) });
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  static final class Builder implements TextReplacementConfig.Builder {
    @Nullable
    Pattern matchPattern;
    
    @Nullable
    BiFunction<MatchResult, TextComponent.Builder, ComponentLike> replacement;
    
    TextReplacementConfig.Condition continuer = (matchResult, index, replacement) -> PatternReplacementResult.REPLACE;
    
    Builder(TextReplacementConfigImpl instance) {
      this.matchPattern = instance.matchPattern;
      this.replacement = instance.replacement;
      this.continuer = instance.continuer;
    }
    
    @NotNull
    public Builder match(@NotNull Pattern pattern) {
      this.matchPattern = Objects.<Pattern>requireNonNull(pattern, "pattern");
      return this;
    }
    
    @NotNull
    public Builder condition(TextReplacementConfig.Condition condition) {
      this.continuer = Objects.<TextReplacementConfig.Condition>requireNonNull(condition, "continuation");
      return this;
    }
    
    @NotNull
    public Builder replacement(@NotNull BiFunction<MatchResult, TextComponent.Builder, ComponentLike> replacement) {
      this.replacement = Objects.<BiFunction<MatchResult, TextComponent.Builder, ComponentLike>>requireNonNull(replacement, "replacement");
      return this;
    }
    
    @NotNull
    public TextReplacementConfig build() {
      if (this.matchPattern == null)
        throw new IllegalStateException("A pattern must be provided to match against"); 
      if (this.replacement == null)
        throw new IllegalStateException("A replacement action must be provided"); 
      return new TextReplacementConfigImpl(this);
    }
    
    Builder() {}
  }
}
