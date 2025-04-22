package com.github.zyypj.tadeuBooter.api.minecraft.items.util

import com.comphenix.protocol.wrappers.nbt.NbtCompound
import com.comphenix.protocol.wrappers.nbt.NbtFactory
import com.github.zyypj.tadeuBooter.api.minecraft.items.ItemFactory
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Method

object ItemsUtil {
    private val serverVersion = Bukkit.getServer().javaClass.`package`.name.substringAfterLast('.')

    private val craftItemStackClass = Class.forName("org.bukkit.craftbukkit.$serverVersion.inventory.CraftItemStack")
    private val nmsItemClass = Class.forName("net.minecraft.server.$serverVersion.ItemStack")
    private val nbtTagCompoundClass = Class.forName("net.minecraft.server.$serverVersion.NBTTagCompound")
    private val mojangsonParserClass = Class.forName("net.minecraftr.server.$serverVersion.MojangsonParser")

    fun serialize(item: ItemStack?): String = when {
        item == null || item.type == Material.AIR -> ""
        else -> {
            val asNms = craftItemStackClass.getMethod("asNMSCopy", ItemStack::class.java).invoke(null, item)

            val tag =
                nmsItemClass.getMethod("getTag").invoke(asNms) ?: nbtTagCompoundClass.getConstructor().newInstance()

            tag.toString()
        }
    }

    fun deserialize(data: String): ItemStack = when {
        data.isBlank() -> ItemStack(Material.AIR)
        else -> {
            val parseMethod: Method = mojangsonParserClass.getMethod("a", String::class.java)

            val compound = parseMethod.invoke(null, data)
            val nmsItem = nmsItemClass.getConstructor(nbtTagCompoundClass).newInstance(compound)

            craftItemStackClass.getMethod("asBukkitCopy", nmsItemClass).invoke(null, nmsItem) as ItemStack
        }
    }

    fun applyFactory(serialized: String): ItemFactory = ItemFactory(deserialize(serialized).type).also { factory ->
        val bukkit = deserialize(serialized)
        bukkit.itemMeta?.let { factory.item.itemMeta = it }
    }

    fun formatEnchantments(enchants: Map<Enchantment, Int>): String = enchants.entries.joinToString(", ") {
        "${it.key}:${
            it.value
        }"
    }

    fun extractSkullValue(item: ItemStack): String? {
        val meta = item.itemMeta as? SkullMeta ?: return null
        val field = meta::class.java.getDeclaredField("profile").apply { isAccessible = true }
        val profile = field.get(meta) as com.mojang.authlib.GameProfile
        return profile.properties.get("textures")?.firstOrNull()?.value
    }

    fun setTag(item: ItemStack, key: String, value: String): ItemStack {
        val compound = NbtFactory.fromItemTag(item) as NbtCompound
        compound.put(key, value)
        NbtFactory.setItemTag(item, compound)
        return item
    }

    fun getTag(item: ItemStack, key: String): String? {
        val compound = NbtFactory.fromItemTag(item) as NbtCompound
        return if (compound.containsKey(key)) compound.getString(key) else null
    }

    fun removeTag(item: ItemStack, key: String): ItemStack {
        val compound = NbtFactory.fromItemTag(item) as NbtCompound
        compound.remove<String>(key)
        NbtFactory.setItemTag(item, compound)
        return item
    }

    fun hasTag(item: ItemStack, key: String): Boolean {
        val compound = NbtFactory.fromItemTag(item) as NbtCompound
        return compound.containsKey(key)
    }
}