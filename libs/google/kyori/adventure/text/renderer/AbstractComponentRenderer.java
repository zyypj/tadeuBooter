package me.syncwrld.booter.libs.google.kyori.adventure.text.renderer;

import me.syncwrld.booter.libs.google.kyori.adventure.text.BlockNBTComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.text.EntityNBTComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.KeybindComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.ScoreComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.SelectorComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.StorageNBTComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.TextComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.TranslatableComponent;
import me.syncwrld.booter.libs.jtann.NotNull;

public abstract class AbstractComponentRenderer<C> implements ComponentRenderer<C> {
  @NotNull
  public Component render(@NotNull Component component, @NotNull C context) {
    if (component instanceof TextComponent)
      return renderText((TextComponent)component, context); 
    if (component instanceof TranslatableComponent)
      return renderTranslatable((TranslatableComponent)component, context); 
    if (component instanceof KeybindComponent)
      return renderKeybind((KeybindComponent)component, context); 
    if (component instanceof ScoreComponent)
      return renderScore((ScoreComponent)component, context); 
    if (component instanceof SelectorComponent)
      return renderSelector((SelectorComponent)component, context); 
    if (component instanceof me.syncwrld.booter.libs.google.kyori.adventure.text.NBTComponent) {
      if (component instanceof BlockNBTComponent)
        return renderBlockNbt((BlockNBTComponent)component, context); 
      if (component instanceof EntityNBTComponent)
        return renderEntityNbt((EntityNBTComponent)component, context); 
      if (component instanceof StorageNBTComponent)
        return renderStorageNbt((StorageNBTComponent)component, context); 
    } 
    return component;
  }
  
  @NotNull
  protected abstract Component renderBlockNbt(@NotNull BlockNBTComponent paramBlockNBTComponent, @NotNull C paramC);
  
  @NotNull
  protected abstract Component renderEntityNbt(@NotNull EntityNBTComponent paramEntityNBTComponent, @NotNull C paramC);
  
  @NotNull
  protected abstract Component renderStorageNbt(@NotNull StorageNBTComponent paramStorageNBTComponent, @NotNull C paramC);
  
  @NotNull
  protected abstract Component renderKeybind(@NotNull KeybindComponent paramKeybindComponent, @NotNull C paramC);
  
  @NotNull
  protected abstract Component renderScore(@NotNull ScoreComponent paramScoreComponent, @NotNull C paramC);
  
  @NotNull
  protected abstract Component renderSelector(@NotNull SelectorComponent paramSelectorComponent, @NotNull C paramC);
  
  @NotNull
  protected abstract Component renderText(@NotNull TextComponent paramTextComponent, @NotNull C paramC);
  
  @NotNull
  protected abstract Component renderTranslatable(@NotNull TranslatableComponent paramTranslatableComponent, @NotNull C paramC);
}
