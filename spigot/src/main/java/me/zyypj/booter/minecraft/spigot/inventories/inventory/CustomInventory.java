package me.zyypj.booter.minecraft.spigot.inventories.inventory;

import java.util.function.Consumer;
import me.zyypj.booter.minecraft.spigot.inventories.inventory.configuration.InventoryConfiguration;
import me.zyypj.booter.minecraft.spigot.inventories.viewer.Viewer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** @author Henry Fábio Github: https://github.com/HenryFabio */
public interface CustomInventory {

    @NotNull
    String getId();

    @NotNull
    String getTitle();

    int getSize();

    @NotNull
    <T extends InventoryConfiguration> T getConfiguration();

    <T extends InventoryConfiguration> void configuration(@NotNull Consumer<T> consumer);

    <T extends CustomInventory> T init();

    <T extends Viewer> void openInventory(
            @NotNull Player player, @Nullable Consumer<T> viewerConsumer);

    void openInventory(@NotNull Player player);

    void updateInventory(@NotNull Player player);
}
