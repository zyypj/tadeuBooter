package com.github.zyypj.tadeuBooter.minecraft.packet;

import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

public abstract class CustomPacket {
    public abstract void write(PacketContainer container);

    public abstract void read(PacketContainer container);

    public abstract void handle(Player player);
}