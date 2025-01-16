package me.syncwrld.booter.libs.google.yamlbeans.parser;

public class Event {
  public static final Event MAPPING_END = new Event(EventType.MAPPING_END);
  
  public static final Event SEQUENCE_END = new Event(EventType.SEQUENCE_END);
  
  public static final Event STREAM_END = new Event(EventType.STREAM_END);
  
  public static final Event STREAM_START = new Event(EventType.STREAM_START);
  
  public static final Event DOCUMENT_END_TRUE = new DocumentEndEvent(true);
  
  public static final Event DOCUMENT_END_FALSE = new DocumentEndEvent(false);
  
  public final EventType type;
  
  public Event(EventType type) {
    this.type = type;
  }
  
  public String toString() {
    return "<" + this.type + ">";
  }
}
