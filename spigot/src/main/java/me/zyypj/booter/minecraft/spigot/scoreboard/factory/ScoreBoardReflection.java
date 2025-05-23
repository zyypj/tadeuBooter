package me.zyypj.booter.minecraft.spigot.scoreboard.factory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Predicate;
import org.bukkit.Bukkit;

public class ScoreBoardReflection {

    private static final String NM_PACKAGE = "net.minecraft";
    private static final String OBC_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();
    private static final String NMS_PACKAGE =
            OBC_PACKAGE.replace("org.bukkit.craftbukkit", NM_PACKAGE + ".server");

    private static final MethodType VOID_METHOD_TYPE = MethodType.methodType(void.class);
    private static final boolean NMS_REPACKAGED =
            optionalClass(NM_PACKAGE + ".network.protocol.Packet").isPresent();
    private static final boolean MOJANG_MAPPINGS =
            optionalClass(NM_PACKAGE + ".network.chat.Component").isPresent();

    // Usado para fallback caso não exista um construtor padrão via MethodHandles.
    private static volatile Object theUnsafe;

    private ScoreBoardReflection() {
        throw new UnsupportedOperationException();
    }

    public static boolean isRepackaged() {
        return NMS_REPACKAGED;
    }

    public static String nmsClassName(String post1_17package, String className) {
        if (NMS_REPACKAGED) {
            String classPackage =
                    post1_17package == null ? NM_PACKAGE : NM_PACKAGE + '.' + post1_17package;
            return classPackage + '.' + className;
        }
        return NMS_PACKAGE + '.' + className;
    }

    public static Class<?> nmsClass(String post1_17package, String className)
            throws ClassNotFoundException {
        return Class.forName(nmsClassName(post1_17package, className));
    }

    public static Class<?> nmsClass(String post1_17package, String spigotClass, String mojangClass)
            throws ClassNotFoundException {
        return nmsClass(post1_17package, MOJANG_MAPPINGS ? mojangClass : spigotClass);
    }

    public static Optional<Class<?>> nmsOptionalClass(String post1_17package, String className) {
        return optionalClass(nmsClassName(post1_17package, className));
    }

    public static String obcClassName(String className) {
        return OBC_PACKAGE + '.' + className;
    }

    public static Class<?> obcClass(String className) throws ClassNotFoundException {
        return Class.forName(obcClassName(className));
    }

    public static Optional<Class<?>> obcOptionalClass(String className) {
        return optionalClass(obcClassName(className));
    }

    public static Optional<Class<?>> optionalClass(String className) {
        try {
            return Optional.of(Class.forName(className));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    public static Object enumValueOf(Class<?> enumClass, String enumName) {
        return Enum.valueOf(enumClass.asSubclass(Enum.class), enumName);
    }

    public static Object enumValueOf(Class<?> enumClass, String enumName, int fallbackOrdinal) {
        try {
            return enumValueOf(enumClass, enumName);
        } catch (IllegalArgumentException e) {
            Object[] constants = enumClass.getEnumConstants();
            if (constants.length > fallbackOrdinal) {
                return constants[fallbackOrdinal];
            }
            throw e;
        }
    }

    static Class<?> innerClass(Class<?> parentClass, Predicate<Class<?>> classPredicate)
            throws ClassNotFoundException {
        for (Class<?> innerClass : parentClass.getDeclaredClasses()) {
            if (classPredicate.test(innerClass)) {
                return innerClass;
            }
        }
        throw new ClassNotFoundException(
                "No class in " + parentClass.getCanonicalName() + " matches the predicate.");
    }

    static Optional<MethodHandle> optionalConstructor(
            Class<?> declaringClass, MethodHandles.Lookup lookup, MethodType type)
            throws IllegalAccessException {
        try {
            return Optional.of(lookup.findConstructor(declaringClass, type));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    /**
     * Tenta obter um construtor padrão (sem parâmetros) para a classe de packet. Se não for
     * possível, utiliza o Unsafe para instanciar a classe.
     */
    public static PacketConstructor findPacketConstructor(
            Class<?> packetClass, MethodHandles.Lookup lookup) throws Exception {
        try {
            MethodHandle constructor = lookup.findConstructor(packetClass, VOID_METHOD_TYPE);
            return constructor::invoke;
        } catch (NoSuchMethodException | IllegalAccessException e) {
            // Fallback usando Unsafe
        }
        if (theUnsafe == null) {
            synchronized (ScoreBoardReflection.class) {
                if (theUnsafe == null) {
                    Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                    theUnsafe = getStaticFieldValue(unsafeClass);
                }
            }
        }
        MethodType allocateMethodType = MethodType.methodType(Object.class, Class.class);
        MethodHandle allocateMethod =
                lookup.findVirtual(theUnsafe.getClass(), "allocateInstance", allocateMethodType);
        return () -> allocateMethod.invoke(theUnsafe, packetClass);
    }

    /** Obtém o valor de um campo estático a partir de uma classe. */
    private static Object getStaticFieldValue(Class<?> clazz) throws Exception {
        Field field = clazz.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return field.get(null);
    }

    @FunctionalInterface
    public interface PacketConstructor {
        Object invoke() throws Throwable;
    }
}
