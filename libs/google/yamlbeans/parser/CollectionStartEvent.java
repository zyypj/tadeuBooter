package me.syncwrld.booter.libs.google.yamlbeans.parser;

public abstract class CollectionStartEvent extends NodeEvent {
  public final String tag;
  
  public final boolean isImplicit;
  
  public final boolean isFlowStyle;
  
  protected CollectionStartEvent(EventType eventType, String anchor, String tag, boolean isImplicit, boolean isFlowStyle) {
    super(eventType, anchor);
    this.tag = tag;
    this.isImplicit = isImplicit;
    this.isFlowStyle = isFlowStyle;
  }
  
  public String toString() {
    return "<" + this.type + " anchor='" + this.anchor + "' tag='" + this.tag + "' implicit='" + this.isImplicit + "' flowStyle='" + this.isFlowStyle + "'>";
  }
}
