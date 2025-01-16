package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.properties.AdventureProperties;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Nag;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;
import me.syncwrld.booter.libs.jtann.VisibleForTesting;

final class TextComponentImpl extends AbstractComponent implements TextComponent {
  private static final boolean WARN_WHEN_LEGACY_FORMATTING_DETECTED = Boolean.TRUE.equals(AdventureProperties.TEXT_WARN_WHEN_LEGACY_FORMATTING_DETECTED.value());
  
  @VisibleForTesting
  static final char SECTION_CHAR = '§';
  
  static final TextComponent EMPTY = createDirect("");
  
  static final TextComponent NEWLINE = createDirect("\n");
  
  static final TextComponent SPACE = createDirect(" ");
  
  private final String content;
  
  static TextComponent create(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull String content) {
    List<Component> filteredChildren = ComponentLike.asComponents(children, IS_NOT_EMPTY);
    if (filteredChildren.isEmpty() && style.isEmpty() && content.isEmpty())
      return Component.empty(); 
    return new TextComponentImpl(filteredChildren, 
        
        Objects.<Style>requireNonNull(style, "style"), 
        Objects.<String>requireNonNull(content, "content"));
  }
  
  @NotNull
  private static TextComponent createDirect(@NotNull String content) {
    return new TextComponentImpl(Collections.emptyList(), Style.empty(), content);
  }
  
  TextComponentImpl(@NotNull List<Component> children, @NotNull Style style, @NotNull String content) {
    super((List)children, style);
    this.content = content;
    if (WARN_WHEN_LEGACY_FORMATTING_DETECTED) {
      LegacyFormattingDetected nag = warnWhenLegacyFormattingDetected();
      if (nag != null)
        Nag.print(nag); 
    } 
  }
  
  @VisibleForTesting
  @Nullable
  final LegacyFormattingDetected warnWhenLegacyFormattingDetected() {
    if (this.content.indexOf('§') != -1)
      return new LegacyFormattingDetected(this); 
    return null;
  }
  
  @NotNull
  public String content() {
    return this.content;
  }
  
  @NotNull
  public TextComponent content(@NotNull String content) {
    if (Objects.equals(this.content, content))
      return this; 
    return create((List)this.children, this.style, content);
  }
  
  @NotNull
  public TextComponent children(@NotNull List<? extends ComponentLike> children) {
    return create(children, this.style, this.content);
  }
  
  @NotNull
  public TextComponent style(@NotNull Style style) {
    return create((List)this.children, style, this.content);
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof TextComponentImpl))
      return false; 
    if (!super.equals(other))
      return false; 
    TextComponentImpl that = (TextComponentImpl)other;
    return Objects.equals(this.content, that.content);
  }
  
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + this.content.hashCode();
    return result;
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  @NotNull
  public TextComponent.Builder toBuilder() {
    return new BuilderImpl(this);
  }
  
  static final class BuilderImpl extends AbstractComponentBuilder<TextComponent, TextComponent.Builder> implements TextComponent.Builder {
    private String content = "";
    
    BuilderImpl(@NotNull TextComponent component) {
      super(component);
      this.content = component.content();
    }
    
    @NotNull
    public TextComponent.Builder content(@NotNull String content) {
      this.content = Objects.<String>requireNonNull(content, "content");
      return this;
    }
    
    @NotNull
    public String content() {
      return this.content;
    }
    
    @NotNull
    public TextComponent build() {
      if (isEmpty())
        return Component.empty(); 
      return TextComponentImpl.create((List)this.children, buildStyle(), this.content);
    }
    
    private boolean isEmpty() {
      return (this.content.isEmpty() && this.children.isEmpty() && !hasStyle());
    }
    
    BuilderImpl() {}
  }
}
