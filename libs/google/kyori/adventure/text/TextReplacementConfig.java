package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import me.syncwrld.booter.libs.google.kyori.adventure.builder.AbstractBuilder;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.google.kyori.adventure.util.IntFunction2;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.ijann.RegExp;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface TextReplacementConfig extends Buildable<TextReplacementConfig, TextReplacementConfig.Builder>, Examinable {
  @NotNull
  static Builder builder() {
    return new TextReplacementConfigImpl.Builder();
  }
  
  @NotNull
  Pattern matchPattern();
  
  @FunctionalInterface
  public static interface Condition {
    @NotNull
    PatternReplacementResult shouldReplace(@NotNull MatchResult param1MatchResult, int param1Int1, int param1Int2);
  }
  
  public static interface Builder extends AbstractBuilder<TextReplacementConfig>, Buildable.Builder<TextReplacementConfig> {
    @Contract("_ -> this")
    default Builder matchLiteral(String literal) {
      return match(Pattern.compile(literal, 16));
    }
    
    @Contract("_ -> this")
    @NotNull
    default Builder match(@NotNull @RegExp String pattern) {
      return match(Pattern.compile(pattern));
    }
    
    @NotNull
    default Builder once() {
      return times(1);
    }
    
    @Contract("_ -> this")
    @NotNull
    default Builder times(int times) {
      return condition((index, replaced) -> (replaced < times) ? PatternReplacementResult.REPLACE : PatternReplacementResult.STOP);
    }
    
    @Contract("_ -> this")
    @NotNull
    default Builder condition(@NotNull IntFunction2<PatternReplacementResult> condition) {
      return condition((result, matchCount, replaced) -> (PatternReplacementResult)condition.apply(matchCount, replaced));
    }
    
    @Contract("_ -> this")
    @NotNull
    default Builder replacement(@NotNull String replacement) {
      Objects.requireNonNull(replacement, "replacement");
      return replacement(builder -> builder.content(replacement));
    }
    
    @Contract("_ -> this")
    @NotNull
    default Builder replacement(@Nullable ComponentLike replacement) {
      Component baked = ComponentLike.unbox(replacement);
      return replacement((result, input) -> baked);
    }
    
    @Contract("_ -> this")
    @NotNull
    default Builder replacement(@NotNull Function<TextComponent.Builder, ComponentLike> replacement) {
      Objects.requireNonNull(replacement, "replacement");
      return replacement((result, input) -> (ComponentLike)replacement.apply(input));
    }
    
    @Contract("_ -> this")
    @NotNull
    Builder match(@NotNull Pattern param1Pattern);
    
    @Contract("_ -> this")
    @NotNull
    Builder condition(@NotNull TextReplacementConfig.Condition param1Condition);
    
    @Contract("_ -> this")
    @NotNull
    Builder replacement(@NotNull BiFunction<MatchResult, TextComponent.Builder, ComponentLike> param1BiFunction);
  }
}
