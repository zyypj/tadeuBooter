package me.zyypj.booter.minecraft.spigot.inventories.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import me.zyypj.booter.minecraft.spigot.inventories.viewer.Viewer;

/** @author Henry Fábio Github: https://github.com/HenryFabio */
@Getter
public final class ViewerController {

    private final Map<String, Viewer> viewerMap = new LinkedHashMap<>();

    public <T extends Viewer> T registerViewer(T viewer) {
        this.viewerMap.put(viewer.getName(), viewer);
        return viewer;
    }

    public <T extends Viewer> T unregisterViewer(String viewerName) {
        return (T) this.viewerMap.remove(viewerName);
    }

    public <T extends Viewer> Optional<T> findViewer(String viewerName) {
        return Optional.ofNullable((T) this.viewerMap.get(viewerName));
    }
}
