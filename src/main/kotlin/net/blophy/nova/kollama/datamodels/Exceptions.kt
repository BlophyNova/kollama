package net.blophy.nova.kollama.datamodels

sealed class OllamaException(message: String, cause: Throwable? = null) : Exception(message, cause)

class OllamaBadRequestException(message: String) : OllamaException(message)
class OllamaNotFoundException(message: String) : OllamaException(message)
class OllamaServerException(message: String) : OllamaException(message)
class OllamaHTTPException(message: String) : OllamaException(message)
