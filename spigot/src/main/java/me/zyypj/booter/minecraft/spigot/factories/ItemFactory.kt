package me.zyypj.booter.minecraft.spigot.factories;

import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import com.cryptomorin.xseries.profiles.objects.ProfileInputType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

/*
 * Fluent factory for constructing and customizing ItemStacks,
 * including skull handling via XSeries for cross-version support.
 */
class ItemFactory(material: Material) {
    /* Base ItemStack constructed from provided Material */
    var item: ItemStack = ItemStack(material)
        private set
    /* Metadata for modifications before building final ItemStack */
    var itemMeta: ItemMeta = item.itemMeta!!
        private set

    /*
     * @deprecated Use constructor(material: Material) instead
     * Constructor by type ID is deprecated in favor of Material reference.
     */
    @Deprecated("Use constructor(material: Material) instead")
    constructor(typeId: Int) : this(
        Material.getMaterial(typeId)
            ?: throw IllegalArgumentException("Material id $typeId not found")
    )

    /* Sets the display name, translating '&' color codes to '§' */
    fun setName(name: String): ItemFactory = apply {
        itemMeta.displayName = name.replace("&", "§")
    }

    /* Adds a glow effect without visible enchantment */
    fun setGlow(glow: Boolean): ItemFactory = apply {
        if (glow) {
            itemMeta.addEnchant(Enchantment.LUCK, 1, true)
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
    }

    /* Adds a safe enchantment to the item meta */
    fun addEnchant(enchant: Enchantment, level: Int): ItemFactory = apply {
        itemMeta.addEnchant(enchant, level, true)
    }

    /* Adds an unsafe enchantment directly to the ItemStack */
    fun addUnsafeEnchant(enchant: Enchantment, level: Int): ItemFactory = apply {
        item.addUnsafeEnchantment(enchant, level)
    }

    /* Sets lore from vararg strings, translating color codes */
    fun setLore(vararg lore: String): ItemFactory = apply {
        itemMeta.lore = lore.map { it.replace("&", "§") }
    }

    /* Sets lore from a List of Strings */
    fun setLore(lore: List<String>): ItemFactory = apply {
        itemMeta.lore = lore.map { it.replace("&", "§") }
    }

    /*
     * Sets skull owner by username using XSeries,
     * updating itemMeta to SkullMeta after applying profile.
     */
    fun setSkullOwner(owner: String): ItemFactory = apply {
        XSkull.of(item)
            .profile(Profileable.username(owner))
            .apply()
        itemMeta = item.itemMeta as SkullMeta
    }

    /*
     * Sets custom skull texture via Base64 value using XSeries,
     * then updates itemMeta to SkullMeta.
     */
    fun setSkullValue(base64: String): ItemFactory = apply {
        XSkull.of(item)
            .profile(Profileable.of(ProfileInputType.BASE64, base64))
            .apply()
        itemMeta = item.itemMeta as SkullMeta
    }

    /* Changes the Material of the item, refreshing itemMeta */
    fun setMaterial(material: Material): ItemFactory = apply {
        item.type = material
        itemMeta = item.itemMeta!!
    }

    /* Sets raw durability/data as a short value */
    fun setData(data: Short): ItemFactory = apply {
        item.durability = data
    }

    /* Sets durability (damage) level from an Int */
    fun setDurability(durability: Int): ItemFactory = apply {
        item.durability = durability.toShort()
    }

    /* Adds one or more item flags */
    fun addItemFlags(vararg flags: ItemFlag): ItemFactory = apply {
        itemMeta.addItemFlags(*flags)
    }

    /* Sets leather armor color if meta is LeatherArmorMeta */
    fun setLeatherColor(color: Color): ItemFactory = apply {
        (itemMeta as? LeatherArmorMeta)?.color = color
    }

    /*
     * Applies all meta changes and returns the final ItemStack.
     */
    fun build(): ItemStack {
        item.itemMeta = itemMeta
        return item
    }
}