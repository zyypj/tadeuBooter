package com.github.zyypj.tadeuBooter.minecraft.inventories.event.impl;

import com.github.zyypj.tadeuBooter.minecraft.inventories.event.CustomInventoryEvent;
import com.github.zyypj.tadeuBooter.minecraft.inventories.viewer.Viewer;
import lombok.Getter;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
@Getter
public final class CustomInventoryCloseEvent extends CustomInventoryEvent {

    private final InventoryCloseEvent primaryEvent;

    public CustomInventoryCloseEvent(Viewer viewer, InventoryCloseEvent primaryEvent) {
        super(viewer);
        this.primaryEvent = primaryEvent;
    }

}
