package me.zyypj.booter.minecraft.spigot.inventories.schedule;

import java.util.Map;
import me.zyypj.booter.minecraft.spigot.inventories.controller.InventoryController;
import me.zyypj.booter.minecraft.spigot.inventories.controller.ViewerController;
import me.zyypj.booter.minecraft.spigot.inventories.inventory.CustomInventory;
import me.zyypj.booter.minecraft.spigot.inventories.inventory.configuration.InventoryConfiguration;
import me.zyypj.booter.minecraft.spigot.inventories.inventory.impl.global.GlobalInventory;
import me.zyypj.booter.minecraft.spigot.inventories.manager.InventoryManager;
import me.zyypj.booter.minecraft.spigot.inventories.viewer.Viewer;
import me.zyypj.booter.minecraft.spigot.inventories.viewer.impl.global.GlobalViewer;

/** @author Henry Fábio Github: https://github.com/HenryFabio */
public final class InventoryUpdateRunnable implements Runnable {

    private double second = 0;

    @Override
    public void run() {
        InventoryController inventoryController = InventoryManager.getInventoryController();
        ViewerController viewerController = InventoryManager.getViewerController();

        Map<String, CustomInventory> inventoryMap = inventoryController.getInventoryMap();
        for (CustomInventory customInventory : inventoryMap.values()) {
            if (!(customInventory instanceof GlobalInventory)) continue;
            updateGlobalInventory(customInventory);
        }

        Map<String, Viewer> viewerMap = viewerController.getViewerMap();
        for (Viewer viewer : viewerMap.values()) {
            if (viewer instanceof GlobalViewer) continue;
            updateViewerInventory(viewer);
        }

        incrementSecond();
    }

    private void updateGlobalInventory(CustomInventory customInventory) {
        if (canUpdate(customInventory)) {
            GlobalInventory globalInventory = (GlobalInventory) customInventory;
            globalInventory.updateInventory();
        }
    }

    private void updateViewerInventory(Viewer viewer) {
        CustomInventory customInventory = viewer.getCustomInventory();
        if (canUpdate(customInventory)) {
            customInventory.updateInventory(viewer.getPlayer());
        }
    }

    private boolean canUpdate(CustomInventory customInventory) {
        InventoryConfiguration configuration = customInventory.getConfiguration();

        double secondUpdate = configuration.secondUpdate();
        return secondUpdate > 0 && (this.second / secondUpdate) % 1 == 0;
    }

    private void incrementSecond() {
        second += 0.05;
        if (second >= 60) second = 0;
    }
}
