package com.github.zyypj.tadeuBooter.minecraft.reload;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class ModuleManager {
    private final Map<String, LoadedModule> modules = new HashMap<>();

    /**
     * Carrega um módulo a partir de um arquivo JAR.
     *
     * @param moduleId      Identificador único do módulo.
     * @param jarFile       Arquivo JAR contendo o módulo.
     * @param mainClassName Nome da classe principal que implementa ReloadableModule.
     */
    public void loadModule(String moduleId, File jarFile, String mainClassName) throws Exception {
        URL url = jarFile.toURI().toURL();
        URLClassLoader loader = new URLClassLoader(new URL[]{url}, this.getClass().getClassLoader());
        Class<?> clazz = loader.loadClass(mainClassName);
        ReloadableModule module = (ReloadableModule) clazz.newInstance();
        module.onLoad();
        modules.put(moduleId, new LoadedModule(module, loader, jarFile, jarFile.lastModified()));
    }

    /**
     * Recarrega o módulo especificado e também os módulos que dependem dele.
     *
     * @param moduleId Identificador do módulo a ser recarregado.
     */
    public void reloadModule(String moduleId) throws Exception {
        reloadModule(moduleId, new HashSet<>());
    }

    private void reloadModule(String moduleId, Set<String> visited) throws Exception {
        if (visited.contains(moduleId)) return;
        visited.add(moduleId);

        for (String key : new ArrayList<>(modules.keySet())) {
            if (!key.equals(moduleId)) {
                ReloadableModule mod = modules.get(key).module;
                if (mod.getDependencies().contains(moduleId)) {
                    reloadModule(key, visited);
                }
            }
        }

        if (!modules.containsKey(moduleId)) {
            throw new IllegalArgumentException("Module not loaded: " + moduleId);
        }
        LoadedModule loaded = modules.get(moduleId);
        loaded.module.onUnload();
        modules.remove(moduleId);

        File jarFile = loaded.jarFile;
        loadModule(moduleId, jarFile, loaded.module.getClass().getName());
    }

    /**
     * Descarrega o módulo especificado.
     *
     * @param moduleId Identificador do módulo a ser descarregado.
     */
    public void unloadModule(String moduleId) {
        if (!modules.containsKey(moduleId)) return;
        LoadedModule loaded = modules.get(moduleId);
        loaded.module.onUnload();
        modules.remove(moduleId);
    }

    /**
     * Retorna os IDs dos módulos carregados.
     */
    public Collection<String> getLoadedModuleIds() {
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