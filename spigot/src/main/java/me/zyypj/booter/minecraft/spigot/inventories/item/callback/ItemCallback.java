package me.zyypj.booter.minecraft.spigot.inventories.item.callback;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import lombok.Data;
import me.zyypj.booter.minecraft.spigot.inventories.event.impl.CustomInventoryClickEvent;
import me.zyypj.booter.minecraft.spigot.inventories.item.callback.update.ItemUpdateCallback;
import org.bukkit.event.inventory.ClickType;

/** @author Henry Fábio Github: https://github.com/HenryFabio */
@Data
public final class ItemCallback {

    private final Map<ClickType, Consumer<CustomInventoryClickEvent>> callbackMap = new HashMap<>();
    private ItemUpdateCallback updateCallback;

    public void callback(ClickType clickType, Consumer<CustomInventoryClickEvent> eventConsumer) {
        this.callbackMap.put(clickType, eventConsumer);
    }

    public Consumer<CustomInventoryClickEvent> getClickCallback(ClickType clickType) {
        return this.callbackMap.getOrDefault(clickType, this.callbackMap.get(null));
    }
}
