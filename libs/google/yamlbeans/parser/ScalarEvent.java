package me.syncwrld.booter.libs.google.yamlbeans.parser;

import java.util.Arrays;

public class ScalarEvent extends NodeEvent {
  public final String tag;
  
  public final boolean[] implicit;
  
  public final String value;
  
  public final char style;
  
  public ScalarEvent(String anchor, String tag, boolean[] implicit, String value, char style) {
    super(EventType.SCALAR, anchor);
    this.tag = tag;
    this.implicit = implicit;
    this.value = value;
    this.style = style;
  }
  
  public String toString() {
    return "<" + this.type + " value='" + this.value + "' anchor='" + this.anchor + "' tag='" + this.tag + "' implicit='" + 
      Arrays.toString(this.implicit) + "' style='" + ((this.style == '\000') ? "" : (String)Character.valueOf(this.style)) + "'>";
  }
}
