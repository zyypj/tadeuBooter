package me.syncwrld.booter.libs.google.yamlbeans.tokenizer;

public class AliasToken extends Token {
  private String instanceName;
  
  public AliasToken() {
    super(TokenType.ALIAS);
  }
  
  public String getInstanceName() {
    return this.instanceName;
  }
  
  public void setInstanceName(String instanceName) {
    this.instanceName = instanceName;
  }
  
  public String toString() {
    return "<" + this.type + " aliasName='" + this.instanceName + "'>";
  }
}
