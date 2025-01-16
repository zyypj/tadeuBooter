package me.syncwrld.booter.libs.reflections.scanners;

import java.util.List;
import java.util.Map;
import me.syncwrld.booter.libs.javassist.bytecode.ClassFile;

@Deprecated
class AbstractScanner implements Scanner {
  protected final Scanner scanner;
  
  AbstractScanner(Scanner scanner) {
    this.scanner = scanner;
  }
  
  public String index() {
    return this.scanner.index();
  }
  
  public List<Map.Entry<String, String>> scan(ClassFile cls) {
    return this.scanner.scan(cls);
  }
}
