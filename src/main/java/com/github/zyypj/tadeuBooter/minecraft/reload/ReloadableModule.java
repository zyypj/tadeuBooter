package com.github.zyypj.tadeuBooter.minecraft.reload;

import java.util.Collections;
import java.util.List;

public interface ReloadableModule {
    void onLoad();
    void onUnload();

    /**
     * Retorna os IDs dos módulos dos quais este módulo depende.
     * Por padrão, não depende de nenhum.
     */
    default List<String> getDependencies() {
        return Collections.emptyList();
    }
}