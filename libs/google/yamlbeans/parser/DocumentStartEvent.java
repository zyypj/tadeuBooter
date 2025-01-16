package me.syncwrld.booter.libs.google.yamlbeans.parser;

import java.util.Map;
import me.syncwrld.booter.libs.google.yamlbeans.Version;

public class DocumentStartEvent extends Event {
  public final boolean isExplicit;
  
  public final Version version;
  
  public final Map<String, String> tags;
  
  public DocumentStartEvent(boolean explicit, Version version, Map<String, String> tags) {
    super(EventType.DOCUMENT_START);
    this.isExplicit = explicit;
    this.version = version;
    this.tags = tags;
  }
  
  public String toString() {
    return "<" + this.type + " explicit='" + this.isExplicit + "' version='" + this.version + "' tags='" + this.tags + "'>";
  }
}
