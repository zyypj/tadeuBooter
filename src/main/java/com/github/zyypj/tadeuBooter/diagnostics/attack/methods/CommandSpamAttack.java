package com.github.zyypj.tadeuBooter.diagnostics.attack.methods;

import com.github.zyypj.tadeuBooter.diagnostics.attack.AttackMethod;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandSpamAttack extends AttackMethod {
    private final JavaPlugin plugin;
    private boolean running = true;

    public CommandSpamAttack(String target, int duration, int intensity, JavaPlugin plugin) {
        super(target, duration, intensity);
        this.plugin = plugin;
    }

    @Override
    public void start() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (!running) return;

            for (Player player : Bukkit.getOnlinePlayers()) {
                for (int i = 0; i < intensity; i++) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.performCommand("say §cTADEUBOOTER ATTACKS - Teste de Spam de Comandos!");
                    });
                }
            }
        }, 0L, 20L);
    }

    @Override
    public void stop() {
        running = false;
    }
}