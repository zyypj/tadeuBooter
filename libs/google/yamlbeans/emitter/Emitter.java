package me.syncwrld.booter.libs.google.yamlbeans.emitter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import me.syncwrld.booter.libs.google.yamlbeans.Version;
import me.syncwrld.booter.libs.google.yamlbeans.parser.CollectionStartEvent;
import me.syncwrld.booter.libs.google.yamlbeans.parser.DocumentEndEvent;
import me.syncwrld.booter.libs.google.yamlbeans.parser.DocumentStartEvent;
import me.syncwrld.booter.libs.google.yamlbeans.parser.Event;
import me.syncwrld.booter.libs.google.yamlbeans.parser.EventType;
import me.syncwrld.booter.libs.google.yamlbeans.parser.MappingStartEvent;
import me.syncwrld.booter.libs.google.yamlbeans.parser.NodeEvent;
import me.syncwrld.booter.libs.google.yamlbeans.parser.ScalarEvent;
import me.syncwrld.booter.libs.google.yamlbeans.parser.SequenceStartEvent;

public class Emitter {
  private static final Pattern HANDLE_FORMAT = Pattern.compile("^![-\\w]*!$");
  
  private static final Pattern ANCHOR_FORMAT = Pattern.compile("^[-\\w]*$");
  
  final EmitterConfig config;
  
  final EmitterWriter writer;
  
  final EmitterState[] table = new EmitterState[18];
  
  int state = 0;
  
  final List<Integer> states = new ArrayList<Integer>();
  
  final List<Event> events = new ArrayList<Event>();
  
  final List<Integer> indents = new ArrayList<Integer>();
  
  boolean isVersion10 = false;
  
  Event event;
  
  int flowLevel = 0;
  
  int indent = -1;
  
  boolean mappingContext = false;
  
  boolean simpleKeyContext = false;
  
  Map<String, String> tagPrefixes;
  
  String preparedTag;
  
  String preparedAnchor;
  
  ScalarAnalysis analysis;
  
  char style = Character.MIN_VALUE;
  
  private static final int S_STREAM_START = 0;
  
  private static final int S_FIRST_DOCUMENT_START = 1;
  
  private static final int S_DOCUMENT_ROOT = 2;
  
  private static final int S_NOTHING = 3;
  
  private static final int S_DOCUMENT_START = 4;
  
  private static final int S_DOCUMENT_END = 5;
  
  private static final int S_FIRST_FLOW_SEQUENCE_ITEM = 6;
  
  private static final int S_FLOW_SEQUENCE_ITEM = 7;
  
  private static final int S_FIRST_FLOW_MAPPING_KEY = 8;
  
  private static final int S_FLOW_MAPPING_SIMPLE_VALUE = 9;
  
  private static final int S_FLOW_MAPPING_VALUE = 10;
  
  private static final int S_FLOW_MAPPING_KEY = 11;
  
  private static final int S_BLOCK_SEQUENCE_ITEM = 12;
  
  private static final int S_FIRST_BLOCK_MAPPING_KEY = 13;
  
  private static final int S_BLOCK_MAPPING_SIMPLE_VALUE = 14;
  
  private static final int S_BLOCK_MAPPING_VALUE = 15;
  
  private static final int S_BLOCK_MAPPING_KEY = 16;
  
  private static final int S_FIRST_BLOCK_SEQUENCE_ITEM = 17;
  
  public Emitter(Writer writer) {
    this(writer, new EmitterConfig());
  }
  
  public Emitter(Writer writer, EmitterConfig config) {
    this.config = config;
    if (writer == null)
      throw new IllegalArgumentException("stream cannot be null."); 
    if (!(writer instanceof BufferedWriter))
      writer = new BufferedWriter(writer); 
    this.writer = new EmitterWriter(writer);
    initStateTable();
  }
  
  public void emit(Event event) throws IOException, EmitterException {
    if (event == null)
      throw new IllegalArgumentException("event cannot be null."); 
    this.events.add(event);
    while (!needMoreEvents()) {
      this.event = this.events.remove(0);
      this.table[this.state].expect();
      this.event = null;
    } 
  }
  
  public void close() throws IOException {
    this.writer.close();
  }
  
  private boolean needMoreEvents() {
    if (this.events.isEmpty())
      return true; 
    this.event = this.events.get(0);
    if (this.event == null)
      return false; 
    switch (this.event.type) {
      case DOCUMENT_START:
        return needEvents(1);
      case SEQUENCE_START:
        return needEvents(2);
      case MAPPING_START:
        return needEvents(3);
    } 
    return false;
  }
  
  private boolean needEvents(int count) {
    int level = 0;
    Iterator<Event> iter = this.events.iterator();
    iter.next();
    while (iter.hasNext()) {
      Event curr = iter.next();
      if (curr.type == EventType.DOCUMENT_START || curr.type == EventType.MAPPING_START || curr.type == EventType.SEQUENCE_START) {
        level++;
      } else if (curr.type == EventType.DOCUMENT_END || curr.type == EventType.MAPPING_END || curr.type == EventType.SEQUENCE_END) {
        level--;
      } else if (curr.type == EventType.STREAM_END) {
        level = -1;
      } 
      if (level < 0)
        return false; 
    } 
    return (this.events.size() < count + 1);
  }
  
  private void initStateTable() {
    this.table[0] = new EmitterState() {
        public void expect() {
          if (Emitter.this.event.type == EventType.STREAM_START) {
            Emitter.this.writer.writeStreamStart();
            Emitter.this.state = 1;
          } else {
            throw new EmitterException("Expected 'stream start' but found: " + Emitter.this.event);
          } 
        }
      };
    this.table[1] = new EmitterState() {
        public void expect() throws IOException {
          if (Emitter.this.event.type == EventType.DOCUMENT_START) {
            DocumentStartEvent documentStartEvent = (DocumentStartEvent)Emitter.this.event;
            if (documentStartEvent.version != null) {
              if (documentStartEvent.version.getMajor() != 1)
                throw new EmitterException("Unsupported YAML version: " + documentStartEvent.version); 
              Emitter.this.writer.writeVersionDirective(documentStartEvent.version.toString());
            } 
            if (documentStartEvent.version == Version.V1_0) {
              Emitter.this.isVersion10 = true;
              Emitter.this.tagPrefixes = new HashMap<String, String>(Emitter.DEFAULT_TAG_PREFIXES_1_0);
            } else {
              Emitter.this.tagPrefixes = new HashMap<String, String>(Emitter.DEFAULT_TAG_PREFIXES_1_1);
            } 
            if (documentStartEvent.tags != null) {
              Set<String> handles = new TreeSet<String>();
              handles.addAll(documentStartEvent.tags.keySet());
              for (Iterator<String> iter = handles.iterator(); iter.hasNext(); ) {
                String handle = iter.next();
                String prefix = (String)documentStartEvent.tags.get(handle);
                Emitter.this.tagPrefixes.put(prefix, handle);
                String handleText = Emitter.this.prepareTagHandle(handle);
                String prefixText = Emitter.this.prepareTagPrefix(prefix);
                Emitter.this.writer.writeTagDirective(handleText, prefixText);
              } 
            } 
          } 
          Emitter.this.expectDocumentStart(true);
        }
      };
    this.table[2] = new EmitterState() {
        public void expect() throws IOException {
          Emitter.this.states.add(0, Integer.valueOf(5));
          Emitter.this.expectNode(true, false, false, false);
        }
      };
    this.table[3] = new EmitterState() {
        public void expect() {
          throw new EmitterException("Expected no event but found: " + Emitter.this.event);
        }
      };
    this.table[4] = new EmitterState() {
        public void expect() throws IOException {
          Emitter.this.expectDocumentStart(false);
        }
      };
    this.table[5] = new EmitterState() {
        public void expect() throws IOException {
          if (Emitter.this.event.type == EventType.DOCUMENT_END) {
            Emitter.this.writer.writeIndent(Emitter.this.indent);
            if (((DocumentEndEvent)Emitter.this.event).isExplicit) {
              Emitter.this.writer.writeIndicator("...", true, false, false);
              Emitter.this.writer.writeIndent(Emitter.this.indent);
            } 
            Emitter.this.writer.flushStream();
            Emitter.this.state = 4;
          } else {
            throw new EmitterException("Expected 'document end' but found: " + Emitter.this.event);
          } 
        }
      };
    this.table[6] = new EmitterState() {
        public void expect() throws IOException {
          if (Emitter.this.event.type == EventType.SEQUENCE_END) {
            Emitter.this.indent = ((Integer)Emitter.this.indents.remove(0)).intValue();
            Emitter.this.flowLevel--;
            Emitter.this.writer.writeIndicator("]", false, false, false);
            Emitter.this.state = ((Integer)Emitter.this.states.remove(0)).intValue();
          } else {
            if (Emitter.this.config.prettyFlow)
              Emitter.this.writer.writeIndent(Emitter.this.flowLevel * Emitter.this.config.indentSize); 
            if (Emitter.this.config.canonical || Emitter.this.writer.column > Emitter.this.config.wrapColumn)
              Emitter.this.writer.writeIndent(Emitter.this.indent); 
            Emitter.this.states.add(0, Integer.valueOf(7));
            Emitter.this.expectNode(false, true, false, false);
          } 
        }
      };
    this.table[7] = new EmitterState() {
        public void expect() throws IOException {
          if (Emitter.this.event.type == EventType.SEQUENCE_END) {
            Emitter.this.indent = ((Integer)Emitter.this.indents.remove(0)).intValue();
            Emitter.this.flowLevel--;
            if (Emitter.this.config.prettyFlow)
              Emitter.this.writer.writeIndent(Emitter.this.flowLevel * Emitter.this.config.indentSize); 
            if (Emitter.this.config.canonical)
              Emitter.this.writer.writeIndent(Emitter.this.indent); 
            Emitter.this.writer.writeIndicator("]", false, false, false);
            Emitter.this.state = ((Integer)Emitter.this.states.remove(0)).intValue();
          } else {
            Emitter.this.writer.writeIndicator(",", false, false, false);
            if (Emitter.this.config.prettyFlow)
              Emitter.this.writer.writeIndent(Emitter.this.flowLevel * Emitter.this.config.indentSize); 
            if (Emitter.this.config.canonical || Emitter.this.writer.column > Emitter.this.config.wrapColumn)
              Emitter.this.writer.writeIndent(Emitter.this.indent); 
            Emitter.this.states.add(0, Integer.valueOf(7));
            Emitter.this.expectNode(false, true, false, false);
          } 
        }
      };
    this.table[8] = new EmitterState() {
        public void expect() throws IOException {
          if (Emitter.this.event.type == EventType.MAPPING_END) {
            Emitter.this.indent = ((Integer)Emitter.this.indents.remove(0)).intValue();
            Emitter.this.flowLevel--;
            Emitter.this.writer.writeIndicator("}", false, false, false);
            Emitter.this.state = ((Integer)Emitter.this.states.remove(0)).intValue();
          } else {
            if (Emitter.this.config.prettyFlow)
              Emitter.this.writer.writeIndent(Emitter.this.flowLevel * Emitter.this.config.indentSize); 
            if (Emitter.this.config.canonical || Emitter.this.writer.column > Emitter.this.config.wrapColumn)
              Emitter.this.writer.writeIndent(Emitter.this.indent); 
            if (!Emitter.this.config.canonical && Emitter.this.checkSimpleKey()) {
              Emitter.this.states.add(0, Integer.valueOf(9));
              Emitter.this.expectNode(false, false, true, true);
            } else {
              Emitter.this.writer.writeIndicator("?", true, false, false);
              Emitter.this.states.add(0, Integer.valueOf(10));
              Emitter.this.expectNode(false, false, true, false);
            } 
          } 
        }
      };
    this.table[9] = new EmitterState() {
        public void expect() throws IOException {
          Emitter.this.writer.writeIndicator(": ", false, true, false);
          Emitter.this.states.add(0, Integer.valueOf(11));
          Emitter.this.expectNode(false, false, true, false);
        }
      };
    this.table[10] = new EmitterState() {
        public void expect() throws IOException {
          if (Emitter.this.config.canonical || Emitter.this.writer.column > Emitter.this.config.wrapColumn)
            Emitter.this.writer.writeIndent(Emitter.this.indent); 
          Emitter.this.writer.writeIndicator(": ", false, true, false);
          Emitter.this.states.add(0, Integer.valueOf(11));
          Emitter.this.expectNode(false, false, true, false);
        }
      };
    this.table[11] = new EmitterState() {
        public void expect() throws IOException {
          if (Emitter.this.event.type == EventType.MAPPING_END) {
            Emitter.this.indent = ((Integer)Emitter.this.indents.remove(0)).intValue();
            Emitter.this.flowLevel--;
            if (Emitter.this.config.prettyFlow)
              Emitter.this.writer.writeIndent(Emitter.this.flowLevel * Emitter.this.config.indentSize); 
            if (Emitter.this.config.canonical)
              Emitter.this.writer.writeIndent(Emitter.this.indent); 
            Emitter.this.writer.writeIndicator("}", false, false, false);
            Emitter.this.state = ((Integer)Emitter.this.states.remove(0)).intValue();
          } else {
            Emitter.this.writer.writeIndicator(",", false, false, false);
            if (Emitter.this.config.prettyFlow)
              Emitter.this.writer.writeIndent(Emitter.this.flowLevel * Emitter.this.config.indentSize); 
            if (Emitter.this.config.canonical || Emitter.this.writer.column > Emitter.this.config.wrapColumn)
              Emitter.this.writer.writeIndent(Emitter.this.indent); 
            if (!Emitter.this.config.canonical && Emitter.this.checkSimpleKey()) {
              Emitter.this.states.add(0, Integer.valueOf(9));
              Emitter.this.expectNode(false, false, true, true);
            } else {
              Emitter.this.writer.writeIndicator("?", true, false, false);
              Emitter.this.states.add(0, Integer.valueOf(10));
              Emitter.this.expectNode(false, false, true, false);
            } 
          } 
        }
      };
    this.table[12] = new EmitterState() {
        public void expect() throws IOException {
          Emitter.this.expectBlockSequenceItem(false);
        }
      };
    this.table[13] = new EmitterState() {
        public void expect() throws IOException {
          Emitter.this.expectBlockMappingKey(true);
        }
      };
    this.table[14] = new EmitterState() {
        public void expect() throws IOException {
          Emitter.this.writer.writeIndicator(": ", false, true, false);
          Emitter.this.states.add(0, Integer.valueOf(16));
          Emitter.this.expectNode(false, false, true, false);
        }
      };
    this.table[15] = new EmitterState() {
        public void expect() throws IOException {
          Emitter.this.writer.writeIndent(Emitter.this.indent);
          Emitter.this.writer.writeIndicator(": ", true, true, true);
          Emitter.this.states.add(0, Integer.valueOf(16));
          Emitter.this.expectNode(false, false, true, false);
        }
      };
    this.table[16] = new EmitterState() {
        public void expect() throws IOException {
          Emitter.this.expectBlockMappingKey(false);
        }
      };
    this.table[17] = new EmitterState() {
        public void expect() throws IOException {
          Emitter.this.expectBlockSequenceItem(true);
        }
      };
  }
  
  private void increaseIndent(boolean flow, boolean indentless) {
    this.indents.add(0, Integer.valueOf(this.indent));
    if (this.indent == -1) {
      if (flow) {
        this.indent = this.config.indentSize;
      } else {
        this.indent = 0;
      } 
    } else if (!indentless) {
      this.indent += this.config.indentSize;
    } 
  }
  
  void expectDocumentStart(boolean first) throws IOException {
    if (this.event.type == EventType.DOCUMENT_START) {
      DocumentStartEvent ev = (DocumentStartEvent)this.event;
      boolean implicit = (first && !ev.isExplicit && !this.config.canonical && ev.version == null && ev.tags == null && !checkEmptyDocument());
      if (!implicit) {
        this.writer.writeIndent(this.indent);
        this.writer.writeIndicator("--- ", true, true, false);
        if (this.config.canonical)
          this.writer.writeIndent(this.indent); 
      } 
      this.state = 2;
    } else if (this.event.type == EventType.STREAM_END) {
      this.writer.writeStreamEnd();
      this.state = 3;
    } else {
      throw new EmitterException("Expected 'document start' but found: " + this.event);
    } 
  }
  
  void expectBlockSequenceItem(boolean first) throws IOException {
    if (!first && this.event.type == EventType.SEQUENCE_END) {
      this.indent = ((Integer)this.indents.remove(0)).intValue();
      this.state = ((Integer)this.states.remove(0)).intValue();
    } else {
      this.writer.writeIndent(this.indent);
      this.writer.writeIndicator("-", true, false, true);
      this.states.add(0, Integer.valueOf(12));
      expectNode(false, true, false, false);
    } 
  }
  
  void expectBlockMappingKey(boolean first) throws IOException {
    if (!first && this.event.type == EventType.MAPPING_END) {
      this.indent = ((Integer)this.indents.remove(0)).intValue();
      this.state = ((Integer)this.states.remove(0)).intValue();
    } else {
      this.writer.writeIndent(this.indent);
      if (checkSimpleKey()) {
        this.states.add(0, Integer.valueOf(14));
        expectNode(false, false, true, true);
      } else {
        this.writer.writeIndicator("?", true, false, true);
        this.states.add(0, Integer.valueOf(15));
        expectNode(false, false, true, false);
      } 
    } 
  }
  
  void expectNode(boolean root, boolean sequence, boolean mapping, boolean simpleKey) throws IOException {
    this.mappingContext = mapping;
    this.simpleKeyContext = simpleKey;
    if (this.event.type == EventType.ALIAS) {
      expectAlias();
    } else if (this.event.type == EventType.SCALAR || this.event.type == EventType.MAPPING_START || this.event.type == EventType.SEQUENCE_START) {
      processAnchor("&");
      processTag();
      if (this.event.type == EventType.SCALAR) {
        expectScalar();
      } else if (this.event.type == EventType.SEQUENCE_START) {
        if (this.flowLevel != 0 || this.config.canonical || ((SequenceStartEvent)this.event).isFlowStyle || checkEmptySequence()) {
          expectFlowSequence();
        } else {
          expectBlockSequence();
        } 
      } else if (this.event.type == EventType.MAPPING_START) {
        if (this.flowLevel != 0 || this.config.canonical || ((MappingStartEvent)this.event).isFlowStyle || checkEmptyMapping()) {
          expectFlowMapping();
        } else {
          expectBlockMapping();
        } 
      } 
    } else {
      throw new EmitterException("Expected 'scalar', 'mapping start', or 'sequence start' but found: " + this.event);
    } 
  }
  
  private void expectAlias() throws IOException {
    if (((NodeEvent)this.event).anchor == null)
      throw new EmitterException("Anchor is not specified for alias."); 
    processAnchor("*");
    this.state = ((Integer)this.states.remove(0)).intValue();
  }
  
  private void expectScalar() throws IOException {
    increaseIndent(true, false);
    processScalar();
    this.indent = ((Integer)this.indents.remove(0)).intValue();
    this.state = ((Integer)this.states.remove(0)).intValue();
  }
  
  private void expectFlowSequence() throws IOException {
    this.writer.writeIndicator("[", true, true, false);
    this.flowLevel++;
    increaseIndent(true, false);
    this.state = 6;
  }
  
  private void expectBlockSequence() {
    increaseIndent(false, (this.mappingContext && !this.writer.indentation));
    this.state = 17;
  }
  
  private void expectFlowMapping() throws IOException {
    this.writer.writeIndicator("{", true, true, false);
    this.flowLevel++;
    increaseIndent(true, false);
    this.state = 8;
  }
  
  private void expectBlockMapping() {
    increaseIndent(false, false);
    this.state = 13;
  }
  
  private boolean checkEmptySequence() {
    return (this.event.type == EventType.SEQUENCE_START && !this.events.isEmpty() && ((Event)this.events.get(0)).type == EventType.SEQUENCE_END);
  }
  
  private boolean checkEmptyMapping() {
    return (this.event.type == EventType.MAPPING_START && !this.events.isEmpty() && ((Event)this.events.get(0)).type == EventType.MAPPING_END);
  }
  
  private boolean checkEmptyDocument() {
    if (this.event.type != EventType.DOCUMENT_START || this.events.isEmpty())
      return false; 
    Event ev = this.events.get(0);
    return (ev.type == EventType.SCALAR && ((ScalarEvent)ev).anchor == null && ((ScalarEvent)ev).tag == null && ((ScalarEvent)ev).implicit != null && ((ScalarEvent)ev).value
      .equals(""));
  }
  
  boolean checkSimpleKey() {
    int length = 0;
    if (this.event instanceof NodeEvent && ((NodeEvent)this.event).anchor != null) {
      if (this.preparedAnchor == null)
        this.preparedAnchor = prepareAnchor(((NodeEvent)this.event).anchor); 
      length += this.preparedAnchor.length();
    } 
    String tag = null;
    if (this.event.type == EventType.SCALAR) {
      tag = ((ScalarEvent)this.event).tag;
    } else if (this.event.type == EventType.MAPPING_START || this.event.type == EventType.SEQUENCE_START) {
      tag = ((CollectionStartEvent)this.event).tag;
    } 
    if (tag != null) {
      if (this.preparedTag == null)
        this.preparedTag = prepareTag(tag); 
      length += this.preparedTag.length();
    } 
    if (this.event.type == EventType.SCALAR && this.analysis == null) {
      this.analysis = ScalarAnalysis.analyze(((ScalarEvent)this.event).value, this.config.escapeUnicode);
      length += this.analysis.scalar.length();
    } 
    return (length < 1024 && (this.event.type == EventType.ALIAS || (this.event.type == EventType.SCALAR && !this.analysis.empty && !this.analysis.multiline) || 
      checkEmptySequence() || checkEmptyMapping()));
  }
  
  private void processAnchor(String indicator) throws IOException {
    NodeEvent ev = (NodeEvent)this.event;
    if (ev.anchor == null) {
      this.preparedAnchor = null;
      return;
    } 
    if (this.preparedAnchor == null)
      this.preparedAnchor = prepareAnchor(ev.anchor); 
    if (this.preparedAnchor != null && !"".equals(this.preparedAnchor))
      this.writer.writeIndicator(indicator + this.preparedAnchor, true, false, false); 
    this.preparedAnchor = null;
  }
  
  private void processTag() throws IOException {
    String tag = null;
    if (this.event.type == EventType.SCALAR) {
      ScalarEvent ev = (ScalarEvent)this.event;
      tag = ev.tag;
      if (this.style == '\000')
        this.style = chooseScalarStyle(); 
      if ((!this.config.canonical || tag == null) && ((Character.MIN_VALUE == this.style && ev.implicit[0]) || (Character.MIN_VALUE != this.style && ev.implicit[1]))) {
        this.preparedTag = null;
        return;
      } 
      if (ev.implicit[0] && tag == null) {
        tag = "!";
        this.preparedTag = null;
      } 
    } else {
      CollectionStartEvent ev = (CollectionStartEvent)this.event;
      tag = ev.tag;
      if ((!this.config.canonical || tag == null) && ev.isImplicit) {
        this.preparedTag = null;
        return;
      } 
    } 
    if (tag == null)
      throw new EmitterException("Tag is not specified."); 
    if (this.preparedTag == null)
      this.preparedTag = prepareTag(tag); 
    if (this.preparedTag != null && !"".equals(this.preparedTag))
      this.writer.writeIndicator(this.preparedTag, true, false, false); 
    this.preparedTag = null;
  }
  
  private char chooseScalarStyle() {
    ScalarEvent ev = (ScalarEvent)this.event;
    if (this.analysis == null)
      this.analysis = ScalarAnalysis.analyze(ev.value, this.config.escapeUnicode); 
    if (ev.style == '"' || this.config.canonical)
      return '"'; 
    if ((ev.style == '\000' || ev.style == '|' || ev.style == '>') && (!this.simpleKeyContext || (!this.analysis.empty && !this.analysis.multiline)) && ((this.flowLevel != 0 && this.analysis.allowFlowPlain) || (this.flowLevel == 0 && this.analysis.allowBlockPlain)))
      return Character.MIN_VALUE; 
    if ((ev.style == '\000' || ev.style == '\'') && this.analysis.allowSingleQuoted && (!this.simpleKeyContext || !this.analysis.multiline))
      return '\''; 
    if ((ev.style == '\000' || ev.style == '|' || ev.style == '>') && this.analysis.multiline && this.flowLevel == 0 && this.analysis.allowBlock)
      return (ev.style == '\000') ? '|' : ev.style; 
    return '"';
  }
  
  private void processScalar() throws IOException {
    ScalarEvent ev = (ScalarEvent)this.event;
    if (this.analysis == null)
      this.analysis = ScalarAnalysis.analyze(ev.value, this.config.escapeUnicode); 
    if (this.style == '\000')
      this.style = chooseScalarStyle(); 
    boolean split = !this.simpleKeyContext;
    if (this.style == '"') {
      this.writer.writeDoubleQuoted(this.analysis.scalar, split, this.indent, this.config.wrapColumn, this.config.escapeUnicode);
    } else if (this.style == '\'') {
      this.writer.writeSingleQuoted(this.analysis.scalar, split, this.indent, this.config.wrapColumn);
    } else if (this.style == '>') {
      this.writer.writeFolded(this.analysis.scalar, this.indent, this.config.wrapColumn);
    } else if (this.style == '|') {
      this.writer.writeLiteral(this.analysis.scalar, this.indent);
    } else {
      this.writer.writePlain(this.analysis.scalar, split, this.indent, this.config.wrapColumn);
    } 
    this.analysis = null;
    this.style = Character.MIN_VALUE;
  }
  
  private String prepareTag(String tag) {
    if (tag == null || "".equals(tag))
      throw new EmitterException("Tag cannot be empty."); 
    if (tag.equals("!"))
      return tag; 
    String handle = null;
    String suffix = tag;
    for (Iterator<String> iter = this.tagPrefixes.keySet().iterator(); iter.hasNext(); ) {
      String prefix = iter.next();
      if (tag.startsWith(prefix) && (prefix.equals("!") || prefix.length() < tag.length())) {
        handle = this.tagPrefixes.get(prefix);
        suffix = tag.substring(prefix.length());
      } 
    } 
    StringBuilder chunks = new StringBuilder();
    int start = 0, ending = 0;
    while (ending < suffix.length())
      ending++; 
    if (start < ending)
      chunks.append(suffix.substring(start, ending)); 
    String suffixText = chunks.toString();
    if (tag.charAt(0) == '!' && this.isVersion10)
      return tag; 
    if (handle != null)
      return handle + suffixText; 
    if (this.config.useVerbatimTags)
      return "!<" + suffixText + ">"; 
    return "!" + suffixText;
  }
  
  String prepareTagHandle(String handle) {
    if (handle == null || "".equals(handle))
      throw new EmitterException("Tag handle cannot be empty."); 
    if (handle.charAt(0) != '!' || handle.charAt(handle.length() - 1) != '!')
      throw new EmitterException("Tag handle must begin and end with '!': " + handle); 
    if (!"!".equals(handle) && !HANDLE_FORMAT.matcher(handle).matches())
      throw new EmitterException("Invalid syntax for tag handle: " + handle); 
    return handle;
  }
  
  String prepareTagPrefix(String prefix) {
    if (prefix == null || "".equals(prefix))
      throw new EmitterException("Tag prefix cannot be empty."); 
    StringBuilder chunks = new StringBuilder();
    int start = 0, ending = 0;
    if (prefix.charAt(0) == '!')
      ending = 1; 
    while (ending < prefix.length())
      ending++; 
    if (start < ending)
      chunks.append(prefix.substring(start, ending)); 
    return chunks.toString();
  }
  
  private String prepareAnchor(String anchor) {
    if (anchor == null || "".equals(anchor))
      throw new EmitterException("Anchor cannot be empty."); 
    if (!ANCHOR_FORMAT.matcher(anchor).matches())
      throw new EmitterException("Invalid syntax for anchor: " + anchor); 
    return anchor;
  }
  
  static final Map<String, String> DEFAULT_TAG_PREFIXES_1_0 = new HashMap<String, String>();
  
  static final Map<String, String> DEFAULT_TAG_PREFIXES_1_1 = new HashMap<String, String>();
  
  static {
    DEFAULT_TAG_PREFIXES_1_0.put("tag:yaml.org,2002:", "!");
    DEFAULT_TAG_PREFIXES_1_1.put("!", "!");
    DEFAULT_TAG_PREFIXES_1_1.put("tag:yaml.org,2002:", "!!");
  }
  
  private static interface EmitterState {
    void expect() throws IOException;
  }
}
