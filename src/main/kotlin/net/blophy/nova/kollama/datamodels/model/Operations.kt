package net.blophy.nova.kollama.datamodels.model

import net.blophy.nova.kollama.datamodels.ChatMessage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class PullModelRequest(
    val name: String,
    val insecure: Boolean = false,
    val stream: Boolean = true
)

@Serializable
data class PushModelRequest(
    val name: String,
    val insecure: Boolean = false,
    val stream: Boolean = true
)

@Serializable
data class CreateModelRequest(
    val name: String,
    val from: String? = null,
    val template: String? = null,
    val license: List<String>? = null,
    @SerialName("system")
    val systemPrompt: String? = null,
    val parameters: Map<String, JsonElement>? = null,
    @SerialName("messages")
    val messageHistory: List<ChatMessage>? = null,
    val quantize: String? = null,
    val stream: Boolean = true
)

@Serializable
data class DeleteModelRequest(
    val name: String
)

@Serializable
data class CopyModelRequest(
    val source: String,
    val destination: String
)