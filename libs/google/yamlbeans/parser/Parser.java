package me.syncwrld.booter.libs.google.yamlbeans.parser;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import me.syncwrld.booter.libs.google.yamlbeans.Version;
import me.syncwrld.booter.libs.google.yamlbeans.tokenizer.AliasToken;
import me.syncwrld.booter.libs.google.yamlbeans.tokenizer.AnchorToken;
import me.syncwrld.booter.libs.google.yamlbeans.tokenizer.DirectiveToken;
import me.syncwrld.booter.libs.google.yamlbeans.tokenizer.ScalarToken;
import me.syncwrld.booter.libs.google.yamlbeans.tokenizer.TagToken;
import me.syncwrld.booter.libs.google.yamlbeans.tokenizer.Token;
import me.syncwrld.booter.libs.google.yamlbeans.tokenizer.TokenType;
import me.syncwrld.booter.libs.google.yamlbeans.tokenizer.Tokenizer;

public class Parser {
  Tokenizer tokenizer = null;
  
  List<Production> parseStack = null;
  
  final List<String> tags = new LinkedList<String>();
  
  final List<String> anchors = new LinkedList<String>();
  
  Map<String, String> tagHandles = new HashMap<String, String>();
  
  Version defaultVersion;
  
  Version documentVersion;
  
  final Production[] table = new Production[46];
  
  Event peekedEvent;
  
  private static final int P_STREAM = 0;
  
  private static final int P_STREAM_START = 1;
  
  private static final int P_STREAM_END = 2;
  
  private static final int P_IMPLICIT_DOCUMENT = 3;
  
  private static final int P_EXPLICIT_DOCUMENT = 4;
  
  private static final int P_DOCUMENT_START = 5;
  
  private static final int P_DOCUMENT_START_IMPLICIT = 6;
  
  private static final int P_DOCUMENT_END = 7;
  
  private static final int P_BLOCK_NODE = 8;
  
  private static final int P_BLOCK_CONTENT = 9;
  
  private static final int P_PROPERTIES = 10;
  
  private static final int P_PROPERTIES_END = 11;
  
  private static final int P_FLOW_CONTENT = 12;
  
  private static final int P_BLOCK_SEQUENCE = 13;
  
  private static final int P_BLOCK_MAPPING = 14;
  
  private static final int P_FLOW_SEQUENCE = 15;
  
  private static final int P_FLOW_MAPPING = 16;
  
  private static final int P_SCALAR = 17;
  
  private static final int P_BLOCK_SEQUENCE_ENTRY = 18;
  
  private static final int P_BLOCK_MAPPING_ENTRY = 19;
  
  private static final int P_BLOCK_MAPPING_ENTRY_VALUE = 20;
  
  private static final int P_BLOCK_NODE_OR_INDENTLESS_SEQUENCE = 21;
  
  private static final int P_BLOCK_SEQUENCE_START = 22;
  
  private static final int P_BLOCK_SEQUENCE_END = 23;
  
  private static final int P_BLOCK_MAPPING_START = 24;
  
  private static final int P_BLOCK_MAPPING_END = 25;
  
  private static final int P_INDENTLESS_BLOCK_SEQUENCE = 26;
  
  private static final int P_BLOCK_INDENTLESS_SEQUENCE_START = 27;
  
  private static final int P_INDENTLESS_BLOCK_SEQUENCE_ENTRY = 28;
  
  private static final int P_BLOCK_INDENTLESS_SEQUENCE_END = 29;
  
  private static final int P_FLOW_SEQUENCE_START = 30;
  
  private static final int P_FLOW_SEQUENCE_ENTRY = 31;
  
  private static final int P_FLOW_SEQUENCE_END = 32;
  
  private static final int P_FLOW_MAPPING_START = 33;
  
  private static final int P_FLOW_MAPPING_ENTRY = 34;
  
  private static final int P_FLOW_MAPPING_END = 35;
  
  private static final int P_FLOW_INTERNAL_MAPPING_START = 36;
  
  private static final int P_FLOW_INTERNAL_CONTENT = 37;
  
  private static final int P_FLOW_INTERNAL_VALUE = 38;
  
  private static final int P_FLOW_INTERNAL_MAPPING_END = 39;
  
  private static final int P_FLOW_ENTRY_MARKER = 40;
  
  private static final int P_FLOW_NODE = 41;
  
  private static final int P_FLOW_MAPPING_INTERNAL_CONTENT = 42;
  
  private static final int P_FLOW_MAPPING_INTERNAL_VALUE = 43;
  
  private static final int P_ALIAS = 44;
  
  private static final int P_EMPTY_SCALAR = 45;
  
  public Parser(Reader reader) {
    this(reader, Version.DEFAULT_VERSION);
  }
  
  public Parser(Reader reader, Version defaultVersion) {
    if (reader == null)
      throw new IllegalArgumentException("reader cannot be null."); 
    if (defaultVersion == null)
      throw new IllegalArgumentException("defaultVersion cannot be null."); 
    this.tokenizer = new Tokenizer(reader);
    this.defaultVersion = defaultVersion;
    initProductionTable();
    this.parseStack = new LinkedList<Production>();
    this.parseStack.add(0, this.table[0]);
  }
  
  public Event getNextEvent() throws ParserException, Tokenizer.TokenizerException {
    if (this.peekedEvent != null)
      try {
        return this.peekedEvent;
      } finally {
        this.peekedEvent = null;
      }  
    while (!this.parseStack.isEmpty()) {
      Event event = ((Production)this.parseStack.remove(0)).produce();
      if (event != null)
        return event; 
    } 
    return null;
  }
  
  public Event peekNextEvent() throws ParserException, Tokenizer.TokenizerException {
    if (this.peekedEvent != null)
      return this.peekedEvent; 
    this.peekedEvent = getNextEvent();
    return this.peekedEvent;
  }
  
  public int getLineNumber() {
    return this.tokenizer.getLineNumber();
  }
  
  public int getColumn() {
    return this.tokenizer.getColumn();
  }
  
  public void close() throws IOException {
    this.tokenizer.close();
  }
  
  private void initProductionTable() {
    this.table[0] = new Production() {
        public Event produce() {
          Parser.this.parseStack.add(0, Parser.this.table[2]);
          Parser.this.parseStack.add(0, Parser.this.table[4]);
          Parser.this.parseStack.add(0, Parser.this.table[3]);
          Parser.this.parseStack.add(0, Parser.this.table[1]);
          return null;
        }
      };
    this.table[1] = new Production() {
        public Event produce() {
          Parser.this.tokenizer.getNextToken();
          return Event.STREAM_START;
        }
      };
    this.table[2] = new Production() {
        public Event produce() {
          Parser.this.tokenizer.getNextToken();
          return Event.STREAM_END;
        }
      };
    this.table[3] = new Production() {
        public Event produce() {
          TokenType type = Parser.this.tokenizer.peekNextTokenType();
          if (type != TokenType.DIRECTIVE && type != TokenType.DOCUMENT_START && type != TokenType.STREAM_END) {
            Parser.this.parseStack.add(0, Parser.this.table[7]);
            Parser.this.parseStack.add(0, Parser.this.table[8]);
            Parser.this.parseStack.add(0, Parser.this.table[6]);
          } 
          return null;
        }
      };
    this.table[4] = new Production() {
        public Event produce() {
          if (Parser.this.tokenizer.peekNextTokenType() != TokenType.STREAM_END) {
            Parser.this.parseStack.add(0, Parser.this.table[4]);
            Parser.this.parseStack.add(0, Parser.this.table[7]);
            Parser.this.parseStack.add(0, Parser.this.table[8]);
            Parser.this.parseStack.add(0, Parser.this.table[5]);
          } 
          return null;
        }
      };
    this.table[5] = new Production() {
        public Event produce() {
          Token token = Parser.this.tokenizer.peekNextToken();
          DocumentStartEvent documentStartEvent = Parser.this.processDirectives(true);
          if (Parser.this.tokenizer.peekNextTokenType() != TokenType.DOCUMENT_START)
            throw new Parser.ParserException("Expected 'document start' but found: " + token.type); 
          Parser.this.tokenizer.getNextToken();
          return documentStartEvent;
        }
      };
    this.table[6] = new Production() {
        public Event produce() {
          return Parser.this.processDirectives(false);
        }
      };
    this.table[7] = new Production() {
        public Event produce() {
          boolean explicit = false;
          while (Parser.this.tokenizer.peekNextTokenType() == TokenType.DOCUMENT_END) {
            Parser.this.tokenizer.getNextToken();
            explicit = true;
          } 
          return explicit ? Event.DOCUMENT_END_TRUE : Event.DOCUMENT_END_FALSE;
        }
      };
    this.table[8] = new Production() {
        public Event produce() {
          TokenType type = Parser.this.tokenizer.peekNextTokenType();
          if (type == TokenType.DIRECTIVE || type == TokenType.DOCUMENT_START || type == TokenType.DOCUMENT_END || type == TokenType.STREAM_END) {
            Parser.this.parseStack.add(0, Parser.this.table[45]);
          } else if (type == TokenType.ALIAS) {
            Parser.this.parseStack.add(0, Parser.this.table[44]);
          } else {
            Parser.this.parseStack.add(0, Parser.this.table[11]);
            Parser.this.parseStack.add(0, Parser.this.table[9]);
            Parser.this.parseStack.add(0, Parser.this.table[10]);
          } 
          return null;
        }
      };
    this.table[9] = new Production() {
        public Event produce() {
          TokenType type = Parser.this.tokenizer.peekNextTokenType();
          if (type == TokenType.BLOCK_SEQUENCE_START) {
            Parser.this.parseStack.add(0, Parser.this.table[13]);
          } else if (type == TokenType.BLOCK_MAPPING_START) {
            Parser.this.parseStack.add(0, Parser.this.table[14]);
          } else if (type == TokenType.FLOW_SEQUENCE_START) {
            Parser.this.parseStack.add(0, Parser.this.table[15]);
          } else if (type == TokenType.FLOW_MAPPING_START) {
            Parser.this.parseStack.add(0, Parser.this.table[16]);
          } else if (type == TokenType.SCALAR) {
            Parser.this.parseStack.add(0, Parser.this.table[17]);
          } else {
            throw new Parser.ParserException("Expected a sequence, mapping, or scalar but found: " + type);
          } 
          return null;
        }
      };
    this.table[10] = new Production() {
        public Event produce() {
          String anchor = null, tagHandle = null, tagSuffix = null;
          if (Parser.this.tokenizer.peekNextTokenType() == TokenType.ANCHOR) {
            anchor = ((AnchorToken)Parser.this.tokenizer.getNextToken()).getInstanceName();
            if (Parser.this.tokenizer.peekNextTokenType() == TokenType.TAG) {
              TagToken tagToken = (TagToken)Parser.this.tokenizer.getNextToken();
              tagHandle = tagToken.getHandle();
              tagSuffix = tagToken.getSuffix();
            } 
          } else if (Parser.this.tokenizer.peekNextTokenType() == TokenType.TAG) {
            TagToken tagToken = (TagToken)Parser.this.tokenizer.getNextToken();
            tagHandle = tagToken.getHandle();
            tagSuffix = tagToken.getSuffix();
            if (Parser.this.tokenizer.peekNextTokenType() == TokenType.ANCHOR)
              anchor = ((AnchorToken)Parser.this.tokenizer.getNextToken()).getInstanceName(); 
          } 
          String tag = null;
          if (tagHandle != null) {
            if (!Parser.this.tagHandles.containsKey(tagHandle))
              throw new Parser.ParserException("Undefined tag handle: " + tagHandle); 
            tag = (String)Parser.this.tagHandles.get(tagHandle) + tagSuffix;
          } else {
            tag = tagSuffix;
          } 
          Parser.this.anchors.add(0, anchor);
          Parser.this.tags.add(0, tag);
          return null;
        }
      };
    this.table[11] = new Production() {
        public Event produce() {
          Parser.this.anchors.remove(0);
          Parser.this.tags.remove(0);
          return null;
        }
      };
    this.table[12] = new Production() {
        public Event produce() {
          TokenType type = Parser.this.tokenizer.peekNextTokenType();
          if (type == TokenType.FLOW_SEQUENCE_START) {
            Parser.this.parseStack.add(0, Parser.this.table[15]);
          } else if (type == TokenType.FLOW_MAPPING_START) {
            Parser.this.parseStack.add(0, Parser.this.table[16]);
          } else if (type == TokenType.SCALAR) {
            Parser.this.parseStack.add(0, Parser.this.table[17]);
          } else {
            throw new Parser.ParserException("Expected a sequence, mapping, or scalar but found: " + type);
          } 
          return null;
        }
      };
    this.table[13] = new Production() {
        public Event produce() {
          Parser.this.parseStack.add(0, Parser.this.table[23]);
          Parser.this.parseStack.add(0, Parser.this.table[18]);
          Parser.this.parseStack.add(0, Parser.this.table[22]);
          return null;
        }
      };
    this.table[14] = new Production() {
        public Event produce() {
          Parser.this.parseStack.add(0, Parser.this.table[25]);
          Parser.this.parseStack.add(0, Parser.this.table[19]);
          Parser.this.parseStack.add(0, Parser.this.table[24]);
          return null;
        }
      };
    this.table[15] = new Production() {
        public Event produce() {
          Parser.this.parseStack.add(0, Parser.this.table[32]);
          Parser.this.parseStack.add(0, Parser.this.table[31]);
          Parser.this.parseStack.add(0, Parser.this.table[30]);
          return null;
        }
      };
    this.table[16] = new Production() {
        public Event produce() {
          Parser.this.parseStack.add(0, Parser.this.table[35]);
          Parser.this.parseStack.add(0, Parser.this.table[34]);
          Parser.this.parseStack.add(0, Parser.this.table[33]);
          return null;
        }
      };
    this.table[17] = new Production() {
        public Event produce() {
          ScalarToken token = (ScalarToken)Parser.this.tokenizer.getNextToken();
          boolean[] implicit = null;
          if ((token.getPlain() && Parser.this.tags.get(0) == null) || "!".equals(Parser.this.tags.get(0))) {
            implicit = new boolean[] { true, false };
          } else if (Parser.this.tags.get(0) == null) {
            implicit = new boolean[] { false, true };
          } else {
            implicit = new boolean[] { false, false };
          } 
          return new ScalarEvent(Parser.this.anchors.get(0), Parser.this.tags.get(0), implicit, token.getValue(), token.getStyle());
        }
      };
    this.table[18] = new Production() {
        public Event produce() {
          if (Parser.this.tokenizer.peekNextTokenType() == TokenType.BLOCK_ENTRY) {
            Parser.this.tokenizer.getNextToken();
            TokenType type = Parser.this.tokenizer.peekNextTokenType();
            if (type == TokenType.BLOCK_ENTRY || type == TokenType.BLOCK_END) {
              Parser.this.parseStack.add(0, Parser.this.table[18]);
              Parser.this.parseStack.add(0, Parser.this.table[45]);
            } else {
              Parser.this.parseStack.add(0, Parser.this.table[18]);
              Parser.this.parseStack.add(0, Parser.this.table[8]);
            } 
          } 
          return null;
        }
      };
    this.table[19] = new Production() {
        public Event produce() {
          TokenType type = Parser.this.tokenizer.peekNextTokenType();
          if (type == TokenType.KEY) {
            Parser.this.tokenizer.getNextToken();
            type = Parser.this.tokenizer.peekNextTokenType();
            if (type == TokenType.KEY || type == TokenType.VALUE || type == TokenType.BLOCK_END) {
              Parser.this.parseStack.add(0, Parser.this.table[19]);
              Parser.this.parseStack.add(0, Parser.this.table[20]);
              Parser.this.parseStack.add(0, Parser.this.table[45]);
            } else {
              Parser.this.parseStack.add(0, Parser.this.table[19]);
              Parser.this.parseStack.add(0, Parser.this.table[20]);
              Parser.this.parseStack.add(0, Parser.this.table[21]);
              Parser.this.parseStack.add(0, Parser.this.table[10]);
            } 
          } else if (type == TokenType.VALUE) {
            Parser.this.parseStack.add(0, Parser.this.table[19]);
            Parser.this.parseStack.add(0, Parser.this.table[20]);
            Parser.this.parseStack.add(0, Parser.this.table[45]);
          } 
          return null;
        }
      };
    this.table[20] = new Production() {
        public Event produce() {
          TokenType type = Parser.this.tokenizer.peekNextTokenType();
          if (type == TokenType.VALUE) {
            Parser.this.tokenizer.getNextToken();
            type = Parser.this.tokenizer.peekNextTokenType();
            if (type == TokenType.KEY || type == TokenType.VALUE || type == TokenType.BLOCK_END) {
              Parser.this.parseStack.add(0, Parser.this.table[45]);
            } else {
              Parser.this.parseStack.add(0, Parser.this.table[21]);
              Parser.this.parseStack.add(0, Parser.this.table[10]);
            } 
          } else if (type == TokenType.KEY) {
            Parser.this.parseStack.add(0, Parser.this.table[45]);
          } 
          return null;
        }
      };
    this.table[21] = new Production() {
        public Event produce() {
          TokenType type = Parser.this.tokenizer.peekNextTokenType();
          if (type == TokenType.ALIAS) {
            Parser.this.parseStack.add(0, Parser.this.table[44]);
          } else if (type == TokenType.BLOCK_ENTRY) {
            Parser.this.parseStack.add(0, Parser.this.table[26]);
          } else {
            Parser.this.parseStack.add(0, Parser.this.table[9]);
          } 
          return null;
        }
      };
    this.table[22] = new Production() {
        public Event produce() {
          boolean implicit = (Parser.this.tags.get(0) == null || ((String)Parser.this.tags.get(0)).equals("!"));
          Parser.this.tokenizer.getNextToken();
          return new SequenceStartEvent(Parser.this.anchors.get(0), Parser.this.tags.get(0), implicit, false);
        }
      };
    this.table[23] = new Production() {
        public Event produce() {
          if (Parser.this.tokenizer.peekNextTokenType() != TokenType.BLOCK_END)
            throw new Parser.ParserException("Expected a 'block end' but found: " + Parser.this.tokenizer.peekNextTokenType()); 
          Parser.this.tokenizer.getNextToken();
          return Event.SEQUENCE_END;
        }
      };
    this.table[24] = new Production() {
        public Event produce() {
          boolean implicit = (Parser.this.tags.get(0) == null || ((String)Parser.this.tags.get(0)).equals("!"));
          Parser.this.tokenizer.getNextToken();
          return new MappingStartEvent(Parser.this.anchors.get(0), Parser.this.tags.get(0), implicit, false);
        }
      };
    this.table[25] = new Production() {
        public Event produce() {
          if (Parser.this.tokenizer.peekNextTokenType() != TokenType.BLOCK_END)
            throw new Parser.ParserException("Expected a 'block end' but found: " + Parser.this.tokenizer.peekNextTokenType()); 
          Parser.this.tokenizer.getNextToken();
          return Event.MAPPING_END;
        }
      };
    this.table[26] = new Production() {
        public Event produce() {
          Parser.this.parseStack.add(0, Parser.this.table[29]);
          Parser.this.parseStack.add(0, Parser.this.table[28]);
          Parser.this.parseStack.add(0, Parser.this.table[27]);
          return null;
        }
      };
    this.table[27] = new Production() {
        public Event produce() {
          boolean implicit = (Parser.this.tags.get(0) == null || ((String)Parser.this.tags.get(0)).equals("!"));
          return new SequenceStartEvent(Parser.this.anchors.get(0), Parser.this.tags.get(0), implicit, false);
        }
      };
    this.table[28] = new Production() {
        public Event produce() {
          if (Parser.this.tokenizer.peekNextTokenType() == TokenType.BLOCK_ENTRY) {
            Parser.this.tokenizer.getNextToken();
            TokenType type = Parser.this.tokenizer.peekNextTokenType();
            if (type == TokenType.BLOCK_ENTRY || type == TokenType.KEY || type == TokenType.VALUE || type == TokenType.BLOCK_END) {
              Parser.this.parseStack.add(0, Parser.this.table[28]);
              Parser.this.parseStack.add(0, Parser.this.table[45]);
            } else {
              Parser.this.parseStack.add(0, Parser.this.table[28]);
              Parser.this.parseStack.add(0, Parser.this.table[8]);
            } 
          } 
          return null;
        }
      };
    this.table[29] = new Production() {
        public Event produce() {
          return Event.SEQUENCE_END;
        }
      };
    this.table[30] = new Production() {
        public Event produce() {
          boolean implicit = (Parser.this.tags.get(0) == null || ((String)Parser.this.tags.get(0)).equals("!"));
          Parser.this.tokenizer.getNextToken();
          return new SequenceStartEvent(Parser.this.anchors.get(0), Parser.this.tags.get(0), implicit, true);
        }
      };
    this.table[31] = new Production() {
        public Event produce() {
          if (Parser.this.tokenizer.peekNextTokenType() != TokenType.FLOW_SEQUENCE_END)
            if (Parser.this.tokenizer.peekNextTokenType() == TokenType.KEY) {
              Parser.this.parseStack.add(0, Parser.this.table[31]);
              Parser.this.parseStack.add(0, Parser.this.table[40]);
              Parser.this.parseStack.add(0, Parser.this.table[39]);
              Parser.this.parseStack.add(0, Parser.this.table[38]);
              Parser.this.parseStack.add(0, Parser.this.table[37]);
              Parser.this.parseStack.add(0, Parser.this.table[36]);
            } else {
              Parser.this.parseStack.add(0, Parser.this.table[31]);
              Parser.this.parseStack.add(0, Parser.this.table[41]);
              Parser.this.parseStack.add(0, Parser.this.table[40]);
            }  
          return null;
        }
      };
    this.table[32] = new Production() {
        public Event produce() {
          Parser.this.tokenizer.getNextToken();
          return Event.SEQUENCE_END;
        }
      };
    this.table[33] = new Production() {
        public Event produce() {
          boolean implicit = (Parser.this.tags.get(0) == null || ((String)Parser.this.tags.get(0)).equals("!"));
          Parser.this.tokenizer.getNextToken();
          return new MappingStartEvent(Parser.this.anchors.get(0), Parser.this.tags.get(0), implicit, true);
        }
      };
    this.table[34] = new Production() {
        public Event produce() {
          if (Parser.this.tokenizer.peekNextTokenType() != TokenType.FLOW_MAPPING_END)
            if (Parser.this.tokenizer.peekNextTokenType() == TokenType.KEY) {
              Parser.this.parseStack.add(0, Parser.this.table[34]);
              Parser.this.parseStack.add(0, Parser.this.table[40]);
              Parser.this.parseStack.add(0, Parser.this.table[43]);
              Parser.this.parseStack.add(0, Parser.this.table[42]);
            } else {
              Parser.this.parseStack.add(0, Parser.this.table[34]);
              Parser.this.parseStack.add(0, Parser.this.table[41]);
              Parser.this.parseStack.add(0, Parser.this.table[40]);
            }  
          return null;
        }
      };
    this.table[35] = new Production() {
        public Event produce() {
          Parser.this.tokenizer.getNextToken();
          return Event.MAPPING_END;
        }
      };
    this.table[36] = new Production() {
        public Event produce() {
          Parser.this.tokenizer.getNextToken();
          return new MappingStartEvent(null, null, true, true);
        }
      };
    this.table[37] = new Production() {
        public Event produce() {
          TokenType type = Parser.this.tokenizer.peekNextTokenType();
          if (type == TokenType.VALUE || type == TokenType.FLOW_ENTRY || type == TokenType.FLOW_SEQUENCE_END) {
            Parser.this.parseStack.add(0, Parser.this.table[45]);
          } else {
            Parser.this.parseStack.add(0, Parser.this.table[41]);
          } 
          return null;
        }
      };
    this.table[38] = new Production() {
        public Event produce() {
          if (Parser.this.tokenizer.peekNextTokenType() == TokenType.VALUE) {
            Parser.this.tokenizer.getNextToken();
            if (Parser.this.tokenizer.peekNextTokenType() == TokenType.FLOW_ENTRY || Parser.this.tokenizer.peekNextTokenType() == TokenType.FLOW_SEQUENCE_END) {
              Parser.this.parseStack.add(0, Parser.this.table[45]);
            } else {
              Parser.this.parseStack.add(0, Parser.this.table[41]);
            } 
          } else {
            Parser.this.parseStack.add(0, Parser.this.table[45]);
          } 
          return null;
        }
      };
    this.table[39] = new Production() {
        public Event produce() {
          return Event.MAPPING_END;
        }
      };
    this.table[40] = new Production() {
        public Event produce() {
          if (Parser.this.tokenizer.peekNextTokenType() == TokenType.FLOW_ENTRY)
            Parser.this.tokenizer.getNextToken(); 
          return null;
        }
      };
    this.table[41] = new Production() {
        public Event produce() {
          if (Parser.this.tokenizer.peekNextTokenType() == TokenType.ALIAS) {
            Parser.this.parseStack.add(0, Parser.this.table[44]);
          } else {
            Parser.this.parseStack.add(0, Parser.this.table[11]);
            Parser.this.parseStack.add(0, Parser.this.table[12]);
            Parser.this.parseStack.add(0, Parser.this.table[10]);
          } 
          return null;
        }
      };
    this.table[42] = new Production() {
        public Event produce() {
          TokenType type = Parser.this.tokenizer.peekNextTokenType();
          if (type == TokenType.VALUE || type == TokenType.FLOW_ENTRY || type == TokenType.FLOW_MAPPING_END) {
            Parser.this.parseStack.add(0, Parser.this.table[45]);
          } else {
            Parser.this.tokenizer.getNextToken();
            Parser.this.parseStack.add(0, Parser.this.table[41]);
          } 
          return null;
        }
      };
    this.table[43] = new Production() {
        public Event produce() {
          if (Parser.this.tokenizer.peekNextTokenType() == TokenType.VALUE) {
            Parser.this.tokenizer.getNextToken();
            if (Parser.this.tokenizer.peekNextTokenType() == TokenType.FLOW_ENTRY || Parser.this.tokenizer.peekNextTokenType() == TokenType.FLOW_MAPPING_END) {
              Parser.this.parseStack.add(0, Parser.this.table[45]);
            } else {
              Parser.this.parseStack.add(0, Parser.this.table[41]);
            } 
          } else {
            Parser.this.parseStack.add(0, Parser.this.table[45]);
          } 
          return null;
        }
      };
    this.table[44] = new Production() {
        public Event produce() {
          AliasToken token = (AliasToken)Parser.this.tokenizer.getNextToken();
          return new AliasEvent(token.getInstanceName());
        }
      };
    this.table[45] = new Production() {
        public Event produce() {
          return new ScalarEvent(null, null, new boolean[] { true, false }, null, false);
        }
      };
  }
  
  DocumentStartEvent processDirectives(boolean explicit) {
    Version version;
    this.documentVersion = null;
    while (this.tokenizer.peekNextTokenType() == TokenType.DIRECTIVE) {
      DirectiveToken token = (DirectiveToken)this.tokenizer.getNextToken();
      if (token.getDirective().equals("YAML")) {
        if (this.documentVersion != null)
          throw new ParserException("Duplicate YAML directive."); 
        this.documentVersion = Version.getVersion(token.getValue());
        if (this.documentVersion == null || this.documentVersion.getMajor() != 1)
          throw new ParserException("Unsupported YAML version (1.x is required): " + token.getValue()); 
        continue;
      } 
      if (token.getDirective().equals("TAG")) {
        String[] values = token.getValue().split(" ");
        String handle = values[0];
        String prefix = values[1];
        if (this.tagHandles.containsKey(handle))
          throw new ParserException("Duplicate tag directive: " + handle); 
        this.tagHandles.put(handle, prefix);
      } 
    } 
    if (this.documentVersion != null) {
      version = this.documentVersion;
    } else {
      version = this.defaultVersion;
    } 
    Map<String, String> tags = null;
    if (!this.tagHandles.isEmpty())
      tags = new HashMap<String, String>(this.tagHandles); 
    Map<String, String> baseTags = (version.getMinor() == 0) ? DEFAULT_TAGS_1_0 : DEFAULT_TAGS_1_1;
    for (String key : baseTags.keySet()) {
      if (!this.tagHandles.containsKey(key))
        this.tagHandles.put(key, baseTags.get(key)); 
    } 
    return new DocumentStartEvent(explicit, version, tags);
  }
  
  private static final Map<String, String> DEFAULT_TAGS_1_0 = new HashMap<String, String>();
  
  private static final Map<String, String> DEFAULT_TAGS_1_1 = new HashMap<String, String>();
  
  static {
    DEFAULT_TAGS_1_0.put("!", "tag:yaml.org,2002:");
    DEFAULT_TAGS_1_1.put("!", "!");
    DEFAULT_TAGS_1_1.put("!!", "tag:yaml.org,2002:");
  }
  
  static interface Production {
    Event produce();
  }
  
  public class ParserException extends RuntimeException {
    public ParserException(String message) {
      super("Line " + Parser.this.tokenizer.getLineNumber() + ", column " + Parser.this.tokenizer.getColumn() + ": " + message);
    }
  }
  
  public static void main(String[] args) throws Exception {
    Parser parser = new Parser(new FileReader("test/test.yml"));
    while (true) {
      Event event = parser.getNextEvent();
      if (event == null)
        break; 
      System.out.println(event);
    } 
  }
}
