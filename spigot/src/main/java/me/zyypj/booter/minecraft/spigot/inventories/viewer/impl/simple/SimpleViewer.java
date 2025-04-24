package me.zyypj.booter.minecraft.spigot.inventories.viewer.impl.simple;

import me.zyypj.booter.minecraft.spigot.inventories.inventory.CustomInventory;
import me.zyypj.booter.minecraft.spigot.inventories.viewer.configuration.impl.ViewerConfigurationImpl;
import me.zyypj.booter.minecraft.spigot.inventories.viewer.impl.ViewerImpl;

/** @author Henry Fábio Github: https://github.com/HenryFabio */
public final class SimpleViewer extends ViewerImpl {

    public SimpleViewer(String name, CustomInventory customInventory) {
        super(name, customInventory, new ViewerConfigurationImpl.Simple());
    }
}
