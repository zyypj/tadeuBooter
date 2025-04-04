package com.github.zyypj.tadeuBooter.api.minecraft.inventories.listener;

import com.github.zyypj.tadeuBooter.api.minecraft.inventories.controller.ViewerController;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.editor.InventoryEditor;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.event.impl.CustomInventoryClickEvent;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.event.impl.CustomInventoryCloseEvent;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.item.callback.ItemCallback;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.manager.InventoryManager;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.viewer.Viewer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.function.Consumer;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
public final class CustomInventoryListener implements Listener {

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        ViewerController viewerController = InventoryManager.getViewerController();

        Viewer viewer = viewerController.unregisterViewer(player.getName());
        if (viewer != null) {
            CustomInventoryCloseEvent closeEvent = new CustomInventoryCloseEvent(viewer, event);
            Bukkit.getPluginManager().callEvent(closeEvent);
        }
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        Player player = (Player) event.getWhoClicked();

        ViewerController viewerController = InventoryManager.getViewerController();
        viewerController.findViewer(player.getName()).ifPresent(viewer -> {
            event.setCancelled(true);

            CustomInventoryClickEvent clickEvent = new CustomInventoryClickEvent(viewer, event);
            Bukkit.getPluginManager().callEvent(clickEvent);

            if (clickedInventory.getType().equals(InventoryType.PLAYER)) return;

            InventoryEditor editor = viewer.getEditor();
            ItemCallback itemCallback = editor.getItemCallback(event.getRawSlot());
            if (itemCallback == null) return;

            Consumer<CustomInventoryClickEvent> callback = itemCallback.getClickCallback(event.getClick());
            if (callback != null) {
                callback.accept(clickEvent);
            }
        });
    }

}
