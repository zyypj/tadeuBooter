package me.zyypj.booter.minecraft.spigot.inventories.viewer.configuration;

/** @author Henry Fábio Github: https://github.com/HenryFabio */
public interface ViewerConfiguration {

    String titleInventory();

    ViewerConfiguration titleInventory(String title);

    int inventorySize();

    ViewerConfiguration inventorySize(int size);

    String backInventory();

    ViewerConfiguration backInventory(String inventory);
}
