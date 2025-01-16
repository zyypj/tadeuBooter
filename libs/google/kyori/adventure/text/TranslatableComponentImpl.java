package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class TranslatableComponentImpl extends AbstractComponent implements TranslatableComponent {
  private final String key;
  
  @Nullable
  private final String fallback;
  
  private final List<TranslationArgument> args;
  
  static TranslatableComponent create(@NotNull List<Component> children, @NotNull Style style, @NotNull String key, @Nullable String fallback, @NotNull ComponentLike[] args) {
    Objects.requireNonNull(args, "args");
    return create((List)children, style, key, fallback, Arrays.asList(args));
  }
  
  static TranslatableComponent create(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull String key, @Nullable String fallback, @NotNull List<? extends ComponentLike> args) {
    return new TranslatableComponentImpl(
        ComponentLike.asComponents(children, IS_NOT_EMPTY), 
        Objects.<Style>requireNonNull(style, "style"), 
        Objects.<String>requireNonNull(key, "key"), fallback, 
        
        asArguments(args));
  }
  
  TranslatableComponentImpl(@NotNull List<Component> children, @NotNull Style style, @NotNull String key, @Nullable String fallback, @NotNull List<TranslationArgument> args) {
    super((List)children, style);
    this.key = key;
    this.fallback = fallback;
    this.args = args;
  }
  
  @NotNull
  public String key() {
    return this.key;
  }
  
  @NotNull
  public TranslatableComponent key(@NotNull String key) {
    if (Objects.equals(this.key, key))
      return this; 
    return create((List)this.children, this.style, key, this.fallback, (List)this.args);
  }
  
  @Deprecated
  @NotNull
  public List<Component> args() {
    return ComponentLike.asComponents((List)this.args);
  }
  
  @NotNull
  public List<TranslationArgument> arguments() {
    return this.args;
  }
  
  @NotNull
  public TranslatableComponent arguments(@NotNull ComponentLike... args) {
    return create(this.children, this.style, this.key, this.fallback, args);
  }
  
  @NotNull
  public TranslatableComponent arguments(@NotNull List<? extends ComponentLike> args) {
    return create((List)this.children, this.style, this.key, this.fallback, args);
  }
  
  @Nullable
  public String fallback() {
    return this.fallback;
  }
  
  @NotNull
  public TranslatableComponent fallback(@Nullable String fallback) {
    return create((List)this.children, this.style, this.key, fallback, (List)this.args);
  }
  
  @NotNull
  public TranslatableComponent children(@NotNull List<? extends ComponentLike> children) {
    return create(children, this.style, this.key, this.fallback, (List)this.args);
  }
  
  @NotNull
  public TranslatableComponent style(@NotNull Style style) {
    return create((List)this.children, style, this.key, this.fallback, (List)this.args);
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof TranslatableComponent))
      return false; 
    if (!super.equals(other))
      return false; 
    TranslatableComponent that = (TranslatableComponent)other;
    return (Objects.equals(this.key, that.key()) && Objects.equals(this.fallback, that.fallback()) && Objects.equals(this.args, that.arguments()));
  }
  
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + this.key.hashCode();
    result = 31 * result + Objects.hashCode(this.fallback);
    result = 31 * result + this.args.hashCode();
    return result;
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  @NotNull
  public TranslatableComponent.Builder toBuilder() {
    return new BuilderImpl(this);
  }
  
  static final class BuilderImpl extends AbstractComponentBuilder<TranslatableComponent, TranslatableComponent.Builder> implements TranslatableComponent.Builder {
    @Nullable
    private String key;
    
    @Nullable
    private String fallback;
    
    private List<TranslationArgument> args = Collections.emptyList();
    
    BuilderImpl(@NotNull TranslatableComponent component) {
      super(component);
      this.key = component.key();
      this.args = component.arguments();
      this.fallback = component.fallback();
    }
    
    @NotNull
    public TranslatableComponent.Builder key(@NotNull String key) {
      this.key = key;
      return this;
    }
    
    @NotNull
    public TranslatableComponent.Builder arguments(@NotNull ComponentLike... args) {
      Objects.requireNonNull(args, "args");
      if (args.length == 0)
        return arguments(Collections.emptyList()); 
      return arguments(Arrays.asList(args));
    }
    
    @NotNull
    public TranslatableComponent.Builder arguments(@NotNull List<? extends ComponentLike> args) {
      this.args = TranslatableComponentImpl.asArguments(Objects.<List<? extends ComponentLike>>requireNonNull(args, "args"));
      return this;
    }
    
    @NotNull
    public TranslatableComponent.Builder fallback(@Nullable String fallback) {
      this.fallback = fallback;
      return this;
    }
    
    @NotNull
    public TranslatableComponent build() {
      if (this.key == null)
        throw new IllegalStateException("key must be set"); 
      return TranslatableComponentImpl.create((List)this.children, buildStyle(), this.key, this.fallback, (List)this.args);
    }
    
    BuilderImpl() {}
  }
  
  static List<TranslationArgument> asArguments(@NotNull List<? extends ComponentLike> likes) {
    if (likes.isEmpty())
      return Collections.emptyList(); 
    List<TranslationArgument> ret = new ArrayList<>(likes.size());
    for (int i = 0; i < likes.size(); i++) {
      ComponentLike like = likes.get(i);
      if (like == null)
        throw new NullPointerException("likes[" + i + "]"); 
      if (like instanceof TranslationArgument) {
        ret.add((TranslationArgument)like);
      } else if (like instanceof TranslationArgumentLike) {
        ret.add(Objects.<TranslationArgument>requireNonNull(((TranslationArgumentLike)like).asTranslationArgument(), "likes[" + i + "].asTranslationArgument()"));
      } else {
        ret.add(TranslationArgument.component(like));
      } 
    } 
    return Collections.unmodifiableList(ret);
  }
}
