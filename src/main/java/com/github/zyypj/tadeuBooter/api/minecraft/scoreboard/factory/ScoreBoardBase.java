package com.github.zyypj.tadeuBooter.api.minecraft.scoreboard.factory;

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
 * Pode ser usada de forma segura de maneira assíncrona, pois tudo é baseado em pacotes.
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
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        VersionType versionTemp;
        try {
            // Determina a versão baseada em classes disponíveis (adapte conforme suas necessidades)
            if (ScoreBoardReflection.isRepackaged()) {
                versionTemp = VersionType.V1_17;
            } else if (ScoreBoardReflection.nmsOptionalClass(null, "ScoreboardServer$Action").isPresent()
                    || ScoreBoardReflection.nmsOptionalClass(null, "ServerScoreboard$Method").isPresent()) {
                versionTemp = VersionType.V1_13;
            } else if (ScoreBoardReflection.nmsOptionalClass(null, "IScoreboardCriteria$EnumScoreboardHealthDisplay").isPresent()
                    || ScoreBoardReflection.nmsOptionalClass(null, "ObjectiveCriteria$RenderType").isPresent()) {
                versionTemp = VersionType.V1_8;
            } else {
                versionTemp = VersionType.V1_7;
            }
            // Supondo que servidores mais novos possam ter uma nova classe para 1.20
            if (ScoreBoardReflection.nmsOptionalClass(null, "ClientboundSetDisplayObjectivePacket").isPresent()) {
                versionTemp = VersionType.V1_20;
            }
        } catch (Exception e) {
            versionTemp = VersionType.V1_7;
        }
        VERSION_TYPE = versionTemp;

        try {
            String gameProtocolPackage = "network.protocol.game";
            Class<?> craftPlayerClass = ScoreBoardReflection.obcClass("entity.CraftPlayer");
            Class<?> entityPlayerClass = ScoreBoardReflection.nmsClass("server.level", "EntityPlayer", "ServerPlayer");
            Class<?> playerConnectionClass = ScoreBoardReflection.nmsClass("server.network", "PlayerConnection", "ServerGamePacketListenerImpl");
            Class<?> packetClass = ScoreBoardReflection.nmsClass("network.protocol", "Packet");
            Class<?> packetSbObjClass = ScoreBoardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardObjective", "ClientboundSetObjectivePacket");
            Class<?> packetSbDisplayObjClass = ScoreBoardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardDisplayObjective", "ClientboundSetDisplayObjectivePacket");
            Class<?> packetSbScoreClass = ScoreBoardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardScore", "ClientboundSetScorePacket");
            Class<?> packetSbTeamClass = ScoreBoardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardTeam", "ClientboundSetPlayerTeamPacket");
            Class<?> sbTeamClass = VERSION_TYPE.isHigherOrEqual(VersionType.V1_17)
                    ? ScoreBoardReflection.innerClass(packetSbTeamClass, inner -> !inner.isEnum()) : null;
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
            CHAT_COMPONENT_CLASS = ScoreBoardReflection.nmsClass("network.chat", "IChatBaseComponent", "Component");
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

            if (numberFormat.isPresent()) { // Versões 1.20.3+
                Class<?> blankFormatClass = ScoreBoardReflection.nmsClass("network.chat.numbers", "BlankFormat");
                Class<?> fixedFormatClass = ScoreBoardReflection.nmsClass("network.chat.numbers", "FixedFormat");
                Class<?> resetScoreClass = ScoreBoardReflection.nmsClass(gameProtocolPackage, "ClientboundResetScorePacket");
                MethodType scoreType = MethodType.methodType(void.class, String.class, String.class, int.class, CHAT_COMPONENT_CLASS, numberFormat.get());
                MethodType scoreTypeOptional = MethodType.methodType(void.class, String.class, String.class, int.class, Optional.class, Optional.class);
                MethodType removeScoreType = MethodType.methodType(void.class, String.class, String.class);
                MethodType fixedFormatType = MethodType.methodType(void.class, CHAT_COMPONENT_CLASS);
                Optional<Field> blankField = Arrays.stream(blankFormatClass.getFields()).filter(f -> f.getType() == blankFormatClass).findAny();
                Optional<MethodHandle> optionalScorePacket = ScoreBoardReflection.optionalConstructor(packetSbScoreClass, lookup, scoreTypeOptional);
                fixedFormatConstructor = lookup.findConstructor(fixedFormatClass, fixedFormatType);
                packetSbSetScore = optionalScorePacket.orElseGet(() -> {
                    try {
                        return lookup.findConstructor(packetSbScoreClass, scoreType);
                    } catch (Throwable t) {
                        throw new RuntimeException(t);
                    }
                });
                scoreOptionalComponents = optionalScorePacket.isPresent();
                packetSbResetScore = lookup.findConstructor(resetScoreClass, removeScoreType);
                blankNumberFormat = blankField.isPresent() ? blankField.get().get(null) : null;
            } else if (VERSION_TYPE.isHigherOrEqual(VersionType.V1_17)) {
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

            // Cache de campos (não está utilizando ReflectionHelper por ser baseado em tipo/índice)
            for (Class<?> clazz : Arrays.asList(packetSbObjClass, packetSbDisplayObjClass, packetSbScoreClass, packetSbTeamClass, sbTeamClass)) {
                if (clazz == null) continue;
                Field[] fields = Arrays.stream(clazz.getDeclaredFields())
                        .filter(field -> !Modifier.isStatic(field.getModifiers()))
                        .peek(field -> field.setAccessible(true))
                        .toArray(Field[]::new);
                PACKETS.put(clazz, fields);
            }

            if (VERSION_TYPE.isHigherOrEqual(VersionType.V1_8)) {
                String enumSbActionClass = VERSION_TYPE.isHigherOrEqual(VersionType.V1_13)
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
     * Cria uma nova ScoreBoard para o jogador informado.
     *
     * @param player o jogador no qual a ScoreBoard será exibida
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

    public void updateTitle(T title) {
        if (this.title.equals(Objects.requireNonNull(title, "title"))) return;
        this.title = title;
        try {
            sendObjectivePacket(ObjectiveMode.UPDATE);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to update scoreboard title", t);
        }
    }

    public List<T> getLines() {
        return new ArrayList<>(this.lines);
    }

    public T getLine(int line) {
        checkLineNumber(line, true, false);
        return this.lines.get(line);
    }

    public Optional<T> getScore(int line) {
        checkLineNumber(line, true, false);
        return Optional.ofNullable(this.scores.get(line));
    }

    public synchronized void updateLine(int line, T text) {
        updateLine(line, text, null);
    }

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

    public synchronized void removeLine(int line) {
        checkLineNumber(line, false, false);
        if (line >= size()) return;
        List<T> newLines = new ArrayList<>(this.lines);
        List<T> newScores = new ArrayList<>(this.scores);
        newLines.remove(line);
        newScores.remove(line);
        updateLines(newLines, newScores);
    }

    public void updateLines(T... lines) {
        updateLines(Arrays.asList(lines));
    }

    public synchronized void updateLines(Collection<T> lines) {
        updateLines(lines, null);
    }

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

    public synchronized void removeScore(int line) {
        updateScore(line, null);
    }

    public synchronized void updateScores(T... texts) {
        updateScores(Arrays.asList(texts));
    }

    public synchronized void updateScores(Collection<T> texts) {
        Objects.requireNonNull(texts, "texts");
        if (this.scores.size() != this.lines.size()) {
            throw new IllegalArgumentException("The size of the scores must match the size of the board");
        }
        List<T> newScores = new ArrayList<>(texts);
        for (int i = 0; i < this.scores.size(); i++) {
            if (Objects.equals(this.scores.get(i), newScores.get(i))) continue;
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

    public boolean customScoresSupported() {
        return BLANK_NUMBER_FORMAT != null;
    }

    public int size() {
        return this.lines.size();
    }

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

    // --- Métodos auxiliares de reflection ---

    /**
     * Define o valor de um campo no objeto alvo com base no tipo e ordem (count) dentre os campos não-estáticos.
     * Caso seja possível, utilize o ReflectionHelper se o nome do campo for conhecido.
     */
    private void setField(Object target, Class<?> fieldType, Object value, int count) throws ReflectiveOperationException {
        int i = 0;
        for (Field field : PACKETS.get(target.getClass())) {
            if (field.getType() == fieldType && count == i++) {
                field.set(target, value);
                return;
            }
        }
        throw new NoSuchFieldException("No field of type " + fieldType.getName() + " at count " + count);
    }

    /**
     * Define o valor de um campo cujo tipo é conhecido. Sobrecarga sem o parâmetro count (usa o primeiro encontrado).
     */
    private void setField(Object target, Class<?> fieldType, Object value) throws ReflectiveOperationException {
        setField(target, fieldType, value, 0);
    }

    /**
     * Define um campo que representa um componente. Se a versão for inferior a 1.13, converte para String usando serializeLine.
     */
    private void setComponentField(Object target, T value, int count) throws Throwable {
        if (!VERSION_TYPE.isHigherOrEqual(VersionType.V1_13)) {
            String line = value != null ? serializeLine(value) : "";
            setField(target, String.class, line, count);
            return;
        }
        int i = 0;
        for (Field field : PACKETS.get(target.getClass())) {
            if ((field.getType() == String.class || field.getType() == CHAT_COMPONENT_CLASS) && count == i++) {
                field.set(target, toMinecraftComponent(value));
                return;
            }
        }
        throw new NoSuchFieldException("No component field found at count " + count);
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
        // Exemplo de uso do ReflectionHelper: se soubermos o nome do campo "name", podemos chamar:
        // ReflectionHelper.setFieldValue(packet, "name", this.id);
        // Aqui continuamos usando o método auxiliar que busca por tipo.
        setField(packet, String.class, this.id);
        setField(packet, int.class, mode.ordinal());
        if (mode != ObjectiveMode.REMOVE) {
            setComponentField(packet, this.title, 1);
            setField(packet, Optional.class, Optional.empty());
            if (VERSION_TYPE.isHigherOrEqual(VersionType.V1_8)) {
                setField(packet, ENUM_SB_HEALTH_DISPLAY, ENUM_SB_HEALTH_DISPLAY_INTEGER);
            }
        } else if (VERSION_TYPE == VersionType.V1_7) {
            setField(packet, String.class, "", 1);
        }
        sendPacket(packet);
    }

    protected void sendDisplayObjectivePacket() throws Throwable {
        Object packet = PACKET_SB_DISPLAY_OBJ.invoke();
        setField(packet, DISPLAY_SLOT_TYPE, SIDEBAR_DISPLAY_SLOT);
        setField(packet, String.class, this.id);
        sendPacket(packet);
    }

    protected void sendScorePacket(int score, ScoreboardAction action) throws Throwable {
        if (VERSION_TYPE.isHigherOrEqual(VersionType.V1_17)) {
            sendModernScorePacket(score, action);
            return;
        }
        Object packet = PACKET_SB_SET_SCORE.invoke();
        setField(packet, String.class, COLOR_CODES[score], 0);
        if (VERSION_TYPE.isHigherOrEqual(VersionType.V1_8)) {
            Object enumAction = action == ScoreboardAction.REMOVE ? ENUM_SB_ACTION_REMOVE : ENUM_SB_ACTION_CHANGE;
            setField(packet, ENUM_SB_ACTION, enumAction);
        } else {
            setField(packet, int.class, action.ordinal(), 1);
        }
        if (action == ScoreboardAction.CHANGE) {
            setField(packet, String.class, this.id, 1);
            setField(packet, int.class, score);
        }
        sendPacket(packet);
    }

    private void sendModernScorePacket(int score, ScoreboardAction action) throws Throwable {
        String objName = COLOR_CODES[score];
        Object enumAction = action == ScoreboardAction.REMOVE ? ENUM_SB_ACTION_REMOVE : ENUM_SB_ACTION_CHANGE;
        if (PACKET_SB_RESET_SCORE == null) { // Pré 1.20.3
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

    protected void sendTeamPacket(int score, TeamMode mode, T prefix, T suffix) throws Throwable {
        if (mode == TeamMode.ADD_PLAYERS || mode == TeamMode.REMOVE_PLAYERS) {
            throw new UnsupportedOperationException();
        }
        Object packet = PACKET_SB_TEAM.invoke();
        setField(packet, String.class, this.id + ':' + score);
        int updateModeIndex = (VERSION_TYPE == VersionType.V1_8) ? 1 : 0;
        setField(packet, int.class, mode.ordinal(), updateModeIndex);
        if (mode == TeamMode.REMOVE) {
            sendPacket(packet);
            return;
        }
        if (VERSION_TYPE.isHigherOrEqual(VersionType.V1_17)) {
            Object team = PACKET_SB_SERIALIZABLE_TEAM.invoke();
            setComponentField(team, null, 0);
            setField(team, CHAT_FORMAT_ENUM, RESET_FORMATTING);
            setComponentField(team, prefix, 1);
            setComponentField(team, suffix, 2);
            setField(team, String.class, "always", 0);
            setField(team, String.class, "always", 1);
            setField(packet, Optional.class, Optional.of(team));
        } else {
            setComponentField(packet, prefix, 2);
            setComponentField(packet, suffix, 3);
            setField(packet, String.class, "always", 4);
            setField(packet, String.class, "always", 5);
        }
        if (mode == TeamMode.CREATE) {
            setField(packet, Collection.class, Collections.singletonList(COLOR_CODES[score]));
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

    private void checkLineNumber(int line, boolean checkInRange, boolean checkMax) {
        if (line < 0) {
            throw new IllegalArgumentException("Line number must be positive, got: " + line);
        }
        if (checkInRange && line >= this.lines.size()) {
            throw new IllegalArgumentException("Line number " + line + " is out of range (current board size: " + this.lines.size() + ")");
        }
        if (checkMax && line >= COLOR_CODES.length - 1) {
            throw new IllegalArgumentException("Line number " + line + " exceeds the maximum allowed (" + (COLOR_CODES.length - 1) + ")");
        }
    }

    // --- Enumerações ---

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
        V1_7, V1_8, V1_13, V1_17, V1_20;

        public boolean isHigherOrEqual(VersionType other) {
            return this.ordinal() >= other.ordinal();
        }
    }
}