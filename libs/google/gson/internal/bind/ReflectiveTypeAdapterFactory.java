package me.syncwrld.booter.libs.google.gson.internal.bind;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.syncwrld.booter.libs.google.gson.FieldNamingStrategy;
import me.syncwrld.booter.libs.google.gson.Gson;
import me.syncwrld.booter.libs.google.gson.JsonIOException;
import me.syncwrld.booter.libs.google.gson.JsonParseException;
import me.syncwrld.booter.libs.google.gson.JsonSyntaxException;
import me.syncwrld.booter.libs.google.gson.ReflectionAccessFilter;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.TypeAdapterFactory;
import me.syncwrld.booter.libs.google.gson.annotations.JsonAdapter;
import me.syncwrld.booter.libs.google.gson.annotations.SerializedName;
import me.syncwrld.booter.libs.google.gson.internal.;
import me.syncwrld.booter.libs.google.gson.internal.ConstructorConstructor;
import me.syncwrld.booter.libs.google.gson.internal.Excluder;
import me.syncwrld.booter.libs.google.gson.internal.ObjectConstructor;
import me.syncwrld.booter.libs.google.gson.internal.Primitives;
import me.syncwrld.booter.libs.google.gson.internal.ReflectionAccessFilterHelper;
import me.syncwrld.booter.libs.google.gson.internal.reflect.ReflectionHelper;
import me.syncwrld.booter.libs.google.gson.reflect.TypeToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;

public final class ReflectiveTypeAdapterFactory implements TypeAdapterFactory {
  private final ConstructorConstructor constructorConstructor;
  
  private final FieldNamingStrategy fieldNamingPolicy;
  
  private final Excluder excluder;
  
  private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;
  
  private final List<ReflectionAccessFilter> reflectionFilters;
  
  public ReflectiveTypeAdapterFactory(ConstructorConstructor constructorConstructor, FieldNamingStrategy fieldNamingPolicy, Excluder excluder, JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory, List<ReflectionAccessFilter> reflectionFilters) {
    this.constructorConstructor = constructorConstructor;
    this.fieldNamingPolicy = fieldNamingPolicy;
    this.excluder = excluder;
    this.jsonAdapterFactory = jsonAdapterFactory;
    this.reflectionFilters = reflectionFilters;
  }
  
  private boolean includeField(Field f, boolean serialize) {
    return (!this.excluder.excludeClass(f.getType(), serialize) && !this.excluder.excludeField(f, serialize));
  }
  
  private List<String> getFieldNames(Field f) {
    SerializedName annotation = f.<SerializedName>getAnnotation(SerializedName.class);
    if (annotation == null) {
      String name = this.fieldNamingPolicy.translateName(f);
      return Collections.singletonList(name);
    } 
    String serializedName = annotation.value();
    String[] alternates = annotation.alternate();
    if (alternates.length == 0)
      return Collections.singletonList(serializedName); 
    List<String> fieldNames = new ArrayList<>(alternates.length + 1);
    fieldNames.add(serializedName);
    Collections.addAll(fieldNames, alternates);
    return fieldNames;
  }
  
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    Class<? super T> raw = type.getRawType();
    if (!Object.class.isAssignableFrom(raw))
      return null; 
    ReflectionAccessFilter.FilterResult filterResult = ReflectionAccessFilterHelper.getFilterResult(this.reflectionFilters, raw);
    if (filterResult == ReflectionAccessFilter.FilterResult.BLOCK_ALL)
      throw new JsonIOException("ReflectionAccessFilter does not permit using reflection for " + raw + ". Register a TypeAdapter for this type or adjust the access filter."); 
    boolean blockInaccessible = (filterResult == ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE);
    if (ReflectionHelper.isRecord(raw)) {
      TypeAdapter<T> adapter = new RecordAdapter<>((Class)raw, getBoundFields(gson, type, raw, blockInaccessible, true), blockInaccessible);
      return adapter;
    } 
    ObjectConstructor<T> constructor = this.constructorConstructor.get(type);
    return new FieldReflectionAdapter<>(constructor, getBoundFields(gson, type, raw, blockInaccessible, false));
  }
  
  private static <M extends AccessibleObject & Member> void checkAccessible(Object object, M member) {
    if (!ReflectionAccessFilterHelper.canAccess((AccessibleObject)member, Modifier.isStatic(((Member)member).getModifiers()) ? null : object)) {
      String memberDescription = ReflectionHelper.getAccessibleObjectDescription((AccessibleObject)member, true);
      throw new JsonIOException(memberDescription + " is not accessible and ReflectionAccessFilter does not permit making it accessible. Register a TypeAdapter for the declaring type, adjust the access filter or increase the visibility of the element and its declaring type.");
    } 
  }
  
  private BoundField createBoundField(final Gson context, Field field, final Method accessor, String name, final TypeToken<?> fieldType, boolean serialize, boolean deserialize, final boolean blockInaccessible) {
    final boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
    int modifiers = field.getModifiers();
    final boolean isStaticFinalField = (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
    JsonAdapter annotation = field.<JsonAdapter>getAnnotation(JsonAdapter.class);
    TypeAdapter<?> mapped = null;
    if (annotation != null)
      mapped = this.jsonAdapterFactory.getTypeAdapter(this.constructorConstructor, context, fieldType, annotation); 
    final boolean jsonAdapterPresent = (mapped != null);
    if (mapped == null)
      mapped = context.getAdapter(fieldType); 
    final TypeAdapter<Object> typeAdapter = (TypeAdapter)mapped;
    return new BoundField(name, field, serialize, deserialize) {
        void write(JsonWriter writer, Object source) throws IOException, IllegalAccessException {
          Object fieldValue;
          if (!this.serialized)
            return; 
          if (blockInaccessible)
            if (accessor == null) {
              ReflectiveTypeAdapterFactory.checkAccessible(source, (M)this.field);
            } else {
              ReflectiveTypeAdapterFactory.checkAccessible(source, (M)accessor);
            }  
          if (accessor != null) {
            try {
              fieldValue = accessor.invoke(source, new Object[0]);
            } catch (InvocationTargetException e) {
              String accessorDescription = ReflectionHelper.getAccessibleObjectDescription(accessor, false);
              throw new JsonIOException("Accessor " + accessorDescription + " threw exception", e.getCause());
            } 
          } else {
            fieldValue = this.field.get(source);
          } 
          if (fieldValue == source)
            return; 
          writer.name(this.name);
          TypeAdapter<Object> t = jsonAdapterPresent ? typeAdapter : new TypeAdapterRuntimeTypeWrapper(context, typeAdapter, fieldType.getType());
          t.write(writer, fieldValue);
        }
        
        void readIntoArray(JsonReader reader, int index, Object[] target) throws IOException, JsonParseException {
          Object fieldValue = typeAdapter.read(reader);
          if (fieldValue == null && isPrimitive)
            throw new JsonParseException("null is not allowed as value for record component '" + this.fieldName + "' of primitive type; at path " + reader
                .getPath()); 
          target[index] = fieldValue;
        }
        
        void readIntoField(JsonReader reader, Object target) throws IOException, IllegalAccessException {
          Object fieldValue = typeAdapter.read(reader);
          if (fieldValue != null || !isPrimitive) {
            if (blockInaccessible) {
              ReflectiveTypeAdapterFactory.checkAccessible(target, (M)this.field);
            } else if (isStaticFinalField) {
              String fieldDescription = ReflectionHelper.getAccessibleObjectDescription(this.field, false);
              throw new JsonIOException("Cannot set value of 'static final' " + fieldDescription);
            } 
            this.field.set(target, fieldValue);
          } 
        }
      };
  }
  
  private Map<String, BoundField> getBoundFields(Gson context, TypeToken<?> type, Class<?> raw, boolean blockInaccessible, boolean isRecord) {
    Map<String, BoundField> result = new LinkedHashMap<>();
    if (raw.isInterface())
      return result; 
    Class<?> originalRaw = raw;
    while (raw != Object.class) {
      Field[] fields = raw.getDeclaredFields();
      if (raw != originalRaw && fields.length > 0) {
        ReflectionAccessFilter.FilterResult filterResult = ReflectionAccessFilterHelper.getFilterResult(this.reflectionFilters, raw);
        if (filterResult == ReflectionAccessFilter.FilterResult.BLOCK_ALL)
          throw new JsonIOException("ReflectionAccessFilter does not permit using reflection for " + raw + " (supertype of " + originalRaw + "). Register a TypeAdapter for this type or adjust the access filter."); 
        blockInaccessible = (filterResult == ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE);
      } 
      for (Field field : fields) {
        boolean serialize = includeField(field, true);
        boolean deserialize = includeField(field, false);
        if (serialize || deserialize) {
          Method accessor = null;
          if (isRecord)
            if (Modifier.isStatic(field.getModifiers())) {
              deserialize = false;
            } else {
              accessor = ReflectionHelper.getAccessor(raw, field);
              if (!blockInaccessible)
                ReflectionHelper.makeAccessible(accessor); 
              if (accessor.getAnnotation(SerializedName.class) != null && field
                .getAnnotation(SerializedName.class) == null) {
                String methodDescription = ReflectionHelper.getAccessibleObjectDescription(accessor, false);
                throw new JsonIOException("@SerializedName on " + methodDescription + " is not supported");
              } 
            }  
          if (!blockInaccessible && accessor == null)
            ReflectionHelper.makeAccessible(field); 
          Type fieldType = .Gson.Types.resolve(type.getType(), raw, field.getGenericType());
          List<String> fieldNames = getFieldNames(field);
          BoundField previous = null;
          for (int i = 0, size = fieldNames.size(); i < size; i++) {
            String name = fieldNames.get(i);
            if (i != 0)
              serialize = false; 
            BoundField boundField = createBoundField(context, field, accessor, name, 
                TypeToken.get(fieldType), serialize, deserialize, blockInaccessible);
            BoundField replaced = result.put(name, boundField);
            if (previous == null)
              previous = replaced; 
          } 
          if (previous != null)
            throw new IllegalArgumentException("Class " + originalRaw.getName() + " declares multiple JSON fields named '" + previous.name + "'; conflict is caused by fields " + 
                
                ReflectionHelper.fieldToString(previous.field) + " and " + ReflectionHelper.fieldToString(field)); 
        } 
      } 
      type = TypeToken.get(.Gson.Types.resolve(type.getType(), raw, raw.getGenericSuperclass()));
      raw = type.getRawType();
    } 
    return result;
  }
  
  static abstract class BoundField {
    final String name;
    
    final Field field;
    
    final String fieldName;
    
    final boolean serialized;
    
    final boolean deserialized;
    
    protected BoundField(String name, Field field, boolean serialized, boolean deserialized) {
      this.name = name;
      this.field = field;
      this.fieldName = field.getName();
      this.serialized = serialized;
      this.deserialized = deserialized;
    }
    
    abstract void write(JsonWriter param1JsonWriter, Object param1Object) throws IOException, IllegalAccessException;
    
    abstract void readIntoArray(JsonReader param1JsonReader, int param1Int, Object[] param1ArrayOfObject) throws IOException, JsonParseException;
    
    abstract void readIntoField(JsonReader param1JsonReader, Object param1Object) throws IOException, IllegalAccessException;
  }
  
  public static abstract class Adapter<T, A> extends TypeAdapter<T> {
    final Map<String, ReflectiveTypeAdapterFactory.BoundField> boundFields;
    
    Adapter(Map<String, ReflectiveTypeAdapterFactory.BoundField> boundFields) {
      this.boundFields = boundFields;
    }
    
    public void write(JsonWriter out, T value) throws IOException {
      if (value == null) {
        out.nullValue();
        return;
      } 
      out.beginObject();
      try {
        for (ReflectiveTypeAdapterFactory.BoundField boundField : this.boundFields.values())
          boundField.write(out, value); 
      } catch (IllegalAccessException e) {
        throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(e);
      } 
      out.endObject();
    }
    
    public T read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
        in.nextNull();
        return null;
      } 
      A accumulator = createAccumulator();
      try {
        in.beginObject();
        while (in.hasNext()) {
          String name = in.nextName();
          ReflectiveTypeAdapterFactory.BoundField field = this.boundFields.get(name);
          if (field == null || !field.deserialized) {
            in.skipValue();
            continue;
          } 
          readField(accumulator, in, field);
        } 
      } catch (IllegalStateException e) {
        throw new JsonSyntaxException(e);
      } catch (IllegalAccessException e) {
        throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(e);
      } 
      in.endObject();
      return finalize(accumulator);
    }
    
    abstract A createAccumulator();
    
    abstract void readField(A param1A, JsonReader param1JsonReader, ReflectiveTypeAdapterFactory.BoundField param1BoundField) throws IllegalAccessException, IOException;
    
    abstract T finalize(A param1A);
  }
  
  private static final class FieldReflectionAdapter<T> extends Adapter<T, T> {
    private final ObjectConstructor<T> constructor;
    
    FieldReflectionAdapter(ObjectConstructor<T> constructor, Map<String, ReflectiveTypeAdapterFactory.BoundField> boundFields) {
      super(boundFields);
      this.constructor = constructor;
    }
    
    T createAccumulator() {
      return (T)this.constructor.construct();
    }
    
    void readField(T accumulator, JsonReader in, ReflectiveTypeAdapterFactory.BoundField field) throws IllegalAccessException, IOException {
      field.readIntoField(in, accumulator);
    }
    
    T finalize(T accumulator) {
      return accumulator;
    }
  }
  
  private static final class RecordAdapter<T> extends Adapter<T, Object[]> {
    static final Map<Class<?>, Object> PRIMITIVE_DEFAULTS = primitiveDefaults();
    
    private final Constructor<T> constructor;
    
    private final Object[] constructorArgsDefaults;
    
    private final Map<String, Integer> componentIndices = new HashMap<>();
    
    RecordAdapter(Class<T> raw, Map<String, ReflectiveTypeAdapterFactory.BoundField> boundFields, boolean blockInaccessible) {
      super(boundFields);
      this.constructor = ReflectionHelper.getCanonicalRecordConstructor(raw);
      if (blockInaccessible) {
        ReflectiveTypeAdapterFactory.checkAccessible(null, (M)this.constructor);
      } else {
        ReflectionHelper.makeAccessible(this.constructor);
      } 
      String[] componentNames = ReflectionHelper.getRecordComponentNames(raw);
      for (int i = 0; i < componentNames.length; i++)
        this.componentIndices.put(componentNames[i], Integer.valueOf(i)); 
      Class<?>[] parameterTypes = this.constructor.getParameterTypes();
      this.constructorArgsDefaults = new Object[parameterTypes.length];
      for (int j = 0; j < parameterTypes.length; j++)
        this.constructorArgsDefaults[j] = PRIMITIVE_DEFAULTS.get(parameterTypes[j]); 
    }
    
    private static Map<Class<?>, Object> primitiveDefaults() {
      Map<Class<?>, Object> zeroes = new HashMap<>();
      zeroes.put(byte.class, Byte.valueOf((byte)0));
      zeroes.put(short.class, Short.valueOf((short)0));
      zeroes.put(int.class, Integer.valueOf(0));
      zeroes.put(long.class, Long.valueOf(0L));
      zeroes.put(float.class, Float.valueOf(0.0F));
      zeroes.put(double.class, Double.valueOf(0.0D));
      zeroes.put(char.class, Character.valueOf(false));
      zeroes.put(boolean.class, Boolean.valueOf(false));
      return zeroes;
    }
    
    Object[] createAccumulator() {
      return (Object[])this.constructorArgsDefaults.clone();
    }
    
    void readField(Object[] accumulator, JsonReader in, ReflectiveTypeAdapterFactory.BoundField field) throws IOException {
      Integer componentIndex = this.componentIndices.get(field.fieldName);
      if (componentIndex == null)
        throw new IllegalStateException("Could not find the index in the constructor '" + 
            ReflectionHelper.constructorToString(this.constructor) + "' for field with name '" + field.fieldName + "', unable to determine which argument in the constructor the field corresponds to. This is unexpected behavior, as we expect the RecordComponents to have the same names as the fields in the Java class, and that the order of the RecordComponents is the same as the order of the canonical constructor parameters."); 
      field.readIntoArray(in, componentIndex.intValue(), accumulator);
    }
    
    T finalize(Object[] accumulator) {
      try {
        return this.constructor.newInstance(accumulator);
      } catch (IllegalAccessException e) {
        throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(e);
      } catch (InstantiationException|IllegalArgumentException e) {
        throw new RuntimeException("Failed to invoke constructor '" + 
            ReflectionHelper.constructorToString(this.constructor) + "' with args " + 
            Arrays.toString(accumulator), e);
      } catch (InvocationTargetException e) {
        throw new RuntimeException("Failed to invoke constructor '" + 
            ReflectionHelper.constructorToString(this.constructor) + "' with args " + 
            Arrays.toString(accumulator), e.getCause());
      } 
    }
  }
}
