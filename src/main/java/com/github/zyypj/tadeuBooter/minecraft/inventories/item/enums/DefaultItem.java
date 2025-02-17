package com.github.zyypj.tadeuBooter.minecraft.inventories.item.enums;

import com.github.zyypj.tadeuBooter.minecraft.inventories.controller.InventoryController;
import com.github.zyypj.tadeuBooter.minecraft.inventories.item.InventoryItem;
import com.github.zyypj.tadeuBooter.minecraft.inventories.manager.InventoryManager;
import com.github.zyypj.tadeuBooter.minecraft.inventories.viewer.Viewer;
import com.github.zyypj.tadeuBooter.minecraft.inventories.viewer.impl.paged.PagedViewer;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
@AllArgsConstructor
public enum DefaultItem {

    BACK(viewer -> {
        ItemStack itemStack = new ItemStack(Material.ARROW);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§aVoltar");
        itemStack.setItemMeta(itemMeta);

        return InventoryItem.of(itemStack)
            .defaultCallback(event -> {
                String backInventory = viewer.getConfiguration().backInventory();
                if (backInventory == null) return;

                InventoryController inventoryController = InventoryManager.getInventoryController();
                inventoryController.findInventory(backInventory)
                    .ifPresent(inventory -> inventory.openInventory(viewer.getPlayer()));
            });
    }),
    CLOSE(viewer -> {
        ItemStack itemStack = new ItemStack(Material.BARRIER);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§cFechar");
        itemStack.setItemMeta(itemMeta);

        return InventoryItem.of(itemStack)
            .defaultCallback(event -> {
                Player player = event.getViewer().getPlayer();
                player.closeInventory();
            });
    }),
    EMPTY(viewer -> {
        ItemStack itemStack = new ItemStack(Material.WEB);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§cVazio");
        itemStack.setItemMeta(itemMeta);

        return InventoryItem.of(itemStack);
    }),
    NEXT_PAGE(viewer -> {
        if (!(viewer instanceof PagedViewer))
            throw new UnsupportedOperationException("viewer isn't from paged inventory");

        PagedViewer pagedViewer = (PagedViewer) viewer;

        ItemStack itemStack = new ItemStack(Material.ARROW);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§aPróxima Página: " + (pagedViewer.getCurrentPage() + 1));
        itemStack.setItemMeta(itemMeta);

        return InventoryItem.of(itemStack)
            .defaultCallback(event -> pagedViewer.nextPage());
    }),
    PREVIOUS_PAGE(viewer -> {
        if (!(viewer instanceof PagedViewer))
            throw new UnsupportedOperationException("viewer isn't from paged inventory");

        PagedViewer pagedViewer = (PagedViewer) viewer;

        ItemStack itemStack = new ItemStack(Material.ARROW);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§aPágina Anterior: " + (pagedViewer.getCurrentPage() - 1));
        itemStack.setItemMeta(itemMeta);

        return InventoryItem.of(itemStack)
            .defaultCallback(event -> pagedViewer.previousPage());
    });

    @Setter private DefaultItemSupplier itemSupplier;

    public InventoryItem toInventoryItem(Viewer viewer) {
        return this.itemSupplier.get(viewer);
    }

    public InventoryItem toInventoryItem() {
        return this.itemSupplier.get(null);
    }

    @FunctionalInterface
    private interface DefaultItemSupplier {

        InventoryItem get(Viewer viewer);

    }

}
