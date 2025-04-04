package com.github.zyypj.tadeuBooter.api.minecraft.scoreboard;

import com.github.zyypj.tadeuBooter.api.minecraft.scoreboard.factory.ScoreBoardBase;
import com.github.zyypj.tadeuBooter.api.minecraft.scoreboard.factory.ScoreBoardReflection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.util.Objects;

/**
 * Implementação padrão do ScoreBoard usando Strings como linhas.
 */
public class ScoreBoard extends ScoreBoardBase<String> {

    private static final MethodHandle MESSAGE_FROM_STRING;
    private static final Object EMPTY_MESSAGE;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            Class<?> craftChatMessageClass = ScoreBoardReflection.obcClass("util.CraftChatMessage");
            MESSAGE_FROM_STRING = lookup.unreflect(craftChatMessageClass.getMethod("fromString", String.class));
            EMPTY_MESSAGE = Array.get(MESSAGE_FROM_STRING.invoke(""), 0);
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }

    /**
     * Cria uma nova ScoreBoard para o jogador informado.
     *
     * @param player o jogador no qual a ScoreBoard será exibida
     */
    public ScoreBoard(Player player) {
        super(player);
    }

    /**
     * Atualiza o título do ScoreBoard, verificando o tamanho da string em versões inferiores a 1.13.
     *
     * @param title o novo título
     */
    @Override
    public void updateTitle(String title) {
        Objects.requireNonNull(title, "title");
        // Em versões abaixo de 1.13, o título não pode ter mais de 32 caracteres.
        if (!ScoreBoardBase.VersionType.V1_13.isHigherOrEqual(VersionType.V1_13) && title.length() > 32) {
            throw new IllegalArgumentException("Title is longer than 32 chars");
        }
        super.updateTitle(title);
    }

    /**
     * Atualiza as linhas do ScoreBoard, validando o tamanho em versões inferiores a 1.13.
     *
     * @param lines as novas linhas
     */
    @Override
    public void updateLines(String... lines) {
        Objects.requireNonNull(lines, "lines");
        if (!VersionType.V1_13.isHigherOrEqual(VersionType.V1_13)) {
            int lineCount = 0;
            for (String s : lines) {
                if (s != null && s.length() > 30) {
                    throw new IllegalArgumentException("Line " + lineCount + " is longer than 30 chars");
                }
                lineCount++;
            }
        }
        super.updateLines(lines);
    }

    /**
     * Converte e divide uma linha para envio, tratando prefixo e sufixo para evitar quebra de códigos de cor.
     *
     * @param score o índice da linha invertido (do placar)
     * @throws Throwable se ocorrer erro durante a conversão ou envio
     */
    @Override
    protected void sendLineChange(int score) throws Throwable {
        // Define o tamanho máximo com base na versão: em versões antigas, há limite de 16, caso contrário 1024.
        int maxLength = hasLinesMaxLength() ? 16 : 1024;
        String line = getLineByScore(score);
        String prefix;
        String suffix = "";

        if (line == null || line.isEmpty()) {
            prefix = COLOR_CODES[score] + ChatColor.RESET;
        } else if (line.length() <= maxLength) {
            prefix = line;
        } else {
            // Evita cortar no meio de um código de cor
            int index = (line.charAt(maxLength - 1) == ChatColor.COLOR_CHAR) ? (maxLength - 1) : maxLength;
            prefix = line.substring(0, index);
            String suffixTmp = line.substring(index);
            ChatColor chatColor = null;
            if (suffixTmp.length() >= 2 && suffixTmp.charAt(0) == ChatColor.COLOR_CHAR) {
                chatColor = ChatColor.getByChar(suffixTmp.charAt(1));
            }
            String color = ChatColor.getLastColors(prefix);
            boolean addColor = chatColor == null || chatColor.isFormat();
            suffix = (addColor ? (color.isEmpty() ? ChatColor.RESET.toString() : color) : "") + suffixTmp;
        }

        if (prefix.length() > maxLength || suffix.length() > maxLength) {
            // Caso ultrapasse o limite, corta para evitar crash ou kick do cliente
            prefix = prefix.substring(0, Math.min(maxLength, prefix.length()));
            suffix = suffix.substring(0, Math.min(maxLength, suffix.length()));
        }

        sendTeamPacket(score, TeamMode.UPDATE, prefix, suffix);
    }

    /**
     * Converte uma string em um componente Minecraft utilizando CraftChatMessage.
     *
     * @param line a string a ser convertida
     * @return o componente Minecraft correspondente
     * @throws Throwable se ocorrer erro na conversão
     */
    @Override
    protected Object toMinecraftComponent(String line) throws Throwable {
        if (line == null || line.isEmpty()) {
            return EMPTY_MESSAGE;
        }
        return Array.get(MESSAGE_FROM_STRING.invoke(line), 0);
    }

    /**
     * Serializa a linha (já sendo uma String, retorna-a inalterada).
     *
     * @param value a string a ser serializada
     * @return a própria string
     */
    @Override
    protected String serializeLine(String value) {
        return value;
    }

    /**
     * Retorna uma linha vazia (representada por uma string vazia).
     *
     * @return uma string vazia
     */
    @Override
    protected String emptyLine() {
        return "";
    }

    /**
     * Indica se há limite de caracteres para prefixo/sufixo,
     * retornando true para versões inferiores a 1.13 (1.12 ou abaixo).
     *
     * @return true se há limite; false caso contrário.
     */
    protected boolean hasLinesMaxLength() {
        return !ScoreBoardBase.VersionType.V1_13.isHigherOrEqual(VersionType.V1_13);
    }
}