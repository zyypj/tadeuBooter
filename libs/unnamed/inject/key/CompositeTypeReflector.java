package me.syncwrld.booter.libs.unnamed.inject.key;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

final class CompositeTypeReflector {
  private static Type getSupertype(Type type, Class<?> rawType, Class<?> resolvingType) {
    Validate.notNull(type, "type", new Object[0]);
    Validate.notNull(rawType, "rawType", new Object[0]);
    Validate.notNull(resolvingType, "resolvingType", new Object[0]);
    if (resolvingType == rawType)
      return type; 
    if (resolvingType.isInterface()) {
      Class<?>[] rawInterfaceTypes = rawType.getInterfaces();
      Type[] genericInterfaceTypes = rawType.getGenericInterfaces();
      for (int i = 0; i < rawInterfaceTypes.length; i++) {
        Class<?> rawInterfaceType = rawInterfaceTypes[i];
        Type interfaceType = genericInterfaceTypes[i];
        if (rawInterfaceType == resolvingType)
          return interfaceType; 
        if (resolvingType.isAssignableFrom(rawInterfaceType))
          return getSupertype(interfaceType, rawInterfaceType, resolvingType); 
      } 
    } 
    if (rawType.isInterface() || rawType == Object.class)
      return resolvingType; 
    Class<?> rawSupertype = rawType.getSuperclass();
    for (; rawType != null && rawType != Object.class; 
      rawType = rawSupertype = rawType.getSuperclass()) {
      if (rawSupertype == resolvingType)
        return rawType.getGenericSuperclass(); 
      if (resolvingType.isAssignableFrom(rawSupertype))
        return getSupertype(rawType.getGenericSuperclass(), rawSupertype, resolvingType); 
    } 
    return resolvingType;
  }
  
  private static Type resolveTypeVariable(TypeReference<?> context, TypeVariable<?> typeVariable) {
    GenericDeclaration declaration = (GenericDeclaration)typeVariable.getGenericDeclaration();
    if (!(declaration instanceof Class))
      return typeVariable; 
    Class<?> classDeclaration = (Class)declaration;
    TypeVariable[] arrayOfTypeVariable = (TypeVariable[])classDeclaration.getTypeParameters();
    Type contextSupertype = getSupertype(context
        .getType(), context.getRawType(), classDeclaration);
    if (!(contextSupertype instanceof ParameterizedType))
      return typeVariable; 
    for (int i = 0; i < arrayOfTypeVariable.length; i++) {
      TypeVariable<?> parameter = arrayOfTypeVariable[i];
      if (parameter.equals(typeVariable))
        return resolveContextually(context, ((ParameterizedType)contextSupertype)
            
            .getActualTypeArguments()[i]); 
    } 
    throw new IllegalStateException("Cannot resolve type variable, no type argument found");
  }
  
  private static Type resolveWildcardType(TypeReference<?> context, WildcardType wildcardType) {
    Type[] lowerBounds = wildcardType.getLowerBounds();
    Type[] upperBounds = wildcardType.getUpperBounds();
    if (lowerBounds.length == 1) {
      Type lowerBound = lowerBounds[0];
      Type resolvedLowerBound = resolveContextually(context, lowerBound);
      if (lowerBound != resolvedLowerBound)
        return Types.wildcardSuperTypeOf(resolvedLowerBound); 
    } 
    if (upperBounds.length == 1) {
      Type upperBound = upperBounds[0];
      Type resolvedUpperBound = resolveContextually(context, upperBound);
      if (upperBound != resolvedUpperBound)
        return Types.wildcardSubTypeOf(resolvedUpperBound); 
    } 
    return wildcardType;
  }
  
  private static Type resolveParameterizedType(TypeReference<?> context, ParameterizedType type) {
    Type ownerType = type.getOwnerType();
    Type resolvedOwnerType = resolveContextually(context, ownerType);
    Type[] typeParameters = type.getActualTypeArguments();
    boolean changed;
    if (changed = (resolvedOwnerType != ownerType))
      typeParameters = (Type[])typeParameters.clone(); 
    for (int i = 0; i < typeParameters.length; i++) {
      Type typeParameter = typeParameters[i];
      Type resolvedTypeParameter = resolveContextually(context, typeParameter);
      if (typeParameter != resolvedTypeParameter) {
        if (!changed) {
          typeParameters = (Type[])typeParameters.clone();
          changed = true;
        } 
        typeParameters[i] = resolvedTypeParameter;
      } 
    } 
    if (changed) {
      Type rawType = type.getRawType();
      return Types.parameterizedTypeOf(resolvedOwnerType, (Class)rawType, typeParameters);
    } 
    return type;
  }
  
  private static Type resolveGenericArrayType(TypeReference<?> context, GenericArrayType genericArrayType) {
    Type componentType = genericArrayType.getGenericComponentType();
    Type resolvedComponentType = resolveContextually(context, componentType);
    if (componentType == resolvedComponentType)
      return genericArrayType; 
    return Types.genericArrayTypeOf(resolvedComponentType);
  }
  
  static Type resolveContextually(TypeReference<?> context, Type type) {
    if (type instanceof TypeVariable)
      return resolveTypeVariable(context, (TypeVariable)type); 
    if (type instanceof WildcardType)
      return resolveWildcardType(context, (WildcardType)type); 
    if (type instanceof ParameterizedType)
      return resolveParameterizedType(context, (ParameterizedType)type); 
    if (type instanceof GenericArrayType)
      return resolveGenericArrayType(context, (GenericArrayType)type); 
    return type;
  }
}
