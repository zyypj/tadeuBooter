package me.zyypj.booter.shared.file;

public interface Configurable {
    void loadConfig();

    void saveConfig();

    void reloadConfig();
}
