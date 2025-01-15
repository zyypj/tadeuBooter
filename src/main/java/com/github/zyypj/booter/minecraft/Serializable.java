package com.github.zyypj.booter.minecraft;

public class Serializable {
    String getAsJson() {
        return BooterConstants.GSON.toJson(this, this.getClass());
    }
}
