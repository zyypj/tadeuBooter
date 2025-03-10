package com.github.zyypj.tadeuBooter.diagnostics.attack.methods;

import com.github.zyypj.tadeuBooter.diagnostics.attack.AttackMethod;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockUpdateFloodAttack extends AttackMethod {
    private final JavaPlugin plugin;
    private boolean running = true;

    public BlockUpdateFloodAttack(String target, int duration, int intensity, JavaPlugin plugin) {
        super(target, duration, intensity);
        this.plugin = plugin;
    }

    @Override
    public void start() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!running) return;

            for (Player player : Bukkit.getOnlinePlayers()) {
                Location loc = player.getLocation().add(0, -1, 0);
                for (int i = 0; i < intensity; i++) {
                    loc.getBlock().setType(Material.REDSTONE_BLOCK);
                    loc.getBlock().setType(Material.AIR);
                }
            }
        }, 0L, 5L);
    }

    @Override
    public void stop() {
        running = false;
    }
}