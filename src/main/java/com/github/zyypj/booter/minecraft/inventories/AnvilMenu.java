package com.github.zyypj.booter.minecraft.inventories;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.ContainerAnvil;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class AnvilMenu {

    private Player player;
    private Map<AnvilSlot, ItemStack> items = new HashMap<>();
    private Inventory inv;

    public AnvilMenu(Player player) {
        this.player = player;
    }

    /**
     * Define um item em um slot do menu de bigorna.
     *
     * @param slot O slot onde o item será colocado.
     * @param item O item a ser colocado no slot.
     */
    public void setSlot(AnvilSlot slot, ItemStack item) {
        this.items.put(slot, item);
    }

    /**
     * Abre o menu de bigorna para o jogador.
     */
    public void open(AnvilClickEventHandler handler) {
        EntityPlayer entityPlayer = ((CraftPlayer) this.player).getHandle();
        AnvilContainer container = new AnvilContainer(entityPlayer);
        this.inv = container.getBukkitView().getTopInventory();

        items.forEach((slot, item) -> this.inv.setItem(slot.getSlot(), item));

        int containerId = entityPlayer.nextContainerCounter();
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerId, "minecraft:anvil", new ChatMessage("Repairing")));
        entityPlayer.activeContainer = container;
        entityPlayer.activeContainer.windowId = containerId;
        entityPlayer.activeContainer.addSlotListener(entityPlayer);

        Bukkit.getPluginManager().registerEvents(new AnvilMenuListener(this, handler), Bukkit.getPluginManager().getPlugins()[0]);
    }

    /**
     * Destrói o menu, limpando os recursos associados.
     */
    public void destroy() {
        this.player = null;
        this.items = null;
        this.inv = null;
    }

    public Player getPlayer() {
        return this.player;
    }

    /**
     * Enumeração dos slots da bigorna.
     */
    public enum AnvilSlot {
        INPUT_LEFT(0),
        INPUT_RIGHT(1),
        OUTPUT(2);

        private final int slot;

        AnvilSlot(int slot) {
            this.slot = slot;
        }

        public int getSlot() {
            return this.slot;
        }

        public static AnvilSlot bySlot(int slot) {
            for (AnvilSlot anvilSlot : values()) {
                if (anvilSlot.getSlot() == slot) {
                    return anvilSlot;
                }
            }
            return null;
        }
    }

    /**
     * Interface para lidar com eventos de clique no menu.
     */
    public interface AnvilClickEventHandler {
        void onAnvilClick(AnvilClickEvent event);
    }

    /**
     * Classe representando o evento de clique na bigorna.
     */
    public class AnvilClickEvent {
        private final AnvilSlot slot;
        private final String name;
        private boolean close = true;
        private boolean destroy = true;

        public AnvilClickEvent(AnvilSlot slot, String name) {
            this.slot = slot;
            this.name = name;
        }

        public AnvilSlot getSlot() {
            return this.slot;
        }

        public String getName() {
            return this.name;
        }

        public boolean getWillClose() {
            return this.close;
        }

        public void setWillClose(boolean close) {
            this.close = close;
        }

        public boolean getWillDestroy() {
            return this.destroy;
        }

        public void setWillDestroy(boolean destroy) {
            this.destroy = destroy;
        }
    }

    /**
     * Container customizado para simular a bigorna.
     */
    private class AnvilContainer extends ContainerAnvil {
        public AnvilContainer(EntityHuman entity) {
            super(entity.inventory, entity.world, new BlockPosition(0, 0, 0), entity);
        }

        @Override
        public boolean a(EntityHuman entityhuman) {
            return true;
        }
    }

    /**
     * Listener temporário para gerenciar eventos do menu de bigorna.
     */
    private static class AnvilMenuListener implements org.bukkit.event.Listener {
        private final AnvilMenu menu;
        private final AnvilClickEventHandler handler;

        public AnvilMenuListener(AnvilMenu menu, AnvilClickEventHandler handler) {
            this.menu = menu;
            this.handler = handler;
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (event.getWhoClicked() instanceof Player && event.getInventory().equals(menu.inv)) {
                event.setCancelled(true);
                ItemStack item = event.getCurrentItem();
                int slot = event.getRawSlot();
                String name = "";

                if (item != null && item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta.hasDisplayName()) {
                        name = meta.getDisplayName();
                    }
                }

                AnvilClickEvent clickEvent = menu.new AnvilClickEvent(AnvilSlot.bySlot(slot), name);
                handler.onAnvilClick(clickEvent);

                if (clickEvent.getWillClose()) {
                    event.getWhoClicked().closeInventory();
                }

                if (clickEvent.getWillDestroy()) {
                    menu.destroy();
                }
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            if (event.getPlayer().equals(menu.getPlayer())) {
                menu.destroy();
                org.bukkit.event.HandlerList.unregisterAll(this);
            }
        }

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            if (event.getPlayer().equals(menu.getPlayer())) {
                menu.destroy();
                org.bukkit.event.HandlerList.unregisterAll(this);
            }
        }
    }
}