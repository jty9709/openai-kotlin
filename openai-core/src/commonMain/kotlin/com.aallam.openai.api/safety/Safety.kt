package com.aallam.openai.api.safety

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlin.jvm.JvmInline

/**
 * The safety case identifier.
 */
@JvmInline
@Serializable
public value class SafetyCaseId(public val id: String)

/**
 * The safety alert identifier.
 */
@JvmInline
@Serializable
public value class SafetyAlertId(public val id: String)

/**
 * A safety case opened by the moderation pipeline.
 *
 * @property id the identifier of the safety case.
 * @property entityIdentifier the entity the case was raised against.
 * @property notice details attached to the case.
 * @property reason why the case was opened.
 */
@Serializable
public data class SafetyCase(
    @SerialName("id") public val id: SafetyCaseId,
    @SerialName("object") public val objectType: String? = null,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("entity_identifier") public val entityIdentifier: String? = null,
    @SerialName("notice") public val notice: JsonObject? = null,
    @SerialName("reason") public val reason: String? = null,
)

/**
 * A safety alert raised for a blocked request.
 *
 * @property id the identifier of the safety alert.
 * @property errorType the type of error that triggered the alert.
 * @property model the model that served the request.
 * @property reason why the request was flagged.
 * @property requestId the identifier of the flagged request.
 * @property requestPaused whether the request was paused.
 * @property responseId the identifier of the response, when one was produced.
 */
@Serializable
public data class SafetyAlert(
    @SerialName("id") public val id: SafetyAlertId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("error_type") public val errorType: String? = null,
    @SerialName("model") public val model: String? = null,
    @SerialName("reason") public val reason: String? = null,
    @SerialName("request_id") public val requestId: String? = null,
    @SerialName("request_paused") public val requestPaused: Boolean? = null,
    @SerialName("response_id") public val responseId: String? = null,
)
