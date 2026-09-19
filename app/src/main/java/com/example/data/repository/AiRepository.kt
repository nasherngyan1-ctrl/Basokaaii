package com.example.data.repository

import com.example.BuildConfig
import com.example.data.api.GeminiApiClient
import com.example.data.api.GeminiChunkResponse
import com.example.data.api.GeminiContent
import com.example.data.api.GeminiGenerationConfig
import com.example.data.api.GeminiPart
import com.example.data.api.GeminiRequest
import com.example.data.api.GeminiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

sealed interface AiEngineStatus {
  object AwaitingConfiguration : AiEngineStatus
  data class Connected(val modelName: String) : AiEngineStatus
  data class Error(val message: String) : AiEngineStatus
}

sealed interface GenerationChunk {
  data class TextChunk(val text: String) : GenerationChunk
  data class Completed(val fullText: String) : GenerationChunk
  data class Error(val throwable: Throwable) : GenerationChunk
}

sealed interface ImageGenerationResult {
  data class Success(
    val imageBytes: ByteArray,
    val mimeType: String,
    val caption: String?
  ) : ImageGenerationResult

  data class Error(
    val message: String,
    val isApiKeyMissing: Boolean = false,
    val rawError: String? = null
  ) : ImageGenerationResult
}

interface AiRepository {
  fun getStatus(): AiEngineStatus
  fun isApiKeyConfigured(): Boolean

  /**
   * Stream content generation from Gemini API
   */
  fun streamGenerateResponse(
    conversationHistory: List<GeminiContent>,
    systemInstructionText: String? = null
  ): Flow<GenerationChunk>

  /**
   * Generate an image from a text prompt using Google image generation models
   */
  suspend fun generateImage(
    prompt: String,
    aspectRatio: String = "1:1"
  ): ImageGenerationResult
}

class BasokaAiRepositoryImpl(
  private val geminiService: GeminiService = GeminiApiClient.service
) : AiRepository {

  private val adapter = GeminiApiClient.moshi.adapter(GeminiChunkResponse::class.java)

  override fun isApiKeyConfigured(): Boolean {
    val key = BuildConfig.GEMINI_API_KEY
    return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
  }

  override fun getStatus(): AiEngineStatus {
    return if (isApiKeyConfigured()) {
      AiEngineStatus.Connected("gemini-3.5-flash / gemini-2.5-flash-image")
    } else {
      AiEngineStatus.AwaitingConfiguration
    }
  }

  override suspend fun generateImage(
    prompt: String,
    aspectRatio: String
  ): ImageGenerationResult = withContext(Dispatchers.IO) {
    val apiKey = BuildConfig.GEMINI_API_KEY
    if (!isApiKeyConfigured()) {
      return@withContext ImageGenerationResult.Error(
        message = "GEMINI_API_KEY_NOT_CONFIGURED",
        isApiKeyMissing = true
      )
    }

    // First attempt: gemini-2.5-flash-image with imageConfig & responseModalities
    try {
      val imageRequest = GeminiRequest(
        contents = listOf(
          GeminiContent(
            parts = listOf(GeminiPart(text = prompt))
          )
        ),
        generationConfig = GeminiGenerationConfig(
          responseModalities = listOf("TEXT", "IMAGE"),
          imageConfig = com.example.data.api.GeminiImageConfig(aspectRatio = aspectRatio)
        )
      )

      val response = geminiService.generateContent(
        model = "gemini-2.5-flash-image",
        apiKey = apiKey,
        request = imageRequest
      )

      val parts = response.candidates?.firstOrNull()?.content?.parts.orEmpty()
      val imagePart = parts.firstOrNull { it.inlineData != null && !it.inlineData.data.isNullOrBlank() }
      val captionPart = parts.firstOrNull { !it.text.isNullOrBlank() }

      if (imagePart?.inlineData != null) {
        val decoded = android.util.Base64.decode(imagePart.inlineData.data, android.util.Base64.DEFAULT)
        return@withContext ImageGenerationResult.Success(
          imageBytes = decoded,
          mimeType = imagePart.inlineData.mimeType,
          caption = captionPart?.text
        )
      }
    } catch (e: Exception) {
      val errorStr = e.message.orEmpty()
      if (errorStr.contains("403") || errorStr.contains("API_KEY_INVALID")) {
        return@withContext ImageGenerationResult.Error(
          message = "INVALID_API_KEY",
          isApiKeyMissing = false,
          rawError = errorStr
        )
      }
      if (errorStr.contains("429")) {
        return@withContext ImageGenerationResult.Error(
          message = "QUOTA_EXCEEDED",
          isApiKeyMissing = false,
          rawError = errorStr
        )
      }
    }

    // Fallback attempt: imagen-3.0-generate-002
    try {
      val imagenReq = com.example.data.api.ImagenPredictRequest(
        instances = listOf(com.example.data.api.ImagenInstance(prompt = prompt)),
        parameters = com.example.data.api.ImagenParameters(sampleCount = 1, aspectRatio = aspectRatio)
      )
      val response = geminiService.predictImagen(
        model = "imagen-3.0-generate-002",
        apiKey = apiKey,
        request = imagenReq
      )
      val prediction = response.predictions?.firstOrNull()
      val base64Data = prediction?.bytesBase64Encoded
      if (!base64Data.isNullOrBlank()) {
        val decoded = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
        return@withContext ImageGenerationResult.Success(
          imageBytes = decoded,
          mimeType = prediction.mimeType ?: "image/png",
          caption = null
        )
      }
    } catch (e: Exception) {
      return@withContext ImageGenerationResult.Error(
        message = e.localizedMessage ?: "IMAGE_GENERATION_FAILED",
        isApiKeyMissing = false,
        rawError = e.message
      )
    }

    return@withContext ImageGenerationResult.Error(
      message = "EMPTY_IMAGE_RESPONSE",
      isApiKeyMissing = false
    )
  }

  override fun streamGenerateResponse(
    conversationHistory: List<GeminiContent>,
    systemInstructionText: String?
  ): Flow<GenerationChunk> = flow {
    val apiKey = BuildConfig.GEMINI_API_KEY
    if (!isApiKeyConfigured()) {
      emit(
        GenerationChunk.Error(
          IllegalStateException("GEMINI_API_KEY_NOT_CONFIGURED")
        )
      )
      return@flow
    }

    val request = GeminiRequest(
      contents = conversationHistory,
      systemInstruction = systemInstructionText?.let {
        GeminiContent(
          role = "system",
          parts = listOf(GeminiPart(text = it))
        )
      },
      generationConfig = GeminiGenerationConfig(
        temperature = 0.7f,
        topP = 0.95f,
        topK = 40
      )
    )

    try {
      val responseBody = geminiService.streamGenerateContent(
        apiKey = apiKey,
        alt = "sse",
        request = request
      )

      val fullResponseBuilder = StringBuilder()

      responseBody.byteStream().bufferedReader().use { reader ->
        var line: String?
        while (reader.readLine().also { line = it } != null) {
          val currentLine = line ?: continue
          val trimmed = currentLine.trim()

          // Server-Sent Events line parsing
          val jsonPayload = when {
            trimmed.startsWith("data:") -> trimmed.substring(5).trim()
            trimmed.startsWith("{") -> trimmed
            else -> null
          }

          if (!jsonPayload.isNullOrEmpty()) {
            try {
              val chunk = adapter.fromJson(jsonPayload)
              val candidate = chunk?.candidates?.firstOrNull()
              val text = candidate?.content?.parts?.firstOrNull()?.text

              if (!text.isNullOrEmpty()) {
                fullResponseBuilder.append(text)
                emit(GenerationChunk.TextChunk(text))
              }
            } catch (_: Exception) {
              // Ignore partial JSON parsing chunk errors in SSE stream
            }
          }
        }
      }

      val finalText = fullResponseBuilder.toString()
      if (finalText.isNotEmpty()) {
        emit(GenerationChunk.Completed(finalText))
      } else {
        emit(GenerationChunk.Error(IllegalStateException("EMPTY_AI_RESPONSE")))
      }
    } catch (e: Exception) {
      emit(GenerationChunk.Error(e))
    }
  }.flowOn(Dispatchers.IO)
}
