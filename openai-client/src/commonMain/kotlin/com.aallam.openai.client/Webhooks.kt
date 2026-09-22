package com.aallam.openai.client

import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.webhook.DeletedWebhookEndpoint
import com.aallam.openai.api.webhook.WebhookEndpoint
import com.aallam.openai.api.webhook.WebhookEndpointCreateRequest
import com.aallam.openai.api.webhook.WebhookEndpointId
import com.aallam.openai.api.webhook.WebhookEndpointTestResult
import com.aallam.openai.api.webhook.WebhookEndpointUpdateRequest
import com.aallam.openai.api.webhook.WebhookEndpointWithSecret
import com.aallam.openai.api.webhook.WebhookEventTypeList

/**
 * Create and manage webhook endpoints.
 */
public interface Webhooks {

    /**
     * Creates a webhook endpoint.
     *
     * The returned endpoint includes its signing secret, which is only returned here and when the
     * secret is rotated.
     */
    public suspend fun createWebhookEndpoint(
        request: WebhookEndpointCreateRequest,
        requestOptions: RequestOptions? = null
    ): WebhookEndpointWithSecret

    /**
     * Retrieves a webhook endpoint by its identifier.
     */
    public suspend fun webhookEndpoint(
        id: WebhookEndpointId,
        requestOptions: RequestOptions? = null
    ): WebhookEndpoint

    /**
     * Updates a webhook endpoint.
     */
    public suspend fun updateWebhookEndpoint(
        id: WebhookEndpointId,
        request: WebhookEndpointUpdateRequest,
        requestOptions: RequestOptions? = null
    ): WebhookEndpoint

    /**
     * Deletes a webhook endpoint.
     */
    public suspend fun deleteWebhookEndpoint(
        id: WebhookEndpointId,
        requestOptions: RequestOptions? = null
    ): DeletedWebhookEndpoint

    /**
     * Lists webhook endpoints.
     */
    public suspend fun webhookEndpoints(
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<WebhookEndpoint>

    /**
     * Rotates the signing secret of a webhook endpoint.
     */
    public suspend fun rotateWebhookSecret(
        id: WebhookEndpointId,
        requestOptions: RequestOptions? = null
    ): WebhookEndpointWithSecret

    /**
     * Lists the event types a webhook endpoint can subscribe to.
     */
    public suspend fun webhookEventTypes(
        requestOptions: RequestOptions? = null
    ): WebhookEventTypeList

    /**
     * Sends a test event to a webhook endpoint.
     */
    public suspend fun testWebhookEndpoint(
        id: WebhookEndpointId,
        requestOptions: RequestOptions? = null
    ): WebhookEndpointTestResult
}
