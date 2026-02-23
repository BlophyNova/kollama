package net.blophy.nova.kollama.dsl

import net.blophy.nova.kollama.datamodels.GenerateOptions
import net.blophy.nova.kollama.datamodels.GenerateRequest
import net.blophy.nova.kollama.datamodels.ThinkMode

@KOllamaDSL
class GenerateOptionsBuilder {
    var seed: Int? = null
    var temperature: Float? = null
    var topK: Int? = null
    var topP: Float? = null
    var minP: Float? = null
    private val stopTokens = mutableListOf<String>()
    var contextSize: Int? = null
    var maxGenerate: Int? = null

    fun stop(vararg tokens: String) {
        stopTokens.addAll(tokens)
    }

    fun build(): GenerateOptions = GenerateOptions(
        seed = seed,
        temperature = temperature,
        topK = topK,
        topP = topP,
        minP = minP,
        stop = stopTokens.takeIf { it.isNotEmpty() },
        contextSize = contextSize,
        maxGenerate = maxGenerate
    )
}

@KOllamaDSL
class GenerateRequestBuilder {
    var model: String = ""
    var prompt: String = ""
    var suffix: String? = null
    private val imageList = mutableListOf<String>()
    var format: String = "json"
    var systemPrompt: String? = null
    var thinkMode: ThinkMode? = null
    var raw: Boolean = false
    var keepAliveFor: String? = null
    private var optionsBuilder: GenerateOptionsBuilder? = null
    var showLogprobs: Boolean = false
    var showTopLogprobs: Int = 0

    fun images(vararg urls: String) {
        imageList.addAll(urls)
    }

    fun options(block: GenerateOptionsBuilder.() -> Unit) {
        optionsBuilder = GenerateOptionsBuilder().apply(block)
    }

    fun build(): GenerateRequest = GenerateRequest(
        model = model,
        prompt = prompt,
        suffix = suffix,
        images = imageList.takeIf { it.isNotEmpty() },
        format = format,
        systemPrompt = systemPrompt,
        stream = false,
        thinkMode = thinkMode,
        raw = raw,
        keepAliveFor = keepAliveFor,
        options = optionsBuilder?.build(),
        showLogprobs = showLogprobs,
        showTopLogprobs = showTopLogprobs
    )
}

fun generateRequest(block: GenerateRequestBuilder.() -> Unit): GenerateRequest =
    GenerateRequestBuilder().apply(block).build()
