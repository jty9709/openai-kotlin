package com.aallam.openai.api.misc

import com.aallam.openai.api.eval.Eval
import com.aallam.openai.api.eval.EvalCreateRequest
import com.aallam.openai.api.eval.EvalDeleted
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestEvals {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun evalDeserializesExpectedFields() {
        val payload = """
            {
              "id":"eval_123",
              "created_at":1741900000,
              "name":"my eval",
              "data_source_config":{"type":"custom"},
              "metadata":{"env":"test"}
            }
        """.trimIndent()

        val eval = json.decodeFromString(Eval.serializer(), payload)
        assertEquals("eval_123", eval.id.id)
        assertEquals("my eval", eval.name)
        assertEquals(1741900000L, eval.createdAt)
        assertEquals("custom", eval.dataSourceConfig?.get("type")?.jsonPrimitive?.content)
        assertEquals("test", eval.metadata?.get("env"))
    }

    @Test
    fun evalCreateRequestSerializesExpectedFields() {
        val request = EvalCreateRequest(
            name = "my eval",
            dataSourceConfig = buildJsonObject { put("type", "custom") },
            metadata = mapOf("env" to "test"),
        )

        val encoded = Json.encodeToJsonElement(EvalCreateRequest.serializer(), request).jsonObject
        assertEquals("my eval", encoded["name"]?.jsonPrimitive?.content)
        assertEquals(
            "custom",
            encoded["data_source_config"]?.jsonObject?.get("type")?.jsonPrimitive?.content,
        )
    }

    @Test
    fun evalDeletedDeserializesExpectedFields() {
        val payload = """{"eval_id":"eval_123","deleted":true}"""

        val deleted = json.decodeFromString(EvalDeleted.serializer(), payload)
        assertEquals("eval_123", deleted.evalId.id)
        assertTrue(deleted.deleted)
    }
}
