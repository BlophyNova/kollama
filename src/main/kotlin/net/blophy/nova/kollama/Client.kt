package net.blophy.nova.kollama

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import net.blophy.nova.kollama.datamodels.*
import net.blophy.nova.kollama.datamodels.model.*
import net.blophy.nova.kollama.dsl.*

/**
 * net.blophy.nova.kollama.KOllamaClient - Kotlin wrapper for Ollama API.
 *
 * This class provides a convenient way to interact with Ollama's API from Kotlin applications.
 * It supports model generation, chat, management (list, show, pull, push, create, delete, copy),
 * and embeddings.
 *
 * API methods are available in three variants:
 * 1. **Direct request object variant** – accepts a request data class (e.g., [GenerateRequest]).
 * 2. **Convenience variant** – accepts simple parameters for the most common use cases.
 * 3. **DSL builder variant** – uses a type-safe builder DSL to construct the request (recommended for complex requests).
 *
 * @param baseUrl The base URL of the Ollama server. Defaults to "http://localhost:11434".
 * @param httpClient The HTTP client used for making requests. If not provided, a default client
 *                   with JSON support, logging, timeouts, redirect handling, and error validation
 *                   is created.
 */
class KOllamaClient(
    private val baseUrl: String = "http://localhost:11434",
    private val httpClient: HttpClient = createDefaultHttpClient()
) {

    companion object {
        /**
         * Creates a default HTTP client with JSON serialization and logging.
         *
         * The client includes:
         * - Content negotiation with JSON (pretty print, lenient mode)
         * - Logging (INFO level)
         * - Timeouts: request 60s, connect 30s, socket 60s
         * - Redirect handling
         * - Error validation mapping HTTP status codes to custom exceptions.
         *
         * @return A configured [HttpClient] instance.
         */
        private fun createDefaultHttpClient(): HttpClient {
            return HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                        encodeDefaults = true
                    })
                }
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.INFO
                }

                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 30000
                    socketTimeoutMillis = 60000
                }

                install(HttpRedirect)

                HttpResponseValidator {
                    handleResponseException { cause ->
                        throw OllamaHTTPException("HTTP request failed: ${cause.message}")
                    }

                    handleResponseExceptionWithRequest { _, response ->
                        val status = response.content.status?.value
                        val message = response.content

                        when (status) {
                            400 -> throw OllamaBadRequestException("Bad Request: $message")
                            404 -> throw OllamaNotFoundException("Not Found: $message")
                            500 -> throw OllamaServerException("Internal Server Error: $message")
                            else -> throw OllamaHTTPException("HTTP Error $status: $message")
                        }
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // 原始方法（基于数据类参数）
    // -------------------------------------------------------------------------

    /**
     * Generates a response from the model based on the provided [GenerateRequest].
     *
     * This corresponds to the Ollama generate endpoint (`/api/generate`).
     *
     * @param request The [GenerateRequest] containing the model name, prompt, and optional parameters.
     * @return A [GenerateResponse] containing the generated text and metadata.
     * @throws OllamaHTTPException If the HTTP request fails or a non‑success status code is returned.
     */
    suspend fun generate(request: GenerateRequest): GenerateResponse {
        val response = httpClient.post("$baseUrl/api/generate") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body()
    }

    /**
     * Generates a response from the model using a simplified parameter list.
     *
     * This is a convenience overload of [generate] that creates a [GenerateRequest] for you.
     *
     * @param model The name of the model to use (e.g., "llama2").
     * @param prompt The input prompt for generation.
     * @param system Optional system prompt to set the context.
     * @return A [GenerateResponse] containing the generated text and metadata.
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun generate(model: String, prompt: String, system: String? = null): GenerateResponse {
        val request = GenerateRequest(
            model = model,
            prompt = prompt,
            systemPrompt = system
        )
        return generate(request)
    }

    /**
     * Sends a chat message to the model and receives a response.
     *
     * This corresponds to the Ollama chat endpoint (`/api/chat`).
     *
     * @param request The [ChatRequest] containing the model name, conversation messages, and options.
     * @return A [ChatResponse] containing the model's reply message and metadata.
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun chat(request: ChatRequest): ChatResponse {
        val response = httpClient.post("$baseUrl/api/chat") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body()
    }

    /**
     * Sends a chat message to the model using a simplified parameter list.
     *
     * This is a convenience overload of [chat] that creates a [ChatRequest] for you.
     *
     * @param model The name of the model to use.
     * @param messages A list of [ChatMessage] representing the conversation history.
     * @return A [ChatResponse] containing the model's reply message and metadata.
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun chat(model: String, messages: List<ChatMessage>): ChatResponse {
        val request = ChatRequest(
            model = model,
            messages = messages
        )
        return chat(request)
    }

    /**
     * Lists all available models on the Ollama server.
     *
     * This corresponds to the Ollama tags endpoint (`/api/tags`).
     *
     * @return A [ListModelsResponse] containing a list of model names and details.
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun listModels(): ListModelsResponse {
        val response = httpClient.get("$baseUrl/api/tags")
        return response.body()
    }

    /**
     * Shows detailed information about a specific model.
     *
     * This corresponds to the Ollama show endpoint (`/api/show`).
     *
     * @param request The [ShowModelRequest] containing the model name.
     * @return A [ShowModelResponse] with the model's details (modelfile, parameters, template, etc.).
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun showModel(request: ShowModelRequest): ShowModelResponse {
        val response = httpClient.post("$baseUrl/api/show") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body()
    }

    /**
     * Shows detailed information about a specific model using the model name directly.
     *
     * This is a convenience overload of [showModel].
     *
     * @param name The name of the model.
     * @return A [ShowModelResponse] with the model's details.
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun showModel(name: String): ShowModelResponse {
        val request = ShowModelRequest(name = name)
        return showModel(request)
    }

    /**
     * Pulls (downloads) a model from the registry.
     *
     * This corresponds to the Ollama pull endpoint (`/api/pull`).
     *
     * @param request The [PullModelRequest] containing the model name and optional insecure flag.
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun pullModel(request: PullModelRequest) {
        httpClient.post("$baseUrl/api/pull") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    /**
     * Pulls (downloads) a model from the registry using the model name directly.
     *
     * This is a convenience overload of [pullModel].
     *
     * @param name The name of the model to pull.
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun pullModel(name: String) {
        val request = PullModelRequest(name = name)
        pullModel(request)
    }

    /**
     * Pushes a model to the registry.
     *
     * This corresponds to the Ollama push endpoint (`/api/push`).
     *
     * @param request The [PushModelRequest] containing the model name and optional insecure flag.
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun pushModel(request: PushModelRequest) {
        httpClient.post("$baseUrl/api/push") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    /**
     * Creates a new model from a Modelfile.
     *
     * This corresponds to the Ollama create endpoint (`/api/create`).
     *
     * @param request The [CreateModelRequest] containing the model name and Modelfile content.
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun createModel(request: CreateModelRequest) {
        httpClient.post("$baseUrl/api/create") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    /**
     * Deletes a model from the local server.
     *
     * This corresponds to the Ollama delete endpoint (`/api/delete`).
     *
     * @param request The [DeleteModelRequest] containing the name of the model to delete.
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun deleteModel(request: DeleteModelRequest) {
        httpClient.delete("$baseUrl/api/delete") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    /**
     * Deletes a model from the local server using the model name directly.
     *
     * This is a convenience overload of [deleteModel].
     *
     * @param name The name of the model to delete.
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun deleteModel(name: String) {
        val request = DeleteModelRequest(name = name)
        deleteModel(request)
    }

    /**
     * Copies a model to a new name.
     *
     * This corresponds to the Ollama copy endpoint (`/api/copy`).
     *
     * @param request The [CopyModelRequest] containing the source and destination model names.
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun copyModel(request: CopyModelRequest) {
        httpClient.post("$baseUrl/api/copy") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    /**
     * Copies a model to a new name using source and destination strings directly.
     *
     * This is a convenience overload of [copyModel].
     *
     * @param source The name of the existing model to copy.
     * @param destination The name for the new copy.
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun copyModel(source: String, destination: String) {
        val request = CopyModelRequest(
            source = source,
            destination = destination
        )
        copyModel(request)
    }

    /**
     * Generates embeddings from the model based on the provided input.
     *
     * This corresponds to the Ollama embeddings endpoint (`/api/embeddings`).
     *
     * @param request The [EmbeddingsRequest] containing the model name and input text.
     * @return An [EmbeddingsResponse] containing the generated embedding vector.
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun embeddings(request: EmbeddingsRequest): EmbeddingsResponse {
        val response = httpClient.post("$baseUrl/api/embeddings") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body()
    }

    /**
     * Generates embeddings from the model using a simplified parameter list.
     *
     * This is a convenience overload of [embeddings].
     *
     * @param model The name of the model to use.
     * @param prompt The input text for which to generate embeddings.
     * @return An [EmbeddingsResponse] containing the embedding vector.
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun embeddings(model: String, prompt: String): EmbeddingsResponse {
        val request = EmbeddingsRequest(
            model = model,
            input = prompt
        )
        return embeddings(request)
    }

    // -------------------------------------------------------------------------
    // DSL 扩展方法（使用带接收者的 lambda 构建请求）
    // -------------------------------------------------------------------------

    /**
     * Generates a response using a DSL builder.
     *
     * This method allows you to construct a [GenerateRequest] using a type-safe builder DSL.
     * It is equivalent to calling [generate] with a manually built request object.
     *
     * Example:
     * ```
     * val response = client.generate {
     *     model = "llama3"
     *     prompt = "Explain quantum computing"
     *     options {
     *         temperature = 0.7f
     *     }
     * }
     * ```
     *
     * @param block A lambda with receiver that configures a [GenerateRequestBuilder].
     * @return A [GenerateResponse] containing the generated text and metadata.
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun generate(block: GenerateRequestBuilder.() -> Unit): GenerateResponse =
        generate(generateRequest(block))

    /**
     * Sends a chat message using a DSL builder.
     *
     * This method allows you to construct a [ChatRequest] using a type-safe builder DSL.
     *
     * Example:
     * ```
     * val response = client.chat {
     *     model = "llama3"
     *     message {
     *         role = ChatRole.User
     *         content = "Hello!"
     *     }
     *     options {
     *         temperature = 0.5f
     *     }
     * }
     * ```
     *
     * @param block A lambda with receiver that configures a [net.blophy.nova.kollama.dsl.ChatRequestBuilder].
     * @return A [ChatResponse] containing the model's reply message and metadata.
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun chat(block: ChatRequestBuilder.() -> Unit): ChatResponse =
        chat(chatRequest(block))

    /**
     * Shows model details using a DSL builder.
     *
     * Example:
     * ```
     * val details = client.showModel {
     *     name = "llama3"
     *     verbose = true
     * }
     * ```
     *
     * @param block A lambda with receiver that configures a [ShowModelRequestBuilder].
     * @return A [ShowModelResponse] with the model's details.
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun showModel(block: ShowModelRequestBuilder.() -> Unit): ShowModelResponse =
        showModel(showModelRequest(block))

    /**
     * Pulls a model using a DSL builder.
     *
     * Example:
     * ```
     * client.pullModel {
     *     name = "llama3"
     *     insecure = false
     *     stream = true
     * }
     * ```
     *
     * @param block A lambda with receiver that configures a [PullModelRequestBuilder].
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun pullModel(block: PullModelRequestBuilder.() -> Unit) {
        pullModel(pullModelRequest(block))
    }

    /**
     * Pushes a model using a DSL builder.
     *
     * Example:
     * ```
     * client.pushModel {
     *     name = "my-username/llama3"
     *     insecure = false
     * }
     * ```
     *
     * @param block A lambda with receiver that configures a [PushModelRequestBuilder].
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun pushModel(block: PushModelRequestBuilder.() -> Unit) {
        pushModel(pushModelRequest(block))
    }

    /**
     * Creates a model using a DSL builder.
     *
     * Example:
     * ```
     * client.createModel {
     *     name = "my-custom-model"
     *     modelfile = """
     *         FROM llama3
     *         SYSTEM You are a coding assistant.
     *     """.trimIndent()
     * }
     * ```
     *
     * @param block A lambda with receiver that configures a [CreateModelRequestBuilder].
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun createModel(block: CreateModelRequestBuilder.() -> Unit) {
        createModel(createModelRequest(block))
    }

    /**
     * Deletes a model using a DSL builder.
     *
     * Example:
     * ```
     * client.deleteModel {
     *     name = "my-custom-model"
     * }
     * ```
     *
     * @param block A lambda with receiver that configures a [DeleteModelRequestBuilder].
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun deleteModel(block: DeleteModelRequestBuilder.() -> Unit) {
        deleteModel(deleteModelRequest(block))
    }

    /**
     * Copies a model using a DSL builder.
     *
     * Example:
     * ```
     * client.copyModel {
     *     source = "llama3"
     *     destination = "llama3-backup"
     * }
     * ```
     *
     * @param block A lambda with receiver that configures a [CopyModelRequestBuilder].
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun copyModel(block: CopyModelRequestBuilder.() -> Unit) {
        copyModel(copyModelRequest(block))
    }

    /**
     * Generates embeddings using a DSL builder.
     *
     * Example:
     * ```
     * val embeddings = client.embeddings {
     *     model = "llama3"
     *     input = "Hello world"
     *     options {
     *         temperature = 0.0f
     *     }
     * }
     * ```
     *
     * @param block A lambda with receiver that configures an [EmbeddingsRequestBuilder].
     * @return An [EmbeddingsResponse] containing the embedding vector.
     * @throws OllamaHTTPException If the HTTP request fails.
     */
    suspend fun embeddings(block: EmbeddingsRequestBuilder.() -> Unit): EmbeddingsResponse =
        embeddings(embeddingsRequest(block))

    /**
     * Retrieve the version of the Ollama.
     *
     * @return The [OllamaVersion] object containing the version of the Ollama service.
     */
    suspend fun ollamaVersion(): OllamaVersion {
        val response = httpClient.get("$baseUrl/api/version")
        return response.body()
    }

    /**
     * Closes the underlying HTTP client and releases any resources held by it.
     *
     * This method should be called when the [KOllamaClient] instance is no longer needed,
     * especially in long‑lived applications, to avoid leaking connections.
     */
    fun close() {
        httpClient.close()
    }
}
