package me.zyypj.booter.minecraft.spigot.inventories.item;

import java.util.function.Consumer;
import lombok.Data;
import me.zyypj.booter.minecraft.spigot.inventories.event.impl.CustomInventoryClickEvent;
import me.zyypj.booter.minecraft.spigot.inventories.item.callback.ItemCallback;
import me.zyypj.booter.minecraft.spigot.inventories.item.callback.update.ItemUpdateCallback;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/** @author Henry Fábio Github: https://github.com/HenryFabio */
@Data(staticConstructor = "of")
public final class InventoryItem {

    private final ItemStack itemStack;
    private final ItemCallback itemCallback = new ItemCallback();

    public InventoryItem callback(
            ClickType clickType, Consumer<CustomInventoryClickEvent> eventConsumer) {
        this.itemCallback.callback(clickType, eventConsumer);
        return this;
    }

    public InventoryItem defaultCallback(Consumer<CustomInventoryClickEvent> eventConsumer) {
        return this.callback(null, eventConsumer);
    }

    public InventoryItem updateCallback(ItemUpdateCallback updateCallback) {
        this.itemCallback.setUpdateCallback(updateCallback);
        return this;
    }
}
