package com.github.zyypj.tadeuBooter.api.minecraft.items.util;


import com.github.zyypj.tadeuBooter.api.reflection.ReflectionHelper;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class NBTUtils {

    private static boolean legacy = true;
    private static Class<?> nbtTagCompoundClass;
    private static Class<?> nbtTagStringClass;
    private static Method modernPutStringMethod;
    private static Method modernGetStringMethod;
    private static Method modernRemoveMethod;
    private static Method legacySetMethod;
    private static Method legacyGetStringMethod;
    private static Method legacyRemoveMethod;

    static {
        try {
            nbtTagCompoundClass = Class.forName("net.minecraft.nbt.CompoundTag");
            legacy = false;
            modernPutStringMethod = nbtTagCompoundClass.getMethod("putString", String.class, String.class);
            modernGetStringMethod = nbtTagCompoundClass.getMethod("getString", String.class);
            modernRemoveMethod = nbtTagCompoundClass.getMethod("remove", String.class);
        } catch (ClassNotFoundException e) {
            legacy = true;
            try {
                String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
                nbtTagCompoundClass = Class.forName("net.minecraft.server." + version + ".NBTTagCompound");
                nbtTagStringClass = Class.forName("net.minecraft.server." + version + ".NBTTagString");
                legacySetMethod = nbtTagCompoundClass.getMethod("set", String.class, nbtTagStringClass.getSuperclass());
                legacyGetStringMethod = nbtTagCompoundClass.getMethod("getString", String.class);
                legacyRemoveMethod = nbtTagCompoundClass.getMethod("remove", String.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adiciona um valor NBT a um ItemStack.
     *
     * @param item  O item ao qual será adicionado o NBT.
     * @param key   A chave do NBT.
     * @param value O valor do NBT.
     * @return O ItemStack modificado com o NBT.
     */
    public static ItemStack setNBT(ItemStack item, String key, String value) {
        if (item == null || key == null || value == null)
            return item;

        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem == null)
            return item;

        try {
            Object tag = ReflectionHelper.invokeMethod(nmsItem, "getTag", new Class<?>[0]);
            if (tag == null) {
                Constructor<?> cons = nbtTagCompoundClass.getConstructor();
                tag = cons.newInstance();
            }

            if (legacy) {
                Constructor<?> tagStringCons = nbtTagStringClass.getConstructor(String.class);
                Object nbtTagString = tagStringCons.newInstance(value);
                ReflectionHelper.invokeMethod(tag, "set", new Class<?>[]{String.class, nbtTagStringClass.getSuperclass()}, key, nbtTagString);
            } else {
                modernPutStringMethod.invoke(tag, key, value);
            }

            ReflectionHelper.invokeMethod(nmsItem, "setTag", new Class<?>[]{nbtTagCompoundClass}, tag);
        } catch (Exception ex) {
            ex.printStackTrace();
            return item;
        }

        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    /**
     * Obtém o valor NBT de um ItemStack.
     *
     * @param item O item do qual será obtido o NBT.
     * @param key  A chave do NBT.
     * @return O valor armazenado no NBT ou null se não existir.
     */
    public static String getNBT(ItemStack item, String key) {
        if (item == null || key == null)
            return null;

        Object nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem == null)
            return null;

        try {
            Object tag = ReflectionHelper.invokeMethod(nmsItem, "getTag", new Class<?>[0]);
            if (tag == null)
                return null;

            if (legacy) {
                return (String) legacyGetStringMethod.invoke(tag, key);
            } else {
                return (String) modernGetStringMethod.invoke(tag, key);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Remove uma tag NBT de um ItemStack.
     *
     * @param item O item do qual será removido o NBT.
     * @param key  A chave do NBT a ser removida.
     * @return O ItemStack modificado sem o NBT especificado.
     */
    public static ItemStack removeNBT(ItemStack item, String key) {
        if (item == null || key == null)
            return item;

        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem == null)
            return item;

        try {
            Object tag = ReflectionHelper.invokeMethod(nmsItem, "getTag", new Class<?>[0]);
            if (tag == null)
                return item;

            if (legacy) {
                legacyRemoveMethod.invoke(tag, key);
            } else {
                modernRemoveMethod.invoke(tag, key);
            }

            ReflectionHelper.invokeMethod(nmsItem, "setTag", new Class<?>[]{nbtTagCompoundClass}, tag);
        } catch (Exception ex) {
            ex.printStackTrace();
            return item;
        }

        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    /**
     * Verifica se um ItemStack contém uma chave NBT específica.
     *
     * @param item O item a ser verificado.
     * @param key  A chave do NBT.
     * @return true se a chave existir, false caso contrário.
     */
    public static boolean hasNBT(ItemStack item, String key) {
        if (item == null || key == null)
            return false;

        Object nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem == null)
            return false;

        try {
            Object tag = ReflectionHelper.invokeMethod(nmsItem, "getTag", new Class<?>[0]);
            if (tag == null)
                return false;

            if (legacy) {
                Method hasKeyMethod = tag.getClass().getMethod("hasKey", String.class);
                return (boolean) hasKeyMethod.invoke(tag, key);
            } else {
                Method containsMethod = tag.getClass().getMethod("contains", String.class);
                return (boolean) containsMethod.invoke(tag, key);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}