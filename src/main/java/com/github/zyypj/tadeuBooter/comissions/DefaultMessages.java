package com.github.zyypj.tadeuBooter.comissions;

import com.github.zyypj.tadeuBooter.minecraft.Debug;

public class DefaultMessages {

    public static void sendMessage(Agency agency) {
        switch (agency) {
            case MRK:
                Debug.log("&5███    ███ ██████  ██   ██", false);
                Debug.log("&5████  ████ ██   ██ ██  ██", false);
                Debug.log("&5██ ████ ██ ██████  █████", false);
                Debug.log("&5██  ██  ██ ██   ██ ██  ██", false);
                Debug.log("&5██      ██ ██   ██ ██   ██", false);
                Debug.log("", false);
                Debug.log("&f&ldiscord.gg/mrkk - MRK © 2025", false);
                Debug.log("&fFeito por tadeu @zypj", false);
                break;
            case MYSTICCODES:
                String pluginMessage =
                        "\n" +
                                "§5  __  __           _   _           _____          _           \n" +
                                "§5 |  \\/  |         | | (_)         / ____|        | |          \n" +
                                "§5 | \\  / |_   _ ___| |_ _  ___    | |     ___   __| | ___  ___ \n" +
                                "§5 | |\\/| | | | / __| __| |/ __|   | |    / _ \\ / _` |/ _ \\/ __|\n" +
                                "§5 | |  | | |_| \\__ \\ |_| | (__    | |___| (_) | (_| |  __/\\__ \\\n" +
                                "§5 |_|  |_|\\__, |___/\\__|_|\\___|    \\_____\\___/ \\__,_|\\___||___/\n" +
                                "§5          __/ |                                             \n" +
                                "§5         |___/                                              \n\n" +
                                "§d§lDiscord: https://discord.gg/G9jKUXPgmz - MysticCodes © 2023-2025\n" +
                                "§d§lPlugin Feito por Tadeu [@zypj]";
                Debug.log(pluginMessage, false);
                break;
            case COMISSIONS:
                Debug.log("████████╗ █████╗ ██████╗ ███████╗██╗   ██╗", false);
                Debug.log("╚══██╔══╝██╔══██╗██╔══██╗██╔════╝██║   ██║", false);
                Debug.log("   ██║   ███████║██║  ██║█████╗  ██║   ██║", false);
                Debug.log("   ██║   ██╔══██║██║  ██║██╔══╝  ██║   ██║", false);
                Debug.log("   ██║   ██║  ██║██████╔╝███████╗╚██████╔╝", false);
                Debug.log("   ╚═╝   ╚═╝  ╚═╝╚═════╝ ╚══════╝ ╚═════╝", false);
                Debug.log("&fFeito por tadeu @zypj", false);
                Debug.log("&f&lgithub.com/zyypj", false);
                break;
        }
    }

    public enum Agency {
        MRK,
        MYSTICCODES,
        COMISSIONS
    }
}