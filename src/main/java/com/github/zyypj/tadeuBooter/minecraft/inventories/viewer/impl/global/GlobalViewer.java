package com.github.zyypj.tadeuBooter.minecraft.inventories.viewer.impl.global;

import com.github.zyypj.tadeuBooter.minecraft.inventories.editor.InventoryEditor;
import com.github.zyypj.tadeuBooter.minecraft.inventories.inventory.CustomInventory;
import com.github.zyypj.tadeuBooter.minecraft.inventories.inventory.impl.global.GlobalInventory;
import com.github.zyypj.tadeuBooter.minecraft.inventories.viewer.configuration.impl.ViewerConfigurationImpl;
import com.github.zyypj.tadeuBooter.minecraft.inventories.viewer.impl.ViewerImpl;
import org.bukkit.inventory.Inventory;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
public final class GlobalViewer extends ViewerImpl {

    public GlobalViewer(String name, CustomInventory customInventory) {
        super(name, customInventory, new ViewerConfigurationImpl.Global());
    }

    @Override
    public Inventory createInventory() {
        GlobalInventory customInventory = (GlobalInventory) this.getCustomInventory();

        InventoryEditor inventoryEditor = customInventory.getInventoryEditor();
        this.editor = inventoryEditor;

        return inventoryEditor.getInventory();
    }

}
