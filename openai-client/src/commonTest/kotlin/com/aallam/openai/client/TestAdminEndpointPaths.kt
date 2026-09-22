package com.aallam.openai.client

import com.aallam.openai.api.admin.AdminApiKeyId
import com.aallam.openai.api.admin.AdminCertificateId
import com.aallam.openai.api.admin.AdminExternalStorageId
import com.aallam.openai.api.admin.AdminGroupId
import com.aallam.openai.api.admin.AdminInviteId
import com.aallam.openai.api.admin.AdminProjectId
import com.aallam.openai.api.admin.AdminRoleId
import com.aallam.openai.api.admin.AdminSpendAlertId
import com.aallam.openai.api.admin.AdminUsageQuery
import com.aallam.openai.api.admin.AdminUserId
import com.aallam.openai.api.admin.ProjectApiKeyId
import com.aallam.openai.api.admin.ProjectCertificateId
import com.aallam.openai.api.admin.ProjectGroupId
import com.aallam.openai.api.admin.ProjectRateLimitId
import com.aallam.openai.api.admin.ProjectRateLimitUpdateRequest
import com.aallam.openai.api.admin.ProjectRoleId
import com.aallam.openai.api.admin.ProjectServiceAccountId
import com.aallam.openai.api.admin.ProjectSpendAlertId
import com.aallam.openai.api.admin.ProjectUserId
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
 * Verifies that each Admin call hits the exact URL the official SDK uses.
 *
 * The request path is captured from a mock engine, so this asserts real wire behaviour rather than
 * the shape of the source.
 */
class TestAdminEndpointPaths {

    @Test
    fun adminEndpointsMatchOfficialPaths() = runTest {
        val captured = mutableListOf<String>()
        val client = openAICapturingPaths(captured)

        val projectId = AdminProjectId("proj_1")
        val usage = AdminUsageQuery(startTime = 1_700_000_000)

        // Organization-level resources.
        runCatching { client.projects() }
        runCatching { client.project(projectId) }
        runCatching { client.archiveProject(projectId) }
        runCatching { client.adminUsers() }
        runCatching { client.adminUser(AdminUserId("user_1")) }
        runCatching { client.invites() }
        runCatching { client.invite(AdminInviteId("inv_1")) }
        runCatching { client.adminApiKeys() }
        runCatching { client.adminApiKey(AdminApiKeyId("key_1")) }
        runCatching { client.adminGroups() }
        runCatching { client.adminGroup(AdminGroupId("grp_1")) }
        runCatching { client.groupRoles(AdminGroupId("grp_1")) }
        runCatching { client.groupRole(AdminGroupId("grp_1"), AdminRoleId("role_1")) }
        runCatching { client.groupUsers(AdminGroupId("grp_1")) }
        runCatching { client.groupUser(AdminGroupId("grp_1"), AdminUserId("user_1")) }
        runCatching { client.userRoles(AdminUserId("user_1")) }
        runCatching { client.userRole(AdminUserId("user_1"), AdminRoleId("role_1")) }
        runCatching { client.adminCertificates() }
        runCatching { client.adminCertificate(AdminCertificateId("cert_1")) }
        runCatching { client.activateAdminCertificate(AdminCertificateId("cert_1")) }
        runCatching { client.deactivateAdminCertificate(AdminCertificateId("cert_1")) }
        runCatching { client.deleteAdminCertificate(AdminCertificateId("cert_1")) }
        runCatching { client.adminRoles() }
        runCatching { client.adminRole(AdminRoleId("role_1")) }
        runCatching { client.adminAuditLogs() }
        runCatching { client.adminSpendAlerts() }
        runCatching { client.adminSpendAlert(AdminSpendAlertId("alert_1")) }
        runCatching { client.adminSpendLimit() }
        runCatching { client.adminDataRetention() }
        runCatching { client.adminExternalStorages() }
        runCatching { client.adminExternalStorage(AdminExternalStorageId("es_1")) }
        runCatching { client.validateAdminExternalStorage(AdminExternalStorageId("es_1")) }

        // Project-scoped resources.
        runCatching { client.projectApiKeys(projectId) }
        runCatching { client.projectApiKey(projectId, ProjectApiKeyId("key_1")) }
        runCatching { client.projectServiceAccounts(projectId) }
        runCatching { client.projectServiceAccountApiKeys(projectId, ProjectServiceAccountId("sa_1")) }
        runCatching { client.projectCertificates(projectId) }
        runCatching { client.activateProjectCertificate(projectId, ProjectCertificateId("cert_1")) }
        runCatching { client.deactivateProjectCertificate(projectId, ProjectCertificateId("cert_1")) }
        runCatching { client.projectServiceAccount(projectId, ProjectServiceAccountId("sa_1")) }
        runCatching {
            client.updateProjectRateLimit(projectId, ProjectRateLimitId("rl_1"), ProjectRateLimitUpdateRequest())
        }
        runCatching { client.projectDataRetention(projectId) }
        runCatching { client.projectGroups(projectId) }
        runCatching { client.projectGroup(projectId, ProjectGroupId("grp_1")) }
        runCatching { client.projectHostedToolPermissions(projectId) }
        runCatching { client.projectModelPermissions(projectId) }
        runCatching { client.projectRateLimits(projectId) }
        runCatching { client.projectRoles(projectId) }
        runCatching { client.projectRole(projectId, ProjectRoleId("role_1")) }
        runCatching { client.projectSpendAlerts(projectId) }
        runCatching { client.projectSpendAlert(projectId, ProjectSpendAlertId("alert_1")) }
        runCatching { client.projectSpendLimit(projectId) }
        runCatching { client.projectUsers(projectId) }
        runCatching { client.projectUser(projectId, ProjectUserId("user_1")) }

        // Role assignments are served from `projects/...`, without the `organization/` prefix.
        runCatching { client.projectGroupRoles(projectId, ProjectGroupId("grp_1")) }
        runCatching { client.projectUserRoles(projectId, ProjectUserId("user_1")) }

        // Usage categories.
        runCatching { client.usageCompletions(usage) }
        runCatching { client.usageEmbeddings(usage) }
        runCatching { client.usageImages(usage) }
        runCatching { client.usageAudioSpeeches(usage) }
        runCatching { client.usageAudioTranscriptions(usage) }
        runCatching { client.usageVectorStores(usage) }
        runCatching { client.usageCodeInterpreterSessions(usage) }
        runCatching { client.usageFileSearchCalls(usage) }
        runCatching { client.usageWebSearchCalls(usage) }
        runCatching { client.usageModerations(usage) }
        runCatching { client.usageCosts(usage) }

        val expected = listOf(
            "/v1/organization/projects",
            "/v1/organization/projects/proj_1",
            "/v1/organization/projects/proj_1/archive",
            "/v1/organization/users",
            "/v1/organization/users/user_1",
            "/v1/organization/invites",
            "/v1/organization/invites/inv_1",
            "/v1/organization/admin_api_keys",
            "/v1/organization/admin_api_keys/key_1",
            "/v1/organization/groups",
            "/v1/organization/groups/grp_1",
            "/v1/organization/groups/grp_1/roles",
            "/v1/organization/groups/grp_1/roles/role_1",
            "/v1/organization/groups/grp_1/users",
            "/v1/organization/groups/grp_1/users/user_1",
            "/v1/organization/users/user_1/roles",
            "/v1/organization/users/user_1/roles/role_1",
            "/v1/organization/certificates",
            "/v1/organization/certificates/cert_1",
            "/v1/organization/certificates/activate",
            "/v1/organization/certificates/deactivate",
            "/v1/organization/certificates/cert_1",
            "/v1/organization/roles",
            "/v1/organization/roles/role_1",
            "/v1/organization/audit_logs",
            "/v1/organization/spend_alerts",
            "/v1/organization/spend_alerts/alert_1",
            "/v1/organization/spend_limit",
            "/v1/organization/data_retention",
            "/v1/organization/external_storage",
            "/v1/organization/external_storage/es_1",
            "/v1/organization/external_storage/es_1/validate",
            "/v1/organization/projects/proj_1/api_keys",
            "/v1/organization/projects/proj_1/api_keys/key_1",
            "/v1/organization/projects/proj_1/service_accounts",
            "/v1/organization/projects/proj_1/service_accounts/sa_1/api_keys",
            "/v1/organization/projects/proj_1/certificates",
            "/v1/organization/projects/proj_1/certificates/activate",
            "/v1/organization/projects/proj_1/certificates/deactivate",
            "/v1/organization/projects/proj_1/service_accounts/sa_1",
            "/v1/organization/projects/proj_1/rate_limits/rl_1",
            "/v1/organization/projects/proj_1/data_retention",
            "/v1/organization/projects/proj_1/groups",
            "/v1/organization/projects/proj_1/groups/grp_1",
            "/v1/organization/projects/proj_1/hosted_tool_permissions",
            "/v1/organization/projects/proj_1/model_permissions",
            "/v1/organization/projects/proj_1/rate_limits",
            "/v1/projects/proj_1/roles",
            "/v1/projects/proj_1/roles/role_1",
            "/v1/organization/projects/proj_1/spend_alerts",
            "/v1/organization/projects/proj_1/spend_alerts/alert_1",
            "/v1/organization/projects/proj_1/spend_limit",
            "/v1/organization/projects/proj_1/users",
            "/v1/organization/projects/proj_1/users/user_1",
            // Role assignments intentionally omit the `organization/` prefix.
            "/v1/projects/proj_1/groups/grp_1/roles",
            "/v1/projects/proj_1/users/user_1/roles",
            // Usage categories, and costs which lives at its own path.
            "/v1/organization/usage/completions",
            "/v1/organization/usage/embeddings",
            "/v1/organization/usage/images",
            "/v1/organization/usage/audio_speeches",
            "/v1/organization/usage/audio_transcriptions",
            "/v1/organization/usage/vector_stores",
            "/v1/organization/usage/code_interpreter_sessions",
            "/v1/organization/usage/file_search_calls",
            "/v1/organization/usage/web_search_calls",
            "/v1/organization/usage/moderations",
            "/v1/organization/costs",
        )

        assertEquals(expected.size, captured.size, "captured paths: $captured")
        expected.forEachIndexed { index, path ->
            assertEquals(path, captured[index], "call #$index")
        }
    }

    private fun openAICapturingPaths(captured: MutableList<String>): OpenAI {
        val engine = MockEngine { request: HttpRequestData ->
            captured += request.url.encodedPath
            // A body that satisfies both paginated lists and delete responses.
            respond(
                content = """{"data":[],"deleted":true,"id":"x","object":"x"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        return OpenAI(OpenAIConfig(token = "test-token", engine = engine))
    }
}
