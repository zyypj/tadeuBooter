package me.zyypj.booter.minecraft.spigot.util.serialization;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class InventorySerializer {

    private final Gson gson;

    public InventorySerializer(Gson gson) {
        this.gson = gson;
    }

    public String serializeInventory(InventoryType type, Inventory inventory) {
        ItemStack[] contents = inventory.getContents();
        Map<String, Object> serializationMap = new HashMap<>();
        Map<Integer, Map<String, Object>> items = new HashMap<>();

        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];
            if (stack == null || stack.getType() == Material.AIR) continue;

            Map<String, Object> itemData = new HashMap<>();
            itemData.put("type", stack.getType().name());
            itemData.put("amount", stack.getAmount());
            itemData.put("durability", stack.getDurability());

            if (stack.hasItemMeta()) {
                itemData.put(
                        "meta",
                        gson.toJson(stack.getItemMeta(), new TypeToken<ItemMeta>() {}.getType()));
            }

            if (stack.getData() != null) {
                itemData.put("data", gson.toJson(stack.getData(), MaterialData.class));
            }

            if (!stack.getEnchantments().isEmpty()) {
                itemData.put("enchantments", gson.toJson(stack.getEnchantments()));
            }

            items.put(i, itemData);
        }

        serializationMap.put("type", type.name());
        serializationMap.put("content", items);
        return gson.toJson(serializationMap);
    }

    public ItemStack[] deserializeInventoryContent(String serializedInventory) {
        Map<String, Object> map = gson.fromJson(serializedInventory, Map.class);
        InventoryType type = InventoryType.valueOf((String) map.get("type"));
        Map<String, Object> contentMap = (Map<String, Object>) map.get("content");
        ItemStack[] contents = new ItemStack[type.getDefaultSize()];

        for (Entry<String, Object> entry : contentMap.entrySet()) {
            int index = Integer.parseInt(entry.getKey());
            Map<String, Object> itemData = (Map<String, Object>) entry.getValue();

            Material material = Material.valueOf((String) itemData.get("type"));
            int amount = ((Number) itemData.get("amount")).intValue();
            short durability = ((Number) itemData.get("durability")).shortValue();
            ItemStack stack = new ItemStack(material, amount, durability);

            if (itemData.containsKey("meta")) {
                ItemMeta meta = gson.fromJson((String) itemData.get("meta"), ItemMeta.class);
                stack.setItemMeta(meta);
            }

            if (itemData.containsKey("data")) {
                MaterialData data =
                        gson.fromJson((String) itemData.get("data"), MaterialData.class);
                stack.setData(data);
            }

            if (itemData.containsKey("enchantments")) {
                Map<Enchantment, Integer> enchants =
                        gson.fromJson((String) itemData.get("enchantments"), Map.class);
                stack.addUnsafeEnchantments(enchants);
            }

            contents[index] = stack;
        }

        return contents;
    }

    public Inventory deserializeInventory(String serializedInventory) {
        Map<String, Object> map = gson.fromJson(serializedInventory, Map.class);
        InventoryType type = InventoryType.valueOf((String) map.get("type"));
        Map<String, Object> contentMap = (Map<String, Object>) map.get("content");
        Inventory inventory = Bukkit.createInventory(null, type);

        for (Entry<String, Object> entry : contentMap.entrySet()) {
            int index = Integer.parseInt(entry.getKey());
            Map<String, Object> itemData = (Map<String, Object>) entry.getValue();

            Material material = Material.valueOf((String) itemData.get("type"));
            int amount = ((Number) itemData.get("amount")).intValue();
            short durability = ((Number) itemData.get("durability")).shortValue();
            ItemStack stack = new ItemStack(material, amount, durability);

            if (itemData.containsKey("meta")) {
                ItemMeta meta = gson.fromJson((String) itemData.get("meta"), ItemMeta.class);
                stack.setItemMeta(meta);
            }

            if (itemData.containsKey("data")) {
                MaterialData data =
                        gson.fromJson((String) itemData.get("data"), MaterialData.class);
                stack.setData(data);
            }

            if (itemData.containsKey("enchantments")) {
                Map<Enchantment, Integer> enchants =
                        gson.fromJson((String) itemData.get("enchantments"), Map.class);
                stack.addUnsafeEnchantments(enchants);
            }

            inventory.setItem(index, stack);
        }

        return inventory;
    }
}
