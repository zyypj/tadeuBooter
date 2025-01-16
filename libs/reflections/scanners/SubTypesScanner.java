package me.syncwrld.booter.libs.reflections.scanners;

import java.util.List;
import java.util.Map;
import me.syncwrld.booter.libs.javassist.bytecode.ClassFile;

@Deprecated
public class SubTypesScanner extends AbstractScanner {
  @Deprecated
  public SubTypesScanner() {
    super(Scanners.SubTypes);
  }
  
  @Deprecated
  public SubTypesScanner(boolean excludeObjectClass) {
    super(excludeObjectClass ? Scanners.SubTypes : Scanners.SubTypes.filterResultsBy(s -> true));
  }
  
  public List<Map.Entry<String, String>> scan(ClassFile cls) {
    return this.scanner.scan(cls);
  }
}
