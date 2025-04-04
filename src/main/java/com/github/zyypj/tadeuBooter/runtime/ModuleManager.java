package com.github.zyypj.tadeuBooter.runtime;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class ModuleManager {

    private final Map<String, LoadedModule> modules = new HashMap<>();

    public void load(String id, File jar, String mainClass) throws Exception {
        URLClassLoader loader = new URLClassLoader(new URL[]{jar.toURI().toURL()}, getClass().getClassLoader());
        Class<?> clazz = loader.loadClass(mainClass);
        ReloadableModule module = (ReloadableModule) clazz.newInstance();
        module.onLoad();
        modules.put(id, new LoadedModule(module, loader, jar, jar.lastModified()));
    }

    public void reload(String id) throws Exception {
        reload(id, new HashSet<>());
    }

    private void reload(String id, Set<String> visited) throws Exception {
        if (visited.contains(id)) return;
        visited.add(id);

        for (String otherId : new ArrayList<>(modules.keySet())) {
            if (!otherId.equals(id)) {
                ReloadableModule mod = modules.get(otherId).module;
                if (mod.getDependencies().contains(id)) {
                    reload(otherId, visited);
                }
            }
        }

        if (!modules.containsKey(id)) throw new IllegalArgumentException("Module not loaded: " + id);

        LoadedModule loaded = modules.remove(id);
        loaded.module.onUnload();

        try {
            loaded.loader.close();
        } catch (Exception ignored) {
        }

        load(id, loaded.jarFile, loaded.module.getClass().getName());
    }

    public void unload(String id) {
        if (!modules.containsKey(id)) return;
        LoadedModule loaded = modules.remove(id);
        loaded.module.onUnload();
        try {
            loaded.loader.close();
        } catch (Exception ignored) {
        }
    }

    public Collection<String> getLoadedIds() {
        return modules.keySet();
    }

    private static class LoadedModule {
        final ReloadableModule module;
        final URLClassLoader loader;
        final File jarFile;
        final long lastModified;

        LoadedModule(ReloadableModule module, URLClassLoader loader, File jarFile, long lastModified) {
            this.module = module;
            this.loader = loader;
            this.jarFile = jarFile;
            this.lastModified = lastModified;
        }
    }
}