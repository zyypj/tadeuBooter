package me.zyypj.booter.minecraft.spigot.scoreboard.hooks.adventure;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import me.zyypj.booter.minecraft.spigot.scoreboard.factory.ScoreBoardBase;
import me.zyypj.booter.minecraft.spigot.scoreboard.factory.ScoreBoardReflection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class ScoreBoard extends ScoreBoardBase<Component> {
    private static final MethodHandle COMPONENT_METHOD;
    private static final Object EMPTY_COMPONENT;
    private static final boolean ADVENTURE_SUPPORT;
    // Cria o serializer usando o builder (novo padrão na API)
    private static final LegacyComponentSerializer LEGACY_SERIALIZER =
            LegacyComponentSerializer.builder().character('§').hexColors().build();

    static {
        ADVENTURE_SUPPORT =
                ScoreBoardReflection.optionalClass("io.papermc.paper.adventure.PaperAdventure")
                        .isPresent();
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        try {
            if (ADVENTURE_SUPPORT) {
                Class<?> paperAdventure =
                        Class.forName("io.papermc.paper.adventure.PaperAdventure");
                Method method = paperAdventure.getDeclaredMethod("asVanilla", Component.class);
                COMPONENT_METHOD = lookup.unreflect(method);
                EMPTY_COMPONENT = COMPONENT_METHOD.invoke(Component.empty());
            } else {
                Class<?> craftChatMessageClass =
                        ScoreBoardReflection.obcClass("util.CraftChatMessage");
                COMPONENT_METHOD =
                        lookup.unreflect(
                                craftChatMessageClass.getMethod("fromString", String.class));
                EMPTY_COMPONENT = Array.get(COMPONENT_METHOD.invoke(""), 0);
            }
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }

    public ScoreBoard(Player player) {
        super(player);
    }

    @Override
    protected void sendLineChange(int score) throws Throwable {
        Component line = getLineByScore(score);
        sendTeamPacket(score, ScoreBoardBase.TeamMode.UPDATE, line, null);
    }

    @Override
    protected Object toMinecraftComponent(Component component) throws Throwable {
        if (component == null) {
            return EMPTY_COMPONENT;
        }

        if (!ADVENTURE_SUPPORT) {
            String legacy = serializeLine(component);
            return Array.get(COMPONENT_METHOD.invoke(legacy), 0);
        }

        return COMPONENT_METHOD.invoke(component);
    }

    @Override
    protected String serializeLine(Component value) {
        return LEGACY_SERIALIZER.serialize(value);
    }

    @Override
    protected Component emptyLine() {
        return Component.empty();
    }
}
