package com.github.zyypj.tadeuBooter.minecraft.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Classe simples para armazenar um par de dois objetos.
 *
 * @param <F> o tipo do primeiro objeto
 * @param <S> o tipo do segundo objeto
 */
@Data
@AllArgsConstructor
public class Pair<F, S> {

    private F first;
    private S second;

    /**
     * Mapeia este par usando os fornecedores fornecidos para definir os objetos primeiro e segundo.
     *
     * @param first  o fornecedor para definir o primeiro objeto
     * @param second o fornecedor para definir o segundo objeto
     * @return este par
     */
    public Pair<F, S> map(@Nullable Supplier<F> first, @Nullable Supplier<S> second) {
        if (first != null)
            this.first = first.get();
        if (second != null)
            this.second = second.get();
        return this;
    }

    /**
     * Mapeia este par usando o fornecedor fornecido para definir o primeiro objeto.
     *
     * @param first o fornecedor para definir o primeiro objeto
     * @return este par
     */
    public Pair<F, S> map(@Nullable Supplier<F> first) {
        if (first != null)
            this.first = first.get();
        return this;
    }
}