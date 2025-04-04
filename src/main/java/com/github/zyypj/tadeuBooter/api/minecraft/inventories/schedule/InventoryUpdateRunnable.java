package com.github.zyypj.tadeuBooter.api.minecraft.inventories.schedule;

import com.github.zyypj.tadeuBooter.api.minecraft.inventories.controller.InventoryController;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.controller.ViewerController;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.inventory.CustomInventory;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.inventory.configuration.InventoryConfiguration;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.inventory.impl.global.GlobalInventory;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.manager.InventoryManager;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.viewer.Viewer;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.viewer.impl.global.GlobalViewer;

import java.util.Map;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
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