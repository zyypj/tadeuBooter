package me.syncwrld.booter.libs.google.yamlbeans.tokenizer;

public enum TokenType {
  DOCUMENT_START, DOCUMENT_END, BLOCK_MAPPING_START, BLOCK_SEQUENCE_START, BLOCK_ENTRY, BLOCK_END, FLOW_ENTRY, FLOW_MAPPING_END, FLOW_MAPPING_START, FLOW_SEQUENCE_END, FLOW_SEQUENCE_START, KEY, VALUE, STREAM_END, STREAM_START, ALIAS, ANCHOR, DIRECTIVE, SCALAR, TAG;
  
  public String toString() {
    return name().toLowerCase().replace('_', ' ');
  }
}
