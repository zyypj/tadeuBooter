package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.translation.Translatable;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface TranslatableComponent extends BuildableComponent<TranslatableComponent, TranslatableComponent.Builder>, ScopedComponent<TranslatableComponent> {
  @NotNull
  String key();
  
  @Contract(pure = true)
  @NotNull
  default TranslatableComponent key(@NotNull Translatable translatable) {
    return key(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey());
  }
  
  @Contract(pure = true)
  @NotNull
  TranslatableComponent key(@NotNull String paramString);
  
  @Deprecated
  @NotNull
  List<Component> args();
  
  @Deprecated
  @Contract(pure = true)
  @NotNull
  TranslatableComponent args(@NotNull ComponentLike... args) {
    return arguments(args);
  }
  
  @Deprecated
  @Contract(pure = true)
  @NotNull
  default TranslatableComponent args(@NotNull List<? extends ComponentLike> args) {
    return arguments(args);
  }
  
  @NotNull
  List<TranslationArgument> arguments();
  
  @Contract(pure = true)
  @NotNull
  TranslatableComponent arguments(@NotNull ComponentLike... paramVarArgs);
  
  @Contract(pure = true)
  @NotNull
  TranslatableComponent arguments(@NotNull List<? extends ComponentLike> paramList);
  
  @Nullable
  String fallback();
  
  @Contract(pure = true)
  @NotNull
  TranslatableComponent fallback(@Nullable String paramString);
  
  @NotNull
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
        Stream.of(new ExaminableProperty[] { ExaminableProperty.of("key", key()), 
            ExaminableProperty.of("arguments", arguments()), 
            ExaminableProperty.of("fallback", fallback()) }), super.examinableProperties());
  }
  
  public static interface Builder extends ComponentBuilder<TranslatableComponent, Builder> {
    @Contract(pure = true)
    @NotNull
    default Builder key(@NotNull Translatable translatable) {
      return key(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey());
    }
    
    @Contract("_ -> this")
    @NotNull
    Builder key(@NotNull String param1String);
    
    @Deprecated
    @Contract("_ -> this")
    @NotNull
    default Builder args(@NotNull ComponentBuilder<?, ?> arg) {
      return arguments(new ComponentLike[] { arg });
    }
    
    @Deprecated
    @Contract("_ -> this")
    @NotNull
    Builder args(@NotNull ComponentBuilder<?, ?>... args) {
      return arguments((ComponentLike[])args);
    }
    
    @Deprecated
    @Contract("_ -> this")
    @NotNull
    default Builder args(@NotNull Component arg) {
      return arguments(new ComponentLike[] { arg });
    }
    
    @Deprecated
    @Contract("_ -> this")
    @NotNull
    Builder args(@NotNull ComponentLike... args) {
      return arguments(args);
    }
    
    @Deprecated
    @Contract("_ -> this")
    @NotNull
    default Builder args(@NotNull List<? extends ComponentLike> args) {
      return arguments(args);
    }
    
    @Contract("_ -> this")
    @NotNull
    Builder arguments(@NotNull ComponentLike... param1VarArgs);
    
    @Contract("_ -> this")
    @NotNull
    Builder arguments(@NotNull List<? extends ComponentLike> param1List);
    
    @Contract("_ -> this")
    @NotNull
    Builder fallback(@Nullable String param1String);
  }
}
