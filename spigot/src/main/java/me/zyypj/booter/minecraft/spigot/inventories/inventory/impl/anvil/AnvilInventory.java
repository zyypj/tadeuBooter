package me.zyypj.booter.minecraft.spigot.inventories.inventory.impl.anvil;

import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;
import me.zyypj.booter.minecraft.spigot.inventories.editor.InventoryEditor;
import me.zyypj.booter.minecraft.spigot.inventories.inventory.configuration.impl.InventoryConfigurationImpl;
import me.zyypj.booter.minecraft.spigot.inventories.inventory.impl.CustomInventoryImpl;
import me.zyypj.booter.minecraft.spigot.inventories.item.InventoryItem;
import me.zyypj.booter.minecraft.spigot.inventories.manager.InventoryManager;
import me.zyypj.booter.minecraft.spigot.inventories.viewer.Viewer;
import me.zyypj.booter.minecraft.spigot.inventories.viewer.impl.anvil.AnvilViewer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class AnvilInventory extends CustomInventoryImpl {

    private final ItemStack defaultItem;
    @Setter private Consumer<String> onInput;
    @Setter private Consumer<Player> onCancel;
    @Setter private Consumer<Player> onConfirm;
    @Setter private Consumer<Player> onClose;

    public AnvilInventory(String id, String title, ItemStack defaultItem) {
        super(id, title, 1, new InventoryConfigurationImpl.Anvil());
        this.defaultItem = defaultItem;
    }

    @Override
    public final <T extends Viewer> void openInventory(
            @NotNull Player player, Consumer<T> viewerConsumer) {
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

        Viewer viewer =
                InventoryManager.getViewerController().findViewer(player.getName()).orElse(null);
        if (viewer != null) {
            InventoryEditor editor = viewer.getEditor();
            editor.setItem(0, InventoryItem.of(defaultItem));
            player.updateInventory();
        }
    }
}
