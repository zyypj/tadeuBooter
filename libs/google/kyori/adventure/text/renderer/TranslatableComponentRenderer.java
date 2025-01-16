package me.syncwrld.booter.libs.google.kyori.adventure.text.renderer;

import java.text.AttributedCharacterIterator;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import me.syncwrld.booter.libs.google.kyori.adventure.text.BlockNBTComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.text.ComponentBuilder;
import me.syncwrld.booter.libs.google.kyori.adventure.text.ComponentLike;
import me.syncwrld.booter.libs.google.kyori.adventure.text.EntityNBTComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.KeybindComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.ScoreComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.SelectorComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.StorageNBTComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.TextComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.TranslatableComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.TranslationArgument;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.HoverEvent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.HoverEventSource;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.google.kyori.adventure.translation.Translator;
import me.syncwrld.booter.libs.google.kyori.adventure.util.TriState;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public abstract class TranslatableComponentRenderer<C> extends AbstractComponentRenderer<C> {
  private static final Set<Style.Merge> MERGES = Style.Merge.merges(new Style.Merge[] { Style.Merge.COLOR, Style.Merge.DECORATIONS, Style.Merge.INSERTION, Style.Merge.FONT });
  
  @NotNull
  public static TranslatableComponentRenderer<Locale> usingTranslationSource(@NotNull final Translator source) {
    Objects.requireNonNull(source, "source");
    return new TranslatableComponentRenderer<Locale>() {
        @Nullable
        protected MessageFormat translate(@NotNull String key, @NotNull Locale context) {
          return source.translate(key, context);
        }
        
        @NotNull
        protected Component renderTranslatable(@NotNull TranslatableComponent component, @NotNull Locale context) {
          TriState anyTranslations = source.hasAnyTranslations();
          if (anyTranslations == TriState.TRUE || anyTranslations == TriState.NOT_SET) {
            Component translated = source.translate(component, context);
            if (translated != null)
              return translated; 
            return super.renderTranslatable(component, context);
          } 
          return (Component)component;
        }
      };
  }
  
  @Nullable
  protected MessageFormat translate(@NotNull String key, @NotNull C context) {
    return null;
  }
  
  @Nullable
  protected MessageFormat translate(@NotNull String key, @Nullable String fallback, @NotNull C context) {
    return translate(key, context);
  }
  
  @NotNull
  protected Component renderBlockNbt(@NotNull BlockNBTComponent component, @NotNull C context) {
    BlockNBTComponent.Builder builder = ((BlockNBTComponent.Builder)nbt(context, Component.blockNBT(), component)).pos(component.pos());
    return mergeStyleAndOptionallyDeepRender((Component)component, builder, context);
  }
  
  @NotNull
  protected Component renderEntityNbt(@NotNull EntityNBTComponent component, @NotNull C context) {
    EntityNBTComponent.Builder builder = ((EntityNBTComponent.Builder)nbt(context, Component.entityNBT(), component)).selector(component.selector());
    return mergeStyleAndOptionallyDeepRender((Component)component, builder, context);
  }
  
  @NotNull
  protected Component renderStorageNbt(@NotNull StorageNBTComponent component, @NotNull C context) {
    StorageNBTComponent.Builder builder = ((StorageNBTComponent.Builder)nbt(context, Component.storageNBT(), component)).storage(component.storage());
    return mergeStyleAndOptionallyDeepRender((Component)component, builder, context);
  }
  
  protected <O extends me.syncwrld.booter.libs.google.kyori.adventure.text.NBTComponent<O, B>, B extends me.syncwrld.booter.libs.google.kyori.adventure.text.NBTComponentBuilder<O, B>> B nbt(@NotNull C context, B builder, O oldComponent) {
    builder
      .nbtPath(oldComponent.nbtPath())
      .interpret(oldComponent.interpret());
    Component separator = oldComponent.separator();
    if (separator != null)
      builder.separator((ComponentLike)render(separator, context)); 
    return builder;
  }
  
  @NotNull
  protected Component renderKeybind(@NotNull KeybindComponent component, @NotNull C context) {
    KeybindComponent.Builder builder = Component.keybind().keybind(component.keybind());
    return mergeStyleAndOptionallyDeepRender((Component)component, builder, context);
  }
  
  @NotNull
  protected Component renderScore(@NotNull ScoreComponent component, @NotNull C context) {
    ScoreComponent.Builder builder = Component.score().name(component.name()).objective(component.objective()).value(component.value());
    return mergeStyleAndOptionallyDeepRender((Component)component, builder, context);
  }
  
  @NotNull
  protected Component renderSelector(@NotNull SelectorComponent component, @NotNull C context) {
    SelectorComponent.Builder builder = Component.selector().pattern(component.pattern());
    return mergeStyleAndOptionallyDeepRender((Component)component, builder, context);
  }
  
  @NotNull
  protected Component renderText(@NotNull TextComponent component, @NotNull C context) {
    TextComponent.Builder builder = Component.text().content(component.content());
    return mergeStyleAndOptionallyDeepRender((Component)component, builder, context);
  }
  
  @NotNull
  protected Component renderTranslatable(@NotNull TranslatableComponent component, @NotNull C context) {
    MessageFormat format = translate(component.key(), component.fallback(), context);
    if (format == null) {
      TranslatableComponent.Builder builder1 = Component.translatable().key(component.key()).fallback(component.fallback());
      if (!component.arguments().isEmpty()) {
        List<TranslationArgument> list = new ArrayList<>(component.arguments());
        for (int i = 0, size = list.size(); i < size; i++) {
          TranslationArgument arg = list.get(i);
          if (arg.value() instanceof Component)
            list.set(i, TranslationArgument.component((ComponentLike)render((Component)arg.value(), context))); 
        } 
        builder1.arguments(list);
      } 
      return mergeStyleAndOptionallyDeepRender((Component)component, builder1, context);
    } 
    List<TranslationArgument> args = component.arguments();
    TextComponent.Builder builder = Component.text();
    mergeStyle((Component)component, builder, context);
    if (args.isEmpty()) {
      builder.content(format.format((Object[])null, new StringBuffer(), (FieldPosition)null).toString());
      return optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
    } 
    Object[] nulls = new Object[args.size()];
    StringBuffer sb = format.format(nulls, new StringBuffer(), (FieldPosition)null);
    AttributedCharacterIterator it = format.formatToCharacterIterator(nulls);
    while (it.getIndex() < it.getEndIndex()) {
      int end = it.getRunLimit();
      Integer index = (Integer)it.getAttribute(MessageFormat.Field.ARGUMENT);
      if (index != null) {
        TranslationArgument arg = args.get(index.intValue());
        if (arg.value() instanceof Component) {
          builder.append(render(arg.asComponent(), context));
        } else {
          builder.append(arg.asComponent());
        } 
      } else {
        builder.append((Component)Component.text(sb.substring(it.getIndex(), end)));
      } 
      it.setIndex(end);
    } 
    return optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
  }
  
  protected <O extends me.syncwrld.booter.libs.google.kyori.adventure.text.BuildableComponent<O, B>, B extends ComponentBuilder<O, B>> O mergeStyleAndOptionallyDeepRender(Component component, B builder, C context) {
    mergeStyle(component, (ComponentBuilder<?, ?>)builder, context);
    return optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
  }
  
  protected <O extends me.syncwrld.booter.libs.google.kyori.adventure.text.BuildableComponent<O, B>, B extends ComponentBuilder<O, B>> O optionallyRenderChildrenAppendAndBuild(List<Component> children, B builder, C context) {
    if (!children.isEmpty())
      children.forEach(child -> builder.append(render(child, (C)context))); 
    return (O)builder.build();
  }
  
  protected <B extends ComponentBuilder<?, ?>> void mergeStyle(Component component, B builder, C context) {
    builder.mergeStyle(component, MERGES);
    builder.clickEvent(component.clickEvent());
    HoverEvent<?> hoverEvent = component.hoverEvent();
    if (hoverEvent != null)
      builder.hoverEvent((HoverEventSource)hoverEvent.withRenderedValue(this, context)); 
  }
}
