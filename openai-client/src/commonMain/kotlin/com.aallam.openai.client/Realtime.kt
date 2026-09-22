package com.aallam.openai.client

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
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject

/**
 * A live WebSocket connection to a realtime session.
 *
 * Close the connection when finished.
 */
public interface RealtimeConnection : AutoCloseable {

    /**
     * Events emitted by the server, in the order they arrive.
     *
     * Collecting this flow more than once is not supported.
     */
    public val events: Flow<RealtimeEvent>

    /**
     * Sends a client event to the server.
     */
    public suspend fun send(event: RealtimeEvent)
}

/**
 * Manage realtime sessions and calls.
 *
 * The realtime conversation itself runs over a WebSocket connection opened with a
 * [RealtimeClientSecret]; this interface covers the REST endpoints that provision and control it,
 * plus [connectRealtime] which opens that connection.
 */
public interface Realtime {

    /**
     * Creates a short-lived client secret used to open a realtime WebSocket connection.
     */
    public suspend fun createRealtimeClientSecret(
        request: RealtimeClientSecretRequest = RealtimeClientSecretRequest(),
        requestOptions: RequestOptions? = null
    ): RealtimeClientSecret

    /**
     * Creates a realtime session, returning the session together with its ephemeral client secret.
     */
    public suspend fun createRealtimeSession(
        request: RealtimeSessionCreateRequest = RealtimeSessionCreateRequest(),
        requestOptions: RequestOptions? = null
    ): RealtimeSession

    /**
     * Creates a realtime transcription session.
     */
    public suspend fun createRealtimeTranscriptionSession(
        request: RealtimeTranscriptionSessionCreateRequest =
            RealtimeTranscriptionSessionCreateRequest(),
        requestOptions: RequestOptions? = null
    ): RealtimeTranscriptionSession

    /**
     * Creates a realtime call.
     */
    public suspend fun createRealtimeCall(
        request: RealtimeCallRequest,
        requestOptions: RequestOptions? = null
    ): RealtimeCall

    /**
     * Accepts an incoming realtime call.
     *
     * [request] is the GA session configuration, including `type: realtime` and the model.
     * It is sent directly as the request body, without a `session` wrapper.
     */
    public suspend fun acceptRealtimeCall(
        id: RealtimeCallId,
        request: JsonObject,
        requestOptions: RequestOptions? = null
    )

    /**
     * Rejects an incoming realtime call.
     */
    public suspend fun rejectRealtimeCall(
        id: RealtimeCallId,
        request: RealtimeCallRejectRequest = RealtimeCallRejectRequest(),
        requestOptions: RequestOptions? = null
    )

    /**
     * Hangs up a realtime call.
     */
    public suspend fun hangupRealtimeCall(
        id: RealtimeCallId,
        requestOptions: RequestOptions? = null
    )

    /**
     * Refers a realtime call to another target.
     */
    public suspend fun referRealtimeCall(
        id: RealtimeCallId,
        request: RealtimeCallReferRequest,
        requestOptions: RequestOptions? = null
    )

    /**
     * Opens a WebSocket connection to a realtime session using a [RealtimeClientSecret].
     *
     * @param clientSecret the secret returned by [createRealtimeClientSecret].
     * @param model the model to use, when the secret is not already bound to one.
     */
    public suspend fun connectRealtime(
        clientSecret: String,
        model: String? = null,
    ): RealtimeConnection
}
