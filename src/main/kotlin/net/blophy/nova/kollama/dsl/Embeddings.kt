package net.blophy.nova.kollama.dsl

import net.blophy.nova.kollama.datamodels.EmbeddingsRequest

@KOllamaDSL
class EmbeddingsRequestBuilder {
    var model: String = ""
    var input: String = ""
    var truncate: Boolean = true
    var dimensions: Int? = null
    var keepAliveFor: String? = null
    private var optionsBuilder: GenerateOptionsBuilder? = null

    fun options(block: GenerateOptionsBuilder.() -> Unit) {
        optionsBuilder = GenerateOptionsBuilder().apply(block)
    }

    fun build(): EmbeddingsRequest = EmbeddingsRequest(
        model = model,
        input = input,
        truncate = truncate,
        dimensions = dimensions,
        keepAliveFor = keepAliveFor,
        options = optionsBuilder?.build()
    )
}

fun embeddingsRequest(block: EmbeddingsRequestBuilder.() -> Unit): EmbeddingsRequest =
    EmbeddingsRequestBuilder().apply(block).build()

