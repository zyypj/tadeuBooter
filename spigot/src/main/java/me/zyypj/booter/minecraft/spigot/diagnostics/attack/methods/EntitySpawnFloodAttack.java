package me.zyypj.booter.minecraft.spigot.diagnostics.attack.methods;

import me.zyypj.booter.minecraft.spigot.diagnostics.attack.AttackMethod;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EntitySpawnFloodAttack extends AttackMethod {
    private final JavaPlugin plugin;
    private boolean running = true;

    public EntitySpawnFloodAttack(String target, int duration, int intensity, JavaPlugin plugin) {
        super(target, duration, intensity);
        this.plugin = plugin;
    }

    @Override
    public void start() {
        Bukkit.getScheduler()
                .runTaskTimer(
                        plugin,
                        () -> {
                            if (!running) return;

                            for (Player player : Bukkit.getOnlinePlayers()) {
                                Location loc = player.getLocation();
                                for (int i = 0; i < intensity; i++) {
                                    player.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
                                }
                            }
                        },
                        0L,
                        10L);
    }

    @Override
    public void stop() {
        running = false;
    }
}
