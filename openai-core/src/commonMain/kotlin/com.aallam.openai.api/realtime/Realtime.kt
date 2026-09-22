package com.aallam.openai.api.realtime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlin.jvm.JvmInline

/**
 * The realtime call identifier.
 */
@JvmInline
@Serializable
public value class RealtimeCallId(public val id: String)

/**
 * Expiry configuration for a realtime client secret.
 */
@Serializable
public data class RealtimeExpiresAfter(
    @SerialName("anchor") public val anchor: String? = null,
    @SerialName("seconds") public val seconds: Long? = null,
)

/**
 * A short-lived secret used to open a realtime WebSocket connection.
 *
 * @property value the secret value.
 * @property expiresAt the Unix timestamp (in seconds) of when the secret expires.
 * @property session the session configuration bound to the secret.
 */
@Serializable
public data class RealtimeClientSecret(
    @SerialName("value") public val value: String? = null,
    @SerialName("expires_at") public val expiresAt: Long? = null,
    @SerialName("session") public val session: JsonObject? = null,
)

/**
 * Creates a realtime client secret.
 *
 * @property session session configuration to bind to the secret.
 * @property expiresAfter how long the secret should remain valid.
 */
@Serializable
public data class RealtimeClientSecretRequest(
    @SerialName("session") public val session: JsonObject? = null,
    @SerialName("expires_after") public val expiresAfter: RealtimeExpiresAfter? = null,
)

/**
 * The SDP answer and response headers returned when creating a realtime call.
 *
 * @property sdp the SDP answer to apply to the peer connection.
 * @property location the URL of the created call from the Location header, when provided.
 * @property headers response headers, including request diagnostics.
 */
public data class RealtimeCall(
    public val sdp: String,
    public val location: String? = null,
    public val headers: Map<String, List<String>> = emptyMap(),
) {
    /** The call identifier extracted from [location], if the server provided it. */
    public val id: RealtimeCallId?
        get() = location?.substringBefore('?')?.substringBefore('#')?.trimEnd('/')
            ?.substringAfterLast('/')?.takeIf { it.isNotEmpty() }?.let(::RealtimeCallId)
}

/**
 * Creates a realtime call.
 *
 * @property sdp the session description of the caller.
 * @property session session configuration for the call.
 */
@Serializable
public data class RealtimeCallRequest(
    @SerialName("sdp") public val sdp: String,
    @SerialName("session") public val session: JsonObject? = null,
)

/**
 * Rejects a realtime call.
 *
 * @property statusCode the status code reported to the caller.
 */
@Serializable
public data class RealtimeCallRejectRequest(
    @SerialName("status_code") public val statusCode: Long? = null,
)

/**
 * A realtime session.
 *
 * @property clientSecret the ephemeral secret used to open the WebSocket connection.
 * @property inputAudioFormat the format of input audio.
 * @property inputAudioTranscription the transcription configuration of input audio.
 * @property instructions system instructions for the session.
 * @property maxResponseOutputTokens the cap on response output tokens.
 * @property modalities the modalities enabled for the session.
 * @property outputAudioFormat the format of output audio.
 * @property speed the playback speed of generated audio.
 */
@Serializable
public data class RealtimeSession(
    @SerialName("client_secret") public val clientSecret: JsonObject? = null,
    @SerialName("input_audio_format") public val inputAudioFormat: String? = null,
    @SerialName("input_audio_transcription") public val inputAudioTranscription: JsonObject? = null,
    @SerialName("instructions") public val instructions: String? = null,
    @SerialName("max_response_output_tokens") public val maxResponseOutputTokens: Long? = null,
    @SerialName("modalities") public val modalities: List<String>? = null,
    @SerialName("output_audio_format") public val outputAudioFormat: String? = null,
    @SerialName("speed") public val speed: Double? = null,
)

/**
 * Creates a realtime session.
 */
@Serializable
public data class RealtimeSessionCreateRequest(
    @SerialName("model") public val model: String? = null,
    @SerialName("instructions") public val instructions: String? = null,
    @SerialName("modalities") public val modalities: List<String>? = null,
    @SerialName("input_audio_format") public val inputAudioFormat: String? = null,
    @SerialName("output_audio_format") public val outputAudioFormat: String? = null,
    @SerialName("max_response_output_tokens") public val maxResponseOutputTokens: Long? = null,
    @SerialName("speed") public val speed: Double? = null,
    @SerialName("temperature") public val temperature: Double? = null,
)

/**
 * A realtime transcription session.
 *
 * @property clientSecret the ephemeral secret used to open the WebSocket connection.
 * @property inputAudioFormat the format of input audio.
 * @property inputAudioTranscription the transcription configuration of input audio.
 * @property modalities the modalities enabled for the session.
 * @property turnDetection the voice activity detection configuration.
 */
@Serializable
public data class RealtimeTranscriptionSession(
    @SerialName("client_secret") public val clientSecret: JsonObject? = null,
    @SerialName("input_audio_format") public val inputAudioFormat: String? = null,
    @SerialName("input_audio_transcription") public val inputAudioTranscription: JsonObject? = null,
    @SerialName("modalities") public val modalities: List<String>? = null,
    @SerialName("turn_detection") public val turnDetection: JsonObject? = null,
)

/**
 * Creates a realtime transcription session.
 */
@Serializable
public data class RealtimeTranscriptionSessionCreateRequest(
    @SerialName("include") public val include: List<String>? = null,
    @SerialName("input_audio_format") public val inputAudioFormat: String? = null,
    @SerialName("input_audio_transcription") public val inputAudioTranscription: JsonObject? = null,
    @SerialName("modalities") public val modalities: List<String>? = null,
    @SerialName("turn_detection") public val turnDetection: JsonObject? = null,
)

/**
 * An event exchanged over a realtime WebSocket connection.
 *
 * The payload is kept as a [JsonObject] so that newly introduced event fields remain accessible
 * without a client release. Use [type] to branch on the event kind, [rawType] when the type is not
 * yet known to this client version, and [json] to read the payload.
 *
 * @property type the type of the event, or [RealtimeEventType.UNKNOWN] if unrecognized.
 * @property rawType the event type exactly as it appeared on the wire.
 * @property json the raw payload of the event.
 */
public data class RealtimeEvent(
    public val type: RealtimeEventType,
    public val rawType: String,
    public val json: JsonObject,
) {

    /**
     * The event identifier, if any.
     */
    public val eventId: String? get() = json.string("event_id")

    /**
     * The identifier of the item this event belongs to, if any.
     */
    public val itemId: String? get() = json.string("item_id")

    /**
     * The response identifier this event belongs to, if any.
     */
    public val responseId: String? get() = json.string("response_id")

    /**
     * The incremental text or audio payload carried by this event, if any.
     */
    public val delta: String? get() = json.string("delta")

    public companion object {

        /**
         * Creates a [RealtimeEvent] from its raw [json] payload, reading the type from the `type`
         * field.
         */
        public fun of(json: JsonObject): RealtimeEvent {
            val raw = (json["type"] as? JsonPrimitive)?.contentOrNull.orEmpty()
            return RealtimeEvent(RealtimeEventType.fromEvent(raw), raw, json)
        }

        /**
         * Creates a client-sent [RealtimeEvent] with the given [type] and optional [json] payload.
         *
         * The `type` field is always present in the resulting payload.
         */
        public fun of(
            type: RealtimeEventType,
            json: JsonObject = JsonObject(emptyMap()),
        ): RealtimeEvent = of(type.event, json)

        /**
         * Creates a client-sent [RealtimeEvent] with a raw [type] string, for events that this
         * client version does not yet model.
         */
        public fun of(
            type: String,
            json: JsonObject = JsonObject(emptyMap()),
        ): RealtimeEvent = RealtimeEvent(
            type = RealtimeEventType.fromEvent(type),
            rawType = type,
            json = JsonObject(json + ("type" to JsonPrimitive(type))),
        )
    }
}

private fun JsonObject.string(key: String): String? =
    (this[key] as? JsonPrimitive)?.contentOrNull

/**
 * The type of an event exchanged over a realtime WebSocket connection.
 *
 * @property event the string representation of the event type.
 */
public enum class RealtimeEventType(public val event: String) {

    // Client events.
    SESSION_UPDATE("session.update"),
    SESSION_CLOSE("session.close"),
    INPUT_AUDIO_BUFFER_APPEND("input_audio_buffer.append"),
    INPUT_AUDIO_BUFFER_COMMIT("input_audio_buffer.commit"),
    INPUT_AUDIO_BUFFER_CLEAR("input_audio_buffer.clear"),
    OUTPUT_AUDIO_BUFFER_CLEAR("output_audio_buffer.clear"),
    CONVERSATION_ITEM_CREATE("conversation.item.create"),
    CONVERSATION_ITEM_RETRIEVE("conversation.item.retrieve"),
    CONVERSATION_ITEM_TRUNCATE("conversation.item.truncate"),
    CONVERSATION_ITEM_DELETE("conversation.item.delete"),
    RESPONSE_CREATE("response.create"),
    RESPONSE_CANCEL("response.cancel"),
    TRANSCRIPTION_SESSION_UPDATE("transcription_session.update"),

    // Server events: session.
    SESSION_CREATED("session.created"),
    SESSION_UPDATED("session.updated"),
    SESSION_CLOSED("session.closed"),
    TRANSCRIPTION_SESSION_UPDATED("transcription_session.updated"),

    // Server events: conversation.
    CONVERSATION_CREATED("conversation.created"),
    CONVERSATION_ITEM_ADDED("conversation.item.added"),
    CONVERSATION_ITEM_CREATED("conversation.item.created"),
    CONVERSATION_ITEM_DONE("conversation.item.done"),
    CONVERSATION_ITEM_RETRIEVED("conversation.item.retrieved"),
    CONVERSATION_ITEM_TRUNCATED("conversation.item.truncated"),
    CONVERSATION_ITEM_DELETED("conversation.item.deleted"),
    CONVERSATION_ITEM_INPUT_AUDIO_TRANSCRIPTION_COMPLETED(
        "conversation.item.input_audio_transcription.completed"
    ),
    CONVERSATION_ITEM_INPUT_AUDIO_TRANSCRIPTION_DELTA(
        "conversation.item.input_audio_transcription.delta"
    ),
    CONVERSATION_ITEM_INPUT_AUDIO_TRANSCRIPTION_FAILED(
        "conversation.item.input_audio_transcription.failed"
    ),
    CONVERSATION_ITEM_INPUT_AUDIO_TRANSCRIPTION_SEGMENT(
        "conversation.item.input_audio_transcription.segment"
    ),

    // Server events: input audio buffer.
    INPUT_AUDIO_BUFFER_COMMITTED("input_audio_buffer.committed"),
    INPUT_AUDIO_BUFFER_CLEARED("input_audio_buffer.cleared"),
    INPUT_AUDIO_BUFFER_SPEECH_STARTED("input_audio_buffer.speech_started"),
    INPUT_AUDIO_BUFFER_SPEECH_STOPPED("input_audio_buffer.speech_stopped"),
    INPUT_AUDIO_BUFFER_TIMEOUT_TRIGGERED("input_audio_buffer.timeout_triggered"),
    INPUT_AUDIO_BUFFER_DTMF_EVENT_RECEIVED("input_audio_buffer.dtmf_event_received"),

    // Server events: output audio buffer.
    OUTPUT_AUDIO_BUFFER_STARTED("output_audio_buffer.started"),
    OUTPUT_AUDIO_BUFFER_STOPPED("output_audio_buffer.stopped"),
    OUTPUT_AUDIO_BUFFER_CLEARED("output_audio_buffer.cleared"),

    // Server events: response.
    RESPONSE_CREATED("response.created"),
    RESPONSE_DONE("response.done"),
    RESPONSE_OUTPUT_ITEM_ADDED("response.output_item.added"),
    RESPONSE_OUTPUT_ITEM_DONE("response.output_item.done"),
    RESPONSE_CONTENT_PART_ADDED("response.content_part.added"),
    RESPONSE_CONTENT_PART_DONE("response.content_part.done"),
    RESPONSE_OUTPUT_TEXT_DELTA("response.output_text.delta"),
    RESPONSE_OUTPUT_TEXT_DONE("response.output_text.done"),
    RESPONSE_TEXT_DELTA("response.text.delta"),
    RESPONSE_TEXT_DONE("response.text.done"),
    RESPONSE_AUDIO_DELTA("response.audio.delta"),
    RESPONSE_AUDIO_DONE("response.audio.done"),
    RESPONSE_AUDIO_TRANSCRIPT_DELTA("response.audio_transcript.delta"),
    RESPONSE_AUDIO_TRANSCRIPT_DONE("response.audio_transcript.done"),
    RESPONSE_OUTPUT_AUDIO_DELTA("response.output_audio.delta"),
    RESPONSE_OUTPUT_AUDIO_DONE("response.output_audio.done"),
    RESPONSE_OUTPUT_AUDIO_TRANSCRIPT_DELTA("response.output_audio_transcript.delta"),
    RESPONSE_OUTPUT_AUDIO_TRANSCRIPT_DONE("response.output_audio_transcript.done"),
    RESPONSE_FUNCTION_CALL_ARGUMENTS_DELTA("response.function_call_arguments.delta"),
    RESPONSE_FUNCTION_CALL_ARGUMENTS_DONE("response.function_call_arguments.done"),
    RESPONSE_MCP_CALL_IN_PROGRESS("response.mcp_call.in_progress"),
    RESPONSE_MCP_CALL_ARGUMENTS_DELTA("response.mcp_call_arguments.delta"),
    RESPONSE_MCP_CALL_ARGUMENTS_DONE("response.mcp_call_arguments.done"),
    RESPONSE_MCP_CALL_COMPLETED("response.mcp_call.completed"),
    RESPONSE_MCP_CALL_FAILED("response.mcp_call.failed"),

    // Server events: rate limits and session-scoped streams.
    RATE_LIMITS_UPDATED("rate_limits.updated"),
    SESSION_INPUT_AUDIO_BUFFER_APPEND("session.input_audio_buffer.append"),
    SESSION_INPUT_TRANSCRIPT_DELTA("session.input_transcript.delta"),
    SESSION_OUTPUT_AUDIO_DELTA("session.output_audio.delta"),
    SESSION_OUTPUT_TRANSCRIPT_DELTA("session.output_transcript.delta"),

    /** The event type is not recognized by this client version. */
    UNKNOWN("unknown");

    public companion object {
        /**
         * Returns the [RealtimeEventType] matching [event], or [UNKNOWN] if unrecognized.
         */
        public fun fromEvent(event: String): RealtimeEventType =
            entries.find { it.event == event } ?: UNKNOWN
    }
}

/**
 * Refers a realtime call to another target.
 *
 * @property targetUri the target of the referral.
 */
@Serializable
public data class RealtimeCallReferRequest(
    @SerialName("target_uri") public val targetUri: String,
)
