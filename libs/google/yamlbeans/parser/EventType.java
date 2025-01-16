package me.syncwrld.booter.libs.google.yamlbeans.parser;

public enum EventType {
  STREAM_START, STREAM_END, SEQUENCE_START, SEQUENCE_END, SCALAR, MAPPING_START, MAPPING_END, DOCUMENT_START, DOCUMENT_END, ALIAS;
  
  public String toString() {
    return name().toLowerCase().replace('_', ' ');
  }
}
