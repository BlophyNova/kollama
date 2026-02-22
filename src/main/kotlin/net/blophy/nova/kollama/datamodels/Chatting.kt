package net.blophy.nova.kollama.datamodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val role: ChatRole,
    val content: String,
    val thinking: String? = null,
    val images: List<String>? = null,
    val callableTools: List<ToolsCallWrapper>? = null
)



@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val tools: List<Map<ToolType, ToolFunction>>? = null,
    val format: String? = null,
    val options: GenerateOptions? = null,
    val stream: Boolean = false,
    @SerialName("think")
    val thinkMode: ThinkMode? = null,
    @SerialName("keep_alive")
    val keepAliveFor: String? = null, // Duration to keep model loaded
    @SerialName("logprobs")
    val showLogprobs: Boolean = false,
    @SerialName("top_logprobs")
    val showTopLogprobs: Int = 0
)

@Serializable
data class ChatResponse(
    val model: String,
    @SerialName("created_at")
    val createdAt: String,
    val message: ChatMessage,
    val done: Boolean,
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