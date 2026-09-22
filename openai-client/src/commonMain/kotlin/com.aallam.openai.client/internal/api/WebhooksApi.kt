package com.aallam.openai.client.internal.api

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
import com.aallam.openai.client.Webhooks
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class WebhooksApi(private val requester: HttpRequester) : Webhooks {

    override suspend fun createWebhookEndpoint(
        request: WebhookEndpointCreateRequest,
        requestOptions: RequestOptions?
    ): WebhookEndpointWithSecret {
        return requester.perform {
            it.post {
                url(path = ApiPath.WebhookEndpoints)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun webhookEndpoint(
        id: WebhookEndpointId,
        requestOptions: RequestOptions?
    ): WebhookEndpoint {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.WebhookEndpoints}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateWebhookEndpoint(
        id: WebhookEndpointId,
        request: WebhookEndpointUpdateRequest,
        requestOptions: RequestOptions?
    ): WebhookEndpoint {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.WebhookEndpoints}/${id.id}")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteWebhookEndpoint(
        id: WebhookEndpointId,
        requestOptions: RequestOptions?
    ): DeletedWebhookEndpoint {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.WebhookEndpoints}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun webhookEndpoints(
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<WebhookEndpoint> {
        return requester.perform {
            it.get {
                url(path = ApiPath.WebhookEndpoints) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun rotateWebhookSecret(
        id: WebhookEndpointId,
        requestOptions: RequestOptions?
    ): WebhookEndpointWithSecret {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.WebhookEndpoints}/${id.id}/rotate_secret")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun webhookEventTypes(
        requestOptions: RequestOptions?
    ): WebhookEventTypeList {
        return requester.perform {
            it.get {
                url(path = ApiPath.WebhookEventTypes)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun testWebhookEndpoint(
        id: WebhookEndpointId,
        requestOptions: RequestOptions?
    ): WebhookEndpointTestResult {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.WebhookEndpoints}/${id.id}/test")
                requestOptions(requestOptions)
            }.body()
        }
    }
}
