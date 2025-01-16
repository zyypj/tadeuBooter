package com.github.zyypj.tadeuBooter.minecraft.tool;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ActionBarBuilder {

    private final ProtocolManager protocolManager;

    public ActionBarBuilder() {
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
            PacketContainer packetContainer = new PacketContainer(Server.SET_ACTION_BAR_TEXT);
            packetContainer.getChatComponents().write(0, WrappedChatComponent.fromText(content.replace("&", "§")));
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