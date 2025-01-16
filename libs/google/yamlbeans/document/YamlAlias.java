package me.syncwrld.booter.libs.google.yamlbeans.document;

import java.io.IOException;
import me.syncwrld.booter.libs.google.yamlbeans.YamlConfig;
import me.syncwrld.booter.libs.google.yamlbeans.emitter.Emitter;
import me.syncwrld.booter.libs.google.yamlbeans.emitter.EmitterException;
import me.syncwrld.booter.libs.google.yamlbeans.parser.AliasEvent;
import me.syncwrld.booter.libs.google.yamlbeans.parser.Event;

public class YamlAlias extends YamlElement {
  public void emitEvent(Emitter emitter, YamlConfig.WriteConfig config) throws EmitterException, IOException {
    emitter.emit((Event)new AliasEvent(this.anchor));
  }
  
  public String toString() {
    return "*" + this.anchor;
  }
}
