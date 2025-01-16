package com.github.zyypj.booter.minecraft.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

public class PacketManager {
    private final ProtocolManager protocolManager;

    public PacketManager() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void addPacketListener(PacketAdapter adapter) {
        protocolManager.addPacketListener(adapter);
    }

    public void removePacketListener(PacketAdapter adapter) {
        protocolManager.removePacketListener(adapter);
    }

    public PacketContainer createPacket(PacketType type) {
        return protocolManager.createPacket(type);
    }

    public void sendPacket(Player player, PacketContainer packet) {
        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}