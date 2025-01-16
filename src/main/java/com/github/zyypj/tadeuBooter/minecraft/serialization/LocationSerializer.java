package com.github.zyypj.tadeuBooter.minecraft.serialization;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationSerializer {

    private static final String SPACE_PLACEHOLDER = "-";

    public static String serialize(Location location) {
        String worldName = location.getWorld().getName().replace(" ", SPACE_PLACEHOLDER);
        return worldName + " " + location.getX() + " " + location.getY() + " " + location.getZ() + " " + location.getYaw() + " " + location.getPitch();
    }

    public static Location deserialize(String string) {
        String[] split = string.split(" ");
        String worldName = split[0].replace(SPACE_PLACEHOLDER, " ");
        return new Location(
                Bukkit.getWorld(worldName),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Double.parseDouble(split[3]),
                Float.parseFloat(split[4]),
                Float.parseFloat(split[5])
        );
    }
}