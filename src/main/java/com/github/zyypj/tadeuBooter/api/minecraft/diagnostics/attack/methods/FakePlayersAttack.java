package com.github.zyypj.tadeuBooter.api.minecraft.diagnostics.attack.methods;

import com.github.zyypj.tadeuBooter.api.minecraft.diagnostics.attack.AttackMethod;
import com.github.zyypj.tadeuBooter.api.minecraft.diagnostics.attack.methods.fake.players.FakePlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class FakePlayersAttack extends AttackMethod {
    private final List<String> fakePlayers = new ArrayList<>();

    public FakePlayersAttack(String target, int duration, int intensity) {
        super(target, duration, intensity);
    }

    @Override
    public void start() {
        World world = Bukkit.getWorlds().get(0);
        Location spawnLocation = world.getSpawnLocation();

        for (int i = 0; i < intensity; i++) {
            String fakeName = "FakePlayer" + i;
            FakePlayerManager.createFakePlayer(fakeName, spawnLocation);
            fakePlayers.add(fakeName);
        }
    }

    @Override
    public void stop() {
        for (String fakePlayer : fakePlayers) {
            FakePlayerManager.removeFakePlayer(fakePlayer);
        }
    }
}