package me.syncwrld.booter.libs.reflections.scanners;

import java.lang.annotation.Inherited;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.javassist.bytecode.ClassFile;
import me.syncwrld.booter.libs.javassist.bytecode.FieldInfo;
import me.syncwrld.booter.libs.javassist.bytecode.MethodInfo;
import me.syncwrld.booter.libs.reflections.Store;
import me.syncwrld.booter.libs.reflections.util.FilterBuilder;
import me.syncwrld.booter.libs.reflections.util.JavassistHelper;
import me.syncwrld.booter.libs.reflections.util.NameHelper;
import me.syncwrld.booter.libs.reflections.util.QueryBuilder;
import me.syncwrld.booter.libs.reflections.util.QueryFunction;
import me.syncwrld.booter.libs.reflections.vfs.Vfs;

public enum Scanners implements Scanner, QueryBuilder, NameHelper {
  SubTypes {
    Scanners() {
      filterResultsBy((Predicate<String>)(new FilterBuilder()).excludePattern("java\\.lang\\.Object"));
    }
    
    public void scan(ClassFile classFile, List<Map.Entry<String, String>> entries) {
      entries.add(entry(classFile.getSuperclass(), classFile.getName()));
      entries.addAll(entries(Arrays.asList(classFile.getInterfaces()), classFile.getName()));
    }
  },
  TypesAnnotated {
    public boolean acceptResult(String annotation) {
      return (super.acceptResult(annotation) || annotation.equals(Inherited.class.getName()));
    }
    
    public void scan(ClassFile classFile, List<Map.Entry<String, String>> entries) {
      entries.addAll(entries(JavassistHelper.getAnnotations(classFile::getAttribute), classFile.getName()));
    }
  },
  MethodsAnnotated {
    public void scan(ClassFile classFile, List<Map.Entry<String, String>> entries) {
      JavassistHelper.getMethods(classFile).forEach(method -> entries.addAll(entries(JavassistHelper.getAnnotations(method::getAttribute), JavassistHelper.methodName(classFile, method))));
    }
  },
  ConstructorsAnnotated {
    public void scan(ClassFile classFile, List<Map.Entry<String, String>> entries) {
      JavassistHelper.getConstructors(classFile).forEach(constructor -> entries.addAll(entries(JavassistHelper.getAnnotations(constructor::getAttribute), JavassistHelper.methodName(classFile, constructor))));
    }
  },
  FieldsAnnotated {
    public void scan(ClassFile classFile, List<Map.Entry<String, String>> entries) {
      classFile.getFields().forEach(field -> entries.addAll(entries(JavassistHelper.getAnnotations(field::getAttribute), JavassistHelper.fieldName(classFile, field))));
    }
  },
  Resources {
    public boolean acceptsInput(String file) {
      return !file.endsWith(".class");
    }
    
    public List<Map.Entry<String, String>> scan(Vfs.File file) {
      return Collections.singletonList(entry(file.getName(), file.getRelativePath()));
    }
    
    public void scan(ClassFile classFile, List<Map.Entry<String, String>> entries) {
      throw new IllegalStateException();
    }
    
    public QueryFunction<Store, String> with(String pattern) {
      return store -> (LinkedHashSet)((Map)store.getOrDefault(index(), Collections.emptyMap())).entrySet().stream().filter(()).flatMap(()).collect(Collectors.toCollection(LinkedHashSet::new));
    }
  },
  MethodsParameter {
    public void scan(ClassFile classFile, List<Map.Entry<String, String>> entries) {
      JavassistHelper.getMethods(classFile).forEach(method -> {
            String value = JavassistHelper.methodName(classFile, method);
            entries.addAll(entries(JavassistHelper.getParameters(method), value));
            JavassistHelper.getParametersAnnotations(method).forEach(());
          });
    }
  },
  ConstructorsParameter {
    public void scan(ClassFile classFile, List<Map.Entry<String, String>> entries) {
      JavassistHelper.getConstructors(classFile).forEach(constructor -> {
            String value = JavassistHelper.methodName(classFile, constructor);
            entries.addAll(entries(JavassistHelper.getParameters(constructor), value));
            JavassistHelper.getParametersAnnotations(constructor).forEach(());
          });
    }
  },
  MethodsSignature {
    public void scan(ClassFile classFile, List<Map.Entry<String, String>> entries) {
      JavassistHelper.getMethods(classFile).forEach(method -> entries.add(entry(JavassistHelper.getParameters(method).toString(), JavassistHelper.methodName(classFile, method))));
    }
    
    public QueryFunction<Store, String> with(AnnotatedElement... keys) {
      return QueryFunction.single(toNames(keys).toString()).getAll(this::get);
    }
  },
  ConstructorsSignature {
    public void scan(ClassFile classFile, List<Map.Entry<String, String>> entries) {
      JavassistHelper.getConstructors(classFile).forEach(constructor -> entries.add(entry(JavassistHelper.getParameters(constructor).toString(), JavassistHelper.methodName(classFile, constructor))));
    }
    
    public QueryFunction<Store, String> with(AnnotatedElement... keys) {
      return QueryFunction.single(toNames(keys).toString()).getAll(this::get);
    }
  },
  MethodsReturn {
    public void scan(ClassFile classFile, List<Map.Entry<String, String>> entries) {
      JavassistHelper.getMethods(classFile).forEach(method -> entries.add(entry(JavassistHelper.getReturnType(method), JavassistHelper.methodName(classFile, method))));
    }
  };
  
  Scanners() {
    this.resultFilter = (s -> true);
  }
  
  private Predicate<String> resultFilter;
  
  public String index() {
    return name();
  }
  
  public Scanners filterResultsBy(Predicate<String> filter) {
    this.resultFilter = filter;
    return this;
  }
  
  public final List<Map.Entry<String, String>> scan(ClassFile classFile) {
    List<Map.Entry<String, String>> entries = new ArrayList<>();
    scan(classFile, entries);
    return (List<Map.Entry<String, String>>)entries.stream().filter(a -> acceptResult((String)a.getKey())).collect(Collectors.toList());
  }
  
  protected boolean acceptResult(String fqn) {
    return (fqn != null && this.resultFilter.test(fqn));
  }
  
  abstract void scan(ClassFile paramClassFile, List<Map.Entry<String, String>> paramList);
}
