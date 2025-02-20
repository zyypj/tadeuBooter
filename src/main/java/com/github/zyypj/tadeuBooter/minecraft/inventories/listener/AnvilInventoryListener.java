package com.github.zyypj.tadeuBooter.minecraft.inventories.listener;

import com.github.zyypj.tadeuBooter.minecraft.inventories.inventory.impl.anvil.AnvilInventory;
import com.github.zyypj.tadeuBooter.minecraft.inventories.manager.InventoryManager;
import com.github.zyypj.tadeuBooter.minecraft.inventories.viewer.Viewer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;


public class AnvilInventoryListener implements Listener {

    @EventHandler
    private void onAnvilClick(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL) return;
        if (event.getSlot() != 2) return;

        Player player = (Player) event.getWhoClicked();
        Viewer viewer = InventoryManager.getViewerController().findViewer(player.getName()).orElse(null);
        assert viewer != null;
        if (!(viewer.getCustomInventory() instanceof AnvilInventory)) return;

        AnvilInventory anvilInventory = viewer.getCustomInventory();
        ItemStack resultItem = event.getCurrentItem();
        if (resultItem == null || !resultItem.hasItemMeta()) return;

        event.setCancelled(true);
        player.closeInventory();

        anvilInventory.handleInput(player, resultItem.getItemMeta().getDisplayName());
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Viewer viewer = InventoryManager.getViewerController().findViewer(player.getName()).orElse(null);
        if (viewer == null || !(viewer.getCustomInventory() instanceof AnvilInventory)) return;

        AnvilInventory anvilInventory = viewer.getCustomInventory();
        anvilInventory.handleClose(player);
    }
}