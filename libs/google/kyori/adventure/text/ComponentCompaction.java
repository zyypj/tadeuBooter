package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;
import me.syncwrld.booter.libs.jtann.VisibleForTesting;

final class ComponentCompaction {
  @VisibleForTesting
  static final boolean SIMPLIFY_STYLE_FOR_BLANK_COMPONENTS = false;
  
  static Component compact(@NotNull Component self, @Nullable Style parentStyle) {
    List<Component> children = self.children();
    Component optimized = self.children(Collections.emptyList());
    if (parentStyle != null)
      optimized = optimized.style(self.style().unmerge(parentStyle)); 
    int childrenSize = children.size();
    if (childrenSize == 0) {
      if (isBlank(optimized))
        optimized = optimized.style(simplifyStyleForBlank(optimized.style(), parentStyle)); 
      return optimized;
    } 
    if (childrenSize == 1 && optimized instanceof TextComponent) {
      TextComponent textComponent = (TextComponent)optimized;
      if (textComponent.content().isEmpty()) {
        Component child = children.get(0);
        return child.style(child.style().merge(optimized.style(), Style.Merge.Strategy.IF_ABSENT_ON_TARGET)).compact();
      } 
    } 
    Style childParentStyle = optimized.style();
    if (parentStyle != null)
      childParentStyle = childParentStyle.merge(parentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET); 
    List<Component> childrenToAppend = new ArrayList<>(children.size());
    int i;
    for (i = 0; i < children.size(); i++) {
      Component child = children.get(i);
      child = compact(child, childParentStyle);
      if (child.children().isEmpty() && child instanceof TextComponent) {
        TextComponent textComponent = (TextComponent)child;
        if (textComponent.content().isEmpty())
          continue; 
      } 
      childrenToAppend.add(child);
      continue;
    } 
    if (optimized instanceof TextComponent)
      while (!childrenToAppend.isEmpty()) {
        Component child = childrenToAppend.get(0);
        Style childStyle = child.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
        if (child instanceof TextComponent && Objects.equals(childStyle, childParentStyle)) {
          optimized = joinText((TextComponent)optimized, (TextComponent)child);
          childrenToAppend.remove(0);
          childrenToAppend.addAll(0, child.children());
        } 
      }  
    for (i = 0; i + 1 < childrenToAppend.size(); ) {
      Component child = childrenToAppend.get(i);
      Component neighbor = childrenToAppend.get(i + 1);
      if (child.children().isEmpty() && child instanceof TextComponent && neighbor instanceof TextComponent) {
        Style childStyle = child.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
        Style neighborStyle = neighbor.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
        if (childStyle.equals(neighborStyle)) {
          Component combined = joinText((TextComponent)child, (TextComponent)neighbor);
          childrenToAppend.set(i, combined);
          childrenToAppend.remove(i + 1);
          continue;
        } 
      } 
      i++;
    } 
    if (childrenToAppend.isEmpty() && isBlank(optimized))
      optimized = optimized.style(simplifyStyleForBlank(optimized.style(), parentStyle)); 
    return optimized.children((List)childrenToAppend);
  }
  
  private static boolean isBlank(Component component) {
    if (component instanceof TextComponent) {
      TextComponent textComponent = (TextComponent)component;
      String content = textComponent.content();
      for (int i = 0; i < content.length(); i++) {
        char c = content.charAt(i);
        if (c != ' ')
          return false; 
      } 
      return true;
    } 
    return false;
  }
  
  @NotNull
  private static Style simplifyStyleForBlank(@NotNull Style style, @Nullable Style parentStyle) {
    return style;
  }
  
  private static TextComponent joinText(TextComponent one, TextComponent two) {
    return TextComponentImpl.create((List)two.children(), one.style(), one.content() + two.content());
  }
}
