package com.github.zyypj.tadeuBooter.minecraft.inventories.viewer;

import com.github.zyypj.tadeuBooter.minecraft.inventories.editor.InventoryEditor;
import com.github.zyypj.tadeuBooter.minecraft.inventories.inventory.CustomInventory;
import com.github.zyypj.tadeuBooter.minecraft.inventories.viewer.configuration.ViewerConfiguration;
import com.github.zyypj.tadeuBooter.minecraft.inventories.viewer.property.ViewerPropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
public interface Viewer {

    String getName();

    default Player getPlayer() {
        return Bukkit.getPlayer(this.getName());
    }

    <T extends CustomInventory> T getCustomInventory();

    <T extends ViewerConfiguration> T getConfiguration();

    ViewerPropertyMap getPropertyMap();

    InventoryEditor getEditor();

    default Inventory getInventory() {
        return this.getEditor().getInventory();
    }

    Inventory createInventory();

    void resetConfigurations();

}
