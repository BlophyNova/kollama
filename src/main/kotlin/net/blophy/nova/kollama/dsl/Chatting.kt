package net.blophy.nova.kollama.dsl

import kotlinx.serialization.json.JsonElement
import net.blophy.nova.kollama.datamodels.ChatMessage
import net.blophy.nova.kollama.datamodels.ChatRequest
import net.blophy.nova.kollama.datamodels.ChatRole
import net.blophy.nova.kollama.datamodels.ThinkMode
import net.blophy.nova.kollama.datamodels.ToolFunction
import net.blophy.nova.kollama.datamodels.ToolType
import net.blophy.nova.kollama.datamodels.ToolsCallWrapper

@KOllamaDSL
class ToolFunctionBuilder {
    var name: String = ""
    var description: String? = null
    var arguments: JsonElement? = null

    fun build(): ToolFunction = ToolFunction(
        name = name,
        description = description,
        arguments = arguments
    )
}

fun toolFunction(block: ToolFunctionBuilder.() -> Unit): ToolFunction =
    ToolFunctionBuilder().apply(block).build()

@KOllamaDSL
class ChatMessageBuilder {
    var role: ChatRole = ChatRole.User
    var content: String = ""
    var images: List<String>? = null
    private val tools = mutableListOf<ToolsCallWrapper>()

    fun tools(vararg functions: ToolFunction) {
        tools.addAll(functions.map { ToolsCallWrapper(it) })
    }

    fun build(): ChatMessage = ChatMessage(
        role = role,
        content = content,
        images = images,
        callableTools = tools.takeIf { it.isNotEmpty() }
    )
}

fun message(block: ChatMessageBuilder.() -> Unit): ChatMessage =
    ChatMessageBuilder().apply(block).build()

@KOllamaDSL
class ChatRequestBuilder {
    var model: String = ""
    private val messageList = mutableListOf<ChatMessage>()
    private val toolMaps = mutableListOf<Map<ToolType, ToolFunction>>()
    var format: String? = null
    private var optionsBuilder: GenerateOptionsBuilder? = null
    var stream: Boolean = false
    var thinkMode: ThinkMode? = null
    var keepAliveFor: String? = null
    var showLogprobs: Boolean = false
    var showTopLogprobs: Int = 0

    fun message(block: ChatMessageBuilder.() -> Unit) {
        messageList.add(ChatMessageBuilder().apply(block).build())
    }

    fun tool(function: ToolFunction) {
        toolMaps.add(mapOf(ToolType.Function to function))
    }

    fun tool(block: ToolFunctionBuilder.() -> Unit) {
        tool(toolFunction(block))
    }

    fun options(block: GenerateOptionsBuilder.() -> Unit) {
        optionsBuilder = GenerateOptionsBuilder().apply(block)
    }

    fun build(): ChatRequest = ChatRequest(
        model = model,
        messages = messageList,
        tools = toolMaps.takeIf { it.isNotEmpty() },
        format = format,
        options = optionsBuilder?.build(),
        stream = stream,
        thinkMode = thinkMode,
        keepAliveFor = keepAliveFor,
        showLogprobs = showLogprobs,
        showTopLogprobs = showTopLogprobs
    )
}

fun chatRequest(block: ChatRequestBuilder.() -> Unit): ChatRequest =
    ChatRequestBuilder().apply(block).build()