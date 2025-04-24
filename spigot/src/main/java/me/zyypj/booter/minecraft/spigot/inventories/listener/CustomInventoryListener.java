package me.zyypj.booter.minecraft.spigot.inventories.listener;

import java.util.function.Consumer;
import me.zyypj.booter.minecraft.spigot.inventories.controller.ViewerController;
import me.zyypj.booter.minecraft.spigot.inventories.editor.InventoryEditor;
import me.zyypj.booter.minecraft.spigot.inventories.event.impl.CustomInventoryClickEvent;
import me.zyypj.booter.minecraft.spigot.inventories.event.impl.CustomInventoryCloseEvent;
import me.zyypj.booter.minecraft.spigot.inventories.item.callback.ItemCallback;
import me.zyypj.booter.minecraft.spigot.inventories.manager.InventoryManager;
import me.zyypj.booter.minecraft.spigot.inventories.viewer.Viewer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/** @author Henry Fábio Github: https://github.com/HenryFabio */
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
        viewerController
                .findViewer(player.getName())
                .ifPresent(
                        viewer -> {
                            event.setCancelled(true);

                            CustomInventoryClickEvent clickEvent =
                                    new CustomInventoryClickEvent(viewer, event);
                            Bukkit.getPluginManager().callEvent(clickEvent);

                            if (clickedInventory.getType().equals(InventoryType.PLAYER)) return;

                            InventoryEditor editor = viewer.getEditor();
                            ItemCallback itemCallback = editor.getItemCallback(event.getRawSlot());
                            if (itemCallback == null) return;

                            Consumer<CustomInventoryClickEvent> callback =
                                    itemCallback.getClickCallback(event.getClick());
                            if (callback != null) {
                                callback.accept(clickEvent);
                            }
                        });
    }
}
