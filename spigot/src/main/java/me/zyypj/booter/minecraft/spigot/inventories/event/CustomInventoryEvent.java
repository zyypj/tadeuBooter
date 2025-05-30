package me.zyypj.booter.minecraft.spigot.inventories.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.zyypj.booter.minecraft.spigot.inventories.inventory.CustomInventory;
import me.zyypj.booter.minecraft.spigot.inventories.viewer.Viewer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

/** @author Henry Fábio Github: https://github.com/HenryFabio */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class CustomInventoryEvent extends Event {

    @Getter private static final HandlerList handlerList = new HandlerList();

    private final CustomInventory customInventory;
    private final Inventory inventory;

    private final Player player;
    private final Viewer viewer;

    protected CustomInventoryEvent(Viewer viewer) {
        this.viewer = viewer;
        this.customInventory = viewer.getCustomInventory();
        this.inventory = viewer.getInventory();
        this.player = viewer.getPlayer();
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
