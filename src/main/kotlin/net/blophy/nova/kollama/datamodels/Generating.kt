package net.blophy.nova.kollama.datamodels

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class GenerateRequest(
    val model: String,
    val prompt: String,
    val suffix: String? = null,
    val images: List<String>? = null,
    val format: String = "json",
    @SerialName("system")
    val systemPrompt: String? = null,
    @EncodeDefault
    val stream: Boolean = false,
    @SerialName("think")
    val thinkMode: ThinkMode? = null,
    val raw: Boolean = false,
    val keepAliveFor: String? = null, // Duration to keep model loaded (e.g., "5m", "30m")
    val options: GenerateOptions? = null,
    @SerialName("logprobs")
    val showLogprobs: Boolean = false,
    @SerialName("top_logprobs")
    val showTopLogprobs: Int = 0
)

@Serializable
data class GenerateResponse(
    val model: String,
    @SerialName("created_at")
    val createdAt: String,
    val response: String,
    val thinking: String,
    val done: Boolean,
    val context: List<Int>? = null,
    @SerialName("done_reason")
    val doneReason: String? = null,
    @SerialName("total_duration")
    val totalDuration: Long? = null,
    @SerialName("load_duration")
    val loadDuration: Long? = null,
    @SerialName("prompt_eval_count")
    val evaluatedInputTokens: Int? = null,
    @SerialName("prompt_eval_duration")
    val inputEvalDuration: Long? = null,
    @SerialName("eval_count")
    val outputTokens: Int? = null,
    @SerialName("eval_duration")
    val evalDuration: Long? = null,
    val logprobs: List<Logprob>? = null
)