package me.syncwrld.booter.libs.google.yamlbeans.document;

import java.io.IOException;
import me.syncwrld.booter.libs.google.yamlbeans.YamlConfig;
import me.syncwrld.booter.libs.google.yamlbeans.emitter.Emitter;
import me.syncwrld.booter.libs.google.yamlbeans.emitter.EmitterException;

public abstract class YamlElement {
  String tag;
  
  String anchor;
  
  public void setTag(String tag) {
    this.tag = tag;
  }
  
  public void setAnchor(String anchor) {
    this.anchor = anchor;
  }
  
  public String getTag() {
    return this.tag;
  }
  
  public String getAnchor() {
    return this.anchor;
  }
  
  public abstract void emitEvent(Emitter paramEmitter, YamlConfig.WriteConfig paramWriteConfig) throws EmitterException, IOException;
}
