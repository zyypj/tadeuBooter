package com.github.zyypj.tadeuBooter.runtime;

import java.util.Collections;
import java.util.List;

public interface ReloadableModule {
    void onLoad();

    void onUnload();

    default List<String> getDependencies() {
        return Collections.emptyList();
    }
}