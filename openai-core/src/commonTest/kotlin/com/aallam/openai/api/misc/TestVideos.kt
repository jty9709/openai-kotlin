package com.aallam.openai.api.misc

import com.aallam.openai.api.video.Video
import com.aallam.openai.api.video.VideoDeleted
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TestVideos {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun videoDeserializesExpectedFields() {
        val payload = """
            {
              "id":"video_123",
              "object":"video",
              "created_at":1741900000,
              "status":"completed",
              "model":"sora-2",
              "seconds":"8",
              "size":"1280x720",
              "progress":100,
              "prompt":"a cat"
            }
        """.trimIndent()

        val video = json.decodeFromString(Video.serializer(), payload)
        assertEquals("video_123", video.id.id)
        assertEquals("completed", video.status)
        assertEquals("sora-2", video.model?.id)
        assertEquals("8", video.seconds?.seconds)
        assertEquals("1280x720", video.size?.size)
        assertEquals(1741900000L, video.createdAt)
        assertNull(video.error)
    }

    @Test
    fun videoDeserializesGenerationError() {
        val payload = """
            {
              "id":"video_123",
              "status":"failed",
              "error":{"code":"moderation_blocked","message":"blocked"}
            }
        """.trimIndent()

        val video = json.decodeFromString(Video.serializer(), payload)
        assertEquals("failed", video.status)
        assertEquals("moderation_blocked", video.error?.code)
        assertEquals("blocked", video.error?.message)
    }

    @Test
    fun videoDeletedDeserializesExpectedFields() {
        val payload = """{"id":"video_123","object":"video.deleted","deleted":true}"""

        val deleted = json.decodeFromString(VideoDeleted.serializer(), payload)
        assertEquals("video_123", deleted.id.id)
        assertTrue(deleted.deleted)
    }
}
