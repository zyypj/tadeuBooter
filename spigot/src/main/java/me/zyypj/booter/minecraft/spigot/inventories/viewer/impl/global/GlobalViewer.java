package me.zyypj.booter.minecraft.spigot.inventories.viewer.impl.global;

import me.zyypj.booter.minecraft.spigot.inventories.editor.InventoryEditor;
import me.zyypj.booter.minecraft.spigot.inventories.inventory.CustomInventory;
import me.zyypj.booter.minecraft.spigot.inventories.inventory.impl.global.GlobalInventory;
import me.zyypj.booter.minecraft.spigot.inventories.viewer.configuration.impl.ViewerConfigurationImpl;
import me.zyypj.booter.minecraft.spigot.inventories.viewer.impl.ViewerImpl;
import org.bukkit.inventory.Inventory;

/** @author Henry Fábio Github: https://github.com/HenryFabio */
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
