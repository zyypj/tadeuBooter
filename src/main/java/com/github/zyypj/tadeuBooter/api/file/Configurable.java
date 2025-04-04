package com.github.zyypj.tadeuBooter.api.file;

public interface Configurable {
    void loadConfig();
    void saveConfig();
    void reloadConfig();
}