package com.aallam.openai.client

import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.response.CompactedResponse
import com.aallam.openai.api.response.Response
import com.aallam.openai.api.response.ResponseCompactRequest
import com.aallam.openai.api.response.ResponseId
import com.aallam.openai.api.response.ResponseInputTokenCount
import com.aallam.openai.api.response.ResponseInputTokenCountRequest
import com.aallam.openai.api.response.ResponseInputItem
import com.aallam.openai.api.response.ResponseRequest
import com.aallam.openai.api.response.ResponseStreamEvent
import kotlinx.coroutines.flow.Flow

/**
 * Create and manage model responses.
 */
public interface Responses {

    /**
     * Creates a model response.
     */
    public suspend fun response(
        request: ResponseRequest,
        requestOptions: RequestOptions? = null
    ): Response

    /**
     * Creates a model response and streams the emitted events as they arrive.
     *
     * The events are emitted in the order they are produced by the API. Collection stops when the
     * stream is completed, fails, or gets cancelled.
     */
    public fun responseStream(
        request: ResponseRequest,
        requestOptions: RequestOptions? = null
    ): Flow<ResponseStreamEvent>

    /**
     * Retrieves a response by its identifier.
     */
    public suspend fun response(
        id: ResponseId,
        requestOptions: RequestOptions? = null
    ): Response?

    /**
     * Deletes a stored response.
     */
    public suspend fun delete(
        id: ResponseId,
        requestOptions: RequestOptions? = null
    ): Boolean

    /**
     * Cancels an in-progress response.
     */
    public suspend fun cancel(
        id: ResponseId,
        requestOptions: RequestOptions? = null
    ): Response?

    /**
     * Compacts a response's context window.
     */
    public suspend fun compactResponse(
        request: ResponseCompactRequest,
        requestOptions: RequestOptions? = null
    ): CompactedResponse

    /**
     * Counts the tokens an input would consume.
     */
    public suspend fun responseInputTokens(
        request: ResponseInputTokenCountRequest = ResponseInputTokenCountRequest(),
        requestOptions: RequestOptions? = null
    ): ResponseInputTokenCount

    /**
     * Lists input items for a response.
     */
    public suspend fun responseInputItems(
        id: ResponseId,
        limit: Int? = null,
        order: SortOrder? = null,
        after: String? = null,
        before: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<ResponseInputItem>
}
