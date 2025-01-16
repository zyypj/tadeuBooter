package me.syncwrld.booter.libs.google.guava.reflect;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
public abstract class TypeParameter<T> extends TypeCapture<T> {
  final TypeVariable<?> typeVariable;
  
  protected TypeParameter() {
    Type type = capture();
    Preconditions.checkArgument(type instanceof TypeVariable, "%s should be a type variable.", type);
    this.typeVariable = (TypeVariable)type;
  }
  
  public final int hashCode() {
    return this.typeVariable.hashCode();
  }
  
  public final boolean equals(@CheckForNull Object o) {
    if (o instanceof TypeParameter) {
      TypeParameter<?> that = (TypeParameter)o;
      return this.typeVariable.equals(that.typeVariable);
    } 
    return false;
  }
  
  public String toString() {
    return this.typeVariable.toString();
  }
}
