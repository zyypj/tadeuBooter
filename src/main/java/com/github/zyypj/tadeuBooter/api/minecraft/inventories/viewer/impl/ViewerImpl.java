package com.github.zyypj.tadeuBooter.api.minecraft.inventories.viewer.impl;

import com.github.zyypj.tadeuBooter.api.minecraft.inventories.editor.InventoryEditor;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.editor.impl.InventoryEditorImpl;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.inventory.CustomInventory;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.viewer.Viewer;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.viewer.configuration.ViewerConfiguration;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.viewer.property.ViewerPropertyMap;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
@Data
public abstract class ViewerImpl implements Viewer {

    private final String name;
    private final CustomInventory customInventory;

    private final ViewerConfiguration configuration;
    private final ViewerPropertyMap propertyMap = new ViewerPropertyMap();

    @Setter(AccessLevel.PRIVATE) protected InventoryEditor editor;

    @Override
    public Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(
                null,
                configuration.inventorySize(),
                ChatColor.translateAlternateColorCodes('&', configuration.titleInventory())
        );
        this.editor = new InventoryEditorImpl(inventory);
        return inventory;
    }

    @Override
    public void resetConfigurations() {
        this.configuration.titleInventory(this.customInventory.getTitle());
        this.configuration.inventorySize(this.customInventory.getSize());
        this.configuration.backInventory(null);
    }

}
