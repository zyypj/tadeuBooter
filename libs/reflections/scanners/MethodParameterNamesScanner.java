package me.syncwrld.booter.libs.reflections.scanners;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.syncwrld.booter.libs.javassist.bytecode.ClassFile;
import me.syncwrld.booter.libs.javassist.bytecode.CodeAttribute;
import me.syncwrld.booter.libs.javassist.bytecode.LocalVariableAttribute;
import me.syncwrld.booter.libs.javassist.bytecode.MethodInfo;
import me.syncwrld.booter.libs.reflections.util.JavassistHelper;

public class MethodParameterNamesScanner implements Scanner {
  public List<Map.Entry<String, String>> scan(ClassFile classFile) {
    List<Map.Entry<String, String>> entries = new ArrayList<>();
    for (MethodInfo method : classFile.getMethods()) {
      String key = JavassistHelper.methodName(classFile, method);
      String value = getString(method);
      if (!value.isEmpty())
        entries.add(entry(key, value)); 
    } 
    return entries;
  }
  
  private String getString(MethodInfo method) {
    CodeAttribute codeAttribute = method.getCodeAttribute();
    LocalVariableAttribute table = (codeAttribute != null) ? (LocalVariableAttribute)codeAttribute.getAttribute("LocalVariableTable") : null;
    int length = JavassistHelper.getParameters(method).size();
    if (length > 0) {
      int shift = Modifier.isStatic(method.getAccessFlags()) ? 0 : 1;
      return IntStream.range(shift, length + shift)
        .<CharSequence>mapToObj(i -> method.getConstPool().getUtf8Info(table.nameIndex(i)))
        .filter(name -> !name.startsWith("this$"))
        .collect(Collectors.joining(", "));
    } 
    return "";
  }
}
