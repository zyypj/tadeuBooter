package com.github.zyypj.tadeuBooter.api.minecraft.inventories.inventory.impl.anvil;

import com.github.zyypj.tadeuBooter.api.minecraft.inventories.editor.InventoryEditor;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.inventory.configuration.impl.InventoryConfigurationImpl;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.inventory.impl.CustomInventoryImpl;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.item.InventoryItem;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.manager.InventoryManager;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.viewer.Viewer;
import com.github.zyypj.tadeuBooter.api.minecraft.inventories.viewer.impl.anvil.AnvilViewer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Getter
public abstract class AnvilInventory extends CustomInventoryImpl {

    private final ItemStack defaultItem;
    @Setter
    private Consumer<String> onInput;
    @Setter
    private Consumer<Player> onCancel;
    @Setter
    private Consumer<Player> onConfirm;
    @Setter
    private Consumer<Player> onClose;

    public AnvilInventory(String id, String title, ItemStack defaultItem) {
        super(id, title, 1, new InventoryConfigurationImpl.Anvil());
        this.defaultItem = defaultItem;
    }

    @Override
    public final <T extends Viewer> void openInventory(@NotNull Player player, Consumer<T> viewerConsumer) {
        Viewer viewer = new AnvilViewer(player.getName(), this);
        defaultOpenInventory(player, viewer, viewerConsumer);
    }

    @Override
    protected void configureInventory(@NotNull Viewer viewer, @NotNull InventoryEditor editor) {
        editor.setItem(0, InventoryItem.of(defaultItem));
    }

    public void handleInput(Player player, String input) {
        if (onInput != null) {
            onInput.accept(input);
        }
    }

    public void handleConfirm(Player player) {
        if (onConfirm != null) {
            onConfirm.accept(player);
        }
    }

    public void handleCancel(Player player) {
        if (onCancel != null) {
            onCancel.accept(player);
        }
    }

    public void handleClose(Player player) {
        if (onClose != null) {
            onClose.accept(player);
        }
    }

    public void setDefaultItemName(String name) {
        ItemMeta meta = defaultItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            defaultItem.setItemMeta(meta);
        }
    }

    public void updateItemName(Player player, String newName) {
        ItemMeta meta = defaultItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(newName);
            defaultItem.setItemMeta(meta);
        }

        Viewer viewer = InventoryManager.getViewerController().findViewer(player.getName()).orElse(null);
        if (viewer != null) {
            InventoryEditor editor = viewer.getEditor();
            editor.setItem(0, InventoryItem.of(defaultItem));
            player.updateInventory();
        }
    }
}