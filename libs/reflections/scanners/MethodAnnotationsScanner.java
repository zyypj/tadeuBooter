package me.syncwrld.booter.libs.reflections.scanners;

import java.util.List;
import me.syncwrld.booter.libs.javassist.bytecode.ClassFile;

@Deprecated
public class MethodAnnotationsScanner extends AbstractScanner {
  @Deprecated
  public MethodAnnotationsScanner() {
    super(Scanners.MethodsAnnotated);
  }
}
