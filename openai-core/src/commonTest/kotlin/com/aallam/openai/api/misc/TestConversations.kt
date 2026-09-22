package com.aallam.openai.api.misc

import com.aallam.openai.api.conversation.Conversation
import com.aallam.openai.api.conversation.ConversationCreateRequest
import com.aallam.openai.api.conversation.ConversationDeleted
import com.aallam.openai.api.response.ResponseInputItem
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestConversations {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun conversationDeserializesExpectedFields() {
        val payload = """
            {
              "id":"conv_123",
              "object":"conversation",
              "created_at":1741900000,
              "metadata":{"topic":"weather"}
            }
        """.trimIndent()

        val conversation = json.decodeFromString(Conversation.serializer(), payload)
        assertEquals("conv_123", conversation.id.id)
        assertEquals("conversation", conversation.objectType)
        assertEquals(1741900000L, conversation.createdAt)
        assertEquals("weather", conversation.metadata?.get("topic"))
    }

    @Test
    fun conversationCreateRequestSerializesExpectedFields() {
        val request = ConversationCreateRequest(
            items = listOf(ResponseInputItem(type = "message", role = "user")),
            metadata = mapOf("topic" to "weather"),
        )

        val encoded =
            Json.encodeToJsonElement(ConversationCreateRequest.serializer(), request).jsonObject
        assertEquals("weather", encoded["metadata"]?.jsonObject?.get("topic")?.jsonPrimitive?.content)
        assertEquals("message", encoded["items"]?.jsonArray?.first()?.jsonObject?.get("type")?.jsonPrimitive?.content)
        assertEquals("user", encoded["items"]?.jsonArray?.first()?.jsonObject?.get("role")?.jsonPrimitive?.content)
    }

    @Test
    fun conversationDeletedDeserializesExpectedFields() {
        val payload = """{"id":"conv_123","object":"conversation.deleted","deleted":true}"""

        val deleted = json.decodeFromString(ConversationDeleted.serializer(), payload)
        assertEquals("conv_123", deleted.id.id)
        assertEquals("conversation.deleted", deleted.objectType)
        assertTrue(deleted.deleted)
    }
}
