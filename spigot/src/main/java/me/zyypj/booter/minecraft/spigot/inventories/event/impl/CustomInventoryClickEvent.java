package me.zyypj.booter.minecraft.spigot.inventories.event.impl;

import lombok.Getter;
import me.zyypj.booter.minecraft.spigot.inventories.editor.InventoryEditor;
import me.zyypj.booter.minecraft.spigot.inventories.event.CustomInventoryEvent;
import me.zyypj.booter.minecraft.spigot.inventories.inventory.CustomInventory;
import me.zyypj.booter.minecraft.spigot.inventories.viewer.Viewer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/** @author Henry Fábio Github: https://github.com/HenryFabio */
@Getter
public final class CustomInventoryClickEvent extends CustomInventoryEvent implements Cancellable {

    private final InventoryClickEvent primaryEvent;

    private final Inventory clickedInventory;
    private final ItemStack itemStack;
    private final ClickType clickType;
    private final int slot;

    public CustomInventoryClickEvent(Viewer viewer, InventoryClickEvent primaryEvent) {
        super(viewer);
        this.primaryEvent = primaryEvent;
        this.clickedInventory = primaryEvent.getClickedInventory();
        this.itemStack = primaryEvent.getCurrentItem();
        this.clickType = primaryEvent.getClick();
        this.slot = primaryEvent.getRawSlot();
    }

    public void updateItemStack() {
        InventoryEditor inventoryEditor = this.getViewer().getEditor();
        inventoryEditor.updateItemStack(slot);
    }

    public void updateInventory() {
        CustomInventory customInventory = this.getCustomInventory();
        customInventory.updateInventory(this.getPlayer());
    }

    @Override
    public boolean isCancelled() {
        return this.primaryEvent.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.primaryEvent.setCancelled(cancel);
    }
}
