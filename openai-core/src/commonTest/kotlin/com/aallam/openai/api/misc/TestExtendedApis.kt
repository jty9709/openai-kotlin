package com.aallam.openai.api.misc

import com.aallam.openai.api.admin.AdminApiKey
import com.aallam.openai.api.admin.AdminInvite
import com.aallam.openai.api.admin.AdminProject
import com.aallam.openai.api.admin.AdminUser
import com.aallam.openai.api.container.Container
import com.aallam.openai.api.container.ContainerFile
import com.aallam.openai.api.live.LiveSessionCreated
import com.aallam.openai.api.realtime.RealtimeClientSecret
import com.aallam.openai.api.realtime.RealtimeEvent
import com.aallam.openai.api.realtime.RealtimeEventType
import com.aallam.openai.api.safety.SafetyAlert
import com.aallam.openai.api.safety.SafetyCase
import com.aallam.openai.api.skill.Skill
import com.aallam.openai.api.upload.Upload
import com.aallam.openai.api.upload.UploadPart
import com.aallam.openai.api.webhook.WebhookEndpoint
import com.aallam.openai.api.webhook.WebhookEndpointWithSecret
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestExtendedApis {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun safetyCaseDeserializesExpectedFields() {
        val payload = """
            {"id":"case_1","object":"safety.case","created_at":1741900000,"entity_identifier":"ent_1","reason":"flagged"}
        """.trimIndent()

        val case = json.decodeFromString(SafetyCase.serializer(), payload)
        assertEquals("case_1", case.id.id)
        assertEquals("ent_1", case.entityIdentifier)
        assertEquals("flagged", case.reason)
    }

    @Test
    fun safetyAlertDeserializesExpectedFields() {
        val payload = """
            {"id":"alert_1","error_type":"moderation","request_paused":true,"response_id":"resp_1"}
        """.trimIndent()

        val alert = json.decodeFromString(SafetyAlert.serializer(), payload)
        assertEquals("alert_1", alert.id.id)
        assertEquals("moderation", alert.errorType)
        assertTrue(alert.requestPaused == true)
        assertEquals("resp_1", alert.responseId)
    }

    @Test
    fun uploadDeserializesExpectedFields() {
        val payload = """
            {"id":"upload_1","bytes":1024,"filename":"data.jsonl","purpose":"batch","status":"pending"}
        """.trimIndent()

        val upload = json.decodeFromString(Upload.serializer(), payload)
        assertEquals("upload_1", upload.id.id)
        assertEquals(1024L, upload.bytes)
        assertEquals("data.jsonl", upload.filename)
    }

    @Test
    fun uploadPartDeserializesExpectedFields() {
        val payload = """{"id":"part_1","upload_id":"upload_1","created_at":1741900000}"""

        val part = json.decodeFromString(UploadPart.serializer(), payload)
        assertEquals("part_1", part.id.id)
        assertEquals("upload_1", part.uploadId?.id)
    }

    @Test
    fun webhookEndpointDeserializesExpectedFields() {
        val payload = """
            {"id":"wh_1","name":"prod","url":"https://example.com/hook","event_types":["response.completed"]}
        """.trimIndent()

        val endpoint = json.decodeFromString(WebhookEndpoint.serializer(), payload)
        assertEquals("wh_1", endpoint.id.id)
        assertEquals("https://example.com/hook", endpoint.url)
        assertEquals(listOf("response.completed"), endpoint.eventTypes)
    }

    @Test
    fun webhookEndpointSecretIsOptional() {
        val payload = """{"id":"wh_1","signing_secret":"whsec_abc"}"""

        val endpoint = json.decodeFromString(WebhookEndpointWithSecret.serializer(), payload)
        assertEquals("whsec_abc", endpoint.signingSecret)

        val withoutSecret = json.decodeFromString(WebhookEndpoint.serializer(), payload)
        assertEquals("wh_1", withoutSecret.id.id)
    }

    @Test
    fun containerAndFileDeserializeExpectedFields() {
        val container = json.decodeFromString(
            Container.serializer(),
            """{"id":"cntr_1","name":"sandbox","status":"running"}""",
        )
        assertEquals("cntr_1", container.id.id)
        assertEquals("sandbox", container.name)

        val file = json.decodeFromString(
            ContainerFile.serializer(),
            """{"id":"file_1","container_id":"cntr_1","path":"/data.csv","bytes":2048}""",
        )
        assertEquals("file_1", file.id.id)
        assertEquals("/data.csv", file.path)
    }

    @Test
    fun skillDeserializesExpectedFields() {
        val payload = """
            {"id":"skill_1","name":"analyzer","default_version":"2","latest_version":"3"}
        """.trimIndent()

        val skill = json.decodeFromString(Skill.serializer(), payload)
        assertEquals("skill_1", skill.id.id)
        assertEquals("2", skill.defaultVersion)
        assertEquals("3", skill.latestVersion)
    }

    @Test
    fun liveSessionCreatedDeserializesTransport() {
        val payload = """
            {"session":{"id":"sess_1","status":"created"},"transport":{"type":"webrtc","sdp":"v=0"}}
        """.trimIndent()

        val created = json.decodeFromString(LiveSessionCreated.serializer(), payload)
        assertEquals("sess_1", created.session?.id?.id)
        assertEquals("webrtc", created.transport?.type)
        assertEquals("v=0", created.transport?.sdp)
    }

    @Test
    fun adminProjectDeserializesExpectedFields() {
        val payload = """{"id":"proj_1","name":"default","status":"active"}"""

        val project = json.decodeFromString(AdminProject.serializer(), payload)
        assertEquals("proj_1", project.id.id)
        assertEquals("default", project.name)
    }

    @Test
    fun adminUserAndInviteDeserializeExpectedFields() {
        val user = json.decodeFromString(
            AdminUser.serializer(),
            """{"id":"user_1","email":"a@example.com","role":"owner"}""",
        )
        assertEquals("user_1", user.id.id)
        assertEquals("owner", user.role)

        val invite = json.decodeFromString(
            AdminInvite.serializer(),
            """{"id":"inv_1","email":"b@example.com","status":"pending"}""",
        )
        assertEquals("inv_1", invite.id.id)
        assertEquals("pending", invite.status)
    }

    @Test
    fun adminApiKeyDeserializesExpectedFields() {
        val payload = """{"id":"key_1","name":"ci","redacted_value":"sk-...abc"}"""

        val key = json.decodeFromString(AdminApiKey.serializer(), payload)
        assertEquals("key_1", key.id.id)
        assertEquals("sk-...abc", key.redactedValue)
    }

    @Test
    fun realtimeEventResolvesTypedEventType() {
        val payload = """{"type":"response.output_audio.delta","event_id":"evt_1","delta":"abc"}"""

        val event = RealtimeEvent.of(Json.parseToJsonElement(payload).jsonObject)
        assertEquals(RealtimeEventType.RESPONSE_OUTPUT_AUDIO_DELTA, event.type)
        assertEquals("response.output_audio.delta", event.rawType)
        assertEquals("abc", event.delta)
        assertEquals("evt_1", event.eventId)
    }

    @Test
    fun realtimeEventKeepsRawTypeWhenUnknown() {
        val payload = """{"type":"response.something_brand_new","value":1}"""

        val event = RealtimeEvent.of(Json.parseToJsonElement(payload).jsonObject)
        assertEquals(RealtimeEventType.UNKNOWN, event.type)
        assertEquals("response.something_brand_new", event.rawType)
    }

    @Test
    fun realtimeClientEventAlwaysCarriesItsType() {
        val event = RealtimeEvent.of(RealtimeEventType.SESSION_UPDATE)

        assertEquals(RealtimeEventType.SESSION_UPDATE, event.type)
        assertEquals("session.update", event.json["type"]?.jsonPrimitive?.content)
    }

    @Test
    fun realtimeClientEventAcceptsRawTypeForUnmodelledEvents() {
        val event = RealtimeEvent.of("session.future_thing")

        assertEquals(RealtimeEventType.UNKNOWN, event.type)
        assertEquals("session.future_thing", event.rawType)
        assertEquals("session.future_thing", event.json["type"]?.jsonPrimitive?.content)
    }

    @Test
    fun realtimeClientSecretDeserializesExpectedFields() {
        val payload = """{"value":"ek_abc","expires_at":1741900000}"""

        val secret = json.decodeFromString(RealtimeClientSecret.serializer(), payload)
        assertEquals("ek_abc", secret.value)
        assertEquals(1741900000L, secret.expiresAt)
    }
}
