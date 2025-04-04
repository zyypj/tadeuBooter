package com.github.zyypj.tadeuBooter.api.minecraft.diagnostics.attack.methods;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.github.zyypj.tadeuBooter.api.minecraft.diagnostics.attack.AttackMethod;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class PingFloodAttack extends AttackMethod {
    private final JavaPlugin plugin;
    private boolean running = true;
    private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    private final Random random = new Random();

    public PingFloodAttack(String target, int duration, int intensity, JavaPlugin plugin) {
        super(target, duration, intensity);
        this.plugin = plugin;
    }

    @Override
    public void start() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (!running) return;

            for (Player player : Bukkit.getOnlinePlayers()) {
                for (int i = 0; i < intensity; i++) {
                    sendPingPacket(player);
                }
            }
        }, 0L, 1L);
    }

    @Override
    public void stop() {
        running = false;
    }

    /**
     * Envia um pacote de KeepAlive para simular um Ping Flood.
     *
     * @param player O jogador que receberá os pacotes.
     */
    private void sendPingPacket(Player player) {
        try {
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Client.KEEP_ALIVE);
            packet.getIntegers().write(0, random.nextInt());
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}