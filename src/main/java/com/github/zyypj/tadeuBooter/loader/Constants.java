package com.github.zyypj.tadeuBooter.loader;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Constants {
    public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    public static String BASE_PACKAGE = "gg.discord.mrkk.core";
    public static String NAME = "mrk-core";

    public static void setBasePackage(String newPackage) {
        BASE_PACKAGE = newPackage;
    }
}