package com.github.zyypj.tadeuBooter.minecraft.tool.scoreboard.factory;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * API de packet-based scoreboard para plugins Bukkit.
 * Pode ser usada de forma segura de maneira assíncrona, pois tudo é baseado no nível de pacotes.
 * <p>
 * O projeto está disponível no <a href="https://github.com/zyypj/tadeuBooter">GitHub</a>.
 */
public abstract class ScoreBoardBase<T> {

    private static final Map<Class<?>, Field[]> PACKETS = new HashMap<>(8);
    protected static final String[] COLOR_CODES = Arrays.stream(ChatColor.values())
            .map(Object::toString)
            .toArray(String[]::new);
    private static final VersionType VERSION_TYPE;
    private static final Class<?> CHAT_COMPONENT_CLASS;
    private static final Class<?> CHAT_FORMAT_ENUM;
    private static final Object RESET_FORMATTING;
    private static final MethodHandle PLAYER_CONNECTION;
    private static final MethodHandle SEND_PACKET;
    private static final MethodHandle PLAYER_GET_HANDLE;
    private static final MethodHandle FIXED_NUMBER_FORMAT;
    private static final ScoreBoardReflection.PacketConstructor PACKET_SB_OBJ;
    private static final ScoreBoardReflection.PacketConstructor PACKET_SB_DISPLAY_OBJ;
    private static final ScoreBoardReflection.PacketConstructor PACKET_SB_TEAM;
    private static final ScoreBoardReflection.PacketConstructor PACKET_SB_SERIALIZABLE_TEAM;
    private static final MethodHandle PACKET_SB_SET_SCORE;
    private static final MethodHandle PACKET_SB_RESET_SCORE;
    private static final boolean SCORE_OPTIONAL_COMPONENTS;
    private static final Class<?> DISPLAY_SLOT_TYPE;
    private static final Class<?> ENUM_SB_HEALTH_DISPLAY;
    private static final Class<?> ENUM_SB_ACTION;
    private static final Object BLANK_NUMBER_FORMAT;
    private static final Object SIDEBAR_DISPLAY_SLOT;
    private static final Object ENUM_SB_HEALTH_DISPLAY_INTEGER;
    private static final Object ENUM_SB_ACTION_CHANGE;
    private static final Object ENUM_SB_ACTION_REMOVE;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();

            if (ScoreBoardReflection.isRepackaged()) {
                VERSION_TYPE = VersionType.V1_17;
            } else if (ScoreBoardReflection.nmsOptionalClass(null, "ScoreboardServer$Action").isPresent()
                    || ScoreBoardReflection.nmsOptionalClass(null, "ServerScoreboard$Method").isPresent()) {
                VERSION_TYPE = VersionType.V1_13;
            } else if (ScoreBoardReflection.nmsOptionalClass(null, "IScoreboardCriteria$EnumScoreboardHealthDisplay").isPresent()
                    || ScoreBoardReflection.nmsOptionalClass(null, "ObjectiveCriteria$RenderType").isPresent()) {
                VERSION_TYPE = VersionType.V1_8;
            } else {
                VERSION_TYPE = VersionType.V1_7;
            }

            String gameProtocolPackage = "network.protocol.game";
            Class<?> craftPlayerClass = ScoreBoardReflection.obcClass("entity.CraftPlayer");
            Class<?> entityPlayerClass = ScoreBoardReflection.nmsClass("server.level", "EntityPlayer", "ServerPlayer");
            Class<?> playerConnectionClass = ScoreBoardReflection.nmsClass("server.network", "PlayerConnection", "ServerGamePacketListenerImpl");
            Class<?> packetClass = ScoreBoardReflection.nmsClass("network.protocol", "Packet");
            Class<?> packetSbObjClass = ScoreBoardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardObjective", "ClientboundSetObjectivePacket");
            Class<?> packetSbDisplayObjClass = ScoreBoardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardDisplayObjective", "ClientboundSetDisplayObjectivePacket");
            Class<?> packetSbScoreClass = ScoreBoardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardScore", "ClientboundSetScorePacket");
            Class<?> packetSbTeamClass = ScoreBoardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardTeam", "ClientboundSetPlayerTeamPacket");
            Class<?> sbTeamClass = VersionType.V1_17.isHigherOrEqual()
                    ? ScoreBoardReflection.innerClass(packetSbTeamClass, innerClass -> !innerClass.isEnum()) : null;
            Field playerConnectionField = Arrays.stream(entityPlayerClass.getFields())
                    .filter(field -> field.getType().isAssignableFrom(playerConnectionClass))
                    .findFirst().orElseThrow(NoSuchFieldException::new);
            Method sendPacketMethod = Stream.concat(
                            Arrays.stream(playerConnectionClass.getSuperclass().getMethods()),
                            Arrays.stream(playerConnectionClass.getMethods())
                    )
                    .filter(m -> m.getParameterCount() == 1 && m.getParameterTypes()[0] == packetClass)
                    .findFirst().orElseThrow(NoSuchMethodException::new);
            Optional<Class<?>> displaySlotEnum = ScoreBoardReflection.nmsOptionalClass("world.scores", "DisplaySlot");
            CHAT_COMPONENT_CLASS = ScoreBoardReflection.nmsClass("network.chat", "IChatBaseComponent","Component");
            CHAT_FORMAT_ENUM = ScoreBoardReflection.nmsClass(null, "EnumChatFormat", "ChatFormatting");
            DISPLAY_SLOT_TYPE = displaySlotEnum.orElse(int.class);
            RESET_FORMATTING = ScoreBoardReflection.enumValueOf(CHAT_FORMAT_ENUM, "RESET", 21);
            SIDEBAR_DISPLAY_SLOT = displaySlotEnum.isPresent() ? ScoreBoardReflection.enumValueOf(DISPLAY_SLOT_TYPE, "SIDEBAR", 1) : 1;
            PLAYER_GET_HANDLE = lookup.findVirtual(craftPlayerClass, "getHandle", MethodType.methodType(entityPlayerClass));
            PLAYER_CONNECTION = lookup.unreflectGetter(playerConnectionField);
            SEND_PACKET = lookup.unreflect(sendPacketMethod);
            PACKET_SB_OBJ = ScoreBoardReflection.findPacketConstructor(packetSbObjClass, lookup);
            PACKET_SB_DISPLAY_OBJ = ScoreBoardReflection.findPacketConstructor(packetSbDisplayObjClass, lookup);

            Optional<Class<?>> numberFormat = ScoreBoardReflection.nmsOptionalClass("network.chat.numbers", "NumberFormat");
            MethodHandle packetSbSetScore;
            MethodHandle packetSbResetScore = null;
            MethodHandle fixedFormatConstructor = null;
            Object blankNumberFormat = null;
            boolean scoreOptionalComponents = false;

            if (numberFormat.isPresent()) { // 1.20.3
                Class<?> blankFormatClass = ScoreBoardReflection.nmsClass("network.chat.numbers", "BlankFormat");
                Class<?> fixedFormatClass = ScoreBoardReflection.nmsClass("network.chat.numbers", "FixedFormat");
                Class<?> resetScoreClass = ScoreBoardReflection.nmsClass(gameProtocolPackage, "ClientboundResetScorePacket");
                MethodType scoreType = MethodType.methodType(void.class, String.class, String.class, int.class, CHAT_COMPONENT_CLASS, numberFormat.get());
                MethodType scoreTypeOptional = MethodType.methodType(void.class, String.class, String.class, int.class, Optional.class, Optional.class);
                MethodType removeScoreType = MethodType.methodType(void.class, String.class, String.class);
                MethodType fixedFormatType = MethodType.methodType(void.class, CHAT_COMPONENT_CLASS);
                Optional<Field> blankField = Arrays.stream(blankFormatClass.getFields()).filter(f -> f.getType() == blankFormatClass).findAny();
                // Fields are of type Optional in 1.20.5+
                Optional<MethodHandle> optionalScorePacket = ScoreBoardReflection.optionalConstructor(packetSbScoreClass, lookup, scoreTypeOptional);
                fixedFormatConstructor = lookup.findConstructor(fixedFormatClass, fixedFormatType);
                packetSbSetScore = optionalScorePacket.isPresent() ? optionalScorePacket.get()
                        : lookup.findConstructor(packetSbScoreClass, scoreType);
                scoreOptionalComponents = optionalScorePacket.isPresent();
                packetSbResetScore = lookup.findConstructor(resetScoreClass, removeScoreType);
                blankNumberFormat = blankField.isPresent() ? blankField.get().get(null) : null;
            } else if (VersionType.V1_17.isHigherOrEqual()) {
                Class<?> enumSbAction = ScoreBoardReflection.nmsClass("server", "ScoreboardServer$Action", "ServerScoreboard$Method");
                MethodType scoreType = MethodType.methodType(void.class, enumSbAction, String.class, String.class, int.class);
                packetSbSetScore = lookup.findConstructor(packetSbScoreClass, scoreType);
            } else {
                packetSbSetScore = lookup.findConstructor(packetSbScoreClass, MethodType.methodType(void.class));
            }

            PACKET_SB_SET_SCORE = packetSbSetScore;
            PACKET_SB_RESET_SCORE = packetSbResetScore;
            PACKET_SB_TEAM = ScoreBoardReflection.findPacketConstructor(packetSbTeamClass, lookup);
            PACKET_SB_SERIALIZABLE_TEAM = sbTeamClass == null ? null : ScoreBoardReflection.findPacketConstructor(sbTeamClass, lookup);
            FIXED_NUMBER_FORMAT = fixedFormatConstructor;
            BLANK_NUMBER_FORMAT = blankNumberFormat;
            SCORE_OPTIONAL_COMPONENTS = scoreOptionalComponents;

            for (Class<?> clazz : Arrays.asList(packetSbObjClass, packetSbDisplayObjClass, packetSbScoreClass, packetSbTeamClass, sbTeamClass)) {
                if (clazz == null) {
                    continue;
                }
                Field[] fields = Arrays.stream(clazz.getDeclaredFields())
                        .filter(field -> !Modifier.isStatic(field.getModifiers()))
                        .toArray(Field[]::new);
                for (Field field : fields) {
                    field.setAccessible(true);
                }
                PACKETS.put(clazz, fields);
            }

            if (VersionType.V1_8.isHigherOrEqual()) {
                String enumSbActionClass = VersionType.V1_13.isHigherOrEqual()
                        ? "ScoreboardServer$Action"
                        : "PacketPlayOutScoreboardScore$EnumScoreboardAction";
                ENUM_SB_HEALTH_DISPLAY = ScoreBoardReflection.nmsClass("world.scores.criteria", "IScoreboardCriteria$EnumScoreboardHealthDisplay", "ObjectiveCriteria$RenderType");
                ENUM_SB_ACTION = ScoreBoardReflection.nmsClass("server", enumSbActionClass, "ServerScoreboard$Method");
                ENUM_SB_HEALTH_DISPLAY_INTEGER = ScoreBoardReflection.enumValueOf(ENUM_SB_HEALTH_DISPLAY, "INTEGER", 0);
                ENUM_SB_ACTION_CHANGE = ScoreBoardReflection.enumValueOf(ENUM_SB_ACTION, "CHANGE", 0);
                ENUM_SB_ACTION_REMOVE = ScoreBoardReflection.enumValueOf(ENUM_SB_ACTION, "REMOVE", 1);
            } else {
                ENUM_SB_HEALTH_DISPLAY = null;
                ENUM_SB_ACTION = null;
                ENUM_SB_HEALTH_DISPLAY_INTEGER = null;
                ENUM_SB_ACTION_CHANGE = null;
                ENUM_SB_ACTION_REMOVE = null;
            }
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }

    @Getter
    private final Player player;
    @Getter
    private final String id;

    private final List<T> lines = new ArrayList<>();
    private final List<T> scores = new ArrayList<>();
    @Getter
    private T title = emptyLine();
    @Getter
    private boolean deleted = false;

    /**
     * Cria uma nova ScoreBoard.
     *
     * @param player o jogador no qual a ScoreBoard vai aparecer
     */
    protected ScoreBoardBase(Player player) {
        this.player = Objects.requireNonNull(player, "player");
        this.id = "fb-" + Integer.toHexString(ThreadLocalRandom.current().nextInt());

        try {
            sendObjectivePacket(ObjectiveMode.CREATE);
            sendDisplayObjectivePacket();
        } catch (Throwable t) {
            throw new RuntimeException("Unable to create scoreboard", t);
        }
    }

    /**
     * Atualiza uma linha da ScoreBoard
     *
     * @param title o novo título
     * @throws IllegalArgumentException se o título tiver mais que 12 caracteres (1.12 abaixo)
     * @throws IllegalStateException    se {@link #delete()} foi chamado antes
     */
    public void updateTitle(T title) {
        if (this.title.equals(Objects.requireNonNull(title, "title"))) {
            return;
        }

        this.title = title;

        try {
            sendObjectivePacket(ObjectiveMode.UPDATE);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to update scoreboard title", t);
        }
    }

    /**
     * Retorna as linhas da ScoreBoard
     *
     * @return as linhas
     */
    public List<T> getLines() {
        return new ArrayList<>(this.lines);
    }

    /**
     * Retorna uma linha específica da ScoreBoard
     *
     * @param line o número da linha
     * @return a linha
     * @throws IndexOutOfBoundsException se o número requisitado for maior que {@code}
     */
    public T getLine(int line) {
        checkLineNumber(line, true, false);

        return this.lines.get(line);
    }

    /**
     * Obtém como o ScoreBoard de uma linha específica é exibido. Em versões 1.20.2 ou abaixo, o valor retornado não é utilizado.
     *
     * @param line o número da linha
     * @return o texto de como a linha é exibida
     * @throws IndexOutOfBoundsException se a linha for maior que {@code size}
     */
    public Optional<T> getScore(int line) {
        checkLineNumber(line, true, false);

        return Optional.ofNullable(this.scores.get(line));
    }

    /**
     * Atualiza apenas uma linha da ScoreBoard
     *
     * @param line o número da linha
     * @param text o novo texto da linha
     * @throws IndexOutOfBoundsException se a linha for maior que {@link #size() + 1}
     */
    public synchronized void updateLine(int line, T text) {
        updateLine(line, text, null);
    }

    /**
     * Atualiza uma linha do ScoreBoard, incluindo como seu ScoreBoard é exibido.
     * O ScoreBoard será exibido apenas em versões 1.20.3 ou superiores.
     *
     * @param line o número da linha
     * @param text o novo texto da linha
     * @param scoreText o novo ScoreBoard da linha; se for nulo, não alterará o valor atual
     * @throws IndexOutOfBoundsException se a linha for maior que {@link #size() size() + 1}
     */
    public synchronized void updateLine(int line, T text, T scoreText) {
        checkLineNumber(line, false, false);

        try {
            if (line < size()) {
                this.lines.set(line, text);
                this.scores.set(line, scoreText);

                sendLineChange(getScoreByLine(line));

                if (customScoresSupported()) {
                    sendScorePacket(getScoreByLine(line), ScoreboardAction.CHANGE);
                }

                return;
            }

            List<T> newLines = new ArrayList<>(this.lines);
            List<T> newScores = new ArrayList<>(this.scores);

            if (line > size()) {
                for (int i = size(); i < line; i++) {
                    newLines.add(emptyLine());
                    newScores.add(null);
                }
            }

            newLines.add(text);
            newScores.add(scoreText);

            updateLines(newLines, newScores);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to update scoreboard lines", t);
        }
    }

    /**
     * Remove uma linha da ScoreBoard
     *
     * @param line o número da linha
     */
    public synchronized void removeLine(int line) {
        checkLineNumber(line, false, false);

        if (line >= size()) {
            return;
        }

        List<T> newLines = new ArrayList<>(this.lines);
        List<T> newScores = new ArrayList<>(this.scores);
        newLines.remove(line);
        newScores.remove(line);
        updateLines(newLines, newScores);
    }

    /**
     * Atualiza todas as linhas da ScoreBoard
     *
     * @param lines as novas linhas
     * @throws IllegalArgumentException se tiver mais de 30 caracteres (1.12 abaixo)
     * @throws IllegalStateException    se {@link #delete()} foi chamado antes
     */
    public void updateLines(T... lines) {
        updateLines(Arrays.asList(lines));
    }

    /**
     * Atualizar linhas da ScoreBoard
     *
     * @param lines as novas linhas
     * @throws IllegalArgumentException se tiver mais de 30 caracteres (1.12 abaixo)
     * @throws IllegalStateException    se {@link #delete()} foi chamado antes
     */
    public synchronized void updateLines(Collection<T> lines) {
        updateLines(lines, null);
    }

    /**
     * Atualiza as linhas e como seus ScoreBoard são exibidos no ScoreBoard.
     * Os ScoreBoard serão exibidos apenas para servidores nas versões 1.20.3 ou superiores.
     *
     * @param lines as novas linhas do ScoreBoard
     * @param scores o conjunto que define como o ScoreBoard de cada linha deve ser exibido; se for nulo, o valor padrão (em branco) será usado
     * @throws IllegalArgumentException se uma linha for maior que 30 caracteres em versões 1.12 ou inferiores
     * @throws IllegalArgumentException se o número de linhas e ScoreBoard não for o mesmo
     * @throws IllegalStateException se {@link #delete()} tiver sido chamado anteriormente
     */
    public synchronized void updateLines(Collection<T> lines, Collection<T> scores) {
        Objects.requireNonNull(lines, "lines");
        checkLineNumber(lines.size(), false, true);

        if (scores != null && scores.size() != lines.size()) {
            throw new IllegalArgumentException("The size of the scores must match the size of the board");
        }

        List<T> oldLines = new ArrayList<>(this.lines);
        this.lines.clear();
        this.lines.addAll(lines);

        List<T> oldScores = new ArrayList<>(this.scores);
        this.scores.clear();
        this.scores.addAll(scores != null ? scores : Collections.nCopies(lines.size(), null));

        int linesSize = this.lines.size();

        try {
            if (oldLines.size() != linesSize) {
                List<T> oldLinesCopy = new ArrayList<>(oldLines);

                if (oldLines.size() > linesSize) {
                    for (int i = oldLinesCopy.size(); i > linesSize; i--) {
                        sendTeamPacket(i - 1, TeamMode.REMOVE);
                        sendScorePacket(i - 1, ScoreboardAction.REMOVE);
                        oldLines.remove(0);
                    }
                } else {
                    for (int i = oldLinesCopy.size(); i < linesSize; i++) {
                        sendScorePacket(i, ScoreboardAction.CHANGE);
                        sendTeamPacket(i, TeamMode.CREATE, null, null);
                    }
                }
            }

            for (int i = 0; i < linesSize; i++) {
                if (!Objects.equals(getLineByScore(oldLines, i), getLineByScore(i))) {
                    sendLineChange(i);
                }
                if (!Objects.equals(getLineByScore(oldScores, i), getLineByScore(this.scores, i))) {
                    sendScorePacket(i, ScoreboardAction.CHANGE);
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException("Unable to update scoreboard lines", t);
        }
    }

    /**
     * Atualiza como o placar de uma linha específica é exibido no ScoreBoard. Um valor nulo resetará o texto exibido
     * para o valor padrão. Os placares serão exibidos apenas para servidores nas versões 1.20.3 ou superiores.
     *
     * @param line o número da linha
     * @param text o texto a ser exibido como o placar. Se for nulo, nenhum placar será exibido
     * @throws IllegalArgumentException se o número da linha não estiver dentro do intervalo permitido
     * @throws IllegalStateException se {@link #delete()} tiver sido chamado anteriormente
     */
    public synchronized void updateScore(int line, T text) {
        checkLineNumber(line, true, false);

        this.scores.set(line, text);

        try {
            if (customScoresSupported()) {
                sendScorePacket(getScoreByLine(line), ScoreboardAction.CHANGE);
            }
        } catch (Throwable e) {
            throw new RuntimeException("Unable to update line score", e);
        }
    }

    /**
     * Reseta o placar de uma linha para o valor padrão (em branco). O placar será exibido apenas para servidores nas versões 1.20.3 ou superiores.
     *
     * @param line o número da linha
     * @throws IllegalArgumentException se o número da linha não estiver dentro do intervalo permitido
     * @throws IllegalStateException se {@link #delete()} tiver sido chamado anteriormente
     */
    public synchronized void removeScore(int line) {
        updateScore(line, null);
    }

    /**
     * Atualiza como os placares de todas as linhas são exibidos no ScoreBoard. Um valor nulo resetará o texto exibido para o valor padrão.
     * Os placares serão exibidos apenas para servidores nas versões 1.20.3 ou superiores.
     *
     * @param texts o conjunto de textos a serem exibidos como os placares
     * @throws IllegalArgumentException se o tamanho dos textos não corresponder ao tamanho atual do ScoreBoard
     * @throws IllegalStateException se {@link #delete()} tiver sido chamado anteriormente
     */
    public synchronized void updateScores(T... texts) {
        updateScores(Arrays.asList(texts));
    }

    /**
     * Atualiza como os placares de todas as linhas são exibidos no ScoreBoard. Um valor nulo resetará o texto exibido
     * para o valor padrão (em branco). Disponível apenas para servidores 1.20.3 ou superiores.
     *
     * @param texts o conjunto de textos a serem exibidos como os placares
     * @throws IllegalArgumentException se o tamanho dos textos não corresponder ao tamanho atual do ScoreBoard
     * @throws IllegalStateException se {@link #delete()} tiver sido chamado anteriormente
     */
    public synchronized void updateScores(Collection<T> texts) {
        Objects.requireNonNull(texts, "texts");

        if (this.scores.size() != this.lines.size()) {
            throw new IllegalArgumentException("The size of the scores must match the size of the board");
        }

        List<T> newScores = new ArrayList<>(texts);
        for (int i = 0; i < this.scores.size(); i++) {
            if (Objects.equals(this.scores.get(i), newScores.get(i))) {
                continue;
            }

            this.scores.set(i, newScores.get(i));

            try {
                if (customScoresSupported()) {
                    sendScorePacket(getScoreByLine(i), ScoreboardAction.CHANGE);
                }
            } catch (Throwable e) {
                throw new RuntimeException("Unable to update scores", e);
            }
        }
    }

    /**
     * Retorna se o servidor suporta ScoreBoards personalizadas (1.20.3+).
     *
     * @return true se o servidor suporta
     */
    public boolean customScoresSupported() {
        return BLANK_NUMBER_FORMAT != null;
    }

    /**
     * Suporta o tamanho da ScoreBoard (número de linhas).
     *
     * @return o tamanho
     */
    public int size() {
        return this.lines.size();
    }

    /**
     * Deleta a ScoreBoard, e remove para o jogador que ela está associada.
     * Depois disso, todos os usos de {@link #updateLines} e {@link #updateTitle} darão esse erro: {@link IllegalStateException}
     *
     * @throws IllegalStateException se a ScoreBoard não existe mais
     */
    public void delete() {
        try {
            for (int i = 0; i < this.lines.size(); i++) {
                sendTeamPacket(i, TeamMode.REMOVE);
            }

            sendObjectivePacket(ObjectiveMode.REMOVE);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to delete scoreboard", t);
        }

        this.deleted = true;
    }

    protected abstract void sendLineChange(int score) throws Throwable;

    protected abstract Object toMinecraftComponent(T value) throws Throwable;

    protected abstract String serializeLine(T value);

    protected abstract T emptyLine();

    private void checkLineNumber(int line, boolean checkInRange, boolean checkMax) {
        if (line < 0) {
            throw new IllegalArgumentException("Line number must be positive");
        }

        if (checkInRange && line >= this.lines.size()) {
            throw new IllegalArgumentException("Line number must be under " + this.lines.size());
        }

        if (checkMax && line >= COLOR_CODES.length - 1) {
            throw new IllegalArgumentException("Line number is too high: " + line);
        }
    }

    protected int getScoreByLine(int line) {
        return this.lines.size() - line - 1;
    }

    protected T getLineByScore(int score) {
        return getLineByScore(this.lines, score);
    }

    protected T getLineByScore(List<T> lines, int score) {
        return score < lines.size() ? lines.get(lines.size() - score - 1) : null;
    }

    protected void sendObjectivePacket(ObjectiveMode mode) throws Throwable {
        Object packet = PACKET_SB_OBJ.invoke();

        setField(packet, String.class, this.id);
        setField(packet, int.class, mode.ordinal());

        if (mode != ObjectiveMode.REMOVE) {
            setComponentField(packet, this.title, 1);
            setField(packet, Optional.class, Optional.empty()); // Number format for 1.20.5+, previously nullable

            if (VersionType.V1_8.isHigherOrEqual()) {
                setField(packet, ENUM_SB_HEALTH_DISPLAY, ENUM_SB_HEALTH_DISPLAY_INTEGER);
            }
        } else if (VERSION_TYPE == VersionType.V1_7) {
            setField(packet, String.class, "", 1);
        }

        sendPacket(packet);
    }

    protected void sendDisplayObjectivePacket() throws Throwable {
        Object packet = PACKET_SB_DISPLAY_OBJ.invoke();

        setField(packet, DISPLAY_SLOT_TYPE, SIDEBAR_DISPLAY_SLOT); // Position
        setField(packet, String.class, this.id); // Score Name

        sendPacket(packet);
    }

    protected void sendScorePacket(int score, ScoreboardAction action) throws Throwable {
        if (VersionType.V1_17.isHigherOrEqual()) {
            sendModernScorePacket(score, action);
            return;
        }

        Object packet = PACKET_SB_SET_SCORE.invoke();

        setField(packet, String.class, COLOR_CODES[score], 0); // Player Name

        if (VersionType.V1_8.isHigherOrEqual()) {
            Object enumAction = action == ScoreboardAction.REMOVE
                    ? ENUM_SB_ACTION_REMOVE : ENUM_SB_ACTION_CHANGE;
            setField(packet, ENUM_SB_ACTION, enumAction);
        } else {
            setField(packet, int.class, action.ordinal(), 1); // Action
        }

        if (action == ScoreboardAction.CHANGE) {
            setField(packet, String.class, this.id, 1); // Objective Name
            setField(packet, int.class, score); // Score
        }

        sendPacket(packet);
    }

    private void sendModernScorePacket(int score, ScoreboardAction action) throws Throwable {
        String objName = COLOR_CODES[score];
        Object enumAction = action == ScoreboardAction.REMOVE
                ? ENUM_SB_ACTION_REMOVE : ENUM_SB_ACTION_CHANGE;

        if (PACKET_SB_RESET_SCORE == null) { // Pre 1.20.3
            sendPacket(PACKET_SB_SET_SCORE.invoke(enumAction, this.id, objName, score));
            return;
        }

        if (action == ScoreboardAction.REMOVE) {
            sendPacket(PACKET_SB_RESET_SCORE.invoke(objName, this.id));
            return;
        }

        T scoreFormat = getLineByScore(this.scores, score);
        Object format = scoreFormat != null
                ? FIXED_NUMBER_FORMAT.invoke(toMinecraftComponent(scoreFormat))
                : BLANK_NUMBER_FORMAT;
        Object scorePacket = SCORE_OPTIONAL_COMPONENTS
                ? PACKET_SB_SET_SCORE.invoke(objName, this.id, score, Optional.empty(), Optional.of(format))
                : PACKET_SB_SET_SCORE.invoke(objName, this.id, score, null, format);

        sendPacket(scorePacket);
    }

    protected void sendTeamPacket(int score, TeamMode mode) throws Throwable {
        sendTeamPacket(score, mode, null, null);
    }

    protected void sendTeamPacket(int score, TeamMode mode, T prefix, T suffix)
            throws Throwable {
        if (mode == TeamMode.ADD_PLAYERS || mode == TeamMode.REMOVE_PLAYERS) {
            throw new UnsupportedOperationException();
        }

        Object packet = PACKET_SB_TEAM.invoke();

        setField(packet, String.class, this.id + ':' + score); // Team name
        setField(packet, int.class, mode.ordinal(), VERSION_TYPE == VersionType.V1_8 ? 1 : 0); // Update mode

        if (mode == TeamMode.REMOVE) {
            sendPacket(packet);
            return;
        }

        if (VersionType.V1_17.isHigherOrEqual()) {
            Object team = PACKET_SB_SERIALIZABLE_TEAM.invoke();
            // Since the packet is initialized with null values, we need to change more things.
            setComponentField(team, null, 0); // Display name
            setField(team, CHAT_FORMAT_ENUM, RESET_FORMATTING); // Color
            setComponentField(team, prefix, 1); // Prefix
            setComponentField(team, suffix, 2); // Suffix
            setField(team, String.class, "always", 0); // Visibility
            setField(team, String.class, "always", 1); // Collisions
            setField(packet, Optional.class, Optional.of(team));
        } else {
            setComponentField(packet, prefix, 2); // Prefix
            setComponentField(packet, suffix, 3); // Suffix
            setField(packet, String.class, "always", 4); // Visibility for 1.8+
            setField(packet, String.class, "always", 5); // Collisions for 1.9+
        }

        if (mode == TeamMode.CREATE) {
            setField(packet, Collection.class, Collections.singletonList(COLOR_CODES[score])); // Players in the team
        }

        sendPacket(packet);
    }

    private void sendPacket(Object packet) throws Throwable {
        if (this.deleted) {
            throw new IllegalStateException("This FastBoard is deleted");
        }

        if (this.player.isOnline()) {
            Object entityPlayer = PLAYER_GET_HANDLE.invoke(this.player);
            Object playerConnection = PLAYER_CONNECTION.invoke(entityPlayer);
            SEND_PACKET.invoke(playerConnection, packet);
        }
    }

    private void setField(Object object, Class<?> fieldType, Object value)
            throws ReflectiveOperationException {
        setField(object, fieldType, value, 0);
    }

    private void setField(Object packet, Class<?> fieldType, Object value, int count)
            throws ReflectiveOperationException {
        int i = 0;
        for (Field field : PACKETS.get(packet.getClass())) {
            if (field.getType() == fieldType && count == i++) {
                field.set(packet, value);
            }
        }
    }

    private void setComponentField(Object packet, T value, int count) throws Throwable {
        if (!VersionType.V1_13.isHigherOrEqual()) {
            String line = value != null ? serializeLine(value) : "";
            setField(packet, String.class, line, count);
            return;
        }

        int i = 0;
        for (Field field : PACKETS.get(packet.getClass())) {
            if ((field.getType() == String.class || field.getType() == CHAT_COMPONENT_CLASS) && count == i++) {
                field.set(packet, toMinecraftComponent(value));
            }
        }
    }

    public enum ObjectiveMode {
        CREATE, REMOVE, UPDATE
    }

    public enum TeamMode {
        CREATE, REMOVE, UPDATE, ADD_PLAYERS, REMOVE_PLAYERS
    }

    public enum ScoreboardAction {
        CHANGE, REMOVE
    }

    public enum VersionType {
        V1_7, V1_8, V1_13, V1_17;

        public boolean isHigherOrEqual() {
            return VERSION_TYPE.ordinal() >= ordinal();
        }
    }
}