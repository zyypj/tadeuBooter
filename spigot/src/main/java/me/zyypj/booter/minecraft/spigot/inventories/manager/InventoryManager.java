package me.zyypj.booter.minecraft.spigot.inventories.manager;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.zyypj.booter.minecraft.spigot.inventories.controller.InventoryController;
import me.zyypj.booter.minecraft.spigot.inventories.controller.ViewerController;
import me.zyypj.booter.minecraft.spigot.inventories.listener.AnvilInventoryListener;
import me.zyypj.booter.minecraft.spigot.inventories.listener.CustomInventoryListener;
import me.zyypj.booter.minecraft.spigot.inventories.schedule.InventoryUpdateRunnable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

/** @author Henry Fábio Github: https://github.com/HenryFabio */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InventoryManager {

    @Getter private static final InventoryManager instance = new InventoryManager();
    @Getter private static boolean enabled;

    private final InventoryController inventoryController = new InventoryController();
    private final ViewerController viewerController = new ViewerController();

    public static void enable(Plugin plugin) {
        if (InventoryManager.isEnabled()) return;

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new CustomInventoryListener(), plugin);
        pluginManager.registerEvents(new AnvilInventoryListener(), plugin);

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimerAsynchronously(plugin, new InventoryUpdateRunnable(), 0, 1);

        InventoryManager.enabled = true;
    }

    public static InventoryController getInventoryController() {
        return instance.inventoryController;
    }

    public static ViewerController getViewerController() {
        return instance.viewerController;
    }
}
