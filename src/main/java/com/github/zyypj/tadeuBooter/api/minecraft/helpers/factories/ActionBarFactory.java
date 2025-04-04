package com.github.zyypj.tadeuBooter.api.minecraft.helpers.factories;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.entity.Player;

import java.util.Collection;


public class ActionBarFactory {

    private final ProtocolManager protocolManager;

    public ActionBarFactory() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    /**
     * Envia uma mensagem de Action Bar para um único jogador.
     *
     * @param content A mensagem a ser enviada (suporta cores com &).
     * @param viewer  O jogador que receberá a mensagem.
     */
    public void sendActionBar(String content, Player viewer) {
        try {
            PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.CHAT);
            packetContainer.getChatComponents().write(0, WrappedChatComponent.fromText(content.replace("&", "§")));
            packetContainer.getBytes().write(0, (byte) 2); // Indica que a mensagem é um ActionBar na versão 1.8.8

            protocolManager.sendServerPacket(viewer, packetContainer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Envia uma mensagem de Action Bar para múltiplos jogadores.
     *
     * @param content A mensagem a ser enviada (suporta cores com &).
     * @param viewers Os jogadores que receberão a mensagem.
     */
    public void sendActionBar(String content, Player... viewers) {
        for (Player viewer : viewers) {
            sendActionBar(content, viewer);
        }
    }

    /**
     * Envia uma mensagem de Action Bar para uma coleção de jogadores.
     *
     * @param content A mensagem a ser enviada (suporta cores com &).
     * @param viewers A coleção de jogadores que receberão a mensagem.
     */
    public void sendActionBar(String content, Collection<? extends Player> viewers) {
        for (Player viewer : viewers) {
            sendActionBar(content, viewer);
        }
    }

}