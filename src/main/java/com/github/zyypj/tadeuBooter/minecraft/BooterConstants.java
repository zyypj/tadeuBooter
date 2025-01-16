package com.github.zyypj.tadeuBooter.minecraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BooterConstants {
    public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    public static String BASE_PACKAGE = "com.github.zyypj.tadeuBooter";

    public static void setBasePackage(String newPackage) {
        BASE_PACKAGE = newPackage;
    }
}