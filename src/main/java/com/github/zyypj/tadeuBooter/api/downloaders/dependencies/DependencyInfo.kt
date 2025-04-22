package com.github.zyypj.tadeuBooter.api.downloaders.dependencies

/**
 * Informações essenciais de uma dependência.
 */
data class DependencyInfo(
    val name: String,
    val version: String,
    val downloadURL: String
)
