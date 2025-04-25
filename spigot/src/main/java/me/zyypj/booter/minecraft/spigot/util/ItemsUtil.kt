package me.zyypj.booter.minecraft.spigot.util

import com.comphenix.protocol.wrappers.nbt.NbtCompound
import com.comphenix.protocol.wrappers.nbt.NbtFactory
import com.mojang.authlib.GameProfile
import me.zyypj.booter.minecraft.spigot.factories.ItemFactory
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Field
import java.lang.reflect.Method

object ItemsUtil {
    private val serverVersion: String
    private val craftItemStackClass: Class<*>
    private val nmsItemClass: Class<*>
    private val nbtTagCompoundClass: Class<*>
    private val mojangsonParserClass: Class<*>

    init {
        try {
            val pkg = Bukkit.getServer().javaClass.`package`.name
            serverVersion = pkg.substringAfterLast('.')
            craftItemStackClass = Class.forName("org.bukkit.craftbukkit.$serverVersion.inventory.CraftItemStack")
            nmsItemClass = Class.forName("net.minecraft.server.$serverVersion.ItemStack")
            nbtTagCompoundClass = Class.forName("net.minecraft.server.$serverVersion.NBTTagCompound")
            mojangsonParserClass = Class.forName("net.minecraft.server.$serverVersion.MojangsonParser")
        } catch (e: Exception) {
            throw ExceptionInInitializerError(e)
        }
    }

    @JvmStatic
    fun serialize(item: ItemStack?): String {
        if (item == null || item.type == Material.AIR) return ""
        return try {
            val asNms = craftItemStackClass.getMethod("asNMSCopy", ItemStack::class.java).invoke(null, item)
            val tag =
                nmsItemClass.getMethod("getTag").invoke(asNms) ?: nbtTagCompoundClass.getConstructor().newInstance()
            tag.toString()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @JvmStatic
    fun deserialize(data: String?): ItemStack {
        if (data.isNullOrBlank()) return ItemStack(Material.AIR)
        return try {
            val parseMethod: Method = mojangsonParserClass.getMethod("a", String::class.java)
            val compound = parseMethod.invoke(null, data)
            val nmsItem = nmsItemClass.getConstructor(nbtTagCompoundClass).newInstance(compound)
            craftItemStackClass.getMethod("asBukkitCopy", nmsItemClass).invoke(null, nmsItem) as ItemStack
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @JvmStatic
    fun applyFactory(serialized: String): ItemFactory {
        val bukkit = deserialize(serialized)
        return ItemFactory(bukkit.type).also { factory ->
            bukkit.itemMeta?.let { factory.item.itemMeta = it }
        }
    }

    @JvmStatic
    fun formatEnchantments(enchants: Map<Enchantment, Int>): String =
        enchants.entries.joinToString(", ") { "${it.key}: ${it.value}" }

    @JvmStatic
    fun extractSkullValue(item: ItemStack): String? {
        val meta = item.itemMeta as? SkullMeta ?: return null
        return try {
            val field: Field = meta.javaClass.getDeclaredField("profile").apply { isAccessible = true }
            val profile = field.get(meta) as GameProfile
            profile.properties["textures"]?.firstOrNull()?.value
        } catch (e: Exception) {
            null
        }
    }

    @JvmStatic
    fun setTag(item: ItemStack, key: String, value: String): ItemStack {
        val compound = NbtFactory.fromItemTag(item) as NbtCompound
        compound.put(key, value)
        NbtFactory.setItemTag(item, compound)
        return item
    }

    @JvmStatic
    fun getTag(item: ItemStack, key: String): String? {
        val compound = NbtFactory.fromItemTag(item) as NbtCompound
        return if (compound.containsKey(key)) compound.getString(key) else null
    }

    @JvmStatic
    fun removeTag(item: ItemStack, key: String): ItemStack {
        val compound = NbtFactory.fromItemTag(item) as NbtCompound
        compound.remove<String>(key)
        NbtFactory.setItemTag(item, compound)
        return item
    }

    @JvmStatic
    fun hasTag(item: ItemStack, key: String): Boolean {
        val compound = NbtFactory.fromItemTag(item) as NbtCompound
        return compound.containsKey(key)
    }
}