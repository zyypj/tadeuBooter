package com.github.zyypj.tadeuBooter.minecraft.tool;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Sistema para gerenciamento de encantamentos customizados, incluindo criação, aplicação e remoção.
 */
public class CustomEnchantManager {

    /**
     * Adiciona um encantamento customizado a um item.
     *
     * @param item         O item que receberá o encantamento.
     * @param enchantName  Nome do encantamento customizado.
     * @param enchantLevel Nível do encantamento customizado.
     * @return O item atualizado.
     */
    public static ItemStack addCustomEnchant(ItemStack item, String enchantName, int enchantLevel) {
        if (item == null || item.getType() == Material.AIR) {
            throw new IllegalArgumentException("O item fornecido é inválido.");
        }

        ItemBuilder itemBuilder = new ItemBuilder(item.getType());
        List<String> lore = item.getItemMeta().getLore() != null ? item.getItemMeta().getLore() : new ArrayList<>();
        lore.add("§6" + enchantName + " §e" + enchantLevel);
        return itemBuilder.setLore(lore).build();
    }

    /**
     * Remove um encantamento customizado de um item.
     *
     * @param item        O item que terá o encantamento removido.
     * @param enchantName Nome do encantamento a ser removido.
     * @return O item atualizado.
     */
    public static ItemStack removeCustomEnchant(ItemStack item, String enchantName) {
        if (item == null || item.getType() == Material.AIR) {
            throw new IllegalArgumentException("O item fornecido é inválido.");
        }

        ItemBuilder itemBuilder = new ItemBuilder(item.getType());
        List<String> lore = item.getItemMeta().getLore() != null ? new ArrayList<>(item.getItemMeta().getLore()) : new ArrayList<>();
        lore.removeIf(line -> line.startsWith("§6" + enchantName));
        return itemBuilder.setLore(lore).build();
    }

    /**
     * Verifica se um item possui um encantamento customizado.
     *
     * @param item        O item a ser verificado.
     * @param enchantName Nome do encantamento customizado.
     * @return true se o item possuir o encantamento, false caso contrário.
     */
    public static boolean hasCustomEnchant(ItemStack item, String enchantName) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        List<String> lore = item.getItemMeta().getLore();
        return lore != null && lore.stream().anyMatch(line -> line.startsWith("§6" + enchantName));
    }

    /**
     * Obtém o nível de um encantamento customizado de um item.
     *
     * @param item        O item que contém o encantamento.
     * @param enchantName Nome do encantamento customizado.
     * @return O nível do encantamento ou -1 se não encontrado.
     */
    public static int getCustomEnchantLevel(ItemStack item, String enchantName) {
        if (item == null || item.getType() == Material.AIR) {
            return -1;
        }

        List<String> lore = item.getItemMeta().getLore();
        if (lore != null) {
            for (String line : lore) {
                if (line.startsWith("§6" + enchantName)) {
                    String[] parts = line.split(" ");
                    return Integer.parseInt(parts[parts.length - 1]);
                }
            }
        }
        return -1;
    }

    /**
     * Aplica múltiplos encantamentos customizados a um item.
     *
     * @param item     O item que receberá os encantamentos.
     * @param enchants Mapeamento de nomes de encantamentos para seus níveis.
     * @return O item atualizado.
     */
    public static ItemStack applyMultipleCustomEnchants(ItemStack item, List<CustomEnchant> enchants) {
        if (item == null || item.getType() == Material.AIR) {
            throw new IllegalArgumentException("O item fornecido é inválido.");
        }

        ItemBuilder itemBuilder = new ItemBuilder(item.getType());
        List<String> lore = item.getItemMeta().getLore() != null ? item.getItemMeta().getLore() : new ArrayList<>();

        for (CustomEnchant enchant : enchants) {
            lore.add("§6" + enchant.getName() + " §e" + enchant.getLevel());
        }

        return itemBuilder.setLore(lore).build();
    }

/*
    public static void debugExample(Player player) {
        ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD)
                .setDisplayName("§bEspada Lendária")
                .build();

        sword = addCustomEnchant(sword, "Explosão", 3);
        sword = addCustomEnchant(sword, "Veneno", 2);

        player.getInventory().addItem(sword);
        player.sendMessage("§aEspada com encantamentos customizados adicionada ao seu inventário!");
    }
*/

    /**
     * Classe auxiliar para representar encantamentos customizados.
     */
    @Getter
    public static class CustomEnchant {
        private final String name;
        private final int level;

        public CustomEnchant(String name, int level) {
            this.name = name;
            this.level = level;
        }
    }
}