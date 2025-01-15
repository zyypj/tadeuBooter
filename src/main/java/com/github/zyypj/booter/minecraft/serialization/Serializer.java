package com.github.zyypj.booter.minecraft.serialization;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.zyypj.booter.Constants;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

/**
 * Classe para serializar e desserializar inventários do Bukkit, incluindo tipos de inventário
 * e os conteúdos dos itens armazenados.
 */
public class Serializer {
    private final Gson gson;

    public Serializer() {
        this.gson = Constants.GSON;
    }

    /**
     * Serializa um inventário Bukkit em uma string JSON.
     *
     * @param inventoryType O tipo de inventário.
     * @param inventory      O inventário a ser serializado.
     * @return Uma string JSON representando o inventário.
     */
    public String serializeInventory(InventoryType inventoryType, Inventory inventory) {
        ItemStack[] content = inventory.getContents();
        Map<String, Object> serializationMap = new HashMap<>();
        Map<Integer, String> items = new HashMap<>();

        for (int index = 0; index < content.length; index++) {
            ItemStack stack = content[index];
            if (stack != null && stack.getType() != Material.AIR) {
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("type", this.gson.toJson(stack.getType(), Material.class));
                itemData.put("amount", stack.getAmount());
                itemData.put("durability", stack.getDurability());

                if (stack.hasItemMeta()) {
                    itemData.put("meta", this.gson.toJson(stack.getItemMeta(), new TypeToken<ItemMeta>() {}.getType()));
                }

                if (stack.getData() != null) {
                    itemData.put("data", this.gson.toJson(stack.getData(), MaterialData.class));
                }

                if (!stack.getEnchantments().isEmpty()) {
                    itemData.put("enchantments", this.gson.toJson(stack.getEnchantments()));
                }

                items.put(index, this.gson.toJson(itemData));
            }
        }

        serializationMap.put("type", this.gson.toJson(inventoryType));
        serializationMap.put("content", items);
        return this.gson.toJson(serializationMap);
    }

    /**
     * Desserializa o conteúdo de um inventário a partir de uma string JSON.
     *
     * @param serializedInventory A string JSON representando o inventário.
     * @return Um array de ItemStack representando os conteúdos do inventário.
     */
    public ItemStack[] deserializeInventoryContent(String serializedInventory) {
        Map<String, Object> deserializationMap = this.gson.fromJson(serializedInventory, HashMap.class);
        InventoryType inventoryType = this.gson.fromJson((String) deserializationMap.get("type"), InventoryType.class);
        Map<String, String> itemsMap = this.gson.fromJson(this.gson.toJson(deserializationMap.get("content")), HashMap.class);
        ItemStack[] contents = new ItemStack[inventoryType.getDefaultSize()];

        for (Entry<String, String> entry : itemsMap.entrySet()) {
            int index = Integer.parseInt(entry.getKey());
            Map<String, Object> itemData = this.gson.fromJson(entry.getValue(), HashMap.class);

            Material type = this.gson.fromJson((String) itemData.get("type"), Material.class);
            int amount = ((Double) itemData.get("amount")).intValue();
            short durability = ((Double) itemData.get("durability")).shortValue();

            ItemStack stack = new ItemStack(type, amount, durability);

            if (itemData.containsKey("meta")) {
                ItemMeta meta = this.gson.fromJson((String) itemData.get("meta"), ItemMeta.class);
                stack.setItemMeta(meta);
            }

            if (itemData.containsKey("data")) {
                MaterialData data = this.gson.fromJson((String) itemData.get("data"), MaterialData.class);
                stack.setData(data);
            }

            if (itemData.containsKey("enchantments")) {
                Map<Enchantment, Integer> enchantments = this.gson.fromJson((String) itemData.get("enchantments"), HashMap.class);
                stack.addUnsafeEnchantments(enchantments);
            }

            contents[index] = stack;
        }

        return contents;
    }

    /**
     * Desserializa um inventário completo a partir de uma string JSON.
     *
     * @param serializedInventory A string JSON representando o inventário.
     * @return O inventário desserializado.
     */
    public Inventory deserializeInventory(String serializedInventory) {
        Map<String, Object> deserializationMap = this.gson.fromJson(serializedInventory, HashMap.class);
        InventoryType inventoryType = this.gson.fromJson((String) deserializationMap.get("type"), InventoryType.class);
        Map<String, String> itemsMap = this.gson.fromJson(this.gson.toJson(deserializationMap.get("content")), HashMap.class);
        Inventory inventory = Bukkit.createInventory(null, inventoryType);

        for (Entry<String, String> entry : itemsMap.entrySet()) {
            int index = Integer.parseInt(entry.getKey());
            Map<String, Object> itemData = this.gson.fromJson(entry.getValue(), HashMap.class);

            Material type = this.gson.fromJson((String) itemData.get("type"), Material.class);
            int amount = ((Double) itemData.get("amount")).intValue();
            short durability = ((Double) itemData.get("durability")).shortValue();

            ItemStack stack = new ItemStack(type, amount, durability);

            if (itemData.containsKey("meta")) {
                ItemMeta meta = this.gson.fromJson((String) itemData.get("meta"), ItemMeta.class);
                stack.setItemMeta(meta);
            }

            if (itemData.containsKey("data")) {
                MaterialData data = this.gson.fromJson((String) itemData.get("data"), MaterialData.class);
                stack.setData(data);
            }

            if (itemData.containsKey("enchantments")) {
                Map<Enchantment, Integer> enchantments = this.gson.fromJson((String) itemData.get("enchantments"), HashMap.class);
                stack.addUnsafeEnchantments(enchantments);
            }

            inventory.setItem(index, stack);
        }

        return inventory;
    }
}