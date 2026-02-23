package net.blophy.nova.kollama.dsl

import kotlinx.serialization.json.JsonElement
import net.blophy.nova.kollama.datamodels.ChatMessage
import net.blophy.nova.kollama.datamodels.model.*

@KOllamaDSL
class ShowModelRequestBuilder {
    var name: String = ""
    var verbose: Boolean = false

    fun build(): ShowModelRequest = ShowModelRequest(
        name = name,
        verbose = verbose
    )
}

fun showModelRequest(block: ShowModelRequestBuilder.() -> Unit): ShowModelRequest =
    ShowModelRequestBuilder().apply(block).build()

@KOllamaDSL
class PullModelRequestBuilder {
    var name: String = ""
    var insecure: Boolean = false
    var stream: Boolean = true

    fun build(): PullModelRequest = PullModelRequest(
        name = name,
        insecure = insecure,
        stream = stream
    )
}

fun pullModelRequest(block: PullModelRequestBuilder.() -> Unit): PullModelRequest =
    PullModelRequestBuilder().apply(block).build()

@KOllamaDSL
class PushModelRequestBuilder {
    var name: String = ""
    var insecure: Boolean = false
    var stream: Boolean = true

    fun build(): PushModelRequest = PushModelRequest(
        name = name,
        insecure = insecure,
        stream = stream
    )
}

fun pushModelRequest(block: PushModelRequestBuilder.() -> Unit): PushModelRequest =
    PushModelRequestBuilder().apply(block).build()

@KOllamaDSL
class CreateModelRequestBuilder {
    var name: String = ""
    var from: String? = null
    var template: String? = null
    var license: List<String>? = null
    var systemPrompt: String? = null
    var parameters: Map<String, JsonElement>? = null
    var messageHistory: List<ChatMessage>? = null
    var quantize: String? = null

    fun build(): CreateModelRequest = CreateModelRequest(
        name = name,
        from = from,
        template = template,
        license = license,
        systemPrompt = systemPrompt,
        parameters = parameters,
        messageHistory = messageHistory,
        quantize = quantize,
        stream = false
    )
}

fun createModelRequest(block: CreateModelRequestBuilder.() -> Unit): CreateModelRequest =
    CreateModelRequestBuilder().apply(block).build()

@KOllamaDSL
class DeleteModelRequestBuilder {
    var name: String = ""

    fun build(): DeleteModelRequest = DeleteModelRequest(name = name)
}

fun deleteModelRequest(block: DeleteModelRequestBuilder.() -> Unit): DeleteModelRequest =
    DeleteModelRequestBuilder().apply(block).build()

@KOllamaDSL
class CopyModelRequestBuilder {
    var source: String = ""
    var destination: String = ""

    fun build(): CopyModelRequest = CopyModelRequest(
        source = source,
        destination = destination
    )
}

fun copyModelRequest(block: CopyModelRequestBuilder.() -> Unit): CopyModelRequest =
    CopyModelRequestBuilder().apply(block).build()