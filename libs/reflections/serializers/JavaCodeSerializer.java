package me.syncwrld.booter.libs.reflections.serializers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.syncwrld.booter.libs.reflections.Reflections;
import me.syncwrld.booter.libs.reflections.scanners.TypeElementsScanner;

public class JavaCodeSerializer implements Serializer {
  private static final String pathSeparator = "_";
  
  private static final String doubleSeparator = "__";
  
  private static final String dotSeparator = ".";
  
  private static final String arrayDescriptor = "$$";
  
  private static final String tokenSeparator = "_";
  
  private StringBuilder sb;
  
  private List<String> prevPaths;
  
  private int indent;
  
  public Reflections read(InputStream inputStream) {
    throw new UnsupportedOperationException("read is not implemented on JavaCodeSerializer");
  }
  
  public File save(Reflections reflections, String name) {
    String packageName, className;
    if (name.endsWith("/"))
      name = name.substring(0, name.length() - 1); 
    String filename = name.replace('.', '/').concat(".java");
    File file = Serializer.prepareFile(filename);
    int lastDot = name.lastIndexOf('.');
    if (lastDot == -1) {
      packageName = "";
      className = name.substring(name.lastIndexOf('/') + 1);
    } else {
      packageName = name.substring(name.lastIndexOf('/') + 1, lastDot);
      className = name.substring(lastDot + 1);
    } 
    try {
      this.sb = new StringBuilder();
      this.sb.append("//generated using Reflections JavaCodeSerializer").append(" [").append(new Date()).append("]").append("\n");
      if (packageName.length() != 0) {
        this.sb.append("package ").append(packageName).append(";\n");
        this.sb.append("\n");
      } 
      this.sb.append("public interface ").append(className).append(" {\n\n");
      toString(reflections);
      this.sb.append("}\n");
      Files.write((new File(filename)).toPath(), this.sb.toString().getBytes(Charset.defaultCharset()), new java.nio.file.OpenOption[0]);
    } catch (IOException e) {
      throw new RuntimeException();
    } 
    return file;
  }
  
  private void toString(Reflections reflections) {
    Map<String, Set<String>> map = (Map<String, Set<String>>)reflections.getStore().get(TypeElementsScanner.class.getSimpleName());
    this.prevPaths = new ArrayList<>();
    this.indent = 1;
    map.keySet().stream().sorted().forEach(fqn -> {
          List<String> typePaths = Arrays.asList(fqn.split("\\."));
          String className = fqn.substring(fqn.lastIndexOf('.') + 1);
          List<String> fields = new ArrayList<>();
          List<String> methods = new ArrayList<>();
          List<String> annotations = new ArrayList<>();
          ((Set)map.get(fqn)).stream().sorted().forEach(());
          int i = indentOpen(typePaths, this.prevPaths);
          addPackages(typePaths, i);
          addClass(typePaths, className);
          addFields(typePaths, fields);
          addMethods(typePaths, fields, methods);
          addAnnotations(typePaths, annotations);
          this.prevPaths = typePaths;
        });
    indentClose(this.prevPaths);
  }
  
  protected int indentOpen(List<String> typePaths, List<String> prevPaths) {
    int i = 0;
    while (i < Math.min(typePaths.size(), prevPaths.size()) && ((String)typePaths.get(i)).equals(prevPaths.get(i)))
      i++; 
    for (int j = prevPaths.size(); j > i; j--)
      this.sb.append(indent(--this.indent)).append("}\n"); 
    return i;
  }
  
  protected void indentClose(List<String> prevPaths) {
    for (int j = prevPaths.size(); j >= 1; j--)
      this.sb.append(indent(j)).append("}\n"); 
  }
  
  protected void addPackages(List<String> typePaths, int i) {
    for (int j = i; j < typePaths.size() - 1; j++)
      this.sb.append(indent(this.indent++)).append("interface ").append(uniqueName(typePaths.get(j), typePaths, j)).append(" {\n"); 
  }
  
  protected void addClass(List<String> typePaths, String className) {
    this.sb.append(indent(this.indent++)).append("interface ").append(uniqueName(className, typePaths, typePaths.size() - 1)).append(" {\n");
  }
  
  protected void addFields(List<String> typePaths, List<String> fields) {
    if (!fields.isEmpty()) {
      this.sb.append(indent(this.indent++)).append("interface fields {\n");
      for (String field : fields)
        this.sb.append(indent(this.indent)).append("interface ").append(uniqueName(field, typePaths)).append(" {}\n"); 
      this.sb.append(indent(--this.indent)).append("}\n");
    } 
  }
  
  protected void addMethods(List<String> typePaths, List<String> fields, List<String> methods) {
    if (!methods.isEmpty()) {
      this.sb.append(indent(this.indent++)).append("interface methods {\n");
      for (String method : methods) {
        String methodName = uniqueName(method, fields);
        this.sb.append(indent(this.indent)).append("interface ").append(uniqueName(methodName, typePaths)).append(" {}\n");
      } 
      this.sb.append(indent(--this.indent)).append("}\n");
    } 
  }
  
  protected void addAnnotations(List<String> typePaths, List<String> annotations) {
    if (!annotations.isEmpty()) {
      this.sb.append(indent(this.indent++)).append("interface annotations {\n");
      for (String annotation : annotations)
        this.sb.append(indent(this.indent)).append("interface ").append(uniqueName(annotation, typePaths)).append(" {}\n"); 
      this.sb.append(indent(--this.indent)).append("}\n");
    } 
  }
  
  private String uniqueName(String candidate, List<String> prev, int offset) {
    String normalized = normalize(candidate);
    for (int i = 0; i < offset; i++) {
      if (normalized.equals(prev.get(i)))
        return uniqueName(normalized + "_", prev, offset); 
    } 
    return normalized;
  }
  
  private String normalize(String candidate) {
    return candidate.replace(".", "_");
  }
  
  private String uniqueName(String candidate, List<String> prev) {
    return uniqueName(candidate, prev, prev.size());
  }
  
  private String indent(int times) {
    return IntStream.range(0, times).<CharSequence>mapToObj(i -> "  ").collect(Collectors.joining());
  }
}
