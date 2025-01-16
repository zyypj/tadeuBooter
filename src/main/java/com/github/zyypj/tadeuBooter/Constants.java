package com.github.zyypj.tadeuBooter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Constants {
    public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    public static String NAME = "tadeuBooter";
}