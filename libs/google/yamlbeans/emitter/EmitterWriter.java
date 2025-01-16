package me.syncwrld.booter.libs.google.yamlbeans.emitter;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

class EmitterWriter {
  private static final Map<Integer, String> ESCAPE_REPLACEMENTS = new HashMap<Integer, String>();
  
  private final Writer writer;
  
  static {
    ESCAPE_REPLACEMENTS.put(Integer.valueOf(0), "0");
    ESCAPE_REPLACEMENTS.put(Integer.valueOf(7), "a");
    ESCAPE_REPLACEMENTS.put(Integer.valueOf(8), "b");
    ESCAPE_REPLACEMENTS.put(Integer.valueOf(9), "t");
    ESCAPE_REPLACEMENTS.put(Integer.valueOf(10), "n");
    ESCAPE_REPLACEMENTS.put(Integer.valueOf(11), "v");
    ESCAPE_REPLACEMENTS.put(Integer.valueOf(12), "f");
    ESCAPE_REPLACEMENTS.put(Integer.valueOf(13), "r");
    ESCAPE_REPLACEMENTS.put(Integer.valueOf(27), "e");
    ESCAPE_REPLACEMENTS.put(Integer.valueOf(34), "\"");
    ESCAPE_REPLACEMENTS.put(Integer.valueOf(92), "\\");
    ESCAPE_REPLACEMENTS.put(Integer.valueOf(133), "N");
    ESCAPE_REPLACEMENTS.put(Integer.valueOf(160), "_");
  }
  
  private boolean whitespace = true;
  
  int column = 0;
  
  boolean indentation = true;
  
  public EmitterWriter(Writer stream) {
    this.writer = stream;
  }
  
  public void writeStreamStart() {}
  
  public void writeStreamEnd() throws IOException {
    flushStream();
  }
  
  public void writeIndicator(String indicator, boolean needWhitespace, boolean whitespace, boolean indentation) throws IOException {
    String data = null;
    if (this.whitespace || !needWhitespace) {
      data = indicator;
    } else {
      data = " " + indicator;
    } 
    this.whitespace = whitespace;
    this.indentation = (this.indentation && indentation);
    this.column += data.length();
    this.writer.write(data);
  }
  
  public void writeIndent(int indent) throws IOException {
    if (indent == -1)
      indent = 0; 
    if (!this.indentation || this.column > indent || (this.column == indent && !this.whitespace))
      writeLineBreak(null); 
    if (this.column < indent) {
      this.whitespace = true;
      StringBuffer data = new StringBuffer();
      for (int i = 0, j = indent - this.column; i < j; i++)
        data.append(" "); 
      this.column = indent;
      this.writer.write(data.toString());
    } 
  }
  
  public void writeVersionDirective(String version_text) throws IOException {
    this.writer.write("%YAML " + version_text);
    writeLineBreak(null);
  }
  
  public void writeTagDirective(String handle, String prefix) throws IOException {
    this.writer.write("%TAG " + handle + " " + prefix);
    writeLineBreak(null);
  }
  
  public void writeDoubleQuoted(String text, boolean split, int indent, int wrapColumn, boolean escapeUnicode) throws IOException {
    writeIndicator("\"", true, false, false);
    int start = 0;
    int ending = 0;
    String data = null;
    while (ending <= text.length()) {
      int ch = 0;
      if (ending < text.length())
        ch = text.codePointAt(ending); 
      if (ch == 0 || "\"\\".indexOf(ch) != -1 || 32 > ch || ch > 126) {
        if (start < ending) {
          data = text.substring(start, ending);
          this.column += data.length();
          this.writer.write(data);
          start = ending;
        } 
        if (ch != 0) {
          if (ESCAPE_REPLACEMENTS.containsKey(Integer.valueOf(ch))) {
            data = "\\" + (String)ESCAPE_REPLACEMENTS.get(Integer.valueOf(ch));
          } else if (escapeUnicode) {
            data = Integer.toString(ch, 16);
            if (data.length() == 1) {
              data = "000" + data;
            } else if (data.length() == 2) {
              data = "00" + data;
            } else if (data.length() == 3) {
              data = "0" + data;
            } 
            data = "\\u" + data;
          } else {
            data = new String(Character.toChars(ch));
          } 
          this.column += data.length();
          this.writer.write(data);
          start = ending + 1;
        } 
      } 
      if (0 < ending && ending < text.length() - 1 && (ch == 32 || start <= ending) && this.column + ending - start > wrapColumn && split) {
        if (start < ending) {
          data = text.substring(start, ending) + '\\';
        } else {
          data = "\\";
        } 
        if (start < ending)
          start = ending; 
        this.column += data.length();
        this.writer.write(data);
        writeIndent(indent);
        this.whitespace = false;
        this.indentation = false;
        if (text.charAt(start) == ' ') {
          data = "\\";
          this.column += data.length();
          this.writer.write(data);
        } 
      } 
      ending++;
    } 
    writeIndicator("\"", false, false, false);
  }
  
  public void writeSingleQuoted(String text, boolean split, int indent, int wrapColumn) throws IOException {
    writeIndicator("'", true, false, false);
    boolean spaces = false;
    boolean breaks = false;
    int start = 0, ending = 0;
    char ceh = Character.MIN_VALUE;
    String data = null;
    while (ending <= text.length()) {
      ceh = Character.MIN_VALUE;
      if (ending < text.length())
        ceh = text.charAt(ending); 
      if (spaces) {
        if (ceh == '\000' || ceh != ' ') {
          if (start + 1 == ending && this.column > wrapColumn && split && start != 0 && ending != text.length()) {
            writeIndent(indent);
          } else {
            data = text.substring(start, ending);
            this.column += data.length();
            this.writer.write(data);
          } 
          start = ending;
        } 
      } else if (breaks) {
        if (ceh == '\000' || ('\n' != ceh && '' != ceh)) {
          data = text.substring(start, ending);
          for (int i = 0, j = data.length(); i < j; i++) {
            char cha = data.charAt(i);
            if ('\n' == cha) {
              writeLineBreak(null);
            } else {
              writeLineBreak("" + cha);
            } 
          } 
          writeIndent(indent);
          start = ending;
        } 
      } else if ((ceh == '\000' || ('\n' != ceh && '' != ceh)) && 
        start < ending) {
        data = text.substring(start, ending);
        this.column += data.length();
        this.writer.write(data);
        start = ending;
      } 
      if (ceh == '\'') {
        data = "''";
        this.column += 2;
        this.writer.write(data);
        start = ending + 1;
      } 
      if (ceh != '\000') {
        spaces = (ceh == ' ');
        breaks = (ceh == '\n' || ceh == '');
      } 
      ending++;
    } 
    writeIndicator("'", false, false, false);
  }
  
  public void writeFolded(String text, int indent, int wrapColumn) throws IOException {
    String chomp = determineChomp(text);
    writeIndicator(">" + chomp, true, false, false);
    writeIndent(indent);
    boolean leadingSpace = false;
    boolean spaces = false;
    boolean breaks = false;
    int start = 0, ending = 0;
    String data = null;
    while (ending <= text.length()) {
      char ceh = Character.MIN_VALUE;
      if (ending < text.length())
        ceh = text.charAt(ending); 
      if (breaks) {
        if (ceh == '\000' || ('\n' != ceh && '' != ceh)) {
          if (!leadingSpace && ceh != '\000' && ceh != ' ' && text.charAt(start) == '\n')
            writeLineBreak(null); 
          leadingSpace = (ceh == ' ');
          data = text.substring(start, ending);
          for (int i = 0, j = data.length(); i < j; i++) {
            char cha = data.charAt(i);
            if ('\n' == cha) {
              writeLineBreak(null);
            } else {
              writeLineBreak("" + cha);
            } 
          } 
          if (ceh != '\000')
            writeIndent(indent); 
          start = ending;
        } 
      } else if (spaces) {
        if (ceh != ' ') {
          if (start + 1 == ending && this.column > wrapColumn) {
            writeIndent(indent);
          } else {
            data = text.substring(start, ending);
            this.column += data.length();
            this.writer.write(data);
          } 
          start = ending;
        } 
      } else if (ceh == '\000' || ' ' == ceh || '\n' == ceh || '' == ceh) {
        data = text.substring(start, ending);
        this.writer.write(data);
        if (ceh == '\000')
          writeLineBreak(null); 
        start = ending;
      } 
      if (ceh != '\000') {
        breaks = ('\n' == ceh || '' == ceh);
        spaces = (ceh == ' ');
      } 
      ending++;
    } 
  }
  
  public void writeLiteral(String text, int indent) throws IOException {
    String chomp = determineChomp(text);
    writeIndicator("|" + chomp, true, false, false);
    writeIndent(indent);
    boolean breaks = false;
    int start = 0, ending = 0;
    String data = null;
    while (ending <= text.length()) {
      char ceh = Character.MIN_VALUE;
      if (ending < text.length())
        ceh = text.charAt(ending); 
      if (breaks) {
        if (ceh == '\000' || ('\n' != ceh && '' != ceh)) {
          data = text.substring(start, ending);
          for (int i = 0, j = data.length(); i < j; i++) {
            char cha = data.charAt(i);
            if ('\n' == cha) {
              writeLineBreak(null);
            } else {
              writeLineBreak("" + cha);
            } 
          } 
          if (ceh != '\000')
            writeIndent(indent); 
          start = ending;
        } 
      } else if (ceh == '\000' || '\n' == ceh || '' == ceh) {
        data = text.substring(start, ending);
        this.writer.write(data);
        if (ceh == '\000')
          writeLineBreak(null); 
        start = ending;
      } 
      if (ceh != '\000')
        breaks = ('\n' == ceh || '' == ceh); 
      ending++;
    } 
  }
  
  public void writePlain(String text, boolean split, int indent, int wrapColumn) throws IOException {
    if (text == null || "".equals(text))
      return; 
    String data = null;
    if (!this.whitespace) {
      data = " ";
      this.column += data.length();
      this.writer.write(data);
    } 
    this.whitespace = false;
    this.indentation = false;
    boolean spaces = false, breaks = false;
    int start = 0, ending = 0;
    while (ending <= text.length()) {
      char ceh = Character.MIN_VALUE;
      if (ending < text.length())
        ceh = text.charAt(ending); 
      if (spaces) {
        if (ceh != ' ') {
          if (start + 1 == ending && this.column > wrapColumn && split) {
            writeIndent(indent);
            this.whitespace = false;
            this.indentation = false;
          } else {
            data = text.substring(start, ending);
            this.column += data.length();
            this.writer.write(data);
          } 
          start = ending;
        } 
      } else if (breaks) {
        if (ceh != '\n' && ceh != '') {
          if (text.charAt(start) == '\n')
            writeLineBreak(null); 
          data = text.substring(start, ending);
          for (int i = 0, j = data.length(); i < j; i++) {
            char cha = data.charAt(i);
            if ('\n' == cha) {
              writeLineBreak(null);
            } else {
              writeLineBreak("" + cha);
            } 
          } 
          writeIndent(indent);
          this.whitespace = false;
          this.indentation = false;
          start = ending;
        } 
      } else if (ceh == '\000' || ' ' == ceh || '\n' == ceh || '' == ceh) {
        data = text.substring(start, ending);
        this.column += data.length();
        this.writer.write(data);
        start = ending;
      } 
      if (ceh != '\000') {
        spaces = (ceh == ' ');
        breaks = (ceh == '\n' || ceh == '');
      } 
      ending++;
    } 
  }
  
  public void writeLineBreak(String data) throws IOException {
    if (data == null)
      data = System.getProperty("line.separator"); 
    this.whitespace = true;
    this.indentation = true;
    this.column = 0;
    this.writer.write(data);
  }
  
  public void flushStream() throws IOException {
    this.writer.flush();
  }
  
  private String determineChomp(String text) {
    String tail = text.substring(text.length() - 2, text.length() - 1);
    while (tail.length() < 2)
      tail = " " + tail; 
    char ceh = tail.charAt(tail.length() - 1);
    char ceh2 = tail.charAt(tail.length() - 2);
    return (ceh == '\n' || ceh == '') ? ((ceh2 == '\n' || ceh2 == '') ? "+" : "") : "-";
  }
  
  public void close() throws IOException {
    this.writer.close();
  }
}
