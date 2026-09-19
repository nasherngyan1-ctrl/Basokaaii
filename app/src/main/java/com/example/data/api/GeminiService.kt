package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

@JsonClass(generateAdapter = true)
data class GeminiRequest(
  @Json(name = "contents") val contents: List<GeminiContent>,
  @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null,
  @Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
  @Json(name = "role") val role: String? = null,
  @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiInlineData(
  @Json(name = "mimeType") val mimeType: String,
  @Json(name = "data") val data: String
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
  @Json(name = "text") val text: String? = null,
  @Json(name = "inlineData") val inlineData: GeminiInlineData? = null
)

@JsonClass(generateAdapter = true)
data class GeminiImageConfig(
  @Json(name = "aspectRatio") val aspectRatio: String? = "1:1",
  @Json(name = "imageSize") val imageSize: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
  @Json(name = "temperature") val temperature: Float? = 0.7f,
  @Json(name = "topP") val topP: Float? = 0.95f,
  @Json(name = "topK") val topK: Int? = 40,
  @Json(name = "responseModalities") val responseModalities: List<String>? = null,
  @Json(name = "imageConfig") val imageConfig: GeminiImageConfig? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
  @Json(name = "content") val content: GeminiContent? = null,
  @Json(name = "finishReason") val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiChunkResponse(
  @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiGenerateResponse(
  @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

// Imagen 3 fallback structures
@JsonClass(generateAdapter = true)
data class ImagenInstance(
  @Json(name = "prompt") val prompt: String
)

@JsonClass(generateAdapter = true)
data class ImagenParameters(
  @Json(name = "sampleCount") val sampleCount: Int? = 1,
  @Json(name = "aspectRatio") val aspectRatio: String? = "1:1"
)

@JsonClass(generateAdapter = true)
data class ImagenPredictRequest(
  @Json(name = "instances") val instances: List<ImagenInstance>,
  @Json(name = "parameters") val parameters: ImagenParameters? = null
)

@JsonClass(generateAdapter = true)
data class ImagenPrediction(
  @Json(name = "bytesBase64Encoded") val bytesBase64Encoded: String? = null,
  @Json(name = "mimeType") val mimeType: String? = "image/png"
)

@JsonClass(generateAdapter = true)
data class ImagenPredictResponse(
  @Json(name = "predictions") val predictions: List<ImagenPrediction>? = null
)

interface GeminiService {
  @POST("v1beta/models/{model}:streamGenerateContent")
  @Streaming
  suspend fun streamGenerateContent(
    @Path("model") model: String = "gemini-3.5-flash",
    @Query("key") apiKey: String,
    @Query("alt") alt: String = "sse",
    @Body request: GeminiRequest
  ): ResponseBody

  @POST("v1beta/models/{model}:generateContent")
  suspend fun generateContent(
    @Path("model") model: String,
    @Query("key") apiKey: String,
    @Body request: GeminiRequest
  ): GeminiGenerateResponse

  @POST("v1beta/models/{model}:predict")
  suspend fun predictImagen(
    @Path("model") model: String = "imagen-3.0-generate-002",
    @Query("key") apiKey: String,
    @Body request: ImagenPredictRequest
  ): ImagenPredictResponse
}
