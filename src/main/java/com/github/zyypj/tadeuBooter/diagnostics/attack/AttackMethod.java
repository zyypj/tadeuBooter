package com.github.zyypj.tadeuBooter.diagnostics.attack;

import lombok.Getter;

@Getter
public abstract class AttackMethod {
    protected final String target;
    protected final int duration;
    protected final int intensity;

    public AttackMethod(String target, int duration, int intensity) {
        this.target = target;
        this.duration = duration;
        this.intensity = intensity;
    }

    public abstract void start();
    public abstract void stop();
}
