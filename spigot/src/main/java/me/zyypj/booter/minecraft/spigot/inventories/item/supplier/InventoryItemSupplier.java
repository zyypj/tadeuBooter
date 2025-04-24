package me.zyypj.booter.minecraft.spigot.inventories.item.supplier;

import me.zyypj.booter.minecraft.spigot.inventories.item.InventoryItem;

/** @author Henry Fábio Github: https://github.com/HenryFabio */
@FunctionalInterface
public interface InventoryItemSupplier {

    InventoryItem get();
}
