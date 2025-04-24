package me.zyypj.booter.minecraft.spigot.inventories.inventory.configuration.impl;

import lombok.Data;
import lombok.experimental.Accessors;
import me.zyypj.booter.minecraft.spigot.inventories.inventory.configuration.InventoryConfiguration;

/** @author Henry Fábio Github: https://github.com/HenryFabio */
@Accessors(fluent = true)
@Data
public abstract class InventoryConfigurationImpl implements InventoryConfiguration {

    private double secondUpdate;

    public static class Simple extends InventoryConfigurationImpl {}

    public static class Paged extends InventoryConfigurationImpl {}

    public static class Global extends InventoryConfigurationImpl {}

    public static class Anvil extends InventoryConfigurationImpl {}
}
