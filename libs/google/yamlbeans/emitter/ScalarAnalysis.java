package me.syncwrld.booter.libs.google.yamlbeans.emitter;

import java.util.regex.Pattern;

class ScalarAnalysis {
  private static final Pattern DOCUMENT_INDICATOR = Pattern.compile("^(---|\\.\\.\\.)");
  
  private static final String NULL_BL_T_LINEBR = "\000 \t\r\n";
  
  private static final String SPECIAL_INDICATOR = "#,[]{}#&*!|>'\"%@`";
  
  private static final String FLOW_INDICATOR = ",?[]{}";
  
  public final String scalar;
  
  public final boolean empty;
  
  public final boolean multiline;
  
  public final boolean allowFlowPlain;
  
  public final boolean allowBlockPlain;
  
  public final boolean allowSingleQuoted;
  
  public final boolean allowDoubleQuoted;
  
  public final boolean allowBlock;
  
  private ScalarAnalysis(String scalar, boolean empty, boolean multiline, boolean allowFlowPlain, boolean allowBlockPlain, boolean allowSingleQuoted, boolean allowDoubleQuoted, boolean allowBlock) {
    this.scalar = scalar;
    this.empty = empty;
    this.multiline = multiline;
    this.allowFlowPlain = allowFlowPlain;
    this.allowBlockPlain = allowBlockPlain;
    this.allowSingleQuoted = allowSingleQuoted;
    this.allowDoubleQuoted = allowDoubleQuoted;
    this.allowBlock = allowBlock;
  }
  
  public static ScalarAnalysis analyze(String scalar, boolean escapeUnicode) {
    if (scalar == null)
      return new ScalarAnalysis(scalar, true, false, false, true, true, true, false); 
    if ("".equals(scalar))
      return new ScalarAnalysis(scalar, false, false, false, false, false, true, false); 
    boolean blockIndicators = false;
    boolean flowIndicators = false;
    boolean lineBreaks = false;
    boolean specialCharacters = false;
    boolean inlineBreaks = false;
    boolean leadingSpaces = false;
    boolean leadingBreaks = false;
    boolean trailingSpaces = false;
    boolean trailingBreaks = false;
    boolean inlineBreaksSpaces = false;
    boolean mixedBreaksSpaces = false;
    if (DOCUMENT_INDICATOR.matcher(scalar).matches()) {
      blockIndicators = true;
      flowIndicators = true;
    } 
    boolean preceededBySpace = true;
    boolean followedBySpace = (scalar.length() == 1 || "\000 \t\r\n".indexOf(scalar.charAt(1)) != -1);
    boolean spaces = false;
    boolean breaks = false;
    boolean mixed = false;
    boolean leading = false;
    int index = 0;
    while (index < scalar.length()) {
      char ceh = scalar.charAt(index);
      if (index == 0) {
        if ("#,[]{}#&*!|>'\"%@`".indexOf(ceh) != -1) {
          flowIndicators = true;
          blockIndicators = true;
        } 
        if (ceh == '?' || ceh == ':') {
          flowIndicators = true;
          if (followedBySpace)
            blockIndicators = true; 
        } 
        if (ceh == '-' && followedBySpace) {
          flowIndicators = true;
          blockIndicators = true;
        } 
      } else {
        if (",?[]{}".indexOf(ceh) != -1)
          flowIndicators = true; 
        if (ceh == ':') {
          flowIndicators = true;
          if (followedBySpace)
            blockIndicators = true; 
        } 
        if (ceh == '#' && preceededBySpace) {
          flowIndicators = true;
          blockIndicators = true;
        } 
      } 
      if (ceh == '\n' || '' == ceh)
        lineBreaks = true; 
      if (escapeUnicode && 
        ceh != '\n' && ceh != '\t' && (' ' > ceh || ceh > '~'))
        specialCharacters = true; 
      if (' ' == ceh || '\n' == ceh || '' == ceh) {
        if (spaces && breaks) {
          if (ceh != ' ')
            mixed = true; 
        } else if (spaces) {
          if (ceh != ' ') {
            breaks = true;
            mixed = true;
          } 
        } else if (breaks) {
          if (ceh == ' ')
            spaces = true; 
        } else {
          leading = (index == 0);
          if (ceh == ' ') {
            spaces = true;
          } else {
            breaks = true;
          } 
        } 
      } else if (spaces || breaks) {
        if (leading) {
          if (spaces && breaks) {
            mixedBreaksSpaces = true;
          } else if (spaces) {
            leadingSpaces = true;
          } else if (breaks) {
            leadingBreaks = true;
          } 
        } else if (mixed) {
          mixedBreaksSpaces = true;
        } else if (spaces && breaks) {
          inlineBreaksSpaces = true;
        } else if (!spaces) {
          if (breaks)
            inlineBreaks = true; 
        } 
        spaces = breaks = mixed = leading = false;
      } 
      if ((spaces || breaks) && index == scalar.length() - 1) {
        if (spaces && breaks) {
          mixedBreaksSpaces = true;
        } else if (spaces) {
          trailingSpaces = true;
          if (leading)
            leadingSpaces = true; 
        } else if (breaks) {
          trailingBreaks = true;
          if (leading)
            leadingBreaks = true; 
        } 
        spaces = breaks = mixed = leading = false;
      } 
      index++;
      preceededBySpace = ("\000 \t\r\n".indexOf(ceh) != -1);
      followedBySpace = (index + 1 >= scalar.length() || "\000 \t\r\n".indexOf(scalar.charAt(index + 1)) != -1);
    } 
    boolean allowFlowPlain = true;
    boolean allowBlockPlain = true;
    boolean allowSingleQuoted = true;
    boolean allowDoubleQuoted = true;
    boolean allowBlock = true;
    if (leadingSpaces || leadingBreaks || trailingSpaces)
      allowFlowPlain = allowBlockPlain = allowBlock = false; 
    if (trailingBreaks)
      allowFlowPlain = allowBlockPlain = false; 
    if (inlineBreaksSpaces)
      allowFlowPlain = allowBlockPlain = allowSingleQuoted = false; 
    if (mixedBreaksSpaces || specialCharacters)
      allowFlowPlain = allowBlockPlain = allowSingleQuoted = allowBlock = false; 
    if (inlineBreaks)
      allowFlowPlain = allowBlockPlain = allowSingleQuoted = false; 
    if (trailingBreaks)
      allowSingleQuoted = false; 
    if (lineBreaks)
      allowFlowPlain = allowBlockPlain = false; 
    if (flowIndicators)
      allowFlowPlain = false; 
    if (blockIndicators)
      allowBlockPlain = false; 
    return new ScalarAnalysis(scalar, false, lineBreaks, allowFlowPlain, allowBlockPlain, allowSingleQuoted, allowDoubleQuoted, allowBlock);
  }
}
