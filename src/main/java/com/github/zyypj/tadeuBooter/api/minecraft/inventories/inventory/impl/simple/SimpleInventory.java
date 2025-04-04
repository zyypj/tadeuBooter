package com.github.zyypj.tadeuBooter.api.minecraft.inventories.inventory.impl.simple;

import com.github.zyypj.tadeuBooter.api.minecraft.inventories.inventory.configuration.impl.InventoryConfigurationImpl;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.inventory.impl.CustomInventoryImpl;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.viewer.Viewer;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.viewer.impl.simple.SimpleViewer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
public abstract class SimpleInventory extends CustomInventoryImpl {

    public SimpleInventory(String id, String title, int size) {
        super(id, title, size, new InventoryConfigurationImpl.Simple());
    }

    @Override
    public final <T extends Viewer> void openInventory(@NotNull Player player, Consumer<T> viewerConsumer) {
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
