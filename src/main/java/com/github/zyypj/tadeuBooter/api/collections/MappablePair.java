package com.github.zyypj.tadeuBooter.api.collections;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.annotation.Nullable;
import java.util.function.Supplier;

@Data
@AllArgsConstructor
public class MappablePair<F, S> {

    private F first;
    private S second;

    public MappablePair<F, S> map(@Nullable Supplier<F> first, @Nullable Supplier<S> second) {
        if (first != null) this.first = first.get();
        if (second != null) this.second = second.get();
        return this;
    }

    public MappablePair<F, S> map(@Nullable Supplier<F> first) {
        if (first != null) this.first = first.get();
        return this;
    }
}