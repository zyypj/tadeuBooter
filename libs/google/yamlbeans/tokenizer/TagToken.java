package me.syncwrld.booter.libs.google.yamlbeans.tokenizer;

public class TagToken extends Token {
  private final String handle;
  
  private final String suffix;
  
  public TagToken(String handle, String suffix) {
    super(TokenType.TAG);
    this.handle = handle;
    this.suffix = suffix;
  }
  
  public String getHandle() {
    return this.handle;
  }
  
  public String getSuffix() {
    return this.suffix;
  }
  
  public String toString() {
    return "<" + this.type + " handle='" + this.handle + "' suffix='" + this.suffix + "'>";
  }
}
