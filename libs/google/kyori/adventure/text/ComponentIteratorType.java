package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.Deque;
import java.util.List;
import java.util.Set;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.HoverEvent;
import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;
import me.syncwrld.booter.libs.jtann.NotNull;

@FunctionalInterface
@NonExtendable
public interface ComponentIteratorType {
  public static final ComponentIteratorType DEPTH_FIRST;
  
  public static final ComponentIteratorType BREADTH_FIRST;
  
  static {
    DEPTH_FIRST = ((component, deque, flags) -> {
        if (flags.contains(ComponentIteratorFlag.INCLUDE_TRANSLATABLE_COMPONENT_ARGUMENTS) && component instanceof TranslatableComponent) {
          TranslatableComponent translatable = (TranslatableComponent)component;
          List<? extends ComponentLike> args = (List)translatable.arguments();
          for (int j = args.size() - 1; j >= 0; j--)
            deque.addFirst(((ComponentLike)args.get(j)).asComponent()); 
        } 
        HoverEvent<?> hoverEvent = component.hoverEvent();
        if (hoverEvent != null) {
          HoverEvent.Action<?> action = hoverEvent.action();
          if (flags.contains(ComponentIteratorFlag.INCLUDE_HOVER_SHOW_ENTITY_NAME) && action == HoverEvent.Action.SHOW_ENTITY) {
            deque.addFirst(((HoverEvent.ShowEntity)hoverEvent.value()).name());
          } else if (flags.contains(ComponentIteratorFlag.INCLUDE_HOVER_SHOW_TEXT_COMPONENT) && action == HoverEvent.Action.SHOW_TEXT) {
            deque.addFirst((Component)hoverEvent.value());
          } 
        } 
        List<Component> children = component.children();
        for (int i = children.size() - 1; i >= 0; i--)
          deque.addFirst(children.get(i)); 
      });
    BREADTH_FIRST = ((component, deque, flags) -> {
        if (flags.contains(ComponentIteratorFlag.INCLUDE_TRANSLATABLE_COMPONENT_ARGUMENTS) && component instanceof TranslatableComponent)
          for (TranslationArgument argument : ((TranslatableComponent)component).arguments())
            deque.add(argument.asComponent());  
        HoverEvent<?> hoverEvent = component.hoverEvent();
        if (hoverEvent != null) {
          HoverEvent.Action<?> action = hoverEvent.action();
          if (flags.contains(ComponentIteratorFlag.INCLUDE_HOVER_SHOW_ENTITY_NAME) && action == HoverEvent.Action.SHOW_ENTITY) {
            deque.addLast(((HoverEvent.ShowEntity)hoverEvent.value()).name());
          } else if (flags.contains(ComponentIteratorFlag.INCLUDE_HOVER_SHOW_TEXT_COMPONENT) && action == HoverEvent.Action.SHOW_TEXT) {
            deque.addLast((Component)hoverEvent.value());
          } 
        } 
        deque.addAll(component.children());
      });
  }
  
  void populate(@NotNull Component paramComponent, @NotNull Deque<Component> paramDeque, @NotNull Set<ComponentIteratorFlag> paramSet);
}
