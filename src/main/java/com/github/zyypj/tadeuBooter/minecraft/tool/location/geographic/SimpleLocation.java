package com.github.zyypj.tadeuBooter.minecraft.tool.location.geographic;

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

    /**
     * Construtor que recebe um objeto {@link World}.
     *
     * @param world Objeto World.
     * @param x   Coordenada X.
     * @param y   Coordenada Y.
     * @param z   Coordenada Z.
     */
    public SimpleLocation(World world, int x, int y, int z) {
        this(world.getName(), x, y, z);
    }
}