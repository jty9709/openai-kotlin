package com.aallam.openai.client

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import com.aallam.openai.api.admin.AdminApiKeyId
import com.aallam.openai.api.admin.AdminCertificateId
import com.aallam.openai.api.admin.AdminGroupCreateRequest
import com.aallam.openai.api.admin.AdminGroupId
import com.aallam.openai.api.admin.AdminGroupRoleRequest
import com.aallam.openai.api.admin.AdminGroupUserRequest
import com.aallam.openai.api.admin.AdminInviteCreateRequest
import com.aallam.openai.api.admin.AdminProjectCreateRequest
import com.aallam.openai.api.admin.AdminProjectId
import com.aallam.openai.api.admin.AdminRoleCreateRequest
import com.aallam.openai.api.admin.AdminRoleId
import com.aallam.openai.api.admin.AdminSpendAlertId
import com.aallam.openai.api.admin.AdminUsageQuery
import com.aallam.openai.api.admin.AdminUserId
import com.aallam.openai.api.admin.AdminUserRoleRequest
import com.aallam.openai.api.admin.ProjectApiKeyId
import com.aallam.openai.api.admin.ProjectCertificateId
import com.aallam.openai.api.admin.ProjectGroupId
import com.aallam.openai.api.admin.ProjectRoleCreateRequest
import com.aallam.openai.api.admin.ProjectRoleId
import com.aallam.openai.api.admin.ProjectSpendAlertId
import com.aallam.openai.api.admin.ProjectServiceAccountCreateRequest
import com.aallam.openai.api.admin.ProjectServiceAccountId
import com.aallam.openai.api.admin.ProjectUserCreateRequest
import com.aallam.openai.api.admin.ProjectUserId
import com.aallam.openai.api.beta.AgentArtifactId
import com.aallam.openai.api.beta.AgentCreateRequest
import com.aallam.openai.api.beta.AgentEnvironmentId
import com.aallam.openai.api.beta.AgentId
import com.aallam.openai.api.beta.AgentSessionId
import com.aallam.openai.api.beta.AgentSessionCreateRequest
import com.aallam.openai.api.beta.ChatKitSessionCreateRequest
import com.aallam.openai.api.beta.ChatKitWorkflow
import com.aallam.openai.api.beta.AgentSubagentId
import com.aallam.openai.api.beta.AgentTurnId
import com.aallam.openai.api.beta.ChatKitSessionId
import com.aallam.openai.api.beta.ChatKitThreadId
import com.aallam.openai.api.beta.EnvironmentFileCreateRequest
import com.aallam.openai.api.beta.EnvironmentTemplateCreateRequest
import com.aallam.openai.api.beta.EnvironmentTemplateId
import com.aallam.openai.api.beta.EnvironmentTemplateUpdateRequest
import com.aallam.openai.api.beta.VaultCreateRequest
import com.aallam.openai.api.beta.VaultCredentialCreateRequest
import com.aallam.openai.api.beta.VaultCredentialId
import com.aallam.openai.api.beta.VaultId
import com.aallam.openai.api.container.ContainerCreateRequest
import com.aallam.openai.api.container.ContainerFileId
import com.aallam.openai.api.container.ContainerId
import com.aallam.openai.api.live.LiveSessionCreateRequest
import com.aallam.openai.api.live.LiveSessionId
import com.aallam.openai.api.realtime.RealtimeCallId
import com.aallam.openai.api.realtime.RealtimeCallRequest
import com.aallam.openai.api.response.ResponseCompactRequest
import com.aallam.openai.api.response.ResponseInputTokenCountRequest
import com.aallam.openai.api.safety.SafetyAlertId
import com.aallam.openai.api.safety.SafetyCaseId
import com.aallam.openai.api.skill.SkillId
import com.aallam.openai.api.upload.UploadCreateRequest
import com.aallam.openai.api.upload.UploadId
import com.aallam.openai.api.upload.UploadPartId
import com.aallam.openai.api.webhook.WebhookEndpointCreateRequest
import com.aallam.openai.api.webhook.WebhookEndpointId

/**
 * Compile-time checks that every newly added API surface is reachable from [OpenAI] without overload
 * ambiguity against the pre-existing interfaces.
 *
 * These functions are never invoked; they only need to compile.
 */
@Suppress("unused")
private object ExtendedApiSurface {

    suspend fun responseCompaction(openAI: OpenAI) {
        openAI.compactResponse(ResponseCompactRequest())
        openAI.responseInputTokens(ResponseInputTokenCountRequest())
    }

    suspend fun safety(openAI: OpenAI) {
        openAI.safetyCase(SafetyCaseId("case_1"))
        openAI.safetyAlert(SafetyAlertId("alert_1"))
    }

    suspend fun uploads(openAI: OpenAI) {
        openAI.createUpload(
            UploadCreateRequest(filename = "data.jsonl", bytes = 1024, mimeType = "application/jsonl")
        )
        openAI.cancelUpload(UploadId("upload_1"))
        openAI.completeUpload(UploadId("upload_1"), listOf(UploadPartId("part_1")))
    }

    suspend fun webhooks(openAI: OpenAI) {
        openAI.createWebhookEndpoint(
            WebhookEndpointCreateRequest(
                eventTypes = listOf("response.completed"),
                name = "prod",
                url = "https://example.com/hook",
            )
        )
        openAI.webhookEndpoint(WebhookEndpointId("wh_1"))
        openAI.rotateWebhookSecret(WebhookEndpointId("wh_1"))
        openAI.webhookEventTypes()
        openAI.webhookEndpoints(limit = 10)
    }

    suspend fun containers(openAI: OpenAI) {
        openAI.createContainer(ContainerCreateRequest(name = "sandbox"))
        openAI.container(ContainerId("cntr_1"))
        openAI.containers()
        openAI.containerFiles(ContainerId("cntr_1"))
        openAI.containerFileContent(ContainerId("cntr_1"), ContainerFileId("file_1"))
        openAI.deleteContainerFile(ContainerId("cntr_1"), ContainerFileId("file_1"))
    }

    suspend fun skills(openAI: OpenAI) {
        openAI.skill(SkillId("skill_1"))
        openAI.skills()
        openAI.deleteSkill(SkillId("skill_1"))
    }

    suspend fun live(openAI: OpenAI) {
        openAI.createLiveSession(LiveSessionCreateRequest())
        openAI.acceptLiveSession(LiveSessionId("sess_1"))
        openAI.rejectLiveSession(LiveSessionId("sess_1"))
        openAI.hangupLiveSession(LiveSessionId("sess_1"))
    }

    suspend fun admin(openAI: OpenAI) {
        openAI.createProject(AdminProjectCreateRequest(name = "default"))
        openAI.project(AdminProjectId("proj_1"))
        openAI.archiveProject(AdminProjectId("proj_1"))
        openAI.projects(limit = 10)
        openAI.adminUsers()
        openAI.deleteAdminUser(AdminUserId("user_1"))
        openAI.createInvite(AdminInviteCreateRequest(email = "a@example.com", role = "owner"))
        openAI.invites()
        openAI.adminApiKeys()
        openAI.deleteAdminApiKey(AdminApiKeyId("key_1"))
    }

    suspend fun realtime(openAI: OpenAI) {
        openAI.createRealtimeClientSecret()
        openAI.createRealtimeSession()
        openAI.createRealtimeTranscriptionSession()
        openAI.createRealtimeCall(RealtimeCallRequest(sdp = "v=0"))
        openAI.acceptRealtimeCall(RealtimeCallId("call_1"), buildJsonObject { put("type", "realtime"); put("model", "gpt-realtime") })
        openAI.hangupRealtimeCall(RealtimeCallId("call_1"))
    }

    suspend fun realtimeWebSocket(openAI: OpenAI): RealtimeConnection =
        openAI.connectRealtime(clientSecret = "ek_abc", model = "gpt-4o-realtime-preview")

    suspend fun beta(openAI: OpenAI) {
        openAI.agents()
        openAI.createAgent(AgentCreateRequest(model = "gpt-4.1"))
        openAI.agent(AgentId("agent_1"))
        openAI.deleteAgent(AgentId("agent_1"))

        openAI.agentSessions()
        openAI.createAgentSession(AgentSessionCreateRequest(environment = buildJsonObject { put("type", "none") }))
        openAI.deleteAgentSession(AgentSessionId("session_1"))

        openAI.agentEnvironment(AgentEnvironmentId("env_1"))

        openAI.vaults()
        openAI.createVault(VaultCreateRequest(name = "prod"))
        openAI.deleteVault(VaultId("vault_1"))

        openAI.createChatKitSession(ChatKitSessionCreateRequest(user = "user_1", workflow = ChatKitWorkflow("wf_1")))
        openAI.cancelChatKitSession(ChatKitSessionId("session_1"))
        openAI.chatKitThreads()
        openAI.chatKitThread(ChatKitThreadId("thread_1"))
        openAI.deleteChatKitThread(ChatKitThreadId("thread_1"))
    }

    suspend fun betaSubResources(openAI: OpenAI) {
        val vaultId = VaultId("vault_1")
        openAI.vaultCredentials(vaultId)
        openAI.createVaultCredential(vaultId, VaultCredentialCreateRequest(name = "token", value = "s3cr3t"))
        openAI.deleteVaultCredential(vaultId, VaultCredentialId("cred_1"))

        val sessionId = AgentSessionId("session_1")
        openAI.agentSessionArtifacts(sessionId)
        openAI.agentSessionTurns(sessionId)
        openAI.agentSessionItems(sessionId)
        openAI.agentSessionEvents(sessionId)
        openAI.agentSessionSubagents(sessionId)

        val subagentId = AgentSubagentId("subagent_1")
        openAI.agentSubagent(sessionId, subagentId)
        openAI.agentSubagentItems(sessionId, subagentId)
        openAI.agentSubagentTurns(sessionId, subagentId)
        openAI.agentSubagentTurn(sessionId, subagentId, AgentTurnId("turn_1"))
        openAI.agentSubagentTurnItems(sessionId, subagentId, AgentTurnId("turn_1"))

        openAI.agentArtifact(sessionId, AgentArtifactId("artifact_1"))
        openAI.agentArtifactContent(sessionId, AgentArtifactId("artifact_1"))

        openAI.createAgentEnvironmentFile(
            AgentEnvironmentId("env_1"),
            EnvironmentFileCreateRequest(path = "/data.csv")
        )

        openAI.agentEnvironmentFiles(AgentEnvironmentId("env_1"))

        openAI.environmentTemplates()
        openAI.createEnvironmentTemplate(EnvironmentTemplateCreateRequest(name = "python"))
        openAI.environmentTemplate(EnvironmentTemplateId("tmpl_1"))
        openAI.updateEnvironmentTemplate(
            EnvironmentTemplateId("tmpl_1"),
            EnvironmentTemplateUpdateRequest(name = "python-3.12")
        )
        openAI.deleteEnvironmentTemplate(EnvironmentTemplateId("tmpl_1"))
    }

    suspend fun skillVersions(openAI: OpenAI) {
        openAI.skillVersions(SkillId("skill_1"))
        openAI.skillVersion(SkillId("skill_1"), version = "2")
        openAI.skillVersionContent(SkillId("skill_1"), version = "2")
        openAI.deleteSkillVersion(SkillId("skill_1"), version = "2")
    }

    suspend fun adminProjects(openAI: OpenAI) {
        val projectId = AdminProjectId("proj_1")

        openAI.projectApiKeys(projectId)
        openAI.deleteProjectApiKey(projectId, ProjectApiKeyId("key_1"))

        openAI.projectServiceAccounts(projectId)
        openAI.createProjectServiceAccount(projectId, ProjectServiceAccountCreateRequest(name = "ci"))
        openAI.deleteProjectServiceAccount(projectId, ProjectServiceAccountId("sa_1"))

        openAI.projectUsers(projectId)
        openAI.createProjectUser(projectId, ProjectUserCreateRequest(role = "owner", userId = AdminUserId("user_1")))
        openAI.deleteProjectUser(projectId, ProjectUserId("user_1"))

        openAI.projectGroups(projectId)
        openAI.deleteProjectGroup(projectId, ProjectGroupId("group_1"))

        openAI.projectRoles(projectId)
        openAI.createProjectRole(projectId, ProjectRoleCreateRequest(roleName = "r", permissions = listOf("read")))
        openAI.deleteProjectRole(projectId, ProjectRoleId("role_1"))

        openAI.projectRateLimits(projectId)
        openAI.projectModelPermissions(projectId)
        openAI.projectHostedToolPermissions(projectId)
        openAI.projectSpendAlerts(projectId)
        openAI.projectSpendAlert(projectId, ProjectSpendAlertId("alert_1"))
        openAI.projectSpendLimit(projectId)
        openAI.projectDataRetention(projectId)
        openAI.projectCertificates(projectId)
        openAI.deactivateProjectCertificate(projectId, ProjectCertificateId("cert_1"))
        openAI.projectServiceAccountApiKeys(projectId, ProjectServiceAccountId("sa_1"))
        openAI.projectGroup(projectId, ProjectGroupId("group_1"))
        openAI.projectGroupRoles(projectId, ProjectGroupId("group_1"))
        openAI.projectUserRoles(projectId, ProjectUserId("user_1"))
    }

    suspend fun adminOrganization(openAI: OpenAI) {
        openAI.adminGroups()
        openAI.createAdminGroup(AdminGroupCreateRequest(name = "eng"))
        openAI.deleteAdminGroup(AdminGroupId("group_1"))

        openAI.adminCertificates()
        openAI.activateAdminCertificate(AdminCertificateId("cert_1"))

        openAI.adminRoles()
        openAI.createAdminRole(AdminRoleCreateRequest(roleName = "r", permissions = listOf("read")))
        openAI.deleteAdminRole(AdminRoleId("role_1"))

        openAI.adminAuditLogs(limit = 10)
        openAI.adminSpendAlerts()
        openAI.adminSpendAlert(AdminSpendAlertId("alert_1"))
        openAI.adminSpendLimit()
        openAI.adminDataRetention()
        openAI.adminExternalStorages()
        val usageQuery = AdminUsageQuery(startTime = 1_700_000_000)
        openAI.usageCompletions(usageQuery)
        openAI.usageEmbeddings(usageQuery)
        openAI.usageImages(usageQuery)
        openAI.usageCosts(usageQuery)

        openAI.groupRoles(AdminGroupId("group_1"))
        openAI.groupRole(AdminGroupId("group_1"), AdminRoleId("role_1"))
        openAI.createGroupRole(AdminGroupId("group_1"), AdminGroupRoleRequest(roleId = AdminRoleId("role_1")))
        openAI.deleteGroupRole(AdminGroupId("group_1"), AdminRoleId("role_1"))

        openAI.groupUsers(AdminGroupId("group_1"))
        openAI.groupUser(AdminGroupId("group_1"), AdminUserId("user_1"))
        openAI.createGroupUser(AdminGroupId("group_1"), AdminGroupUserRequest(userId = AdminUserId("user_1")))
        openAI.deleteGroupUser(AdminGroupId("group_1"), AdminUserId("user_1"))

        openAI.userRoles(AdminUserId("user_1"))
        openAI.userRole(AdminUserId("user_1"), AdminRoleId("role_1"))
        openAI.createUserRole(AdminUserId("user_1"), AdminUserRoleRequest(roleId = AdminRoleId("role_1")))
        openAI.deleteUserRole(AdminUserId("user_1"), AdminRoleId("role_1"))
    }
}
