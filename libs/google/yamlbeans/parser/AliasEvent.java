package me.syncwrld.booter.libs.google.yamlbeans.parser;

public class AliasEvent extends NodeEvent {
  public AliasEvent(String anchor) {
    super(EventType.ALIAS, anchor);
  }
  
  public String toString() {
    return "<" + this.type + " anchor='" + this.anchor + "'>";
  }
}
