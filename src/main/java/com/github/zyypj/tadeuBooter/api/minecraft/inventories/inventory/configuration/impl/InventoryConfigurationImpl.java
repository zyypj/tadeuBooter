package com.github.zyypj.tadeuBooter.api.minecraft.inventories.inventory.configuration.impl;

import com.github.zyypj.tadeuBooter.api.minecraft.inventories.inventory.configuration.InventoryConfiguration;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
@Accessors(fluent = true)
@Data
public abstract class InventoryConfigurationImpl implements InventoryConfiguration {

    private double secondUpdate;

    public static class Simple extends InventoryConfigurationImpl {
    }

    public static class Paged extends InventoryConfigurationImpl {
    }

    public static class Global extends InventoryConfigurationImpl {
    }

    public static class Anvil extends InventoryConfigurationImpl {
    }
}
