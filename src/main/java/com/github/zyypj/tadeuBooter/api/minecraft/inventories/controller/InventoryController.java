package com.github.zyypj.tadeuBooter.api.minecraft.inventories.controller;

import com.github.zyypj.tadeuBooter.api.minecraft.inventories.inventory.CustomInventory;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
@Getter
public final class InventoryController {

    private final Map<String, CustomInventory> inventoryMap = new LinkedHashMap<>();

    public <T extends CustomInventory> T registerInventory(T inventory) {
        this.inventoryMap.put(inventory.getId(), inventory);
        return inventory;
    }

    public <T extends CustomInventory> Optional<T> findInventory(String id) {
        return Optional.ofNullable((T) this.inventoryMap.get(id));
    }

}
