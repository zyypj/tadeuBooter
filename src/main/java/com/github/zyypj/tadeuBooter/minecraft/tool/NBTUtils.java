package com.github.zyypj.tadeuBooter.minecraft.tool;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NBTUtils {

    /**
     * Adiciona um valor NBT a um ItemStack.
     *
     * @param item  O item ao qual será adicionado o NBT.
     * @param key   A chave do NBT.
     * @param value O valor do NBT.
     * @return O ItemStack modificado com o NBT.
     */
    public static ItemStack setNBT(ItemStack item, String key, String value) {
        if (item == null || key == null || value == null) return item;

        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem == null) return item;

        NBTTagCompound tag = (nmsItem.getTag() != null) ? nmsItem.getTag() : new NBTTagCompound();
        tag.set(key, new NBTTagString(value));
        nmsItem.setTag(tag);

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
        if (item == null || key == null) return null;

        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem == null || nmsItem.getTag() == null) return null;

        return nmsItem.getTag().hasKey(key) ? nmsItem.getTag().getString(key) : null;
    }

    /**
     * Remove uma tag NBT de um ItemStack.
     *
     * @param item O item do qual será removido o NBT.
     * @param key  A chave do NBT a ser removida.
     * @return O ItemStack modificado sem o NBT especificado.
     */
    public static ItemStack removeNBT(ItemStack item, String key) {
        if (item == null || key == null) return item;

        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem == null || nmsItem.getTag() == null) return item;

        NBTTagCompound tag = nmsItem.getTag();
        tag.remove(key);
        nmsItem.setTag(tag);

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
        if (item == null || key == null) return false;

        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        return nmsItem != null && nmsItem.getTag() != null && nmsItem.getTag().hasKey(key);
    }
}