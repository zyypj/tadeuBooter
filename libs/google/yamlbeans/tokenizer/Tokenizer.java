package me.syncwrld.booter.libs.google.yamlbeans.tokenizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
  private static final String LINEBR = "\n  ";
  
  private static final String NULL_BL_LINEBR = "\000 \r\n";
  
  private static final String NULL_BL_T_LINEBR = "\000 \t\r\n";
  
  private static final String NULL_OR_OTHER = "\000 \t\r\n";
  
  private static final String NULL_OR_LINEBR = "\000\r\n";
  
  private static final String FULL_LINEBR = "\r\n";
  
  private static final String BLANK_OR_LINEBR = " \r\n";
  
  private static final String S4 = "\000 \t\r\n([]{}";
  
  private static final String ALPHA = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
  
  private static final String STRANGE_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-#;/?:@&=+$,_.!~*'()[]";
  
  private static final String RN = "\r\n";
  
  private static final String BLANK_T = " \t";
  
  private static final String SPACES_AND_STUFF = "'\"\\\000 \t\r\n";
  
  private static final String DOUBLE_ESC = "\"\\";
  
  private static final String NON_ALPHA_OR_NUM = "\000 \t\r\n?:,]}%@`";
  
  private static final Pattern NON_PRINTABLE = Pattern.compile("[^\t\n\r -~ -ÿ]");
  
  private static final Pattern NOT_HEXA = Pattern.compile("[^0-9A-Fa-f]");
  
  private static final Pattern NON_ALPHA = Pattern.compile("[^-0-9A-Za-z_]");
  
  private static final Pattern R_FLOWZERO = Pattern.compile("[\000 \t\r\n]|(:[\000 \t\r\n])");
  
  private static final Pattern R_FLOWNONZERO = Pattern.compile("[\000 \t\r\n\\[\\]{},:?]");
  
  private static final Pattern END_OR_START = Pattern.compile("^(---|\\.\\.\\.)[\000 \t\r\n]$");
  
  private static final Pattern ENDING = Pattern.compile("^---[\000 \t\r\n]$");
  
  private static final Pattern START = Pattern.compile("^\\.\\.\\.[\000 \t\r\n]$");
  
  private static final Pattern BEG = Pattern.compile("^([^\000 \t\r\n\\-?:,\\[\\]{}#&*!|>'\"%@]|([\\-?:][^\000 \t\r\n]))");
  
  private static final Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap<Character, String>();
  
  private static final Map<Character, Integer> ESCAPE_CODES = new HashMap<Character, Integer>();
  
  static {
    ESCAPE_REPLACEMENTS.put(Character.valueOf('0'), "\000");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('a'), "\007");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('b'), "\b");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('t'), "\t");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\t'), "\t");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('n'), "\n");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('v'), "\013");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('f'), "\f");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('r'), "\r");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('e'), "\033");
    ESCAPE_REPLACEMENTS.put(Character.valueOf(' '), " ");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('"'), "\"");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\\'), "\\");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('N'), "");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('_'), " ");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('L'), " ");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('P'), " ");
    ESCAPE_CODES.put(Character.valueOf('x'), Integer.valueOf(2));
    ESCAPE_CODES.put(Character.valueOf('u'), Integer.valueOf(4));
    ESCAPE_CODES.put(Character.valueOf('U'), Integer.valueOf(8));
  }
  
  private boolean done = false;
  
  private int flowLevel = 0;
  
  private int tokensTaken = 0;
  
  private int indent = -1;
  
  private boolean allowSimpleKey = true;
  
  private boolean eof;
  
  private int lineNumber = 0;
  
  private int column = 0;
  
  private int pointer = 0;
  
  private final StringBuilder buffer;
  
  private final Reader reader;
  
  private final List<Token> tokens = new LinkedList<Token>();
  
  private final List<Integer> indents = new LinkedList<Integer>();
  
  private final Map<Integer, SimpleKey> possibleSimpleKeys = new HashMap<Integer, SimpleKey>();
  
  private boolean docStart = false;
  
  public Tokenizer(Reader reader) {
    if (reader == null)
      throw new IllegalArgumentException("reader cannot be null."); 
    if (!(reader instanceof BufferedReader))
      reader = new BufferedReader(reader); 
    this.reader = reader;
    this.buffer = new StringBuilder();
    this.eof = false;
    fetchStreamStart();
  }
  
  public Tokenizer(String yaml) {
    this(new StringReader(yaml));
  }
  
  public Token peekNextToken() throws TokenizerException {
    while (needMoreTokens())
      fetchMoreTokens(); 
    return this.tokens.isEmpty() ? null : this.tokens.get(0);
  }
  
  public TokenType peekNextTokenType() throws TokenizerException {
    Token token = peekNextToken();
    if (token == null)
      return null; 
    return token.type;
  }
  
  public Token getNextToken() throws TokenizerException {
    while (needMoreTokens())
      fetchMoreTokens(); 
    if (!this.tokens.isEmpty()) {
      this.tokensTaken++;
      Token token = this.tokens.remove(0);
      return token;
    } 
    return null;
  }
  
  public Iterator iterator() {
    return new Iterator() {
        public boolean hasNext() {
          return (null != Tokenizer.this.peekNextToken());
        }
        
        public Object next() {
          return Tokenizer.this.getNextToken();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public int getLineNumber() {
    return this.lineNumber;
  }
  
  public int getColumn() {
    return this.column;
  }
  
  public void close() throws IOException {
    this.reader.close();
  }
  
  private char peek() {
    if (this.pointer + 1 > this.buffer.length())
      update(1); 
    return this.buffer.charAt(this.pointer);
  }
  
  private char peek(int index) {
    if (this.pointer + index + 1 > this.buffer.length())
      update(index + 1); 
    return this.buffer.charAt(this.pointer + index);
  }
  
  private String prefix(int length) {
    if (this.pointer + length >= this.buffer.length())
      update(length); 
    if (this.pointer + length > this.buffer.length())
      return this.buffer.substring(this.pointer, this.buffer.length()); 
    return this.buffer.substring(this.pointer, this.pointer + length);
  }
  
  private String prefixForward(int length) {
    if (this.pointer + length + 1 >= this.buffer.length())
      update(length + 1); 
    String buff = null;
    if (this.pointer + length > this.buffer.length()) {
      buff = this.buffer.substring(this.pointer, this.buffer.length());
    } else {
      buff = this.buffer.substring(this.pointer, this.pointer + length);
    } 
    char ch = Character.MIN_VALUE;
    for (int i = 0, j = buff.length(); i < j; i++) {
      ch = buff.charAt(i);
      this.pointer++;
      if ("\n  ".indexOf(ch) != -1 || (ch == '\r' && buff.charAt(i + 1) != '\n')) {
        this.column = 0;
        this.lineNumber++;
      } else if (ch != '﻿') {
        this.column++;
      } 
    } 
    return buff;
  }
  
  private void forward() {
    if (this.pointer + 2 >= this.buffer.length())
      update(2); 
    char ch1 = this.buffer.charAt(this.pointer);
    this.pointer++;
    if (ch1 == '\n' || ch1 == '' || (ch1 == '\r' && this.buffer.charAt(this.pointer) != '\n')) {
      this.column = 0;
      this.lineNumber++;
    } else {
      this.column++;
    } 
  }
  
  private void forward(int length) {
    if (this.pointer + length + 1 >= this.buffer.length())
      update(length + 1); 
    char ch = Character.MIN_VALUE;
    for (int i = 0; i < length; i++) {
      ch = this.buffer.charAt(this.pointer);
      this.pointer++;
      if ("\n  ".indexOf(ch) != -1 || (ch == '\r' && this.buffer.charAt(this.pointer) != '\n')) {
        this.column = 0;
        this.lineNumber++;
      } else if (ch != '﻿') {
        this.column++;
      } 
    } 
  }
  
  private void update(int length) {
    this.buffer.delete(0, this.pointer);
    this.pointer = 0;
    while (this.buffer.length() < length) {
      String rawData = "";
      if (!this.eof) {
        char[] data = new char[1024];
        int converted = -2;
        try {
          converted = this.reader.read(data);
        } catch (IOException ioe) {
          throw new TokenizerException("Error reading from stream.", ioe);
        } 
        if (converted == -1) {
          this.eof = true;
        } else {
          rawData = String.valueOf(data, 0, converted);
        } 
      } 
      this.buffer.append(rawData);
      if (this.eof) {
        this.buffer.append(false);
        break;
      } 
    } 
  }
  
  private boolean needMoreTokens() {
    if (this.done)
      return false; 
    return (this.tokens.isEmpty() || nextPossibleSimpleKey() == this.tokensTaken);
  }
  
  private Token fetchMoreTokens() {
    scanToNextToken();
    unwindIndent(this.column);
    char ch = peek();
    boolean colz = (this.column == 0);
    switch (ch) {
      case '\000':
        return fetchStreamEnd();
      case '\'':
        return fetchSingle();
      case '"':
        return fetchDouble();
      case '?':
        if (this.flowLevel != 0 || "\000 \t\r\n".indexOf(peek(1)) != -1)
          return fetchKey(); 
        break;
      case ':':
        if (this.flowLevel != 0 || "\000 \t\r\n".indexOf(peek(1)) != -1)
          return fetchValue(); 
        break;
      case '%':
        if (colz)
          return fetchDirective(); 
        break;
      case '-':
        if ((colz || this.docStart) && ENDING.matcher(prefix(4)).matches())
          return fetchDocumentStart(); 
        if ("\000 \t\r\n".indexOf(peek(1)) != -1)
          return fetchBlockEntry(); 
        break;
      case '.':
        if (colz && START.matcher(prefix(4)).matches())
          return fetchDocumentEnd(); 
        break;
      case '[':
        return fetchFlowSequenceStart();
      case '{':
        return fetchFlowMappingStart();
      case ']':
        return fetchFlowSequenceEnd();
      case '}':
        return fetchFlowMappingEnd();
      case ',':
        return fetchFlowEntry();
      case '*':
        return fetchAlias();
      case '&':
        return fetchAnchor();
      case '!':
        return fetchTag();
      case '|':
        if (this.flowLevel == 0)
          return fetchLiteral(); 
        break;
      case '>':
        if (this.flowLevel == 0)
          return fetchFolded(); 
        break;
    } 
    if (BEG.matcher(prefix(2)).find())
      return fetchPlain(); 
    if (ch == '\t')
      throw new TokenizerException("Tabs cannot be used for indentation."); 
    throw new TokenizerException("While scanning for the next token, a character that cannot begin a token was found: " + 
        ch(ch));
  }
  
  private int nextPossibleSimpleKey() {
    for (Iterator<SimpleKey> iter = this.possibleSimpleKeys.values().iterator(); iter.hasNext(); ) {
      SimpleKey key = iter.next();
      if (key.tokenNumber > 0)
        return key.tokenNumber; 
    } 
    return -1;
  }
  
  private void savePossibleSimpleKey() {
    if (this.allowSimpleKey)
      this.possibleSimpleKeys.put(Integer.valueOf(this.flowLevel), new SimpleKey(this.tokensTaken + this.tokens.size(), this.column)); 
  }
  
  private void unwindIndent(int col) {
    if (this.flowLevel != 0)
      return; 
    while (this.indent > col) {
      this.indent = ((Integer)this.indents.remove(0)).intValue();
      this.tokens.add(Token.BLOCK_END);
    } 
  }
  
  private boolean addIndent(int col) {
    if (this.indent < col) {
      this.indents.add(0, Integer.valueOf(this.indent));
      this.indent = col;
      return true;
    } 
    return false;
  }
  
  private Token fetchStreamStart() {
    this.docStart = true;
    this.tokens.add(Token.STREAM_START);
    return Token.STREAM_START;
  }
  
  private Token fetchStreamEnd() {
    unwindIndent(-1);
    this.allowSimpleKey = false;
    this.possibleSimpleKeys.clear();
    this.tokens.add(Token.STREAM_END);
    this.done = true;
    return Token.STREAM_END;
  }
  
  private Token fetchDirective() {
    unwindIndent(-1);
    this.allowSimpleKey = false;
    Token tok = scanDirective();
    this.tokens.add(tok);
    return tok;
  }
  
  private Token fetchDocumentStart() {
    this.docStart = false;
    return fetchDocumentIndicator(Token.DOCUMENT_START);
  }
  
  private Token fetchDocumentEnd() {
    return fetchDocumentIndicator(Token.DOCUMENT_END);
  }
  
  private Token fetchDocumentIndicator(Token tok) {
    unwindIndent(-1);
    this.allowSimpleKey = false;
    forward(3);
    this.tokens.add(tok);
    return tok;
  }
  
  private Token fetchFlowSequenceStart() {
    return fetchFlowCollectionStart(Token.FLOW_SEQUENCE_START);
  }
  
  private Token fetchFlowMappingStart() {
    return fetchFlowCollectionStart(Token.FLOW_MAPPING_START);
  }
  
  private Token fetchFlowCollectionStart(Token tok) {
    savePossibleSimpleKey();
    this.flowLevel++;
    this.allowSimpleKey = true;
    forward(1);
    this.tokens.add(tok);
    return tok;
  }
  
  private Token fetchFlowSequenceEnd() {
    return fetchFlowCollectionEnd(Token.FLOW_SEQUENCE_END);
  }
  
  private Token fetchFlowMappingEnd() {
    return fetchFlowCollectionEnd(Token.FLOW_MAPPING_END);
  }
  
  private Token fetchFlowCollectionEnd(Token tok) {
    this.flowLevel--;
    this.allowSimpleKey = false;
    forward(1);
    this.tokens.add(tok);
    return tok;
  }
  
  private Token fetchFlowEntry() {
    this.allowSimpleKey = true;
    forward(1);
    this.tokens.add(Token.FLOW_ENTRY);
    return Token.FLOW_ENTRY;
  }
  
  private Token fetchBlockEntry() {
    if (this.flowLevel == 0) {
      if (!this.allowSimpleKey)
        throw new TokenizerException("Found a sequence entry where it is not allowed."); 
      if (addIndent(this.column))
        this.tokens.add(Token.BLOCK_SEQUENCE_START); 
    } 
    this.allowSimpleKey = true;
    forward();
    this.tokens.add(Token.BLOCK_ENTRY);
    return Token.BLOCK_ENTRY;
  }
  
  private Token fetchKey() {
    if (this.flowLevel == 0) {
      if (!this.allowSimpleKey)
        throw new TokenizerException("Found a mapping key where it is not allowed."); 
      if (addIndent(this.column))
        this.tokens.add(Token.BLOCK_MAPPING_START); 
    } 
    this.allowSimpleKey = (this.flowLevel == 0);
    forward();
    this.tokens.add(Token.KEY);
    return Token.KEY;
  }
  
  private Token fetchValue() {
    SimpleKey key = this.possibleSimpleKeys.get(Integer.valueOf(this.flowLevel));
    if (null == key) {
      if (this.flowLevel == 0 && !this.allowSimpleKey)
        throw new TokenizerException("Found a mapping value where it is not allowed."); 
    } else {
      this.possibleSimpleKeys.remove(Integer.valueOf(this.flowLevel));
      this.tokens.add(key.tokenNumber - this.tokensTaken, Token.KEY);
      if (this.flowLevel == 0 && addIndent(key.column))
        this.tokens.add(key.tokenNumber - this.tokensTaken, Token.BLOCK_MAPPING_START); 
      this.allowSimpleKey = false;
    } 
    forward();
    this.tokens.add(Token.VALUE);
    return Token.VALUE;
  }
  
  private Token fetchAlias() {
    savePossibleSimpleKey();
    this.allowSimpleKey = false;
    Token tok = scanAnchor(new AliasToken());
    this.tokens.add(tok);
    return tok;
  }
  
  private Token fetchAnchor() {
    savePossibleSimpleKey();
    this.allowSimpleKey = false;
    Token tok = scanAnchor(new AnchorToken());
    this.tokens.add(tok);
    return tok;
  }
  
  private Token fetchTag() {
    savePossibleSimpleKey();
    this.allowSimpleKey = false;
    Token tok = scanTag();
    this.tokens.add(tok);
    return tok;
  }
  
  private Token fetchLiteral() {
    return fetchBlockScalar('|');
  }
  
  private Token fetchFolded() {
    return fetchBlockScalar('>');
  }
  
  private Token fetchBlockScalar(char style) {
    this.allowSimpleKey = true;
    Token tok = scanBlockScalar(style);
    this.tokens.add(tok);
    return tok;
  }
  
  private Token fetchSingle() {
    return fetchFlowScalar('\'');
  }
  
  private Token fetchDouble() {
    return fetchFlowScalar('"');
  }
  
  private Token fetchFlowScalar(char style) {
    savePossibleSimpleKey();
    this.allowSimpleKey = false;
    Token tok = scanFlowScalar(style);
    this.tokens.add(tok);
    return tok;
  }
  
  private Token fetchPlain() {
    savePossibleSimpleKey();
    this.allowSimpleKey = false;
    Token tok = scanPlain();
    this.tokens.add(tok);
    return tok;
  }
  
  private void scanToNextToken() {
    while (true) {
      while (peek() == ' ')
        forward(); 
      if (peek() == '#')
        while ("\000\r\n".indexOf(peek()) == -1)
          forward();  
      if (scanLineBreak().length() != 0) {
        if (this.flowLevel == 0)
          this.allowSimpleKey = true; 
        continue;
      } 
      break;
    } 
  }
  
  private Token scanDirective() {
    forward();
    String name = scanDirectiveName();
    String value = null;
    if (name.equals("YAML")) {
      value = scanYamlDirectiveValue();
    } else if (name.equals("TAG")) {
      value = scanTagDirectiveValue();
    } else {
      StringBuilder buffer = new StringBuilder();
      while (true) {
        char ch = peek();
        if ("\000\r\n".indexOf(ch) != -1)
          break; 
        buffer.append(ch);
        forward();
      } 
      value = buffer.toString().trim();
    } 
    scanDirectiveIgnoredLine();
    return new DirectiveToken(name, value);
  }
  
  private String scanDirectiveName() {
    int length = 0;
    char ch = peek(length);
    boolean zlen = true;
    while ("abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_".indexOf(ch) != -1) {
      zlen = false;
      length++;
      ch = peek(length);
    } 
    if (zlen)
      throw new TokenizerException("While scanning for a directive name, expected an alpha or numeric character but found: " + 
          ch(ch)); 
    String value = prefixForward(length);
    if ("\000 \r\n".indexOf(peek()) == -1)
      throw new TokenizerException("While scanning for a directive name, expected an alpha or numeric character but found: " + 
          ch(ch)); 
    return value;
  }
  
  private String scanYamlDirectiveValue() {
    while (peek() == ' ')
      forward(); 
    String major = scanYamlDirectiveNumber();
    if (peek() != '.')
      throw new TokenizerException("While scanning for a directive value, expected a digit or '.' but found: " + ch(peek())); 
    forward();
    String minor = scanYamlDirectiveNumber();
    if ("\000 \r\n".indexOf(peek()) == -1)
      throw new TokenizerException("While scanning for a directive value, expected a digit or '.' but found: " + ch(peek())); 
    return major + "." + minor;
  }
  
  private String scanYamlDirectiveNumber() {
    char ch = peek();
    if (!Character.isDigit(ch))
      throw new TokenizerException("While scanning for a directive number, expected a digit but found: " + ch(ch)); 
    int length = 0;
    while (Character.isDigit(peek(length)))
      length++; 
    String value = prefixForward(length);
    return value;
  }
  
  private String scanTagDirectiveValue() {
    while (peek() == ' ')
      forward(); 
    String handle = scanTagDirectiveHandle();
    while (peek() == ' ')
      forward(); 
    String prefix = scanTagDirectivePrefix();
    return handle + " " + prefix;
  }
  
  private String scanTagDirectiveHandle() {
    String value = scanTagHandle("directive");
    if (peek() != ' ')
      throw new TokenizerException("While scanning for a directive tag handle, expected ' ' but found: " + ch(peek())); 
    return value;
  }
  
  private String scanTagDirectivePrefix() {
    String value = scanTagUri("directive");
    if ("\000 \r\n".indexOf(peek()) == -1)
      throw new TokenizerException("While scanning for a directive tag prefix, expected ' ' but found: " + ch(peek())); 
    return value;
  }
  
  private String scanDirectiveIgnoredLine() {
    while (peek() == ' ')
      forward(); 
    if (peek() == '"')
      while ("\000\r\n".indexOf(peek()) == -1)
        forward();  
    char ch = peek();
    if ("\000\r\n".indexOf(ch) == -1)
      throw new TokenizerException("While scanning a directive, expected a comment or line break but found: " + ch(peek())); 
    return scanLineBreak();
  }
  
  private Token scanAnchor(Token tok) {
    char indicator = peek();
    String name = (indicator == '*') ? "alias" : "anchor";
    forward();
    int length = 0;
    int chunk_size = 16;
    Matcher m = null;
    String chunk = prefix(chunk_size);
    while (!(m = NON_ALPHA.matcher(chunk)).find())
      chunk_size += 16; 
    length = m.start();
    if (length == 0)
      throw new TokenizerException("While scanning an " + name + ", a non-alpha, non-numeric character was found."); 
    String value = prefixForward(length);
    if ("\000 \t\r\n?:,]}%@`".indexOf(peek()) == -1)
      throw new TokenizerException("While scanning an " + name + ", expected an alpha or numeric character but found: " + 
          ch(peek())); 
    if (tok instanceof AnchorToken) {
      ((AnchorToken)tok).setInstanceName(value);
    } else {
      ((AliasToken)tok).setInstanceName(value);
    } 
    return tok;
  }
  
  private Token scanTag() {
    char ch = peek(1);
    String handle = null;
    String suffix = null;
    if (ch == '<') {
      forward(2);
      suffix = scanTagUri("tag");
      if (peek() != '>')
        throw new TokenizerException("While scanning a tag, expected '>' but found: " + ch(peek())); 
      forward();
    } else if ("\000 \t\r\n".indexOf(ch) != -1) {
      suffix = "!";
      forward();
    } else {
      int length = 1;
      boolean useHandle = false;
      while ("\000 \t\r\n".indexOf(ch) == -1) {
        if (ch == '!') {
          useHandle = true;
          break;
        } 
        length++;
        ch = peek(length);
      } 
      handle = "!";
      if (useHandle) {
        handle = scanTagHandle("tag");
      } else {
        handle = "!";
        forward();
      } 
      suffix = scanTagUri("tag");
    } 
    if ("\000 \r\n".indexOf(peek()) == -1)
      throw new TokenizerException("While scanning a tag, expected ' ' but found: " + ch(peek())); 
    return new TagToken(handle, suffix);
  }
  
  private Token scanBlockScalar(char style) {
    boolean folded = (style == '>');
    StringBuilder chunks = new StringBuilder();
    forward();
    Object[] chompi = scanBlockScalarIndicators();
    int chomping = ((Integer)chompi[0]).intValue();
    int increment = ((Integer)chompi[1]).intValue();
    scanBlockScalarIgnoredLine();
    int minIndent = this.indent + 1;
    if (minIndent < 1)
      minIndent = 1; 
    String breaks = null;
    int maxIndent = 0;
    int ind = 0;
    if (increment == -1) {
      Object[] brme = scanBlockScalarIndentation();
      breaks = (String)brme[0];
      maxIndent = ((Integer)brme[1]).intValue();
      if (minIndent > maxIndent) {
        ind = minIndent;
      } else {
        ind = maxIndent;
      } 
    } else {
      ind = minIndent + increment - 1;
      breaks = scanBlockScalarBreaks(ind);
    } 
    String lineBreak = "";
    while (this.column == ind && peek() != '\000') {
      chunks.append(breaks);
      boolean leadingNonSpace = (" \t".indexOf(peek()) == -1);
      int length = 0;
      while ("\000\r\n".indexOf(peek(length)) == -1)
        length++; 
      chunks.append(prefixForward(length));
      lineBreak = scanLineBreak();
      breaks = scanBlockScalarBreaks(ind);
      if (this.column == ind && peek() != '\000') {
        if (folded && lineBreak.equals("\n") && leadingNonSpace && " \t".indexOf(peek()) == -1) {
          if (breaks.length() == 0)
            chunks.append(" "); 
          continue;
        } 
        chunks.append(lineBreak);
      } 
    } 
    if (chomping == 0) {
      chunks.append(lineBreak);
    } else if (chomping == 2) {
      chunks.append(lineBreak);
      chunks.append(breaks);
    } 
    return new ScalarToken(chunks.toString(), false, style);
  }
  
  private Object[] scanBlockScalarIndicators() {
    int chomping = 0;
    int increment = -1;
    char ch = peek();
    if (ch == '-' || ch == '+') {
      chomping = (ch == '-') ? 1 : 2;
      forward();
      ch = peek();
      if (Character.isDigit(ch)) {
        increment = Integer.parseInt("" + ch);
        if (increment == 0)
          throw new TokenizerException("While scanning a black scaler, expected indentation indicator between 1 and 9 but found: 0"); 
        forward();
      } 
    } else if (Character.isDigit(ch)) {
      increment = Integer.parseInt("" + ch);
      if (increment == 0)
        throw new TokenizerException("While scanning a black scaler, expected indentation indicator between 1 and 9 but found: 0"); 
      forward();
      ch = peek();
      if (ch == '-' || ch == '+') {
        chomping = (ch == '-') ? 1 : 2;
        forward();
      } 
    } 
    if ("\000 \r\n".indexOf(peek()) == -1)
      throw new TokenizerException("While scanning a block scalar, expected chomping or indentation indicators but found: " + 
          ch(peek())); 
    return new Object[] { Integer.valueOf(chomping), Integer.valueOf(increment) };
  }
  
  private String scanBlockScalarIgnoredLine() {
    while (peek() == ' ')
      forward(); 
    if (peek() == '#')
      while ("\000\r\n".indexOf(peek()) == -1)
        forward();  
    if ("\000\r\n".indexOf(peek()) == -1)
      throw new TokenizerException("While scanning a block scalar, expected a comment or line break but found: " + ch(peek())); 
    return scanLineBreak();
  }
  
  private Object[] scanBlockScalarIndentation() {
    StringBuilder chunks = new StringBuilder();
    int maxIndent = 0;
    while (" \r\n".indexOf(peek()) != -1) {
      if (peek() != ' ') {
        chunks.append(scanLineBreak());
        continue;
      } 
      forward();
      if (this.column > maxIndent)
        maxIndent = this.column; 
    } 
    return new Object[] { chunks.toString(), Integer.valueOf(maxIndent) };
  }
  
  private String scanBlockScalarBreaks(int indent) {
    StringBuilder chunks = new StringBuilder();
    while (this.column < indent && peek() == ' ')
      forward(); 
    while ("\r\n".indexOf(peek()) != -1) {
      chunks.append(scanLineBreak());
      while (this.column < indent && peek() == ' ')
        forward(); 
    } 
    return chunks.toString();
  }
  
  private Token scanFlowScalar(char style) {
    boolean dbl = (style == '"');
    StringBuilder chunks = new StringBuilder();
    char quote = peek();
    forward();
    chunks.append(scanFlowScalarNonSpaces(dbl));
    while (peek() != quote) {
      chunks.append(scanFlowScalarSpaces());
      chunks.append(scanFlowScalarNonSpaces(dbl));
    } 
    forward();
    return new ScalarToken(chunks.toString(), false, style);
  }
  
  private String scanFlowScalarNonSpaces(boolean dbl) {
    StringBuilder chunks = new StringBuilder();
    while (true) {
      int length = 0;
      while ("'\"\\\000 \t\r\n".indexOf(peek(length)) == -1)
        length++; 
      if (length != 0)
        chunks.append(prefixForward(length)); 
      char ch = peek();
      if (!dbl && ch == '\'' && peek(1) == '\'') {
        chunks.append("'");
        forward(2);
        continue;
      } 
      if ((dbl && ch == '\'') || (!dbl && "\"\\".indexOf(ch) != -1)) {
        chunks.append(ch);
        forward();
        continue;
      } 
      if (dbl && ch == '\\') {
        forward();
        ch = peek();
        if (ESCAPE_REPLACEMENTS.containsKey(Character.valueOf(ch))) {
          chunks.append(ESCAPE_REPLACEMENTS.get(Character.valueOf(ch)));
          forward();
          continue;
        } 
        if (ESCAPE_CODES.containsKey(Character.valueOf(ch))) {
          length = ((Integer)ESCAPE_CODES.get(Character.valueOf(ch))).intValue();
          forward();
          String val = prefix(length);
          if (NOT_HEXA.matcher(val).find())
            throw new TokenizerException("While scanning a double quoted scalar, expected an escape sequence of " + length + " hexadecimal numbers but found: " + 
                ch(peek())); 
          chunks.append(Character.toChars(Integer.parseInt(val, 16)));
          forward(length);
          continue;
        } 
        if ("\r\n".indexOf(ch) != -1) {
          scanLineBreak();
          chunks.append(scanFlowScalarBreaks());
          continue;
        } 
        throw new TokenizerException("While scanning a double quoted scalar, found unknown escape character: " + ch(ch));
      } 
      break;
    } 
    return chunks.toString();
  }
  
  private String scanFlowScalarSpaces() {
    StringBuilder chunks = new StringBuilder();
    int length = 0;
    while (" \t".indexOf(peek(length)) != -1)
      length++; 
    String whitespaces = prefixForward(length);
    char ch = peek();
    if (ch == '\000')
      throw new TokenizerException("While scanning a quoted scalar, found unexpected end of stream."); 
    if ("\r\n".indexOf(ch) != -1) {
      String lineBreak = scanLineBreak();
      String breaks = scanFlowScalarBreaks();
      if (!lineBreak.equals("\n")) {
        chunks.append(lineBreak);
      } else if (breaks.length() == 0) {
        chunks.append(" ");
      } 
      chunks.append(breaks);
    } else {
      chunks.append(whitespaces);
    } 
    return chunks.toString();
  }
  
  private String scanFlowScalarBreaks() {
    StringBuilder chunks = new StringBuilder();
    String pre = null;
    while (true) {
      pre = prefix(3);
      if ((pre.equals("---") || pre.equals("...")) && "\000 \t\r\n".indexOf(peek(3)) != -1)
        throw new TokenizerException("While scanning a quoted scalar, found unexpected document separator."); 
      while (" \t".indexOf(peek()) != -1)
        forward(); 
      if ("\r\n".indexOf(peek()) != -1) {
        chunks.append(scanLineBreak());
        continue;
      } 
      break;
    } 
    return chunks.toString();
  }
  
  private Token scanPlain() {
    StringBuilder chunks = new StringBuilder();
    int ind = this.indent + 1;
    String spaces = "";
    boolean f_nzero = true;
    Pattern r_check = R_FLOWNONZERO;
    if (this.flowLevel == 0) {
      f_nzero = false;
      r_check = R_FLOWZERO;
    } 
    while (peek() != '#') {
      int length = 0;
      int chunkSize = 32;
      Matcher m = null;
      while (!(m = r_check.matcher(prefix(chunkSize))).find())
        chunkSize += 32; 
      length = m.start();
      char ch = peek(length);
      if (f_nzero && ch == ':' && "\000 \t\r\n([]{}".indexOf(peek(length + 1)) == -1) {
        forward(length);
        throw new TokenizerException("While scanning a plain scalar, found unexpected ':'. See: http://pyyaml.org/wiki/YAMLColonInFlowContext");
      } 
      if (length == 0)
        break; 
      this.allowSimpleKey = false;
      chunks.append(spaces);
      chunks.append(prefixForward(length));
      spaces = scanPlainSpaces();
      if (spaces.length() == 0 || (this.flowLevel == 0 && this.column < ind))
        break; 
    } 
    return new ScalarToken(chunks.toString(), true);
  }
  
  private String scanPlainSpaces() {
    StringBuilder chunks = new StringBuilder();
    int length = 0;
    while (peek(length) == ' ' || peek(length) == '\t')
      length++; 
    String whitespaces = prefixForward(length);
    char ch = peek();
    if ("\r\n".indexOf(ch) != -1) {
      String lineBreak = scanLineBreak();
      this.allowSimpleKey = true;
      if (END_OR_START.matcher(prefix(4)).matches())
        return ""; 
      StringBuilder breaks = new StringBuilder();
      while (" \r\n".indexOf(peek()) != -1) {
        if (' ' == peek()) {
          forward();
          continue;
        } 
        breaks.append(scanLineBreak());
        if (END_OR_START.matcher(prefix(4)).matches())
          return ""; 
      } 
      if (!lineBreak.equals("\n")) {
        chunks.append(lineBreak);
      } else if (breaks.length() == 0) {
        chunks.append(" ");
      } 
      chunks.append(breaks);
    } else {
      chunks.append(whitespaces);
    } 
    return chunks.toString();
  }
  
  private String scanTagHandle(String name) {
    char ch = peek();
    if (ch != '!')
      throw new TokenizerException("While scanning a " + name + ", expected '!' but found: " + ch(ch)); 
    int length = 1;
    ch = peek(length);
    if (ch != ' ') {
      while ("abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_".indexOf(ch) != -1) {
        length++;
        ch = peek(length);
      } 
      if ('!' != ch) {
        forward(length);
        throw new TokenizerException("While scanning a " + name + ", expected '!' but found: " + ch(ch));
      } 
      length++;
    } 
    String value = prefixForward(length);
    return value;
  }
  
  private String scanTagUri(String name) {
    StringBuilder chunks = new StringBuilder();
    int length = 0;
    char ch = peek(length);
    while ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-#;/?:@&=+$,_.!~*'()[]".indexOf(ch) != -1) {
      if ('%' == ch) {
        chunks.append(prefixForward(length));
        length = 0;
        chunks.append(scanUriEscapes(name));
      } else {
        length++;
      } 
      ch = peek(length);
    } 
    if (length != 0)
      chunks.append(prefixForward(length)); 
    if (chunks.length() == 0)
      throw new TokenizerException("While scanning a " + name + ", expected a URI but found: " + ch(ch)); 
    return chunks.toString();
  }
  
  private String scanUriEscapes(String name) {
    StringBuilder bytes = new StringBuilder();
    while (peek() == '%') {
      forward();
      try {
        bytes.append(Character.toChars(Integer.parseInt(prefix(2), 16)));
      } catch (NumberFormatException nfe) {
        throw new TokenizerException("While scanning a " + name + ", expected a URI escape sequence of 2 hexadecimal numbers but found: " + 
            ch(peek(1)) + " and " + ch(peek(2)));
      } 
      forward(2);
    } 
    return bytes.toString();
  }
  
  private String scanLineBreak() {
    char val = peek();
    if ("\r\n".indexOf(val) != -1) {
      if ("\r\n".equals(prefix(2))) {
        forward(2);
      } else {
        forward();
      } 
      return "\n";
    } 
    return "";
  }
  
  private String ch(char ch) {
    return "'" + ch + "' (" + ch + ")";
  }
  
  public class TokenizerException extends RuntimeException {
    public TokenizerException(String message, Throwable cause) {
      super("Line " + Tokenizer.this.getLineNumber() + ", column " + Tokenizer.this.getColumn() + ": " + message, cause);
    }
    
    public TokenizerException(String message) {
      this(message, null);
    }
  }
  
  static class SimpleKey {
    public final int tokenNumber;
    
    public final int column;
    
    public SimpleKey(int tokenNumber, int column) {
      this.tokenNumber = tokenNumber;
      this.column = column;
    }
  }
  
  public static void main(String[] args) throws Exception {
    for (Iterator iter = (new Tokenizer(new FileReader("test/test.yml"))).iterator(); iter.hasNext();)
      System.out.println(iter.next()); 
  }
}
