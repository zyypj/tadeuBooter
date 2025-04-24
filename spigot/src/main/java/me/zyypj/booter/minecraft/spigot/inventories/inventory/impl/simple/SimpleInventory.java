package me.zyypj.booter.minecraft.spigot.inventories.inventory.impl.simple;

import java.util.function.Consumer;
import me.zyypj.booter.minecraft.spigot.inventories.inventory.configuration.impl.InventoryConfigurationImpl;
import me.zyypj.booter.minecraft.spigot.inventories.inventory.impl.CustomInventoryImpl;
import me.zyypj.booter.minecraft.spigot.inventories.viewer.Viewer;
import me.zyypj.booter.minecraft.spigot.inventories.viewer.impl.simple.SimpleViewer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/** @author Henry Fábio Github: https://github.com/HenryFabio */
public abstract class SimpleInventory extends CustomInventoryImpl {

    public SimpleInventory(String id, String title, int size) {
        super(id, title, size, new InventoryConfigurationImpl.Simple());
    }

    @Override
    public final <T extends Viewer> void openInventory(
            @NotNull Player player, Consumer<T> viewerConsumer) {
        Viewer viewer = new SimpleViewer(player.getName(), this);
        defaultOpenInventory(player, viewer, viewerConsumer);
    }

    @Override
    public final void updateInventory(@NotNull Player player) {
        super.updateInventory(player);
    }

    protected void configureViewer(@NotNull SimpleViewer viewer) {
        // empty method
    }

    @Override
    protected final void configureViewer(@NotNull Viewer viewer) {
        this.configureViewer(((SimpleViewer) viewer));
    }
}
