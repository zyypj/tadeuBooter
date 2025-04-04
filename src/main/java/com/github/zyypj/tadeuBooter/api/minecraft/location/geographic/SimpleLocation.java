package com.github.zyypj.tadeuBooter.api.minecraft.location.geographic;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.World;

@Data
@AllArgsConstructor
public class SimpleLocation {
    private final String worldName;
    private final int x;
    private final int y;
    private final int z;

    public SimpleLocation(World world, int x, int y, int z) {
        this(world.getName(), x, y, z);
    }
}