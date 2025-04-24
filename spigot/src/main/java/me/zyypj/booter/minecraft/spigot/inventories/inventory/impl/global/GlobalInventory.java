package me.zyypj.booter.minecraft.spigot.inventories.inventory.impl.global;

import java.util.function.Consumer;
import lombok.Getter;
import me.zyypj.booter.minecraft.spigot.inventories.editor.InventoryEditor;
import me.zyypj.booter.minecraft.spigot.inventories.editor.impl.InventoryEditorImpl;
import me.zyypj.booter.minecraft.spigot.inventories.inventory.configuration.impl.InventoryConfigurationImpl;
import me.zyypj.booter.minecraft.spigot.inventories.inventory.impl.CustomInventoryImpl;
import me.zyypj.booter.minecraft.spigot.inventories.viewer.Viewer;
import me.zyypj.booter.minecraft.spigot.inventories.viewer.impl.global.GlobalViewer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/** @author Henry Fábio Github: https://github.com/HenryFabio */
@Getter
public abstract class GlobalInventory extends CustomInventoryImpl {

    private InventoryEditor inventoryEditor;

    public GlobalInventory(String id, String title, int size) {
        super(id, title, size, new InventoryConfigurationImpl.Global());
    }

    @Override
    public final <T extends Viewer> void openInventory(
            @NotNull Player player, Consumer<T> viewerConsumer) {
        createInventoryEditor();

        Viewer viewer = new GlobalViewer(player.getName(), this);
        defaultOpenInventory(player, viewer, viewerConsumer);
    }

    public final void updateInventory() {
        if (this.inventoryEditor == null) return;

        this.update(this.inventoryEditor);
        this.inventoryEditor.updateAllItemStacks();
    }

    @Override
    public final void updateInventory(@NotNull Player player) {
        this.updateInventory();
    }

    @Override
    protected final void configureViewer(@NotNull Viewer viewer) {
        //
    }

    protected void configureInventory(@NotNull InventoryEditor editor) {
        // empty method
    }

    @Override
    protected final void configureInventory(
            @NotNull Viewer viewer, @NotNull InventoryEditor editor) {
        //
    }

    protected void update(@NotNull InventoryEditor editor) {
        // empty method
    }

    @Override
    protected final void update(@NotNull Viewer viewer, @NotNull InventoryEditor editor) {
        this.update(this.inventoryEditor);
    }

    private void createInventoryEditor() {
        if (this.inventoryEditor != null) return;

        Inventory inventory =
                Bukkit.createInventory(
                        null,
                        this.getSize(),
                        ChatColor.translateAlternateColorCodes('&', this.getTitle()));
        this.inventoryEditor = new InventoryEditorImpl(inventory);

        this.configureInventory(this.inventoryEditor);
        this.update(this.inventoryEditor);
    }
}
