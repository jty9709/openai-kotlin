package com.aallam.openai.client

import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.ResponseInput
import com.aallam.openai.api.response.ResponseRequest
import com.aallam.openai.api.response.ResponseStreamEventType
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestResponseStream {

    private val sseBody = """
        data: {"type":"response.created","response":{"id":"resp_1"}}

        data: {"type":"response.output_text.delta","item_id":"msg_1","delta":"Hel"}

        data: {"type":"response.output_text.delta","item_id":"msg_1","delta":"lo"}

        data: {"type":"response.completed","response":{"id":"resp_1"}}

        data: [DONE]

    """.trimIndent()

    @Test
    fun responseStreamParsesSseEvents() = runTest {
        val client = openAIClientReturning(sseBody)

        val events = client.responseStream(
            ResponseRequest(model = ModelId("gpt-4.1"), input = ResponseInput("hi"))
        ).toList()

        assertEquals(4, events.size)
        assertEquals(ResponseStreamEventType.RESPONSE_CREATED, events[0].type)
        assertEquals("resp_1", events[0].responseId)
        assertEquals(ResponseStreamEventType.RESPONSE_OUTPUT_TEXT_DELTA, events[1].type)
        assertEquals("Hel", events[1].delta)
        assertEquals("lo", events[2].delta)
        assertEquals(ResponseStreamEventType.RESPONSE_COMPLETED, events[3].type)
    }

    @Test
    fun responseStreamReportsUnknownEventTypesWithoutFailing() = runTest {
        val body = """
            data: {"type":"response.something_brand_new","delta":"x"}

            data: [DONE]

        """.trimIndent()
        val client = openAIClientReturning(body)

        val events = client.responseStream(
            ResponseRequest(model = ModelId("gpt-4.1"), input = ResponseInput("hi"))
        ).toList()

        assertEquals(1, events.size)
        assertEquals(ResponseStreamEventType.UNKNOWN, events[0].type)
        assertEquals("x", events[0].delta)
    }

    @Test
    fun responseStreamSendsStreamFlagAndEventStreamAccept() = runTest {
        var capturedAccept: String? = null
        var capturedBody: String? = null
        val engine = MockEngine { request ->
            capturedAccept = request.headers[HttpHeaders.Accept]
            capturedBody = (request.body as? OutgoingContent.ByteArrayContent)
                ?.bytes()
                ?.decodeToString()
            respond(content = "data: [DONE]\n\n", headers = eventStreamHeaders())
        }
        val client = OpenAI(OpenAIConfig(token = "test-token", engine = engine))

        client.responseStream(
            ResponseRequest(model = ModelId("gpt-4.1"), input = ResponseInput("hi"))
        ).toList()

        assertTrue(
            capturedAccept?.contains("text/event-stream") == true,
            "Accept header should request an event stream, was: $capturedAccept",
        )
        assertTrue(
            capturedBody?.contains("\"stream\":true") == true,
            "Request body should force stream=true, was: $capturedBody",
        )
    }

    private fun openAIClientReturning(body: String): OpenAI {
        val engine = MockEngine { respond(content = body, headers = eventStreamHeaders()) }
        return OpenAI(OpenAIConfig(token = "test-token", engine = engine))
    }

    private fun eventStreamHeaders() =
        headersOf(HttpHeaders.ContentType, "text/event-stream")
}
