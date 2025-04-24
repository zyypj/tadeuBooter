package me.zyypj.booter.minecraft.spigot.packets.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PacketUtils {

    /**
     * Gera um ID de entidade aleatório para uso em pacotes com ProtocolLib.
     *
     * @return um inteiro representando o ID da entidade.
     */
    public static int generateRandomEntityId() {
        return ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
    }

    /**
     * Envia um PacketContainer para um jogador específico.
     *
     * @param player o jogador para o qual o pacote será enviado.
     * @param packet o pacote a ser enviado.
     */
    public static void sendPacket(Player player, PacketContainer packet) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.sendServerPacket(player, packet);
    }

    /**
     * Envia um PacketContainer para todos os jogadores online.
     *
     * @param packet o pacote a ser enviado.
     */
    public static void broadcastPacket(PacketContainer packet) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        for (Player player : Bukkit.getOnlinePlayers()) {
            protocolManager.sendServerPacket(player, packet);
        }
    }

    /**
     * Cria um novo PacketContainer para o tipo especificado.
     *
     * @param type o tipo do pacote a ser criado.
     * @return uma nova instância de PacketContainer.
     */
    public static PacketContainer createPacket(PacketType type) {
        return new PacketContainer(type);
    }

    /**
     * Retorna uma cópia profunda (deep clone) do PacketContainer fornecido.
     *
     * @param packet o pacote a ser clonado.
     * @return uma cópia do pacote.
     */
    public static PacketContainer clonePacket(PacketContainer packet) {
        return packet.deepClone();
    }
}
