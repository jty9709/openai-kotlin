package com.aallam.openai.client

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import com.aallam.openai.api.container.ContainerId
import com.aallam.openai.api.container.ContainerFileId
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.client.internal.TestFileSystem
import com.aallam.openai.client.internal.testFilePath
import com.aallam.openai.api.live.LiveSessionCreateRequest
import com.aallam.openai.api.live.LiveSessionId
import com.aallam.openai.api.live.LiveSessionReferRequest
import com.aallam.openai.api.realtime.RealtimeCallId
import com.aallam.openai.api.realtime.RealtimeCallReferRequest
import com.aallam.openai.api.realtime.RealtimeCallRequest
import com.aallam.openai.api.safety.SafetyAlertId
import com.aallam.openai.api.safety.SafetyCaseId
import com.aallam.openai.api.skill.SkillId
import com.aallam.openai.api.upload.UploadCreateRequest
import com.aallam.openai.api.upload.UploadId
import com.aallam.openai.api.upload.UploadPartId
import com.aallam.openai.api.webhook.WebhookEndpointCreateRequest
import com.aallam.openai.api.webhook.WebhookEndpointId
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Verifies that each call for the newly added capabilities hits the exact URL the official SDK
 * uses. Paths are captured from a mock engine, so this asserts real wire behaviour.
 */
class TestApiEndpointPaths {

    @Test
    fun safetyUploadsWebhooksPathsMatchOfficial() = runTest {
        val captured = mutableListOf<String>()
        val client = openAICapturing(captured)

        // Safety.
        runCatching { client.safetyCase(SafetyCaseId("case_1")) }
        runCatching { client.safetyAlert(SafetyAlertId("alert_1")) }

        // Uploads.
        runCatching {
            client.createUpload(UploadCreateRequest("f.jsonl", 10, "application/jsonl"))
        }
        runCatching { client.cancelUpload(UploadId("up_1")) }
        runCatching { client.completeUpload(UploadId("up_1"), listOf(UploadPartId("p_1"))) }
        runCatching {
            client.createUploadPart(
                uploadId = UploadId("up_1"),
                data = FileSource(path = testFilePath("json/chatChunk.json"), fileSystem = TestFileSystem),
            )
        }

        // Webhooks.
        runCatching {
            client.createWebhookEndpoint(WebhookEndpointCreateRequest(listOf("a"), "n", "u"))
        }
        runCatching { client.webhookEndpoint(WebhookEndpointId("wh_1")) }
        runCatching { client.deleteWebhookEndpoint(WebhookEndpointId("wh_1")) }
        runCatching { client.webhookEventTypes() }
        runCatching { client.rotateWebhookSecret(WebhookEndpointId("wh_1")) }
        runCatching { client.testWebhookEndpoint(WebhookEndpointId("wh_1")) }

        // Containers.
        runCatching { client.containers() }
        runCatching { client.container(ContainerId("c_1")) }
        runCatching { client.containerFiles(ContainerId("c_1")) }
        runCatching { client.containerFile(ContainerId("c_1"), ContainerFileId("f_1")) }
        runCatching { client.containerFileContent(ContainerId("c_1"), ContainerFileId("f_1")) }

        // Skills.
        runCatching { client.skills() }
        runCatching { client.skill(SkillId("s_1")) }
        runCatching { client.skillContent(SkillId("s_1")) }
        runCatching { client.skillVersions(SkillId("s_1")) }
        runCatching { client.skillVersion(SkillId("s_1"), "2") }
        runCatching { client.skillVersionContent(SkillId("s_1"), "2") }
        runCatching { client.deleteSkillVersion(SkillId("s_1"), "2") }

        assertEquals(
            listOf(
                "/v1/safety/cases/case_1",
                "/v1/safety/alerts/alert_1",
                "/v1/uploads",
                "/v1/uploads/up_1/cancel",
                "/v1/uploads/up_1/complete",
                "/v1/uploads/up_1/parts",
                "/v1/webhook_endpoints",
                "/v1/webhook_endpoints/wh_1",
                "/v1/webhook_endpoints/wh_1",
                "/v1/webhook_event_types",
                "/v1/webhook_endpoints/wh_1/rotate_secret",
                "/v1/webhook_endpoints/wh_1/test",
                "/v1/containers",
                "/v1/containers/c_1",
                "/v1/containers/c_1/files",
                "/v1/containers/c_1/files/f_1",
                "/v1/containers/c_1/files/f_1/content",
                "/v1/skills",
                "/v1/skills/s_1",
                "/v1/skills/s_1/content",
                "/v1/skills/s_1/versions",
                "/v1/skills/s_1/versions/2",
                "/v1/skills/s_1/versions/2/content",
                "/v1/skills/s_1/versions/2",
            ),
            captured,
        )
    }

    @Test
    fun liveAndRealtimePathsMatchOfficial() = runTest {
        val captured = mutableListOf<String>()
        val client = openAICapturing(captured)

        // Live sessions.
        runCatching { client.createLiveSession(LiveSessionCreateRequest()) }
        runCatching { client.acceptLiveSession(LiveSessionId("s_1")) }
        runCatching { client.rejectLiveSession(LiveSessionId("s_1")) }
        runCatching { client.hangupLiveSession(LiveSessionId("s_1")) }
        runCatching { client.forkLiveSession(LiveSessionId("s_1")) }
        runCatching { client.referLiveSession(LiveSessionId("s_1"), LiveSessionReferRequest("tel:+1")) }
        runCatching { client.downloadLiveRecording(LiveSessionId("s_1")) }

        // Realtime REST.
        runCatching { client.createRealtimeClientSecret() }
        runCatching { client.createRealtimeSession() }
        runCatching { client.createRealtimeTranscriptionSession() }
        runCatching { client.createRealtimeCall(RealtimeCallRequest(sdp = "v=0")) }
        runCatching { client.acceptRealtimeCall(RealtimeCallId("call_1"), buildJsonObject { put("type", "realtime"); put("model", "gpt-realtime") }) }
        runCatching { client.rejectRealtimeCall(RealtimeCallId("call_1")) }
        runCatching { client.hangupRealtimeCall(RealtimeCallId("call_1")) }
        runCatching { client.referRealtimeCall(RealtimeCallId("call_1"), RealtimeCallReferRequest("tel:+1")) }

        assertEquals(
            listOf(
                "/v1/live/sessions",
                "/v1/live/sessions/s_1/accept",
                "/v1/live/sessions/s_1/reject",
                "/v1/live/sessions/s_1/hangup",
                "/v1/live/sessions/s_1/fork",
                "/v1/live/sessions/s_1/refer",
                "/v1/live/sessions/s_1/content",
                "/v1/realtime/client_secrets",
                "/v1/realtime/sessions",
                "/v1/realtime/transcription_sessions",
                "/v1/realtime/calls",
                "/v1/realtime/calls/call_1/accept",
                "/v1/realtime/calls/call_1/reject",
                "/v1/realtime/calls/call_1/hangup",
                "/v1/realtime/calls/call_1/refer",
            ),
            captured,
        )
    }

    private fun openAICapturing(captured: MutableList<String>): OpenAI {
        val engine = MockEngine { request: HttpRequestData ->
            captured += request.url.encodedPath
            respond(
                content = """{"data":[],"deleted":true,"id":"x","object":"x"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        return OpenAI(OpenAIConfig(token = "test-token", engine = engine))
    }
}
