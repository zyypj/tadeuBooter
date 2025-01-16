package me.syncwrld.booter.libs.javassist.tools;

import me.syncwrld.booter.libs.javassist.ClassPool;
import me.syncwrld.booter.libs.javassist.CtClass;
import me.syncwrld.booter.libs.javassist.bytecode.analysis.FramePrinter;

public class framedump {
  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("Usage: java javassist.tools.framedump <fully-qualified class name>");
      return;
    } 
    ClassPool pool = ClassPool.getDefault();
    CtClass clazz = pool.get(args[0]);
    System.out.println("Frame Dump of " + clazz.getName() + ":");
    FramePrinter.print(clazz, System.out);
  }
}
