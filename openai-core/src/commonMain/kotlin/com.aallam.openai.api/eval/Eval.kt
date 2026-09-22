package com.aallam.openai.api.eval

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlin.jvm.JvmInline

/**
 * The evaluation identifier.
 */
@JvmInline
@Serializable
public value class EvalId(public val id: String)

/**
 * An evaluation over a data source and a set of testing criteria.
 *
 * @property id the identifier of the eval, which can be referenced in API endpoints.
 * @property createdAt the Unix timestamp (in seconds) of when the eval was created.
 * @property name the name of the eval.
 * @property dataSourceConfig the configuration of the data source the eval runs against.
 * @property testingCriteria the graders used to evaluate each run.
 * @property metadata set of key-value pairs attached to the eval.
 */
@Serializable
public data class Eval(
    @SerialName("id") public val id: EvalId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("data_source_config") public val dataSourceConfig: JsonObject? = null,
    @SerialName("testing_criteria") public val testingCriteria: JsonElement? = null,
    @SerialName("metadata") public val metadata: Map<String, String>? = null,
)

/**
 * Creates an eval.
 *
 * @property name the name of the eval.
 * @property dataSourceConfig the configuration of the data source the eval runs against.
 * @property testingCriteria the graders used to evaluate each run.
 * @property metadata set of key-value pairs to attach to the eval.
 */
@Serializable
public data class EvalCreateRequest(
    @SerialName("name") public val name: String,
    @SerialName("data_source_config") public val dataSourceConfig: JsonObject,
    @SerialName("testing_criteria") public val testingCriteria: JsonElement? = null,
    @SerialName("metadata") public val metadata: Map<String, String>? = null,
)

/**
 * Updates an eval.
 *
 * @property name the new name of the eval.
 * @property metadata set of key-value pairs to attach to the eval.
 */
@Serializable
public data class EvalUpdateRequest(
    @SerialName("name") public val name: String? = null,
    @SerialName("metadata") public val metadata: Map<String, String>? = null,
)

/**
 * The result of deleting an eval.
 */
@Serializable
public data class EvalDeleted(
    @SerialName("eval_id") public val evalId: EvalId,
    @SerialName("deleted") public val deleted: Boolean = false,
)
