package me.syncwrld.booter.libs.google.yamlbeans.tokenizer;

public class Token {
  static final Token DOCUMENT_START = new Token(TokenType.DOCUMENT_START);
  
  static final Token DOCUMENT_END = new Token(TokenType.DOCUMENT_END);
  
  static final Token BLOCK_MAPPING_START = new Token(TokenType.BLOCK_MAPPING_START);
  
  static final Token BLOCK_SEQUENCE_START = new Token(TokenType.BLOCK_SEQUENCE_START);
  
  static final Token BLOCK_ENTRY = new Token(TokenType.BLOCK_ENTRY);
  
  static final Token BLOCK_END = new Token(TokenType.BLOCK_END);
  
  static final Token FLOW_ENTRY = new Token(TokenType.FLOW_ENTRY);
  
  static final Token FLOW_MAPPING_END = new Token(TokenType.FLOW_MAPPING_END);
  
  static final Token FLOW_MAPPING_START = new Token(TokenType.FLOW_MAPPING_START);
  
  static final Token FLOW_SEQUENCE_END = new Token(TokenType.FLOW_SEQUENCE_END);
  
  static final Token FLOW_SEQUENCE_START = new Token(TokenType.FLOW_SEQUENCE_START);
  
  static final Token KEY = new Token(TokenType.KEY);
  
  static final Token VALUE = new Token(TokenType.VALUE);
  
  static final Token STREAM_END = new Token(TokenType.STREAM_END);
  
  static final Token STREAM_START = new Token(TokenType.STREAM_START);
  
  public final TokenType type;
  
  public Token(TokenType type) {
    this.type = type;
  }
  
  public String toString() {
    return "<" + this.type + ">";
  }
}
