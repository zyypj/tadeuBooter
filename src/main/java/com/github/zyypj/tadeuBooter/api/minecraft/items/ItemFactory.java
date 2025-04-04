package com.github.zyypj.tadeuBooter.api.minecraft.items;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemFactory {

    private final ItemStack item;
    private ItemMeta itemMeta;

    public ItemFactory(Material material) {
        this.item = new ItemStack(material);
        this.itemMeta = item.getItemMeta();
    }

    @Deprecated
    public ItemFactory(int id) {
        this.item = new ItemStack(id, 1);
        this.itemMeta = this.item.getItemMeta();
    }

    public ItemFactory setName(String name) {
        itemMeta.setDisplayName(name.replace("&", "§"));
        return this;
    }

    public ItemFactory setGlow(boolean glow) {
        if (glow) {
            itemMeta.addEnchant(Enchantment.LUCK, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemFactory addEnchant(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemFactory addUnsafeEnchant(Enchantment enchantment, int level) {
        item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemFactory setLore(String... lore) {
        itemMeta.setLore(Arrays.stream(lore).map(line -> line.replace("&", "§")).collect(Collectors.toList()));
        return this;
    }

    public ItemFactory setLore(List<String> lore) {
        itemMeta.setLore(lore.stream().map(line -> line.replace("&", "§")).collect(Collectors.toList()));
        return this;
    }

    public ItemFactory setSkullOwner(String owner) {
        if (item.getType() == Material.SKULL_ITEM || item.getType().name().equals("PLAYER_HEAD")) {
            SkullMeta skullMeta = (SkullMeta) itemMeta;
            if (item.getType() == Material.SKULL_ITEM) {
                skullMeta.setOwner(owner);
            } else {
                try {
                    Method method = skullMeta.getClass().getMethod("setOwningPlayer", org.bukkit.OfflinePlayer.class);
                    method.invoke(skullMeta, Bukkit.getOfflinePlayer(owner));
                } catch (Exception e) {
                    skullMeta.setOwner(owner);
                }
            }
            this.itemMeta = skullMeta;
        }
        return this;
    }

    public ItemFactory setSkullValue(String value) {
        if ((item.getType() == Material.SKULL_ITEM && item.getDurability() == 3)
                || item.getType().name().equals("PLAYER_HEAD")) {
            SkullMeta skullMeta = (SkullMeta) itemMeta;
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", value));
            try {
                Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, profile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            this.itemMeta = skullMeta;
        }
        return this;
    }

    public ItemFactory setMaterial(Material material) {
        item.setType(material);
        return this;
    }

    public ItemFactory setData(short data) {
        item.setDurability(data);
        return this;
    }

    public ItemFactory setDurability(int durability) {
        item.setDurability((short) durability);
        return this;
    }

    public ItemFactory addItemFlags(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);
        return this;
    }

    public ItemFactory setLeatherColor(Color color) {
        if (itemMeta instanceof LeatherArmorMeta) {
            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) itemMeta;
            leatherMeta.setColor(color);
        }
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(itemMeta);
        return item;
    }
}