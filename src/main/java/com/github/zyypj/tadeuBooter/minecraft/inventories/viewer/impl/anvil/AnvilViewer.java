package com.github.zyypj.tadeuBooter.minecraft.inventories.viewer.impl.anvil;

import com.github.zyypj.tadeuBooter.minecraft.inventories.inventory.impl.anvil.AnvilInventory;
import com.github.zyypj.tadeuBooter.minecraft.inventories.viewer.impl.ViewerImpl;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

@Getter
public final class AnvilViewer extends ViewerImpl {

    public AnvilViewer(String name, AnvilInventory customInventory) {
        super(name, customInventory, null);
    }

    @Override
    public Inventory createInventory() {
        return Bukkit.createInventory(null, InventoryType.ANVIL, "Renomeie o item");
    }
}