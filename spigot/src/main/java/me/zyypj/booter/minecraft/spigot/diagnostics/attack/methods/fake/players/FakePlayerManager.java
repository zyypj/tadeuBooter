package me.zyypj.booter.minecraft.spigot.diagnostics.attack.methods.fake.players;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FakePlayerManager {
    private static final Map<String, UUID> fakePlayers = new HashMap<>();
    private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    /**
     * Cria um Fake Player e o adiciona ao servidor.
     *
     * @param name Nome do Fake Player.
     * @param location Local onde o Fake Player será criado.
     */
    public static void createFakePlayer(String name, Location location) {
        UUID uuid = UUID.randomUUID();
        fakePlayers.put(name, uuid);

        GameProfile profile = new GameProfile(uuid, name);
        profile.getProperties().put("textures", new Property("textures", getDefaultSkin()));

        PacketContainer spawnPacket =
                protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        spawnPacket.getGameProfiles().write(0, WrappedGameProfile.fromHandle(profile));

        PacketContainer addPlayerPacket =
                protocolManager.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        addPlayerPacket.getIntegers().write(0, uuid.hashCode());
        addPlayerPacket.getDoubles().write(0, location.getX());
        addPlayerPacket.getDoubles().write(1, location.getY());
        addPlayerPacket.getDoubles().write(2, location.getZ());

        sendPacketToAll(spawnPacket);
        sendPacketToAll(addPlayerPacket);

        startFakeMovement(name, location);
    }

    /**
     * Remove um Fake Player do servidor.
     *
     * @param name Nome do Fake Player.
     */
    public static void removeFakePlayer(String name) {
        if (!fakePlayers.containsKey(name)) return;

        UUID uuid = fakePlayers.get(name);
        PacketContainer removePacket =
                protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        removePacket.getIntegerArrays().write(0, new int[] {uuid.hashCode()});

        sendPacketToAll(removePacket);
        fakePlayers.remove(name);
    }

    /**
     * Simula a movimentação de um Fake Player.
     *
     * @param name Nome do Fake Player.
     * @param startLocation Posição inicial.
     */
    private static void startFakeMovement(String name, Location startLocation) {
        new BukkitRunnable() {
            Location loc = startLocation.clone();
            Random random = new Random();

            @Override
            public void run() {
                if (!fakePlayers.containsKey(name)) {
                    cancel();
                    return;
                }

                loc.add(random.nextDouble() * 2 - 1, 0, random.nextDouble() * 2 - 1);

                PacketContainer movePacket =
                        protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
                movePacket.getIntegers().write(0, fakePlayers.get(name).hashCode());
                movePacket.getDoubles().write(0, loc.getX());
                movePacket.getDoubles().write(1, loc.getY());
                movePacket.getDoubles().write(2, loc.getZ());

                sendPacketToAll(movePacket);
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("TadeuBooter"), 0L, 20L);
    }

    /**
     * Envia pacotes para todos os jogadores online.
     *
     * @param packet Pacote a ser enviado.
     */
    private static void sendPacketToAll(PacketContainer packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                protocolManager.sendServerPacket(player, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Obtém uma skin padrão para os Fake Players.
     *
     * @return String da textura Base64.
     */
    private static String getDefaultSkin() {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk1NmQ1MmNhMTgyZWM4YzlmYjQ0ZDgyMTgzMzMzZDlhZWRjMzc4MjM1MTlhOGVmMjJmZTA5ODZhZDMwODQwMSJ9fX0=";
    }
}
