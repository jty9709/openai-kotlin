package com.aallam.openai.api.admin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A time bucket containing usage results, one per requested group. */
@Serializable
public data class AdminUsageBucket<T>(
    @SerialName("start_time") public val startTime: Long,
    @SerialName("end_time") public val endTime: Long,
    @SerialName("results") public val results: List<T>,
)

/** A monetary amount returned in a cost result. */
@Serializable
public data class AdminCostAmount(
    @SerialName("value") public val value: Double? = null,
    @SerialName("currency") public val currency: String? = null,
)

/**
 * A result of completions usage.
 */
@Serializable
public data class AdminCompletionsUsage(
    @SerialName("input_tokens") public val inputTokens: Long? = null,
    @SerialName("output_tokens") public val outputTokens: Long? = null,
    @SerialName("input_cached_tokens") public val inputCachedTokens: Long? = null,
    @SerialName("input_audio_tokens") public val inputAudioTokens: Long? = null,
    @SerialName("input_text_tokens") public val inputTextTokens: Long? = null,
    @SerialName("input_image_tokens") public val inputImageTokens: Long? = null,
    @SerialName("input_uncached_tokens") public val inputUncachedTokens: Long? = null,
    @SerialName("output_audio_tokens") public val outputAudioTokens: Long? = null,
    @SerialName("output_text_tokens") public val outputTextTokens: Long? = null,
    @SerialName("output_image_tokens") public val outputImageTokens: Long? = null,
    @SerialName("num_model_requests") public val numModelRequests: Long? = null,
    @SerialName("project_id") public val projectId: String? = null,
    @SerialName("user_id") public val userId: String? = null,
    @SerialName("api_key_id") public val apiKeyId: String? = null,
    @SerialName("model") public val model: String? = null,
    @SerialName("batch") public val batch: Boolean? = null,
    @SerialName("service_tier") public val serviceTier: String? = null,
)

/** A page of completions usage. */
@Serializable
public data class AdminCompletionsUsagePage(
    @SerialName("data") public val data: List<AdminUsageBucket<AdminCompletionsUsage>> = emptyList(),
    @SerialName("has_more") public val hasMore: Boolean? = null,
    @SerialName("next_page") public val nextPage: String? = null,
)

/**
 * A result of embeddings usage.
 */
@Serializable
public data class AdminEmbeddingsUsage(
    @SerialName("input_tokens") public val inputTokens: Long? = null,
    @SerialName("num_model_requests") public val numModelRequests: Long? = null,
    @SerialName("project_id") public val projectId: String? = null,
    @SerialName("user_id") public val userId: String? = null,
    @SerialName("api_key_id") public val apiKeyId: String? = null,
    @SerialName("model") public val model: String? = null,
)

/** A page of embeddings usage. */
@Serializable
public data class AdminEmbeddingsUsagePage(
    @SerialName("data") public val data: List<AdminUsageBucket<AdminEmbeddingsUsage>> = emptyList(),
    @SerialName("has_more") public val hasMore: Boolean? = null,
    @SerialName("next_page") public val nextPage: String? = null,
)

/**
 * A result of moderations usage.
 */
@Serializable
public data class AdminModerationsUsage(
    @SerialName("input_tokens") public val inputTokens: Long? = null,
    @SerialName("num_model_requests") public val numModelRequests: Long? = null,
    @SerialName("project_id") public val projectId: String? = null,
    @SerialName("user_id") public val userId: String? = null,
    @SerialName("api_key_id") public val apiKeyId: String? = null,
    @SerialName("model") public val model: String? = null,
)

/** A page of moderations usage. */
@Serializable
public data class AdminModerationsUsagePage(
    @SerialName("data") public val data: List<AdminUsageBucket<AdminModerationsUsage>> = emptyList(),
    @SerialName("has_more") public val hasMore: Boolean? = null,
    @SerialName("next_page") public val nextPage: String? = null,
)

/**
 * A result of image usage.
 */
@Serializable
public data class AdminImagesUsage(
    @SerialName("images") public val images: Long? = null,
    @SerialName("num_model_requests") public val numModelRequests: Long? = null,
    @SerialName("project_id") public val projectId: String? = null,
    @SerialName("user_id") public val userId: String? = null,
    @SerialName("api_key_id") public val apiKeyId: String? = null,
    @SerialName("model") public val model: String? = null,
    @SerialName("size") public val size: String? = null,
    @SerialName("source") public val source: String? = null,
)

/** A page of image usage. */
@Serializable
public data class AdminImagesUsagePage(
    @SerialName("data") public val data: List<AdminUsageBucket<AdminImagesUsage>> = emptyList(),
    @SerialName("has_more") public val hasMore: Boolean? = null,
    @SerialName("next_page") public val nextPage: String? = null,
)

/**
 * A result of audio speech usage.
 */
@Serializable
public data class AdminAudioSpeechesUsage(
    @SerialName("characters") public val characters: Long? = null,
    @SerialName("num_model_requests") public val numModelRequests: Long? = null,
    @SerialName("project_id") public val projectId: String? = null,
    @SerialName("user_id") public val userId: String? = null,
    @SerialName("api_key_id") public val apiKeyId: String? = null,
    @SerialName("model") public val model: String? = null,
)

/** A page of audio speech usage. */
@Serializable
public data class AdminAudioSpeechesUsagePage(
    @SerialName("data") public val data: List<AdminUsageBucket<AdminAudioSpeechesUsage>> = emptyList(),
    @SerialName("has_more") public val hasMore: Boolean? = null,
    @SerialName("next_page") public val nextPage: String? = null,
)

/**
 * A result of audio transcription usage.
 */
@Serializable
public data class AdminAudioTranscriptionsUsage(
    @SerialName("seconds") public val seconds: Long? = null,
    @SerialName("num_model_requests") public val numModelRequests: Long? = null,
    @SerialName("project_id") public val projectId: String? = null,
    @SerialName("user_id") public val userId: String? = null,
    @SerialName("api_key_id") public val apiKeyId: String? = null,
    @SerialName("model") public val model: String? = null,
)

/** A page of audio transcription usage. */
@Serializable
public data class AdminAudioTranscriptionsUsagePage(
    @SerialName("data") public val data: List<AdminUsageBucket<AdminAudioTranscriptionsUsage>> = emptyList(),
    @SerialName("has_more") public val hasMore: Boolean? = null,
    @SerialName("next_page") public val nextPage: String? = null,
)

/**
 * A result of vector store usage.
 */
@Serializable
public data class AdminVectorStoresUsage(
    @SerialName("usage_bytes") public val usageBytes: Long? = null,
    @SerialName("project_id") public val projectId: String? = null,
)

/** A page of vector store usage. */
@Serializable
public data class AdminVectorStoresUsagePage(
    @SerialName("data") public val data: List<AdminUsageBucket<AdminVectorStoresUsage>> = emptyList(),
    @SerialName("has_more") public val hasMore: Boolean? = null,
    @SerialName("next_page") public val nextPage: String? = null,
)

/**
 * A result of code interpreter session usage.
 */
@Serializable
public data class AdminCodeInterpreterSessionsUsage(
    @SerialName("num_sessions") public val numSessions: Long? = null,
    @SerialName("project_id") public val projectId: String? = null,
)

/** A page of code interpreter session usage. */
@Serializable
public data class AdminCodeInterpreterSessionsUsagePage(
    @SerialName("data") public val data: List<AdminUsageBucket<AdminCodeInterpreterSessionsUsage>> = emptyList(),
    @SerialName("has_more") public val hasMore: Boolean? = null,
    @SerialName("next_page") public val nextPage: String? = null,
)

/**
 * A result of file search call usage.
 */
@Serializable
public data class AdminFileSearchCallsUsage(
    @SerialName("num_requests") public val numRequests: Long? = null,
    @SerialName("project_id") public val projectId: String? = null,
    @SerialName("user_id") public val userId: String? = null,
    @SerialName("api_key_id") public val apiKeyId: String? = null,
    @SerialName("vector_store_id") public val vectorStoreId: String? = null,
)

/** A page of file search call usage. */
@Serializable
public data class AdminFileSearchCallsUsagePage(
    @SerialName("data") public val data: List<AdminUsageBucket<AdminFileSearchCallsUsage>> = emptyList(),
    @SerialName("has_more") public val hasMore: Boolean? = null,
    @SerialName("next_page") public val nextPage: String? = null,
)

/**
 * A result of web search call usage.
 */
@Serializable
public data class AdminWebSearchCallsUsage(
    @SerialName("num_model_requests") public val numModelRequests: Long? = null,
    @SerialName("num_requests") public val numRequests: Long? = null,
    @SerialName("project_id") public val projectId: String? = null,
    @SerialName("user_id") public val userId: String? = null,
    @SerialName("api_key_id") public val apiKeyId: String? = null,
    @SerialName("model") public val model: String? = null,
    @SerialName("context_level") public val contextLevel: String? = null,
)

/** A page of web search call usage. */
@Serializable
public data class AdminWebSearchCallsUsagePage(
    @SerialName("data") public val data: List<AdminUsageBucket<AdminWebSearchCallsUsage>> = emptyList(),
    @SerialName("has_more") public val hasMore: Boolean? = null,
    @SerialName("next_page") public val nextPage: String? = null,
)

/**
 * A result of cost usage.
 */
@Serializable
public data class AdminCostsUsage(
    @SerialName("amount") public val amount: AdminCostAmount? = null,
    @SerialName("line_item") public val lineItem: String? = null,
    @SerialName("quantity") public val quantity: Double? = null,
    @SerialName("quantity_unit") public val quantityUnit: String? = null,
    @SerialName("project_id") public val projectId: String? = null,
    @SerialName("api_key_id") public val apiKeyId: String? = null,
)

/** A page of cost usage. */
@Serializable
public data class AdminCostsUsagePage(
    @SerialName("data") public val data: List<AdminUsageBucket<AdminCostsUsage>> = emptyList(),
    @SerialName("has_more") public val hasMore: Boolean? = null,
    @SerialName("next_page") public val nextPage: String? = null,
)
