package me.syncwrld.booter.libs.google.yamlbeans.document;

import java.io.IOException;
import me.syncwrld.booter.libs.google.yamlbeans.YamlConfig;
import me.syncwrld.booter.libs.google.yamlbeans.emitter.Emitter;
import me.syncwrld.booter.libs.google.yamlbeans.emitter.EmitterException;
import me.syncwrld.booter.libs.google.yamlbeans.parser.Event;
import me.syncwrld.booter.libs.google.yamlbeans.parser.ScalarEvent;

public class YamlEntry {
  YamlScalar key;
  
  YamlElement value;
  
  public YamlEntry(YamlScalar key, YamlElement value) {
    this.key = key;
    this.value = value;
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.key.toString());
    sb.append(':');
    sb.append(this.value.toString());
    return sb.toString();
  }
  
  public YamlScalar getKey() {
    return this.key;
  }
  
  public YamlElement getValue() {
    return this.value;
  }
  
  public void setKey(YamlScalar key) {
    this.key = key;
  }
  
  public void setValue(YamlElement value) {
    this.value = value;
  }
  
  public void setValue(boolean value) {
    this.value = new YamlScalar(Boolean.valueOf(value));
  }
  
  public void setValue(Number value) {
    this.value = new YamlScalar(value);
  }
  
  public void setValue(String value) {
    this.value = new YamlScalar(value);
  }
  
  public void emitEvent(Emitter emitter, YamlConfig.WriteConfig config) throws EmitterException, IOException {
    this.key.emitEvent(emitter, config);
    if (this.value == null) {
      emitter.emit((Event)new ScalarEvent(null, null, new boolean[] { true, true }, null, config.getQuote().getStyle()));
    } else {
      this.value.emitEvent(emitter, config);
    } 
  }
}
