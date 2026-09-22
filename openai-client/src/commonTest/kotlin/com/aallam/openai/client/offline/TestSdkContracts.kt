package com.aallam.openai.client.offline

import com.aallam.openai.api.admin.*
import com.aallam.openai.api.beta.*
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.live.*
import com.aallam.openai.api.realtime.*
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import io.ktor.client.engine.mock.*
import io.ktor.client.request.HttpRequestData
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import kotlin.test.*

/** Wire contracts taken from openai-java 4.65.0, exercised without network access. */
class TestSdkContracts {
    private fun client(
        body: String = "{}",
        status: HttpStatusCode = HttpStatusCode.OK,
        headers: Headers = headersOf(HttpHeaders.ContentType, "application/json"),
        inspect: suspend (HttpRequestData) -> Unit = {},
    ): OpenAI = OpenAI(OpenAIConfig(token = "test-token", engine = MockEngine { request ->
        inspect(request)
        respond(body, status, headers)
    }))

    private suspend fun HttpRequestData.jsonBody(): JsonObject =
        Json.parseToJsonElement(body.toByteArray().decodeToString()).jsonObject

    @Test
    fun realtimeCallSendsMultipartAndReturnsSdpAndLocation() = runTest {
        val offer = "v=0\r\no=- 123 2 IN IP4 127.0.0.1\r\n"
        val answer = "v=0\r\no=- 456 2 IN IP4 127.0.0.1\r\n"
        val session = buildJsonObject { put("type", "realtime"); put("model", "gpt-realtime") }
        val location = "https://api.openai.com/v1/realtime/calls/call_123"
        val api = client(answer, HttpStatusCode.Created, headersOf(
            HttpHeaders.ContentType to listOf("application/sdp"),
            HttpHeaders.Location to listOf(location),
            "x-request-id" to listOf("req_123"),
        )) { request ->
            assertEquals(HttpMethod.Post, request.method)
            assertEquals("/v1/realtime/calls", request.url.encodedPath)
            assertEquals(ContentType.MultiPart.FormData, request.body.contentType?.withoutParameters())
            val multipart = request.body.toByteArray().decodeToString()
            assertContains(multipart, "name=sdp")
            assertContains(multipart, "Content-Type: application/sdp")
            assertContains(multipart, offer)
            assertContains(multipart, "name=session")
            assertContains(multipart, "Content-Type: application/json")
            assertContains(multipart, session.toString())
        }
        try {
            val call = api.createRealtimeCall(RealtimeCallRequest(offer, session))
            assertEquals(answer, call.sdp)
            assertEquals(location, call.location)
            assertEquals("call_123", call.id?.id)
            assertEquals(listOf("req_123"), call.headers.entries.first { it.key.equals("x-request-id", true) }.value)
        } finally { api.close() }
    }

    @Test
    fun realtimeCallMayOmitSessionAndLocation() = runTest {
        val api = client("v=0", headers = headersOf(HttpHeaders.ContentType, "application/sdp")) { request ->
            assertFalse(request.body.toByteArray().decodeToString().contains("name=session"))
        }
        try { assertNull(api.createRealtimeCall(RealtimeCallRequest("v=0")).id) } finally { api.close() }
    }

    @Test
    fun realtimeControlsAcceptEmptySuccessAndSendExpectedBodies() = runTest {
        val captured = mutableListOf<String>()
        val session = buildJsonObject { put("type", "realtime"); put("model", "gpt-realtime") }
        for (status in listOf(HttpStatusCode.OK, HttpStatusCode.NoContent)) {
            val api = client("", status, headersOf()) { request ->
                assertEquals(HttpMethod.Post, request.method)
                captured += request.url.encodedPath.substringAfterLast('/')
                when (captured.last()) {
                    "accept" -> assertEquals(session, request.jsonBody())
                    "reject" -> assertEquals(JsonPrimitive(486), request.jsonBody()["status_code"])
                    "refer" -> assertEquals(JsonPrimitive("tel:+123"), request.jsonBody()["target_uri"])
                    "hangup" -> assertTrue(request.body.toByteArray().isEmpty())
                }
            }
            try {
                api.acceptRealtimeCall(RealtimeCallId("call_1"), session)
                api.rejectRealtimeCall(RealtimeCallId("call_1"), RealtimeCallRejectRequest(486))
                api.hangupRealtimeCall(RealtimeCallId("call_1"))
                api.referRealtimeCall(RealtimeCallId("call_1"), RealtimeCallReferRequest("tel:+123"))
            } finally { api.close() }
        }
        assertEquals(List(2) { listOf("accept", "reject", "hangup", "refer") }.flatten(), captured)
    }

    @Test
    fun liveControlsAcceptEmptySuccess() = runTest {
        for (status in listOf(HttpStatusCode.OK, HttpStatusCode.NoContent)) {
            val api = client("", status, headersOf())
            try {
                api.acceptLiveSession(LiveSessionId("live_1"), LiveSessionAcceptRequest(buildJsonObject { put("model", "test-model") }))
                api.rejectLiveSession(LiveSessionId("live_1"))
                api.hangupLiveSession(LiveSessionId("live_1"))
                api.referLiveSession(LiveSessionId("live_1"), LiveSessionReferRequest("tel:+123"))
            } finally { api.close() }
        }
    }

    @Test
    fun liveForkReturnsSessionAndTransport() = runTest {
        val api = client("""{"session":{"id":"live_2"},"transport":{"type":"webrtc","sdp":"answer"}}""")
        try {
            val fork = api.forkLiveSession(LiveSessionId("live_1"), LiveSessionForkRequest(LiveTransport("webrtc", "offer")))
            assertEquals("live_2", fork.session?.id?.id)
            assertEquals("answer", fork.transport?.sdp)
        } finally { api.close() }
    }

    @Test
    fun betaRequestsSendResourceHeadersAndStructuredBodies() = runTest {
        val calls = mutableListOf<String>()
        val api = client("""{"id":"resource_1","data":[]}""") { request ->
            calls += request.url.encodedPath
            val chatkit = request.url.encodedPath.contains("/chatkit/")
            assertEquals(if (chatkit) "chatkit_beta=v1" else "agents=v1", request.headers["OpenAI-Beta"])
            if (request.method == HttpMethod.Post) {
                val body = request.jsonBody()
                if (chatkit) assertEquals(JsonPrimitive("wf_1"), body["workflow"]?.jsonObject?.get("id"))
                else assertEquals(JsonPrimitive("none"), body["environment"]?.jsonObject?.get("type"))
            }
        }
        try {
            api.agents()
            api.vaults()
            api.environmentTemplates()
            api.createAgentSession(AgentSessionCreateRequest(buildJsonObject { put("type", "none") }, AgentId("agent_1")))
            api.createChatKitSession(ChatKitSessionCreateRequest("user_1", ChatKitWorkflow("wf_1")))
            api.chatKitThreads()
            assertEquals(6, calls.size)
        } finally { api.close() }
    }

    @Test
    fun betaRequestOptionsCanOverrideDefaultHeader() = runTest {
        val api = client("""{"data":[]}""") { request ->
            assertEquals(listOf("agents=test"), request.headers.getAll("OpenAI-Beta"))
        }
        try { api.agents(requestOptions = RequestOptions(headers = mapOf("OpenAI-Beta" to "agents=test"))) } finally { api.close() }
    }

    @Test
    fun createdKeysRetainTheirSecretValues() = runTest {
        val admin = client("""{"id":"key_1","value":"test-admin-secret","redacted_value":"test-..."}""")
        val service = client("""{"id":"sa_1","api_key":{"id":"key_2","value":"test-service-secret"}}""")
        try {
            assertEquals("test-admin-secret", admin.createAdminApiKey(AdminApiKeyCreateRequest("test")).value)
            val account = service.createProjectServiceAccount(AdminProjectId("proj_1"), ProjectServiceAccountCreateRequest("test"))
            assertEquals("test-service-secret", account.apiKey?.value)
            assertEquals("key_2", account.apiKey?.id?.id)
        } finally { admin.close(); service.close() }
    }

    @Test
    fun projectRoleCrudUsesProjectsRoot() = runTest {
        val captured = mutableListOf<Pair<HttpMethod, String>>()
        val api = client("""{"id":"role_1","object":"role","deleted":true,"data":[]}""") { request ->
            captured += request.method to request.url.encodedPath
        }
        try {
            val project = AdminProjectId("proj_1")
            val role = ProjectRoleId("role_1")
            api.projectRoles(project)
            api.projectRole(project, role)
            api.createProjectRole(project, ProjectRoleCreateRequest("test", listOf("api.responses.read")))
            api.updateProjectRole(project, role, ProjectRoleUpdateRequest(roleName = "updated"))
            api.deleteProjectRole(project, role)
            assertEquals(listOf(
                HttpMethod.Get to "/v1/projects/proj_1/roles",
                HttpMethod.Get to "/v1/projects/proj_1/roles/role_1",
                HttpMethod.Post to "/v1/projects/proj_1/roles",
                HttpMethod.Post to "/v1/projects/proj_1/roles/role_1",
                HttpMethod.Delete to "/v1/projects/proj_1/roles/role_1",
            ), captured)
        } finally { api.close() }
    }

    @Test
    fun externalStorageValidationUsesIdAndReturnsConfiguration() = runTest {
        val api = client("""{"id":"es_1","created_at":1,"geography":"us","project_id":"proj_1","provider":"aws_s3","status":"active"}""") { request ->
            assertEquals(HttpMethod.Post, request.method)
            assertEquals("/v1/organization/external_storage/es_1/validate", request.url.encodedPath)
            assertTrue(request.body.toByteArray().isEmpty())
        }
        try {
            val storage = api.validateAdminExternalStorage(AdminExternalStorageId("es_1"))
            assertEquals("es_1", storage.id.id)
            assertEquals("active", storage.status)
            assertEquals("aws_s3", storage.provider)
        } finally { api.close() }
    }

    @Test
    fun projectUserCanBeIdentifiedByIdOrEmail() = runTest {
        val bodies = mutableListOf<JsonObject>()
        val api = client("""{"id":"user_1"}""") { bodies += it.jsonBody() }
        try {
            api.createProjectUser(AdminProjectId("proj_1"), ProjectUserCreateRequest("member", userId = AdminUserId("user_1")))
            api.createProjectUser(AdminProjectId("proj_1"), ProjectUserCreateRequest("member", email = "user@example.com"))
            assertEquals(Json.parseToJsonElement("""{"role":"member","user_id":"user_1"}"""), bodies[0])
            assertEquals(Json.parseToJsonElement("""{"role":"member","email":"user@example.com"}"""), bodies[1])
        } finally { api.close() }
    }

    @Test
    fun usageFiltersUseBracketedArraysAndPreserveMultipleValues() = runTest {
        val api = client("""{"data":[]}""") { request ->
            val parameters = request.url.parameters
            assertEquals(listOf("project_id", "model"), parameters.getAll("group_by[]"))
            assertEquals(listOf("proj_1", "proj_2"), parameters.getAll("project_ids[]"))
            if (request.url.encodedPath.endsWith("/completions")) {
                assertEquals(listOf("user_1"), parameters.getAll("user_ids[]"))
                assertEquals(listOf("key_1"), parameters.getAll("api_key_ids[]"))
                assertEquals(listOf("test-model"), parameters.getAll("models[]"))
            }
            assertNull(parameters["group_by"])
            assertNull(parameters["project_ids"])
        }
        try {
            val query = AdminUsageQuery(1, groupBy = listOf("project_id", "model"), projectIds = listOf("proj_1", "proj_2"), userIds = listOf("user_1"), apiKeyIds = listOf("key_1"), models = listOf("test-model"))
            api.usageCompletions(query)
            api.usageCosts(query)
        } finally { api.close() }
    }
}
