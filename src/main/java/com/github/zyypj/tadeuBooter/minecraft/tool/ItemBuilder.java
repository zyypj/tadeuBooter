package com.github.zyypj.tadeuBooter.minecraft.tool;

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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {
    private final ItemStack item;
    private ItemMeta itemMeta;

    /**
     * Construtor para criar um ItemBuilder com um material específico.
     *
     * @param material Material do item.
     */
    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.itemMeta = item.getItemMeta();
    }

    /**
     * Define o nome exibido do item.
     *
     * @param displayName Nome a ser exibido.
     * @return Instância atual do ItemBuilder.
     */
    public ItemBuilder setDisplayName(String displayName) {
        itemMeta.setDisplayName(displayName);
        return this;
    }

    /**
     * Adiciona brilho ao item sem encantamentos visíveis.
     *
     * @return Instância atual do ItemBuilder.
     */
    public ItemBuilder setGlow(boolean glow) {
        if (glow) {
            itemMeta.addEnchant(Enchantment.LUCK, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    /**
     * Adiciona um encantamento seguro ao item.
     *
     * @param enchantment Encantamento a ser adicionado.
     * @param level       Nível do encantamento.
     * @return Instância atual do ItemBuilder.
     */
    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    /**
     * Adiciona um encantamento inseguro ao item.
     *
     * @param enchantment Encantamento a ser adicionado.
     * @param level       Nível do encantamento.
     * @return Instância atual do ItemBuilder.
     */
    public ItemBuilder addUnsafeEnchant(Enchantment enchantment, int level) {
        item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    /**
     * Define a lore (descrição) do item.
     *
     * @param lore Linhas de lore a serem definidas.
     * @return Instância atual do ItemBuilder.
     */
    public ItemBuilder setLore(String... lore) {
        itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    /**
     * Define a lore (descrição) do item.
     *
     * @param lore Lista de linhas de lore a serem definidas.
     * @return Instância atual do ItemBuilder.
     */
    public ItemBuilder setLore(List<String> lore) {
        itemMeta.setLore(lore);
        return this;
    }

    /**
     * Define o dono de uma cabeça (suporta SKULL_ITEM e PLAYER_HEAD).
     *
     * @param owner Nome do jogador dono da cabeça.
     * @return Instância atual do ItemBuilder.
     */
    public ItemBuilder setSkullOwner(String owner) {
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

    /**
     * Define o valor de textura para uma cabeça personalizada (suporta SKULL_ITEM e PLAYER_HEAD).
     *
     * @param value Valor da textura em base64.
     * @return Instância atual do ItemBuilder.
     */
    public ItemBuilder setSkullValue(String value) {
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

    /**
     * Define o material do item.
     *
     * @param material Novo material do item.
     * @return Instância atual do ItemBuilder.
     */
    public ItemBuilder setMaterial(Material material) {
        item.setType(material);
        return this;
    }

    /**
     * Define os dados/durabilidade do item.
     *
     * @param data Dados do item.
     * @return Instância atual do ItemBuilder.
     */
    public ItemBuilder setData(short data) {
        item.setDurability(data);
        return this;
    }

    /**
     * Define a durabilidade do item.
     *
     * @param durability Durabilidade a ser definida.
     * @return Instância atual do ItemBuilder.
     */
    public ItemBuilder setDurability(int durability) {
        item.setDurability((short) durability);
        return this;
    }

    /**
     * Adiciona flags ao item.
     *
     * @param flags Flags a serem adicionadas.
     * @return Instância atual do ItemBuilder.
     */
    public ItemBuilder addItemFlags(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);
        return this;
    }

    /**
     * Define a cor de itens de couro (apenas para armaduras de couro).
     *
     * @param color Cor a ser definida.
     * @return Instância atual do ItemBuilder.
     */
    public ItemBuilder setLeatherColor(Color color) {
        if (itemMeta instanceof LeatherArmorMeta) {
            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) itemMeta;
            leatherMeta.setColor(color);
        }
        return this;
    }

    /**
     * Constrói o ItemStack com as propriedades definidas.
     *
     * @return ItemStack configurado.
     */
    public ItemStack build() {
        item.setItemMeta(itemMeta);
        return item;
    }
}