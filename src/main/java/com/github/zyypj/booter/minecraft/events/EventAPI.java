package com.github.zyypj.booter.minecraft.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * API para facilitar a criação e o gerenciamento de eventos no Minecraft.
 */
public class EventAPI {

    private static final Map<Class<? extends Event>, Consumer<? extends Event>> eventHandlers = new HashMap<>();
    private static Plugin plugin;

    /**
     * Inicializa a API com a instância do plugin.
     *
     * @param plugin O plugin principal que utiliza a API.
     */
    public static void initialize(Plugin plugin) {
        EventAPI.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(new InternalListener(), plugin);
    }

    /**
     * Registra um evento com um callback.
     *
     * @param eventClass A classe do evento a ser registrado.
     * @param callback   A lógica a ser executada quando o evento ocorrer.
     * @param <T>        O tipo do evento.
     */
    public static <T extends Event> void registerEvent(Class<T> eventClass, Consumer<T> callback) {
        eventHandlers.put(eventClass, callback);
    }

    /**
     * Remove um evento registrado.
     *
     * @param eventClass A classe do evento a ser removido.
     */
    public static void unregisterEvent(Class<? extends Event> eventClass) {
        eventHandlers.remove(eventClass);
    }

    /**
     * Remove todos os eventos registrados.
     */
    public static void clearAllEvents() {
        eventHandlers.clear();
    }

    /**
     * Verifica se um evento está registrado.
     *
     * @param eventClass A classe do evento.
     * @return True se o evento estiver registrado, false caso contrário.
     */
    public static boolean isEventRegistered(Class<? extends Event> eventClass) {
        return eventHandlers.containsKey(eventClass);
    }

    /**
     * Listener interno para capturar e executar eventos registrados.
     */
    private static class InternalListener implements Listener {
        @EventHandler
        public void onEvent(Event event) {
            @SuppressWarnings("unchecked")
            Consumer<Event> handler = (Consumer<Event>) eventHandlers.get(event.getClass());
            if (handler != null) {
                handler.accept(event);
            }
        }
    }
}