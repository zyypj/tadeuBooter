package com.github.zyypj.tadeuBooter.api.minecraft.inventories.inventory.impl;

import com.github.zyypj.tadeuBooter.api.minecraft.inventories.controller.InventoryController;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.controller.ViewerController;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.editor.InventoryEditor;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.inventory.CustomInventory;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.inventory.configuration.InventoryConfiguration;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.manager.InventoryManager;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.viewer.Viewer;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
@Data
public abstract class CustomInventoryImpl implements CustomInventory {

    private final String id;
    private final String title;
    private final int size;

    private final InventoryConfiguration configuration;

    @Override
    public final <T extends CustomInventory> T init() {
        InventoryController inventoryController = InventoryManager.getInventoryController();
        return (T) inventoryController.registerInventory(this);
    }

    @Override
    public final <T extends InventoryConfiguration> void configuration(@NotNull Consumer<T> consumer) {
        T configuration = this.getConfiguration();
        consumer.accept(configuration);
    }

    @Override
    public final void openInventory(@NotNull Player player) {
        this.openInventory(player, null);
    }

    @Override
    public void updateInventory(@NotNull Player player) {
        ViewerController viewerController = InventoryManager.getViewerController();
        viewerController.findViewer(player.getName()).ifPresent(viewer -> {
            if (viewer.getCustomInventory().getClass().isInstance(this)) {
                InventoryEditor editor = viewer.getEditor();
                update(viewer, editor);
                editor.updateAllItemStacks();

                player.updateInventory();
            }
        });
    }

    @Override
    public final <T extends InventoryConfiguration> @NotNull T getConfiguration() {
        return (T) configuration;
    }

    protected final <T extends Viewer> void defaultOpenInventory(Player player, Viewer viewer, Consumer<T> viewerConsumer) {
        if (viewerConsumer != null) {
            viewerConsumer.accept((T) viewer);
        }

        viewer.resetConfigurations();
        this.configureViewer(viewer);

        Inventory inventory = viewer.createInventory();
        InventoryEditor editor = viewer.getEditor();

        configureInventory(viewer, editor);
        update(viewer, editor);

        player.openInventory(inventory);

        ViewerController viewerController = InventoryManager.getViewerController();
        viewerController.registerViewer(viewer);
    }

    protected void configureViewer(@NotNull Viewer viewer) {
        // empty method
    }

    protected void configureInventory(@NotNull Viewer viewer, @NotNull InventoryEditor editor) {
        // empty method
    }

    protected void update(@NotNull Viewer viewer, @NotNull InventoryEditor editor) {
        // empty method
    }

}
