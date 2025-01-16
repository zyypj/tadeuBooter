package me.syncwrld.booter.libs.google.yamlbeans.document;

import java.io.IOException;
import me.syncwrld.booter.libs.google.yamlbeans.YamlConfig;
import me.syncwrld.booter.libs.google.yamlbeans.emitter.Emitter;
import me.syncwrld.booter.libs.google.yamlbeans.emitter.EmitterException;
import me.syncwrld.booter.libs.google.yamlbeans.parser.Event;
import me.syncwrld.booter.libs.google.yamlbeans.parser.ScalarEvent;

public class YamlScalar extends YamlElement {
  String value;
  
  public YamlScalar() {}
  
  public YamlScalar(Object value) {
    this.value = String.valueOf(value);
  }
  
  public String getValue() {
    return this.value;
  }
  
  public void setValue(String value) {
    this.value = value;
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (this.anchor != null) {
      sb.append('&');
      sb.append(this.anchor);
      sb.append(' ');
    } 
    sb.append(this.value);
    if (this.tag != null) {
      sb.append(" !");
      sb.append(this.tag);
    } 
    return sb.toString();
  }
  
  public void emitEvent(Emitter emitter, YamlConfig.WriteConfig config) throws EmitterException, IOException {
    emitter.emit((Event)new ScalarEvent(this.anchor, this.tag, new boolean[] { true, true }, this.value, config.getQuote().getStyle()));
  }
}
