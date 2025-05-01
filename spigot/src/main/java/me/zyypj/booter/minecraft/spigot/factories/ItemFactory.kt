package me.zyypj.booter.minecraft.spigot.factories

import com.cryptomorin.xseries.profiles.builder.XSkull
import com.cryptomorin.xseries.profiles.objects.Profileable
import com.cryptomorin.xseries.profiles.objects.ProfileInputType
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta

/**
 * Constrói e customiza ItemStacks de forma fluente, com suporte a skulls em múltiplas versões via XSeries.
 */
class ItemFactory {
    private var item: ItemStack
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
        Material.getMaterial(typeId) ?: throw IllegalArgumentException("Material id $typeId not found")
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
    fun addEnchant(enchant: Enchantment, level: Int): ItemFactory = apply {
        itemMeta.addEnchant(enchant, level, true)
    }

    /** Adiciona um enchant inseguro direto no ItemStack. */
    fun addUnsafeEnchant(enchant: Enchantment, level: Int): ItemFactory = apply {
        item.addUnsafeEnchantment(enchant, level)
    }

    /** Define o lore via vararg de Strings. */
    fun setLore(vararg lore: String): ItemFactory = apply {
        itemMeta.lore = lore.map { it.replace("&", "§") }
    }

    /** Define o lore via lista de Strings. */
    fun setLore(lore: List<String>): ItemFactory = apply {
        itemMeta.lore = lore.map { it.replace("&", "§") }
    }

    /**
     * Define o dono da skull por nome de usuário, usando XSeries ProfileInstruction.
     */
    fun setSkullOwner(owner: String): ItemFactory = apply {
        XSkull.of(item)
            .profile(Profileable.username(owner))
            .apply()
        itemMeta = item.itemMeta as SkullMeta
    }

    /**
     * Define textura customizada via valor Base64, usando XSeries ProfileInstruction.
     */
    fun setSkullValue(base64: String): ItemFactory = apply {
        XSkull.of(item)
            .profile(Profileable.of(ProfileInputType.BASE64, base64))
            .apply()
        itemMeta = item.itemMeta as SkullMeta
    }

    /** Altera o Material do ItemStack. */
    fun setMaterial(material: Material): ItemFactory = apply {
        item.type = material
        itemMeta = item.itemMeta!!
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
        (itemMeta as? LeatherArmorMeta)?.color = color
    }

    /** Constrói e retorna o ItemStack. */
    fun build(): ItemStack {
        item.itemMeta = itemMeta
        return item
    }
}