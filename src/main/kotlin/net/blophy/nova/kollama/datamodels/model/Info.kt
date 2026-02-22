package net.blophy.nova.kollama.datamodels.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ModelDetails(
    @SerialName("parent_model")
    val parent: String? = null,
    val format: String,
    val family: String,
    val families: List<String>?,
    @SerialName("parameter_size")
    val parameterSize: String,
    @SerialName("quantization_level")
    val quantization: String
)

@Serializable
data class Model(
    val name: String,
    val model: String,
    @SerialName("remote_model")
    val remoteModelName: String? = null,
    @SerialName("remote_host")
    val remoteHost: String? = null,
    @SerialName("modified_at")
    val modifiedAt: String,
    val size: Long,
    val digest: String,
    val details: ModelDetails
)

@Serializable
data class RunningModel(
    val name: String,
    val model: String,
    val size: Long,
    val digest: String,
    val details: ModelDetails,
    @SerialName("size_vram")
    val vRAMUsage: Int,
    @SerialName("context_length")
    val contextLength: Int
)

@Serializable
data class ShowModelRequest(
    val name: String,
    val verbose: Boolean = false
)

@Serializable
data class ShowModelResponse(
    val parameters: String? = null,
    val license: String? = null,
    @SerialName("modelfile")
    val modelFileAt: String? = null,
    val details: ModelDetails? = null,
    val template: String? = null,
    val capabilities: List<String>? = null,
    @SerialName("model_info")
    val extraInfo: JsonElement? = null
)

@Serializable
data class ListModelsResponse(
    val models: List<Model>
)

@Serializable
data class ListRunningModelResponse(
    val models: List<RunningModel>
)
