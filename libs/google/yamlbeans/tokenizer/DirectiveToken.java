package me.syncwrld.booter.libs.google.yamlbeans.tokenizer;

public class DirectiveToken extends Token {
  private final String directive;
  
  private final String value;
  
  public DirectiveToken(String directive, String value) {
    super(TokenType.DIRECTIVE);
    this.directive = directive;
    this.value = value;
  }
  
  public String getDirective() {
    return this.directive;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public String toString() {
    return "<" + this.type + " directive='" + this.directive + "' value='" + this.value + "'>";
  }
}
