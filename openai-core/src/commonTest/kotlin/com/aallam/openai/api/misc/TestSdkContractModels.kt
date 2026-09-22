package com.aallam.openai.api.misc

import com.aallam.openai.api.admin.*
import com.aallam.openai.api.beta.*
import com.aallam.openai.api.response.ResponseStreamEvent
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*
import kotlin.test.*

class TestSdkContractModels {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun allUsagePagesPreserveBucketsAndGroupedResults() {
        assertUsage(AdminCompletionsUsagePage.serializer(), """{"input_tokens":123,"output_tokens":45,"project_id":"proj_1"}""")
        assertUsage(AdminEmbeddingsUsagePage.serializer(), """{"input_tokens":12,"num_model_requests":2}""")
        assertUsage(AdminModerationsUsagePage.serializer(), """{"input_tokens":14,"num_model_requests":3}""")
        assertUsage(AdminImagesUsagePage.serializer(), """{"images":4,"source":"image.generation"}""")
        assertUsage(AdminAudioSpeechesUsagePage.serializer(), """{"characters":100,"num_model_requests":2}""")
        assertUsage(AdminAudioTranscriptionsUsagePage.serializer(), """{"seconds":30,"num_model_requests":1}""")
        assertUsage(AdminVectorStoresUsagePage.serializer(), """{"usage_bytes":1024,"project_id":"proj_1"}""")
        assertUsage(AdminCodeInterpreterSessionsUsagePage.serializer(), """{"num_sessions":5,"project_id":"proj_1"}""")
        assertUsage(AdminFileSearchCallsUsagePage.serializer(), """{"num_requests":6,"vector_store_id":"vs_1"}""")
        assertUsage(AdminWebSearchCallsUsagePage.serializer(), """{"num_requests":7,"model":"test-model"}""")
        assertUsage(AdminCostsUsagePage.serializer(), """{"amount":{"value":0.125,"currency":"usd"},"line_item":"test","quantity":2.5}""")
    }

    private fun <T> assertUsage(serializer: KSerializer<T>, result: String) {
        // Preserve two result groups and a second, empty time bucket, not only the first metric.
        val payload = """{"data":[{"start_time":1,"end_time":2,"results":[$result,$result]},{"start_time":2,"end_time":3,"results":[]}],"has_more":true,"next_page":"cursor_2"}"""
        val decoded = json.decodeFromString(serializer, payload)
        assertEquals(json.parseToJsonElement(payload), json.encodeToJsonElement(serializer, decoded))
    }

    @Test
    fun usageMetricsAreAvailableAtTheirTypedPaths() {
        val page = json.decodeFromString(AdminCompletionsUsagePage.serializer(), """{"data":[{"start_time":1,"end_time":2,"results":[{"input_tokens":123,"output_tokens":45}]}]}""")
        assertEquals(123, page.data.single().results.single().inputTokens)
        val costs = json.decodeFromString(AdminCostsUsagePage.serializer(), """{"data":[{"start_time":1,"end_time":2,"results":[{"amount":{"value":0.125,"currency":"usd"}}]}]}""")
        assertEquals(0.125, costs.data.single().results.single().amount?.value)
        assertEquals("usd", costs.data.single().results.single().amount?.currency)
    }

    @Test
    fun responseLifecycleEventsExposeNestedIdsAndErrors() {
        for (type in listOf("response.created", "response.completed", "response.failed")) {
            val event = ResponseStreamEvent.of(json.parseToJsonElement("""{"type":"$type","sequence_number":3,"response":{"id":"resp_1","error":{"code":"server_error","message":"failed"}}}""").jsonObject)
            assertEquals("resp_1", event.responseId)
            assertEquals(3L, event.sequenceNumber)
            assertEquals("failed", event.message)
        }
        for (type in listOf("response.output_item.added", "response.output_item.done")) {
            val event = ResponseStreamEvent.of(json.parseToJsonElement("""{"type":"$type","item":{"id":"msg_1"},"output_index":0}""").jsonObject)
            assertEquals("msg_1", event.id)
            assertEquals(0, event.outputIndex)
        }
        val error = ResponseStreamEvent.of(json.parseToJsonElement("""{"type":"error","message":"bad request"}""").jsonObject)
        assertEquals("bad request", error.message)
        assertNull(error.responseId)
        assertNull(error.id)
    }

    @Test
    fun betaRequestsUseStructuredEnvironmentAndWorkflow() {
        val environment = buildJsonObject { put("type", "none") }
        assertEquals(
            json.parseToJsonElement("""{"environment":{"type":"none"},"agent_id":"agent_1"}"""),
            json.encodeToJsonElement(AgentSessionCreateRequest.serializer(), AgentSessionCreateRequest(environment, AgentId("agent_1"))),
        )
        assertEquals(
            json.parseToJsonElement("""{"user":"user_1","workflow":{"id":"wf_1","version":"2"}}"""),
            json.encodeToJsonElement(ChatKitSessionCreateRequest.serializer(), ChatKitSessionCreateRequest("user_1", ChatKitWorkflow("wf_1", version = "2"))),
        )
    }
}
