package me.zyypj.booter.minecraft.spigot.inventories.event.impl;

import lombok.Getter;
import me.zyypj.booter.minecraft.spigot.inventories.event.CustomInventoryEvent;
import me.zyypj.booter.minecraft.spigot.inventories.viewer.Viewer;
import org.bukkit.event.inventory.InventoryCloseEvent;

/** @author Henry Fábio Github: https://github.com/HenryFabio */
@Getter
public final class CustomInventoryCloseEvent extends CustomInventoryEvent {

    private final InventoryCloseEvent primaryEvent;

    public CustomInventoryCloseEvent(Viewer viewer, InventoryCloseEvent primaryEvent) {
        super(viewer);
        this.primaryEvent = primaryEvent;
    }
}
