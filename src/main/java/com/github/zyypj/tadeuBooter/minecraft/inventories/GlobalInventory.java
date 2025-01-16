package com.github.zyypj.tadeuBooter.minecraft.inventories;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Getter
public class GlobalInventory {

    private static final Map<String, GlobalInventory> activeInventories = new ConcurrentHashMap<>();

    private final String id;
    private String title;
    private final int size;
    private Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> clickActions = new HashMap<>();
    private double updateInterval = -1;

    public GlobalInventory(String id, String title, int size) {
        this.id = id;
        this.title = translateColors(title);
        this.size = size;
        this.inventory = Bukkit.createInventory(null, size, this.title);
        activeInventories.put(id, this);
    }

    public GlobalInventory setTitle(String title) {
        this.title = translateColors(title);
        this.inventory = Bukkit.createInventory(null, size, this.title);
        return this;
    }

    public GlobalInventory setUpdateInterval(double seconds) {
        if (seconds < 0.1) {
            throw new IllegalArgumentException("Update interval must be at least 0.1 seconds.");
        }
        this.updateInterval = seconds;
        return this;
    }

    public GlobalInventory setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> action) {
        this.inventory.setItem(slot, item);
        if (action != null) {
            clickActions.put(slot, action);
        }
        return this;
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public void startGlobalAutoUpdate() {
        if (updateInterval > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (activeInventories.isEmpty()) {
                        cancel();
                        return;
                    }

                    activeInventories.values().forEach(GlobalInventory::refreshInventory);
                }
            }.runTaskTimer(Bukkit.getPluginManager().getPlugins()[0], 0L, (long) (updateInterval * 20));
        }
    }

    private void refreshInventory() {
        Inventory newInventory = Bukkit.createInventory(null, size, title);

        for (int i = 0; i < size; i++) {
            ItemStack currentItem = inventory.getItem(i);
            if (currentItem != null && currentItem.getType() != Material.AIR) {
                ItemStack newItem = currentItem.clone();
                newInventory.setItem(i, newItem);
            }
        }

        inventory.setContents(newInventory.getContents());
    }

    public void handleInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().equals(inventory)) {
            event.setCancelled(true);
            Consumer<InventoryClickEvent> action = clickActions.get(event.getRawSlot());
            if (action != null) {
                action.accept(event);
            }
        }
    }

    private static String translateColors(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}