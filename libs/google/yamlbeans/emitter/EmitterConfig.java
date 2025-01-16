package me.syncwrld.booter.libs.google.yamlbeans.emitter;

public class EmitterConfig {
  boolean canonical;
  
  boolean useVerbatimTags = true;
  
  int indentSize = 3;
  
  int wrapColumn = 100;
  
  boolean escapeUnicode = true;
  
  boolean prettyFlow;
  
  public void setCanonical(boolean canonical) {
    this.canonical = canonical;
  }
  
  public void setIndentSize(int indentSize) {
    if (indentSize < 2)
      throw new IllegalArgumentException("indentSize cannot be less than 2."); 
    this.indentSize = indentSize;
  }
  
  public void setWrapColumn(int wrapColumn) {
    if (wrapColumn <= 4)
      throw new IllegalArgumentException("wrapColumn must be greater than 4."); 
    this.wrapColumn = wrapColumn;
  }
  
  public void setUseVerbatimTags(boolean useVerbatimTags) {
    this.useVerbatimTags = useVerbatimTags;
  }
  
  public void setEscapeUnicode(boolean escapeUnicode) {
    this.escapeUnicode = escapeUnicode;
  }
  
  public void setPrettyFlow(boolean prettyFlow) {
    this.prettyFlow = prettyFlow;
  }
}
