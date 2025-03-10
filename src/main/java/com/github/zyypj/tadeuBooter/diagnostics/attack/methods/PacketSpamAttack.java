package com.github.zyypj.tadeuBooter.diagnostics.attack.methods;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.github.zyypj.tadeuBooter.diagnostics.attack.AttackMethod;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PacketSpamAttack extends AttackMethod {
    private final JavaPlugin plugin;
    private boolean running = true;
    private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    public PacketSpamAttack(String target, int duration, int intensity, JavaPlugin plugin) {
        super(target, duration, intensity);
        this.plugin = plugin;
    }

    @Override
    public void start() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (!running) return;

            for (Player player : Bukkit.getOnlinePlayers()) {
                for (int i = 0; i < intensity; i++) {
                    sendRandomPacket(player);
                }
            }
        }, 0L, 1L);
    }

    @Override
    public void stop() {
        running = false;
    }

    private void sendRandomPacket(Player player) {
        try {
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Client.CHAT);
            packet.getStrings().write(0, "Pacote Aleatório");
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}