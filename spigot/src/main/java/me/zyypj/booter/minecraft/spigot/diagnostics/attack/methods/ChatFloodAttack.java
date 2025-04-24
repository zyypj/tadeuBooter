package me.zyypj.booter.minecraft.spigot.diagnostics.attack.methods;

import me.zyypj.booter.minecraft.spigot.diagnostics.attack.AttackMethod;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatFloodAttack extends AttackMethod {
    private final JavaPlugin plugin;
    private boolean running = true;

    public ChatFloodAttack(String target, int duration, int intensity, JavaPlugin plugin) {
        super(target, duration, intensity);
        this.plugin = plugin;
    }

    @Override
    public void start() {
        Bukkit.getScheduler()
                .runTaskTimerAsynchronously(
                        plugin,
                        () -> {
                            if (!running) return;

                            for (Player player : Bukkit.getOnlinePlayers()) {
                                for (int i = 0; i < intensity; i++) {
                                    player.chat("§c[SPAM] Teste de Flood no Chat!");
                                }
                            }
                        },
                        0L,
                        5L);
    }

    @Override
    public void stop() {
        running = false;
    }
}
