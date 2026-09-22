package com.aallam.openai.api.live

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlin.jvm.JvmInline

/**
 * The live session identifier.
 */
@JvmInline
@Serializable
public value class LiveSessionId(public val id: String)

/**
 * The transport used by a live session.
 *
 * @property type the transport type.
 * @property sdp the session description.
 */
@Serializable
public data class LiveTransport(
    @SerialName("type") public val type: String? = null,
    @SerialName("sdp") public val sdp: String? = null,
)

/**
 * A live session.
 *
 * @property id the identifier of the session.
 * @property expiresAt the Unix timestamp (in seconds) of when the session expires.
 * @property model the model serving the session.
 * @property status the current status of the session.
 * @property instructions system instructions for the session.
 * @property store whether session artifacts are stored.
 * @property audio audio configuration of the session.
 * @property client client configuration of the session.
 * @property input input configuration of the session.
 */
@Serializable
public data class LiveSession(
    @SerialName("id") public val id: LiveSessionId,
    @SerialName("expires_at") public val expiresAt: Long? = null,
    @SerialName("model") public val model: String? = null,
    @SerialName("status") public val status: String? = null,
    @SerialName("instructions") public val instructions: String? = null,
    @SerialName("store") public val store: Boolean? = null,
    @SerialName("audio") public val audio: JsonObject? = null,
    @SerialName("client") public val client: JsonObject? = null,
    @SerialName("input") public val input: JsonObject? = null,
)

/**
 * The result of creating a live session.
 */
@Serializable
public data class LiveSessionCreated(
    @SerialName("session") public val session: LiveSession? = null,
    @SerialName("transport") public val transport: LiveTransport? = null,
)

/**
 * Creates a live session.
 */
@Serializable
public data class LiveSessionCreateRequest(
    @SerialName("session") public val session: JsonObject? = null,
    @SerialName("transport") public val transport: LiveTransport? = null,
)

/**
 * Accepts a live session.
 */
@Serializable
public data class LiveSessionAcceptRequest(
    @SerialName("session") public val session: JsonObject? = null,
)

/**
 * Rejects a live session.
 *
 * @property statusCode the status code reported to the caller.
 */
@Serializable
public data class LiveSessionRejectRequest(
    @SerialName("status_code") public val statusCode: Long? = null,
)

/**
 * Refers a live session to another target.
 *
 * @property targetUri the target of the referral.
 */
@Serializable
public data class LiveSessionReferRequest(
    @SerialName("target_uri") public val targetUri: String,
)

/**
 * Forks a live session.
 */
@Serializable
public data class LiveSessionForkRequest(
    @SerialName("transport") public val transport: LiveTransport? = null,
    @SerialName("session") public val session: JsonObject? = null,
)
