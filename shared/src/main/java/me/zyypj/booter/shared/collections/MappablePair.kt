package me.zyypj.booter.shared.collections

data class MappablePair<F, S>(
    var first: F,
    var second: S
) {
    fun map(
        firstSupplier: (() -> F)? = null,
        secondSupplier: (() -> S)? = null
    ): MappablePair<F, S> = apply {
        firstSupplier?.invoke()?.let { first = it }
        secondSupplier?.invoke()?.let { second = it }
    }
}