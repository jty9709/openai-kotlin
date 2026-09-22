package com.aallam.openai.api.conversation

import com.aallam.openai.api.response.ResponseInputItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The conversation identifier.
 */
@JvmInline
@Serializable
public value class ConversationId(public val id: String)

/**
 * A conversation object.
 *
 * @property id the identifier of the conversation, which can be referenced in API endpoints.
 * @property objectType the object type, which is always `conversation`.
 * @property createdAt the Unix timestamp (in seconds) of when the conversation was created.
 * @property metadata set of key-value pairs attached to the conversation.
 */
@Serializable
public data class Conversation(
    @SerialName("id") public val id: ConversationId,
    @SerialName("object") public val objectType: String? = null,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("metadata") public val metadata: Map<String, String>? = null,
)

/**
 * Creates a conversation.
 *
 * @property items initial items to include in the conversation.
 * @property metadata set of key-value pairs to attach to the conversation.
 */
@Serializable
public data class ConversationCreateRequest(
    @SerialName("items") public val items: List<ResponseInputItem>? = null,
    @SerialName("metadata") public val metadata: Map<String, String>? = null,
)

/**
 * Updates a conversation.
 *
 * @property metadata set of key-value pairs to attach to the conversation.
 */
@Serializable
public data class ConversationUpdateRequest(
    @SerialName("metadata") public val metadata: Map<String, String>? = null,
)

/**
 * The result of deleting a conversation.
 *
 * @property id the identifier of the deleted conversation.
 * @property objectType the object type, which is always `conversation.deleted`.
 * @property deleted whether the conversation was deleted.
 */
@Serializable
public data class ConversationDeleted(
    @SerialName("id") public val id: ConversationId,
    @SerialName("object") public val objectType: String? = null,
    @SerialName("deleted") public val deleted: Boolean = false,
)
