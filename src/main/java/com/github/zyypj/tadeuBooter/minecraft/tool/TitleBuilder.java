package com.github.zyypj.tadeuBooter.minecraft.tool;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TitleBuilder {

    private static Class<?> packetTitle;
    private static Class<?> packetActions;
    private static Class<?> nmsChatSerializer;
    private static Class<?> chatBaseComponent;

    private String title = "";
    private ChatColor titleColor = ChatColor.WHITE;
    private String subtitle = "";
    private ChatColor subtitleColor = ChatColor.WHITE;
    private int fadeInTime = -1;
    private int stayTime = -1;
    private int fadeOutTime = -1;
    private boolean ticks = false;

    static {
        try {
            loadClasses();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TitleBuilder() {}

    public TitleBuilder(String title) {
        this.title = title;
    }

    public TitleBuilder(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public TitleBuilder(String title, String subtitle, int fadeInTime, int stayTime, int fadeOutTime) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeInTime = fadeInTime;
        this.stayTime = stayTime;
        this.fadeOutTime = fadeOutTime;
    }

    public TitleBuilder(TitleBuilder titleBuilder) {
        this.title = titleBuilder.title;
        this.subtitle = titleBuilder.subtitle;
        this.titleColor = titleBuilder.titleColor;
        this.subtitleColor = titleBuilder.subtitleColor;
        this.fadeInTime = titleBuilder.fadeInTime;
        this.stayTime = titleBuilder.stayTime;
        this.fadeOutTime = titleBuilder.fadeOutTime;
        this.ticks = titleBuilder.ticks;
    }

    private static void loadClasses() throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        packetTitle = Class.forName("net.minecraft.server." + version + ".PacketPlayOutTitle");
        packetActions = Class.forName("net.minecraft.server." + version + ".PacketPlayOutTitle$EnumTitleAction");
        chatBaseComponent = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
        nmsChatSerializer = Class.forName("net.minecraft.server." + version + ".ChatComponentText");
    }

    public TitleBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public TitleBuilder setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public TitleBuilder setTitleColor(ChatColor color) {
        this.titleColor = color;
        return this;
    }

    public TitleBuilder setSubtitleColor(ChatColor color) {
        this.subtitleColor = color;
        return this;
    }

    public TitleBuilder setFadeInTime(int time) {
        this.fadeInTime = time;
        return this;
    }

    public TitleBuilder setStayTime(int time) {
        this.stayTime = time;
        return this;
    }

    public TitleBuilder setFadeOutTime(int time) {
        this.fadeOutTime = time;
        return this;
    }

    public TitleBuilder setTimingsToTicks() {
        this.ticks = true;
        return this;
    }

    public TitleBuilder setTimingsToSeconds() {
        this.ticks = false;
        return this;
    }

    public void send(Player player) {
        try {
            resetTitle(player);
            Object handle = getHandle(player);
            Object connection = getField(handle.getClass(), "playerConnection").get(handle);
            Object[] actions = packetActions.getEnumConstants();
            Method sendPacket = getMethod(connection.getClass(), "sendPacket");

            if (fadeInTime != -1 && stayTime != -1 && fadeOutTime != -1) {
                Object timingPacket = packetTitle.getConstructor(packetActions, chatBaseComponent, int.class, int.class, int.class)
                        .newInstance(actions[2], null, fadeInTime * (ticks ? 1 : 20), stayTime * (ticks ? 1 : 20), fadeOutTime * (ticks ? 1 : 20));
                sendPacket.invoke(connection, timingPacket);
            }

            if (!title.isEmpty()) {
                Object titleComponent = nmsChatSerializer.getConstructor(String.class)
                        .newInstance(ChatColor.translateAlternateColorCodes('&', title));
                Object titlePacket = packetTitle.getConstructor(packetActions, chatBaseComponent)
                        .newInstance(actions[0], titleComponent);
                sendPacket.invoke(connection, titlePacket);
            }

            if (!subtitle.isEmpty()) {
                Object subtitleComponent = nmsChatSerializer.getConstructor(String.class)
                        .newInstance(ChatColor.translateAlternateColorCodes('&', subtitle));
                Object subtitlePacket = packetTitle.getConstructor(packetActions, chatBaseComponent)
                        .newInstance(actions[1], subtitleComponent);
                sendPacket.invoke(connection, subtitlePacket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void broadcast() {
        Bukkit.getOnlinePlayers().forEach(this::send);
    }

    public void resetTitle(Player player) {
        try {
            Object handle = getHandle(player);
            Object connection = getField(handle.getClass(), "playerConnection").get(handle);
            Object[] actions = packetActions.getEnumConstants();
            Method sendPacket = getMethod(connection.getClass(), "sendPacket");
            Object resetPacket = packetTitle.getConstructor(packetActions, chatBaseComponent)
                    .newInstance(actions[4], null);
            sendPacket.invoke(connection, resetPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object getHandle(Player player) {
        try {
            Method getHandle = getMethod(player.getClass(), "getHandle");
            return getHandle.invoke(player);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Field getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Method getMethod(Class<?> clazz, String name, Class<?>... args) {
        try {
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(name) && equalsTypeArray(method.getParameterTypes(), args)) {
                    method.setAccessible(true);
                    return method;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean equalsTypeArray(Class<?>[] a, Class<?>[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (!a[i].equals(b[i]) && !a[i].isAssignableFrom(b[i])) {
                return false;
            }
        }
        return true;
    }
}