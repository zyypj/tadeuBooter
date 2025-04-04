package com.github.zyypj.tadeuBooter.api.minecraft.inventories.item.supplier;

import com.github.zyypj.tadeuBooter.api.minecraft.inventories.item.InventoryItem;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
@FunctionalInterface
public interface InventoryItemSupplier {

    InventoryItem get();

}
