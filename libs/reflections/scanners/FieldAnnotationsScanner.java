package me.syncwrld.booter.libs.reflections.scanners;

import java.util.List;
import me.syncwrld.booter.libs.javassist.bytecode.ClassFile;

@Deprecated
public class FieldAnnotationsScanner extends AbstractScanner {
  @Deprecated
  public FieldAnnotationsScanner() {
    super(Scanners.FieldsAnnotated);
  }
}
