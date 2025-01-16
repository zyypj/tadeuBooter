package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.gson;

import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.ClickEvent;

final class ClickEventActionSerializer {
  static final TypeAdapter<ClickEvent.Action> INSTANCE = IndexedSerializer.lenient("click action", ClickEvent.Action.NAMES);
}
