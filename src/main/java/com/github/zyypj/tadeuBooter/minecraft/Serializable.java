package com.github.zyypj.tadeuBooter.minecraft;

public class Serializable {
    String getAsJson() {
        return BooterConstants.GSON.toJson(this, this.getClass());
    }
}
