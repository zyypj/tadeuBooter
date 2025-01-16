package com.github.zyypj.tadeuBooter.database.controler.player.reason;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class BasicUnloadReason implements UnloadReason {
    private final String name;

    @Override
    public String name() {
        return name;
    }
}