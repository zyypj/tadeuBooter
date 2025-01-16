package me.syncwrld.booter.libs.reflections.scanners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import me.syncwrld.booter.libs.javassist.CannotCompileException;
import me.syncwrld.booter.libs.javassist.ClassPath;
import me.syncwrld.booter.libs.javassist.ClassPool;
import me.syncwrld.booter.libs.javassist.CtBehavior;
import me.syncwrld.booter.libs.javassist.CtClass;
import me.syncwrld.booter.libs.javassist.CtConstructor;
import me.syncwrld.booter.libs.javassist.CtMethod;
import me.syncwrld.booter.libs.javassist.LoaderClassPath;
import me.syncwrld.booter.libs.javassist.NotFoundException;
import me.syncwrld.booter.libs.javassist.bytecode.ClassFile;
import me.syncwrld.booter.libs.javassist.bytecode.MethodInfo;
import me.syncwrld.booter.libs.javassist.expr.ConstructorCall;
import me.syncwrld.booter.libs.javassist.expr.ExprEditor;
import me.syncwrld.booter.libs.javassist.expr.FieldAccess;
import me.syncwrld.booter.libs.javassist.expr.MethodCall;
import me.syncwrld.booter.libs.javassist.expr.NewExpr;
import me.syncwrld.booter.libs.javax.annotation.Nonnull;
import me.syncwrld.booter.libs.reflections.ReflectionsException;
import me.syncwrld.booter.libs.reflections.util.ClasspathHelper;
import me.syncwrld.booter.libs.reflections.util.JavassistHelper;

public class MemberUsageScanner implements Scanner {
  private Predicate<String> resultFilter = s -> true;
  
  private final ClassLoader[] classLoaders;
  
  private volatile ClassPool classPool;
  
  public MemberUsageScanner() {
    this(ClasspathHelper.classLoaders(new ClassLoader[0]));
  }
  
  public MemberUsageScanner(@Nonnull ClassLoader[] classLoaders) {
    this.classLoaders = classLoaders;
  }
  
  public List<Map.Entry<String, String>> scan(ClassFile classFile) {
    List<Map.Entry<String, String>> entries = new ArrayList<>();
    CtClass ctClass = null;
    try {
      ctClass = getClassPool().get(classFile.getName());
      for (CtConstructor ctConstructor : ctClass.getDeclaredConstructors())
        scanMember((CtBehavior)ctConstructor, entries); 
      for (CtMethod ctMethod : ctClass.getDeclaredMethods())
        scanMember((CtBehavior)ctMethod, entries); 
    } catch (Exception e) {
      throw new ReflectionsException("Could not scan method usage for " + classFile.getName(), e);
    } finally {
      if (ctClass != null)
        ctClass.detach(); 
    } 
    return entries;
  }
  
  public Scanner filterResultsBy(Predicate<String> filter) {
    this.resultFilter = filter;
    return this;
  }
  
  private void scanMember(CtBehavior member, final List<Map.Entry<String, String>> entries) throws CannotCompileException {
    final String key = member.getDeclaringClass().getName() + "." + member.getMethodInfo().getName() + "(" + parameterNames(member.getMethodInfo()) + ")";
    member.instrument(new ExprEditor() {
          public void edit(NewExpr e) {
            try {
              MemberUsageScanner.this.add(entries, e.getConstructor().getDeclaringClass().getName() + ".<init>(" + 
                  MemberUsageScanner.parameterNames(e.getConstructor().getMethodInfo()) + ")", key + " #" + e.getLineNumber());
            } catch (NotFoundException e1) {
              throw new ReflectionsException("Could not find new instance usage in " + key, e1);
            } 
          }
          
          public void edit(MethodCall m) {
            try {
              MemberUsageScanner.this.add(entries, m.getMethod().getDeclaringClass().getName() + "." + m.getMethodName() + "(" + 
                  MemberUsageScanner.parameterNames(m.getMethod().getMethodInfo()) + ")", key + " #" + m.getLineNumber());
            } catch (NotFoundException e) {
              throw new ReflectionsException("Could not find member " + m.getClassName() + " in " + key, e);
            } 
          }
          
          public void edit(ConstructorCall c) {
            try {
              MemberUsageScanner.this.add(entries, c.getConstructor().getDeclaringClass().getName() + ".<init>(" + 
                  MemberUsageScanner.parameterNames(c.getConstructor().getMethodInfo()) + ")", key + " #" + c.getLineNumber());
            } catch (NotFoundException e) {
              throw new ReflectionsException("Could not find member " + c.getClassName() + " in " + key, e);
            } 
          }
          
          public void edit(FieldAccess f) {
            try {
              MemberUsageScanner.this.add(entries, f.getField().getDeclaringClass().getName() + "." + f.getFieldName(), key + " #" + f.getLineNumber());
            } catch (NotFoundException e) {
              throw new ReflectionsException("Could not find member " + f.getFieldName() + " in " + key, e);
            } 
          }
        });
  }
  
  private void add(List<Map.Entry<String, String>> entries, String key, String value) {
    if (this.resultFilter.test(key))
      entries.add(entry(key, value)); 
  }
  
  public static String parameterNames(MethodInfo info) {
    return String.join(", ", JavassistHelper.getParameters(info));
  }
  
  private ClassPool getClassPool() {
    if (this.classPool == null)
      synchronized (this) {
        if (this.classPool == null) {
          this.classPool = new ClassPool();
          for (ClassLoader classLoader : this.classLoaders)
            this.classPool.appendClassPath((ClassPath)new LoaderClassPath(classLoader)); 
        } 
      }  
    return this.classPool;
  }
}
