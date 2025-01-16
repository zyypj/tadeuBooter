package me.syncwrld.booter.libs.google.gson.internal;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Properties;

public final class $Gson$Types {
  static final Type[] EMPTY_TYPE_ARRAY = new Type[0];
  
  private $Gson$Types() {
    throw new UnsupportedOperationException();
  }
  
  public static ParameterizedType newParameterizedTypeWithOwner(Type ownerType, Type rawType, Type... typeArguments) {
    return new ParameterizedTypeImpl(ownerType, rawType, typeArguments);
  }
  
  public static GenericArrayType arrayOf(Type componentType) {
    return new GenericArrayTypeImpl(componentType);
  }
  
  public static WildcardType subtypeOf(Type bound) {
    Type[] upperBounds;
    if (bound instanceof WildcardType) {
      upperBounds = ((WildcardType)bound).getUpperBounds();
    } else {
      upperBounds = new Type[] { bound };
    } 
    return new WildcardTypeImpl(upperBounds, EMPTY_TYPE_ARRAY);
  }
  
  public static WildcardType supertypeOf(Type bound) {
    Type[] lowerBounds;
    if (bound instanceof WildcardType) {
      lowerBounds = ((WildcardType)bound).getLowerBounds();
    } else {
      lowerBounds = new Type[] { bound };
    } 
    return new WildcardTypeImpl(new Type[] { Object.class }, lowerBounds);
  }
  
  public static Type canonicalize(Type type) {
    if (type instanceof Class) {
      Class<?> c = (Class)type;
      return c.isArray() ? new GenericArrayTypeImpl(canonicalize(c.getComponentType())) : c;
    } 
    if (type instanceof ParameterizedType) {
      ParameterizedType p = (ParameterizedType)type;
      return new ParameterizedTypeImpl(p.getOwnerType(), p
          .getRawType(), p.getActualTypeArguments());
    } 
    if (type instanceof GenericArrayType) {
      GenericArrayType g = (GenericArrayType)type;
      return new GenericArrayTypeImpl(g.getGenericComponentType());
    } 
    if (type instanceof WildcardType) {
      WildcardType w = (WildcardType)type;
      return new WildcardTypeImpl(w.getUpperBounds(), w.getLowerBounds());
    } 
    return type;
  }
  
  public static Class<?> getRawType(Type type) {
    if (type instanceof Class)
      return (Class)type; 
    if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)type;
      Type rawType = parameterizedType.getRawType();
      $Gson$Preconditions.checkArgument(rawType instanceof Class);
      return (Class)rawType;
    } 
    if (type instanceof GenericArrayType) {
      Type componentType = ((GenericArrayType)type).getGenericComponentType();
      return Array.newInstance(getRawType(componentType), 0).getClass();
    } 
    if (type instanceof TypeVariable)
      return Object.class; 
    if (type instanceof WildcardType) {
      Type[] bounds = ((WildcardType)type).getUpperBounds();
      assert bounds.length == 1;
      return getRawType(bounds[0]);
    } 
    String className = (type == null) ? "null" : type.getClass().getName();
    throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + className);
  }
  
  private static boolean equal(Object a, Object b) {
    return Objects.equals(a, b);
  }
  
  public static boolean equals(Type a, Type b) {
    if (a == b)
      return true; 
    if (a instanceof Class)
      return a.equals(b); 
    if (a instanceof ParameterizedType) {
      if (!(b instanceof ParameterizedType))
        return false; 
      ParameterizedType pa = (ParameterizedType)a;
      ParameterizedType pb = (ParameterizedType)b;
      return (equal(pa.getOwnerType(), pb.getOwnerType()) && pa
        .getRawType().equals(pb.getRawType()) && 
        Arrays.equals((Object[])pa.getActualTypeArguments(), (Object[])pb.getActualTypeArguments()));
    } 
    if (a instanceof GenericArrayType) {
      if (!(b instanceof GenericArrayType))
        return false; 
      GenericArrayType ga = (GenericArrayType)a;
      GenericArrayType gb = (GenericArrayType)b;
      return equals(ga.getGenericComponentType(), gb.getGenericComponentType());
    } 
    if (a instanceof WildcardType) {
      if (!(b instanceof WildcardType))
        return false; 
      WildcardType wa = (WildcardType)a;
      WildcardType wb = (WildcardType)b;
      return (Arrays.equals((Object[])wa.getUpperBounds(), (Object[])wb.getUpperBounds()) && 
        Arrays.equals((Object[])wa.getLowerBounds(), (Object[])wb.getLowerBounds()));
    } 
    if (a instanceof TypeVariable) {
      if (!(b instanceof TypeVariable))
        return false; 
      TypeVariable<?> va = (TypeVariable)a;
      TypeVariable<?> vb = (TypeVariable)b;
      return (va.getGenericDeclaration() == vb.getGenericDeclaration() && va
        .getName().equals(vb.getName()));
    } 
    return false;
  }
  
  public static String typeToString(Type type) {
    return (type instanceof Class) ? ((Class)type).getName() : type.toString();
  }
  
  private static Type getGenericSupertype(Type context, Class<?> rawType, Class<?> supertype) {
    if (supertype == rawType)
      return context; 
    if (supertype.isInterface()) {
      Class<?>[] interfaces = rawType.getInterfaces();
      for (int i = 0, length = interfaces.length; i < length; i++) {
        if (interfaces[i] == supertype)
          return rawType.getGenericInterfaces()[i]; 
        if (supertype.isAssignableFrom(interfaces[i]))
          return getGenericSupertype(rawType.getGenericInterfaces()[i], interfaces[i], supertype); 
      } 
    } 
    if (!rawType.isInterface())
      while (rawType != Object.class) {
        Class<?> rawSupertype = rawType.getSuperclass();
        if (rawSupertype == supertype)
          return rawType.getGenericSuperclass(); 
        if (supertype.isAssignableFrom(rawSupertype))
          return getGenericSupertype(rawType.getGenericSuperclass(), rawSupertype, supertype); 
        rawType = rawSupertype;
      }  
    return supertype;
  }
  
  private static Type getSupertype(Type context, Class<?> contextRawType, Class<?> supertype) {
    if (context instanceof WildcardType) {
      Type[] bounds = ((WildcardType)context).getUpperBounds();
      assert bounds.length == 1;
      context = bounds[0];
    } 
    $Gson$Preconditions.checkArgument(supertype.isAssignableFrom(contextRawType));
    return resolve(context, contextRawType, 
        getGenericSupertype(context, contextRawType, supertype));
  }
  
  public static Type getArrayComponentType(Type array) {
    return (array instanceof GenericArrayType) ? (
      (GenericArrayType)array).getGenericComponentType() : (
      (Class)array).getComponentType();
  }
  
  public static Type getCollectionElementType(Type context, Class<?> contextRawType) {
    Type collectionType = getSupertype(context, contextRawType, Collection.class);
    if (collectionType instanceof ParameterizedType)
      return ((ParameterizedType)collectionType).getActualTypeArguments()[0]; 
    return Object.class;
  }
  
  public static Type[] getMapKeyAndValueTypes(Type context, Class<?> contextRawType) {
    if (context == Properties.class)
      return new Type[] { String.class, String.class }; 
    Type mapType = getSupertype(context, contextRawType, Map.class);
    if (mapType instanceof ParameterizedType) {
      ParameterizedType mapParameterizedType = (ParameterizedType)mapType;
      return mapParameterizedType.getActualTypeArguments();
    } 
    return new Type[] { Object.class, Object.class };
  }
  
  public static Type resolve(Type context, Class<?> contextRawType, Type toResolve) {
    return resolve(context, contextRawType, toResolve, new HashMap<>());
  }
  
  private static Type resolve(Type context, Class<?> contextRawType, Type toResolve, Map<TypeVariable<?>, Type> visitedTypeVariables) {
    // Byte code:
    //   0: aconst_null
    //   1: astore #4
    //   3: aload_2
    //   4: instanceof java/lang/reflect/TypeVariable
    //   7: ifeq -> 90
    //   10: aload_2
    //   11: checkcast java/lang/reflect/TypeVariable
    //   14: astore #5
    //   16: aload_3
    //   17: aload #5
    //   19: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
    //   24: checkcast java/lang/reflect/Type
    //   27: astore #6
    //   29: aload #6
    //   31: ifnull -> 49
    //   34: aload #6
    //   36: getstatic java/lang/Void.TYPE : Ljava/lang/Class;
    //   39: if_acmpne -> 46
    //   42: aload_2
    //   43: goto -> 48
    //   46: aload #6
    //   48: areturn
    //   49: aload_3
    //   50: aload #5
    //   52: getstatic java/lang/Void.TYPE : Ljava/lang/Class;
    //   55: invokeinterface put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   60: pop
    //   61: aload #4
    //   63: ifnonnull -> 70
    //   66: aload #5
    //   68: astore #4
    //   70: aload_0
    //   71: aload_1
    //   72: aload #5
    //   74: invokestatic resolveTypeVariable : (Ljava/lang/reflect/Type;Ljava/lang/Class;Ljava/lang/reflect/TypeVariable;)Ljava/lang/reflect/Type;
    //   77: astore_2
    //   78: aload_2
    //   79: aload #5
    //   81: if_acmpne -> 87
    //   84: goto -> 482
    //   87: goto -> 3
    //   90: aload_2
    //   91: instanceof java/lang/Class
    //   94: ifeq -> 154
    //   97: aload_2
    //   98: checkcast java/lang/Class
    //   101: invokevirtual isArray : ()Z
    //   104: ifeq -> 154
    //   107: aload_2
    //   108: checkcast java/lang/Class
    //   111: astore #5
    //   113: aload #5
    //   115: invokevirtual getComponentType : ()Ljava/lang/Class;
    //   118: astore #6
    //   120: aload_0
    //   121: aload_1
    //   122: aload #6
    //   124: aload_3
    //   125: invokestatic resolve : (Ljava/lang/reflect/Type;Ljava/lang/Class;Ljava/lang/reflect/Type;Ljava/util/Map;)Ljava/lang/reflect/Type;
    //   128: astore #7
    //   130: aload #6
    //   132: aload #7
    //   134: invokestatic equal : (Ljava/lang/Object;Ljava/lang/Object;)Z
    //   137: ifeq -> 145
    //   140: aload #5
    //   142: goto -> 150
    //   145: aload #7
    //   147: invokestatic arrayOf : (Ljava/lang/reflect/Type;)Ljava/lang/reflect/GenericArrayType;
    //   150: astore_2
    //   151: goto -> 482
    //   154: aload_2
    //   155: instanceof java/lang/reflect/GenericArrayType
    //   158: ifeq -> 210
    //   161: aload_2
    //   162: checkcast java/lang/reflect/GenericArrayType
    //   165: astore #5
    //   167: aload #5
    //   169: invokeinterface getGenericComponentType : ()Ljava/lang/reflect/Type;
    //   174: astore #6
    //   176: aload_0
    //   177: aload_1
    //   178: aload #6
    //   180: aload_3
    //   181: invokestatic resolve : (Ljava/lang/reflect/Type;Ljava/lang/Class;Ljava/lang/reflect/Type;Ljava/util/Map;)Ljava/lang/reflect/Type;
    //   184: astore #7
    //   186: aload #6
    //   188: aload #7
    //   190: invokestatic equal : (Ljava/lang/Object;Ljava/lang/Object;)Z
    //   193: ifeq -> 201
    //   196: aload #5
    //   198: goto -> 206
    //   201: aload #7
    //   203: invokestatic arrayOf : (Ljava/lang/reflect/Type;)Ljava/lang/reflect/GenericArrayType;
    //   206: astore_2
    //   207: goto -> 482
    //   210: aload_2
    //   211: instanceof java/lang/reflect/ParameterizedType
    //   214: ifeq -> 368
    //   217: aload_2
    //   218: checkcast java/lang/reflect/ParameterizedType
    //   221: astore #5
    //   223: aload #5
    //   225: invokeinterface getOwnerType : ()Ljava/lang/reflect/Type;
    //   230: astore #6
    //   232: aload_0
    //   233: aload_1
    //   234: aload #6
    //   236: aload_3
    //   237: invokestatic resolve : (Ljava/lang/reflect/Type;Ljava/lang/Class;Ljava/lang/reflect/Type;Ljava/util/Map;)Ljava/lang/reflect/Type;
    //   240: astore #7
    //   242: aload #7
    //   244: aload #6
    //   246: invokestatic equal : (Ljava/lang/Object;Ljava/lang/Object;)Z
    //   249: ifne -> 256
    //   252: iconst_1
    //   253: goto -> 257
    //   256: iconst_0
    //   257: istore #8
    //   259: aload #5
    //   261: invokeinterface getActualTypeArguments : ()[Ljava/lang/reflect/Type;
    //   266: astore #9
    //   268: iconst_0
    //   269: istore #10
    //   271: aload #9
    //   273: arraylength
    //   274: istore #11
    //   276: iload #10
    //   278: iload #11
    //   280: if_icmpge -> 340
    //   283: aload_0
    //   284: aload_1
    //   285: aload #9
    //   287: iload #10
    //   289: aaload
    //   290: aload_3
    //   291: invokestatic resolve : (Ljava/lang/reflect/Type;Ljava/lang/Class;Ljava/lang/reflect/Type;Ljava/util/Map;)Ljava/lang/reflect/Type;
    //   294: astore #12
    //   296: aload #12
    //   298: aload #9
    //   300: iload #10
    //   302: aaload
    //   303: invokestatic equal : (Ljava/lang/Object;Ljava/lang/Object;)Z
    //   306: ifne -> 334
    //   309: iload #8
    //   311: ifne -> 327
    //   314: aload #9
    //   316: invokevirtual clone : ()Ljava/lang/Object;
    //   319: checkcast [Ljava/lang/reflect/Type;
    //   322: astore #9
    //   324: iconst_1
    //   325: istore #8
    //   327: aload #9
    //   329: iload #10
    //   331: aload #12
    //   333: aastore
    //   334: iinc #10, 1
    //   337: goto -> 276
    //   340: iload #8
    //   342: ifeq -> 362
    //   345: aload #7
    //   347: aload #5
    //   349: invokeinterface getRawType : ()Ljava/lang/reflect/Type;
    //   354: aload #9
    //   356: invokestatic newParameterizedTypeWithOwner : (Ljava/lang/reflect/Type;Ljava/lang/reflect/Type;[Ljava/lang/reflect/Type;)Ljava/lang/reflect/ParameterizedType;
    //   359: goto -> 364
    //   362: aload #5
    //   364: astore_2
    //   365: goto -> 482
    //   368: aload_2
    //   369: instanceof java/lang/reflect/WildcardType
    //   372: ifeq -> 482
    //   375: aload_2
    //   376: checkcast java/lang/reflect/WildcardType
    //   379: astore #5
    //   381: aload #5
    //   383: invokeinterface getLowerBounds : ()[Ljava/lang/reflect/Type;
    //   388: astore #6
    //   390: aload #5
    //   392: invokeinterface getUpperBounds : ()[Ljava/lang/reflect/Type;
    //   397: astore #7
    //   399: aload #6
    //   401: arraylength
    //   402: iconst_1
    //   403: if_icmpne -> 439
    //   406: aload_0
    //   407: aload_1
    //   408: aload #6
    //   410: iconst_0
    //   411: aaload
    //   412: aload_3
    //   413: invokestatic resolve : (Ljava/lang/reflect/Type;Ljava/lang/Class;Ljava/lang/reflect/Type;Ljava/util/Map;)Ljava/lang/reflect/Type;
    //   416: astore #8
    //   418: aload #8
    //   420: aload #6
    //   422: iconst_0
    //   423: aaload
    //   424: if_acmpeq -> 436
    //   427: aload #8
    //   429: invokestatic supertypeOf : (Ljava/lang/reflect/Type;)Ljava/lang/reflect/WildcardType;
    //   432: astore_2
    //   433: goto -> 482
    //   436: goto -> 476
    //   439: aload #7
    //   441: arraylength
    //   442: iconst_1
    //   443: if_icmpne -> 476
    //   446: aload_0
    //   447: aload_1
    //   448: aload #7
    //   450: iconst_0
    //   451: aaload
    //   452: aload_3
    //   453: invokestatic resolve : (Ljava/lang/reflect/Type;Ljava/lang/Class;Ljava/lang/reflect/Type;Ljava/util/Map;)Ljava/lang/reflect/Type;
    //   456: astore #8
    //   458: aload #8
    //   460: aload #7
    //   462: iconst_0
    //   463: aaload
    //   464: if_acmpeq -> 476
    //   467: aload #8
    //   469: invokestatic subtypeOf : (Ljava/lang/reflect/Type;)Ljava/lang/reflect/WildcardType;
    //   472: astore_2
    //   473: goto -> 482
    //   476: aload #5
    //   478: astore_2
    //   479: goto -> 482
    //   482: aload #4
    //   484: ifnull -> 497
    //   487: aload_3
    //   488: aload #4
    //   490: aload_2
    //   491: invokeinterface put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   496: pop
    //   497: aload_2
    //   498: areturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #349	-> 0
    //   #351	-> 3
    //   #352	-> 10
    //   #353	-> 16
    //   #354	-> 29
    //   #356	-> 34
    //   #360	-> 49
    //   #361	-> 61
    //   #362	-> 66
    //   #365	-> 70
    //   #366	-> 78
    //   #367	-> 84
    //   #370	-> 87
    //   #371	-> 107
    //   #372	-> 113
    //   #373	-> 120
    //   #374	-> 130
    //   #375	-> 140
    //   #376	-> 145
    //   #377	-> 151
    //   #379	-> 154
    //   #380	-> 161
    //   #381	-> 167
    //   #382	-> 176
    //   #383	-> 186
    //   #384	-> 196
    //   #385	-> 201
    //   #386	-> 207
    //   #388	-> 210
    //   #389	-> 217
    //   #390	-> 223
    //   #391	-> 232
    //   #392	-> 242
    //   #394	-> 259
    //   #395	-> 268
    //   #396	-> 283
    //   #397	-> 296
    //   #398	-> 309
    //   #399	-> 314
    //   #400	-> 324
    //   #402	-> 327
    //   #395	-> 334
    //   #406	-> 340
    //   #407	-> 345
    //   #408	-> 362
    //   #409	-> 365
    //   #411	-> 368
    //   #412	-> 375
    //   #413	-> 381
    //   #414	-> 390
    //   #416	-> 399
    //   #417	-> 406
    //   #418	-> 418
    //   #419	-> 427
    //   #420	-> 433
    //   #422	-> 436
    //   #423	-> 446
    //   #424	-> 458
    //   #425	-> 467
    //   #426	-> 473
    //   #429	-> 476
    //   #430	-> 479
    //   #437	-> 482
    //   #438	-> 487
    //   #440	-> 497
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   16	71	5	typeVariable	Ljava/lang/reflect/TypeVariable;
    //   29	58	6	previouslyResolved	Ljava/lang/reflect/Type;
    //   113	41	5	original	Ljava/lang/Class;
    //   120	34	6	componentType	Ljava/lang/reflect/Type;
    //   130	24	7	newComponentType	Ljava/lang/reflect/Type;
    //   167	43	5	original	Ljava/lang/reflect/GenericArrayType;
    //   176	34	6	componentType	Ljava/lang/reflect/Type;
    //   186	24	7	newComponentType	Ljava/lang/reflect/Type;
    //   296	38	12	resolvedTypeArgument	Ljava/lang/reflect/Type;
    //   271	69	10	t	I
    //   276	64	11	length	I
    //   223	145	5	original	Ljava/lang/reflect/ParameterizedType;
    //   232	136	6	ownerType	Ljava/lang/reflect/Type;
    //   242	126	7	newOwnerType	Ljava/lang/reflect/Type;
    //   259	109	8	changed	Z
    //   268	100	9	args	[Ljava/lang/reflect/Type;
    //   418	18	8	lowerBound	Ljava/lang/reflect/Type;
    //   458	18	8	upperBound	Ljava/lang/reflect/Type;
    //   381	101	5	original	Ljava/lang/reflect/WildcardType;
    //   390	92	6	originalLowerBound	[Ljava/lang/reflect/Type;
    //   399	83	7	originalUpperBound	[Ljava/lang/reflect/Type;
    //   0	499	0	context	Ljava/lang/reflect/Type;
    //   0	499	1	contextRawType	Ljava/lang/Class;
    //   0	499	2	toResolve	Ljava/lang/reflect/Type;
    //   0	499	3	visitedTypeVariables	Ljava/util/Map;
    //   3	496	4	resolving	Ljava/lang/reflect/TypeVariable;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   16	71	5	typeVariable	Ljava/lang/reflect/TypeVariable<*>;
    //   113	41	5	original	Ljava/lang/Class<*>;
    //   0	499	1	contextRawType	Ljava/lang/Class<*>;
    //   0	499	3	visitedTypeVariables	Ljava/util/Map<Ljava/lang/reflect/TypeVariable<*>;Ljava/lang/reflect/Type;>;
    //   3	496	4	resolving	Ljava/lang/reflect/TypeVariable<*>;
  }
  
  private static Type resolveTypeVariable(Type context, Class<?> contextRawType, TypeVariable<?> unknown) {
    Class<?> declaredByRaw = declaringClassOf(unknown);
    if (declaredByRaw == null)
      return unknown; 
    Type declaredBy = getGenericSupertype(context, contextRawType, declaredByRaw);
    if (declaredBy instanceof ParameterizedType) {
      int index = indexOf((Object[])declaredByRaw.getTypeParameters(), unknown);
      return ((ParameterizedType)declaredBy).getActualTypeArguments()[index];
    } 
    return unknown;
  }
  
  private static int indexOf(Object[] array, Object toFind) {
    for (int i = 0, length = array.length; i < length; i++) {
      if (toFind.equals(array[i]))
        return i; 
    } 
    throw new NoSuchElementException();
  }
  
  private static Class<?> declaringClassOf(TypeVariable<?> typeVariable) {
    GenericDeclaration genericDeclaration = (GenericDeclaration)typeVariable.getGenericDeclaration();
    return (genericDeclaration instanceof Class) ? 
      (Class)genericDeclaration : 
      null;
  }
  
  static void checkNotPrimitive(Type type) {
    $Gson$Preconditions.checkArgument((!(type instanceof Class) || !((Class)type).isPrimitive()));
  }
  
  private static final class ParameterizedTypeImpl implements ParameterizedType, Serializable {
    private final Type ownerType;
    
    private final Type rawType;
    
    private final Type[] typeArguments;
    
    private static final long serialVersionUID = 0L;
    
    public ParameterizedTypeImpl(Type ownerType, Type rawType, Type... typeArguments) {
      Objects.requireNonNull(rawType);
      if (rawType instanceof Class) {
        Class<?> rawTypeAsClass = (Class)rawType;
        boolean isStaticOrTopLevelClass = (Modifier.isStatic(rawTypeAsClass.getModifiers()) || rawTypeAsClass.getEnclosingClass() == null);
        $Gson$Preconditions.checkArgument((ownerType != null || isStaticOrTopLevelClass));
      } 
      this.ownerType = (ownerType == null) ? null : $Gson$Types.canonicalize(ownerType);
      this.rawType = $Gson$Types.canonicalize(rawType);
      this.typeArguments = (Type[])typeArguments.clone();
      for (int t = 0, length = this.typeArguments.length; t < length; t++) {
        Objects.requireNonNull(this.typeArguments[t]);
        $Gson$Types.checkNotPrimitive(this.typeArguments[t]);
        this.typeArguments[t] = $Gson$Types.canonicalize(this.typeArguments[t]);
      } 
    }
    
    public Type[] getActualTypeArguments() {
      return (Type[])this.typeArguments.clone();
    }
    
    public Type getRawType() {
      return this.rawType;
    }
    
    public Type getOwnerType() {
      return this.ownerType;
    }
    
    public boolean equals(Object other) {
      return (other instanceof ParameterizedType && 
        $Gson$Types.equals(this, (ParameterizedType)other));
    }
    
    private static int hashCodeOrZero(Object o) {
      return (o != null) ? o.hashCode() : 0;
    }
    
    public int hashCode() {
      return Arrays.hashCode((Object[])this.typeArguments) ^ this.rawType
        .hashCode() ^ 
        hashCodeOrZero(this.ownerType);
    }
    
    public String toString() {
      int length = this.typeArguments.length;
      if (length == 0)
        return $Gson$Types.typeToString(this.rawType); 
      StringBuilder stringBuilder = new StringBuilder(30 * (length + 1));
      stringBuilder.append($Gson$Types.typeToString(this.rawType)).append("<").append($Gson$Types.typeToString(this.typeArguments[0]));
      for (int i = 1; i < length; i++)
        stringBuilder.append(", ").append($Gson$Types.typeToString(this.typeArguments[i])); 
      return stringBuilder.append(">").toString();
    }
  }
  
  private static final class GenericArrayTypeImpl implements GenericArrayType, Serializable {
    private final Type componentType;
    
    private static final long serialVersionUID = 0L;
    
    public GenericArrayTypeImpl(Type componentType) {
      Objects.requireNonNull(componentType);
      this.componentType = $Gson$Types.canonicalize(componentType);
    }
    
    public Type getGenericComponentType() {
      return this.componentType;
    }
    
    public boolean equals(Object o) {
      return (o instanceof GenericArrayType && 
        $Gson$Types.equals(this, (GenericArrayType)o));
    }
    
    public int hashCode() {
      return this.componentType.hashCode();
    }
    
    public String toString() {
      return $Gson$Types.typeToString(this.componentType) + "[]";
    }
  }
  
  private static final class WildcardTypeImpl implements WildcardType, Serializable {
    private final Type upperBound;
    
    private final Type lowerBound;
    
    private static final long serialVersionUID = 0L;
    
    public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
      $Gson$Preconditions.checkArgument((lowerBounds.length <= 1));
      $Gson$Preconditions.checkArgument((upperBounds.length == 1));
      if (lowerBounds.length == 1) {
        Objects.requireNonNull(lowerBounds[0]);
        $Gson$Types.checkNotPrimitive(lowerBounds[0]);
        $Gson$Preconditions.checkArgument((upperBounds[0] == Object.class));
        this.lowerBound = $Gson$Types.canonicalize(lowerBounds[0]);
        this.upperBound = Object.class;
      } else {
        Objects.requireNonNull(upperBounds[0]);
        $Gson$Types.checkNotPrimitive(upperBounds[0]);
        this.lowerBound = null;
        this.upperBound = $Gson$Types.canonicalize(upperBounds[0]);
      } 
    }
    
    public Type[] getUpperBounds() {
      return new Type[] { this.upperBound };
    }
    
    public Type[] getLowerBounds() {
      (new Type[1])[0] = this.lowerBound;
      return (this.lowerBound != null) ? new Type[1] : $Gson$Types.EMPTY_TYPE_ARRAY;
    }
    
    public boolean equals(Object other) {
      return (other instanceof WildcardType && 
        $Gson$Types.equals(this, (WildcardType)other));
    }
    
    public int hashCode() {
      return ((this.lowerBound != null) ? (31 + this.lowerBound.hashCode()) : 1) ^ 31 + this.upperBound
        .hashCode();
    }
    
    public String toString() {
      if (this.lowerBound != null)
        return "? super " + $Gson$Types.typeToString(this.lowerBound); 
      if (this.upperBound == Object.class)
        return "?"; 
      return "? extends " + $Gson$Types.typeToString(this.upperBound);
    }
  }
}
