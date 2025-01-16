package me.syncwrld.booter.libs.google.guava.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
abstract class TypeCapture<T> {
  final Type capture() {
    Type superclass = getClass().getGenericSuperclass();
    Preconditions.checkArgument(superclass instanceof ParameterizedType, "%s isn't parameterized", superclass);
    return ((ParameterizedType)superclass).getActualTypeArguments()[0];
  }
}
