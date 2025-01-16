package me.syncwrld.booter.libs.google.yamlbeans.parser;

public class DocumentEndEvent extends Event {
  public final boolean isExplicit;
  
  public DocumentEndEvent(boolean isExplicit) {
    super(EventType.DOCUMENT_END);
    this.isExplicit = isExplicit;
  }
  
  public String toString() {
    return "<" + this.type + " explicit='" + this.isExplicit + "'>";
  }
}
