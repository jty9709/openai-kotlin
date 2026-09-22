package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.realtime.RealtimeCall
import com.aallam.openai.api.realtime.RealtimeCallId
import com.aallam.openai.api.realtime.RealtimeCallReferRequest
import com.aallam.openai.api.realtime.RealtimeCallRejectRequest
import com.aallam.openai.api.realtime.RealtimeCallRequest
import com.aallam.openai.api.realtime.RealtimeClientSecret
import com.aallam.openai.api.realtime.RealtimeClientSecretRequest
import com.aallam.openai.api.realtime.RealtimeEvent
import com.aallam.openai.api.realtime.RealtimeSession
import com.aallam.openai.api.realtime.RealtimeSessionCreateRequest
import com.aallam.openai.api.realtime.RealtimeTranscriptionSession
import com.aallam.openai.api.realtime.RealtimeTranscriptionSessionCreateRequest
import com.aallam.openai.client.Realtime
import com.aallam.openai.client.RealtimeConnection
import com.aallam.openai.client.internal.JsonLenient
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

internal class RealtimeApi(private val requester: HttpRequester) : Realtime {

    override suspend fun createRealtimeClientSecret(
        request: RealtimeClientSecretRequest,
        requestOptions: RequestOptions?
    ): RealtimeClientSecret {
        return requester.perform {
            it.post {
                url(path = ApiPath.RealtimeClientSecrets)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createRealtimeSession(
        request: RealtimeSessionCreateRequest,
        requestOptions: RequestOptions?
    ): RealtimeSession {
        return requester.perform {
            it.post {
                url(path = ApiPath.RealtimeSessions)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createRealtimeTranscriptionSession(
        request: RealtimeTranscriptionSessionCreateRequest,
        requestOptions: RequestOptions?
    ): RealtimeTranscriptionSession {
        return requester.perform {
            it.post {
                url(path = ApiPath.RealtimeTranscriptionSessions)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createRealtimeCall(
        request: RealtimeCallRequest,
        requestOptions: RequestOptions?
    ): RealtimeCall {
        val response = requester.perform<HttpResponse> {
            it.submitFormWithBinaryData(
                url = ApiPath.RealtimeCalls,
                formData = formData {
                    append("sdp", request.sdp, headersOf(HttpHeaders.ContentType, "application/sdp"))
                    request.session?.let { session ->
                        append("session", session.toString(), headersOf(HttpHeaders.ContentType, "application/json"))
                    }
                },
            ) {
                requestOptions(requestOptions)
            }
        }
        return RealtimeCall(
            sdp = response.bodyAsText(),
            location = response.headers[HttpHeaders.Location],
            headers = response.headers.entries().associate { it.key to it.value },
        )
    }

    override suspend fun acceptRealtimeCall(
        id: RealtimeCallId,
        request: JsonObject,
        requestOptions: RequestOptions?
    ) {
        requester.perform<Unit> {
            it.post {
                url(path = "${ApiPath.RealtimeCalls}/${id.id}/accept")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun rejectRealtimeCall(
        id: RealtimeCallId,
        request: RealtimeCallRejectRequest,
        requestOptions: RequestOptions?
    ) {
        requester.perform<Unit> {
            it.post {
                url(path = "${ApiPath.RealtimeCalls}/${id.id}/reject")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun hangupRealtimeCall(
        id: RealtimeCallId,
        requestOptions: RequestOptions?
    ) {
        requester.perform<Unit> {
            it.post {
                url(path = "${ApiPath.RealtimeCalls}/${id.id}/hangup")
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun referRealtimeCall(
        id: RealtimeCallId,
        request: RealtimeCallReferRequest,
        requestOptions: RequestOptions?
    ) {
        requester.perform<Unit> {
            it.post {
                url(path = "${ApiPath.RealtimeCalls}/${id.id}/refer")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun connectRealtime(
        clientSecret: String,
        model: String?,
    ): RealtimeConnection {
        val query = model?.let { value -> "?model=$value" }.orEmpty()
        val session = requester.webSocketSession(
            path = "${ApiPath.Realtime}$query",
            headers = mapOf(HttpHeaders.Authorization to "Bearer $clientSecret"),
        )
        return RealtimeWebSocketConnection(session)
    }
}

/**
 * [RealtimeConnection] backed by a Ktor WebSocket session.
 */
internal class RealtimeWebSocketConnection(
    private val session: DefaultClientWebSocketSession,
) : RealtimeConnection {

    override val events: Flow<RealtimeEvent> = session.incoming
        .receiveAsFlow()
        .filterIsInstance<Frame.Text>()
        .map { frame -> RealtimeEvent.of(JsonLenient.parseToJsonElement(frame.readText()).jsonObject) }

    override suspend fun send(event: RealtimeEvent) {
        session.send(Frame.Text(event.json.toString()))
    }

    override fun close() {
        session.cancel()
    }
}
