package net.blophy.nova.kollama.datamodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class TopLogprob(
    val token: String,
    val prob: Int,
    val count: List<Int>
)

@Serializable
data class Logprob(
    val token: String,
    val prob: Int,
    val count: List<Int>,
    @SerialName("top_logprobs")
    val tops: List<TopLogprob>? = null
)

@Serializable
data class GenerateOptions(
    val seed: Int? = null,
    val temperature: Float? = null,
    @SerialName("top_k")
    val topK: Int? = null,
    @SerialName("top_p")
    val topP: Float? = null,
    @SerialName("min_p")
    val minP: Float? = null,
    val stop: List<String>? = null,
    @SerialName("num_ctx")
    val contextSize: Int? = null,
    @SerialName("num_predict")
    val maxGenerate: Int? = null,
)

@Serializable
data class ToolFunction(
    val name: String,
    val description: String? = null,
    val arguments: JsonElement? = null,
)

@Serializable
data class ToolsCallWrapper(val f: ToolFunction)

@Serializable
data class OllamaVersion(val version: String)