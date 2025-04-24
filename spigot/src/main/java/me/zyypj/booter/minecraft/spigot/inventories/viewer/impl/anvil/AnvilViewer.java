package me.zyypj.booter.minecraft.spigot.inventories.viewer.impl.anvil;

import lombok.Getter;
import me.zyypj.booter.minecraft.spigot.inventories.inventory.impl.anvil.AnvilInventory;
import me.zyypj.booter.minecraft.spigot.inventories.viewer.impl.ViewerImpl;
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
