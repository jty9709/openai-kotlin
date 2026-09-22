package com.aallam.openai.api.webhook

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The webhook endpoint identifier.
 */
@JvmInline
@Serializable
public value class WebhookEndpointId(public val id: String)

/**
 * A webhook endpoint that receives event notifications.
 *
 * @property id the identifier of the webhook endpoint.
 * @property createdAt the Unix timestamp (in seconds) of when the endpoint was created.
 * @property eventTypes the event types the endpoint is subscribed to.
 * @property name the name of the endpoint.
 * @property signingSecretHint a hint of the signing secret, safe to display.
 * @property url the URL events are delivered to.
 * @property updatedAt the Unix timestamp (in seconds) of when the endpoint was last updated.
 */
@Serializable
public data class WebhookEndpoint(
    @SerialName("id") public val id: WebhookEndpointId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("event_types") public val eventTypes: List<String> = emptyList(),
    @SerialName("name") public val name: String? = null,
    @SerialName("signing_secret_hint") public val signingSecretHint: String? = null,
    @SerialName("url") public val url: String? = null,
    @SerialName("updated_at") public val updatedAt: Long? = null,
)

/**
 * A webhook endpoint returned together with its signing secret.
 *
 * The secret is only returned when the endpoint is created or its secret is rotated.
 */
@Serializable
public data class WebhookEndpointWithSecret(
    @SerialName("id") public val id: WebhookEndpointId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("event_types") public val eventTypes: List<String> = emptyList(),
    @SerialName("name") public val name: String? = null,
    @SerialName("signing_secret") public val signingSecret: String? = null,
    @SerialName("signing_secret_hint") public val signingSecretHint: String? = null,
    @SerialName("url") public val url: String? = null,
    @SerialName("updated_at") public val updatedAt: Long? = null,
)

/**
 * Creates a webhook endpoint.
 *
 * @property eventTypes the event types to subscribe to.
 * @property name the name of the endpoint.
 * @property url the URL events are delivered to.
 */
@Serializable
public data class WebhookEndpointCreateRequest(
    @SerialName("event_types") public val eventTypes: List<String>,
    @SerialName("name") public val name: String,
    @SerialName("url") public val url: String,
)

/**
 * Updates a webhook endpoint.
 */
@Serializable
public data class WebhookEndpointUpdateRequest(
    @SerialName("event_types") public val eventTypes: List<String>? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("url") public val url: String? = null,
)

/**
 * The result of deleting a webhook endpoint.
 */
@Serializable
public data class DeletedWebhookEndpoint(
    @SerialName("id") public val id: WebhookEndpointId,
    @SerialName("deleted") public val deleted: Boolean = false,
)

/**
 * The result of sending a test event to a webhook endpoint.
 */
@Serializable
public data class WebhookEndpointTestResult(
    @SerialName("event_type") public val eventType: String? = null,
    @SerialName("status_code") public val statusCode: Long? = null,
    @SerialName("success") public val success: Boolean = false,
    @SerialName("webhook_endpoint_id") public val webhookEndpointId: WebhookEndpointId? = null,
)

/**
 * The event types a webhook endpoint can subscribe to.
 */
@Serializable
public data class WebhookEventTypeList(
    @SerialName("data") public val data: List<String> = emptyList(),
)
