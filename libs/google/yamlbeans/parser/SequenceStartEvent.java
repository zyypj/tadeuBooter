package me.syncwrld.booter.libs.google.yamlbeans.parser;

public class SequenceStartEvent extends CollectionStartEvent {
  public SequenceStartEvent(String anchor, String tag, boolean implicit, boolean flowStyle) {
    super(EventType.SEQUENCE_START, anchor, tag, implicit, flowStyle);
  }
}
