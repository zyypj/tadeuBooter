package me.syncwrld.booter.libs.google.yamlbeans.tokenizer;

public class ScalarToken extends Token {
  private String value;
  
  private boolean plain;
  
  private char style;
  
  public ScalarToken(String value, boolean plain) {
    this(value, plain, false);
  }
  
  public ScalarToken(String value, boolean plain, char style) {
    super(TokenType.SCALAR);
    this.value = value;
    this.plain = plain;
    this.style = style;
  }
  
  public boolean getPlain() {
    return this.plain;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public char getStyle() {
    return this.style;
  }
  
  public String toString() {
    return "<" + this.type + " value='" + this.value + "' plain='" + this.plain + "' style='" + ((this.style == '\000') ? "" : (String)Character.valueOf(this.style)) + "'>";
  }
}
