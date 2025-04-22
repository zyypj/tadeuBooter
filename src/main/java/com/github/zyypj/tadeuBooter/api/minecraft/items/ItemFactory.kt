package com.github.zyypj.tadeuBooter.api.minecraft.items

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.UUID

/**
 * Constrói e customiza ItemStacks de forma fluente.
 * Compatível com chamadas Java.
 */
class ItemFactory {
    val item: ItemStack
    private var itemMeta: ItemMeta

    /**
     * Cria o factory com um Material.
     */
    constructor(material: Material) {
        item = ItemStack(material)
        itemMeta = item.itemMeta!!
    }

    /**
     * @deprecated Use o construtor com Material.
     */
    @Deprecated("Use constructor(material: Material) instead")
    constructor(typeId: Int) : this(
        Material.getMaterial(typeId)
            ?: throw IllegalArgumentException("Material id $typeId not found")
    )

    /** Define o nome (displayName) do ItemStack. */
    fun setName(name: String): ItemFactory = apply {
        itemMeta.displayName = name.replace("&", "§")
    }

    /** Adiciona brilho (glow) sem enchant visível. */
    fun setGlow(glow: Boolean): ItemFactory = apply {
        if (glow) {
            itemMeta.addEnchant(Enchantment.LUCK, 1, true)
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
    }

    /** Adiciona um enchant normal. */
    fun addEnchant(enchantment: Enchantment, level: Int): ItemFactory = apply {
        itemMeta.addEnchant(enchantment, level, true)
    }

    /** Adiciona um enchant inseguro direto no ItemStack. */
    fun addUnsafeEnchant(enchantment: Enchantment, level: Int): ItemFactory = apply {
        item.addUnsafeEnchantment(enchantment, level)
    }

    /** Define o lore via array de Strings. */
    fun setLore(vararg lore: String): ItemFactory = apply {
        itemMeta.lore = lore.map { it.replace("&", "§") }
    }

    /** Define o lore via lista de Strings. */
    fun setLore(lore: List<String>): ItemFactory = apply {
        itemMeta.lore = lore.map { it.replace("&", "§") }
    }

    /** Define o dono de cabeça de jogador pelo nome (owner). */
    fun setSkullOwner(owner: String): ItemFactory = apply {
        (itemMeta as? SkullMeta)?.let { skullMeta ->
            try {
                val mModern: Method = skullMeta.javaClass.getMethod(
                    "setOwningPlayer", org.bukkit.OfflinePlayer::class.java
                )
                mModern.invoke(skullMeta, Bukkit.getOfflinePlayer(owner))
            } catch (e1: NoSuchMethodException) {
                try {
                    val mLegacy: Method = skullMeta.javaClass.getMethod(
                        "setOwner", String::class.java
                    )
                    mLegacy.invoke(skullMeta, owner)
                } catch (e2: NoSuchMethodException) {
                    try {
                        val f: Field = skullMeta.javaClass.getDeclaredField("owner")
                        f.isAccessible = true
                        f.set(skullMeta, owner)
                    } catch (_: Exception) {
                    }
                }
            }
            itemMeta = skullMeta
        }
    }

    /** Define o valor de textura de cabeça via GameProfile. */
    fun setSkullValue(value: String): ItemFactory = apply {
        (itemMeta as? SkullMeta)?.let { skullMeta ->
            val profile = GameProfile(UUID.randomUUID(), null).apply {
                properties.put("textures", Property("textures", value))
            }
            var clazz: Class<*>? = skullMeta.javaClass
            while (clazz != null) {
                try {
                    val field: Field = clazz.getDeclaredField("profile")
                    field.isAccessible = true
                    field.set(skullMeta, profile)
                    break
                } catch (_: NoSuchFieldException) {
                    clazz = clazz.superclass
                }
            }
            itemMeta = skullMeta
        }
    }

    /** Altera o Material do ItemStack. */
    fun setMaterial(material: Material): ItemFactory = apply {
        item.type = material
    }

    /** Define o data/durability como short. */
    fun setData(data: Short): ItemFactory = apply {
        item.durability = data
    }

    /** Define a durabilidade (durability) via Int. */
    fun setDurability(durability: Int): ItemFactory = apply {
        item.durability = durability.toShort()
    }

    /** Adiciona flags de item. */
    fun addItemFlags(vararg flags: ItemFlag): ItemFactory = apply {
        itemMeta.addItemFlags(*flags)
    }

    /** Define cor em armadura de couro. */
    fun setLeatherColor(color: Color): ItemFactory = apply {
        if (itemMeta is LeatherArmorMeta) {
            (itemMeta as LeatherArmorMeta).color = color
        }
    }

    /** Constrói e retorna o ItemStack. */
    fun build(): ItemStack {
        item.itemMeta = itemMeta
        return item
    }
}