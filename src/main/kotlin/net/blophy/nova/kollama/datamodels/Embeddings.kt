package net.blophy.nova.kollama.datamodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmbeddingsRequest(
    val model: String,
    val input: String,
    val truncate: Boolean = true,
    val dimensions: Int? = null,
    @SerialName("keep_alive")
    val keepAliveFor: String? = null,
    val options: GenerateOptions? = null
)

@Serializable
data class EmbeddingsResponse(
    val model: String,
    val embedding: List<List<Float>>,
    val totalDuration: Long,
    val loadDuration: Long,
    val evaluatedInputCount: Int
)