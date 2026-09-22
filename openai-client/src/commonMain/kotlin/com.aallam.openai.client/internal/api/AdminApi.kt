package com.aallam.openai.client.internal.api

import com.aallam.openai.api.admin.AdminApiKey
import com.aallam.openai.api.admin.AdminApiKeyCreated
import com.aallam.openai.api.admin.AdminApiKeyCreateRequest
import com.aallam.openai.api.admin.AdminApiKeyId
import com.aallam.openai.api.admin.AdminAuditLog
import com.aallam.openai.api.admin.AdminCertificate
import com.aallam.openai.api.admin.AdminCertificateCreateRequest
import com.aallam.openai.api.admin.AdminCertificateId
import com.aallam.openai.api.admin.AdminCertificateUpdateRequest
import com.aallam.openai.api.admin.AdminDataRetention
import com.aallam.openai.api.admin.AdminDataRetentionUpdateRequest
import com.aallam.openai.api.admin.AdminExternalStorage
import com.aallam.openai.api.admin.AdminExternalStorageCreateRequest
import com.aallam.openai.api.admin.AdminExternalStorageId
import com.aallam.openai.api.admin.AdminGroup
import com.aallam.openai.api.admin.AdminGroupCreateRequest
import com.aallam.openai.api.admin.AdminGroupId
import com.aallam.openai.api.admin.AdminGroupRole
import com.aallam.openai.api.admin.AdminGroupRoleRequest
import com.aallam.openai.api.admin.AdminGroupUpdateRequest
import com.aallam.openai.api.admin.AdminGroupUser
import com.aallam.openai.api.admin.AdminGroupUserRequest
import com.aallam.openai.api.admin.AdminInvite
import com.aallam.openai.api.admin.AdminInviteCreateRequest
import com.aallam.openai.api.admin.AdminInviteId
import com.aallam.openai.api.admin.AdminProject
import com.aallam.openai.api.admin.AdminProjectCreateRequest
import com.aallam.openai.api.admin.AdminProjectId
import com.aallam.openai.api.admin.AdminProjectUpdateRequest
import com.aallam.openai.api.admin.AdminRole
import com.aallam.openai.api.admin.AdminRoleCreateRequest
import com.aallam.openai.api.admin.AdminRoleId
import com.aallam.openai.api.admin.AdminRoleUpdateRequest
import com.aallam.openai.api.admin.AdminSpendAlert
import com.aallam.openai.api.admin.AdminSpendAlertCreateRequest
import com.aallam.openai.api.admin.AdminSpendAlertId
import com.aallam.openai.api.admin.AdminSpendAlertUpdateRequest
import com.aallam.openai.api.admin.AdminSpendLimit
import com.aallam.openai.api.admin.AdminSpendLimitUpdateRequest
import com.aallam.openai.api.admin.AdminUser
import com.aallam.openai.api.admin.AdminUserId
import com.aallam.openai.api.admin.AdminUserRole
import com.aallam.openai.api.admin.AdminUserRoleRequest
import com.aallam.openai.api.admin.AdminAudioSpeechesUsagePage
import com.aallam.openai.api.admin.AdminAudioTranscriptionsUsagePage
import com.aallam.openai.api.admin.AdminCodeInterpreterSessionsUsagePage
import com.aallam.openai.api.admin.AdminCompletionsUsagePage
import com.aallam.openai.api.admin.AdminCostsUsagePage
import com.aallam.openai.api.admin.AdminEmbeddingsUsagePage
import com.aallam.openai.api.admin.AdminFileSearchCallsUsagePage
import com.aallam.openai.api.admin.AdminImagesUsagePage
import com.aallam.openai.api.admin.AdminModerationsUsagePage
import com.aallam.openai.api.admin.AdminVectorStoresUsagePage
import com.aallam.openai.api.admin.AdminWebSearchCallsUsagePage
import com.aallam.openai.api.admin.AdminUsageQuery
import com.aallam.openai.api.admin.AdminUserUpdateRequest
import com.aallam.openai.api.admin.ProjectApiKey
import com.aallam.openai.api.admin.ProjectApiKeyId
import com.aallam.openai.api.admin.ProjectCertificate
import com.aallam.openai.api.admin.ProjectCertificateId
import com.aallam.openai.api.admin.ProjectDataRetention
import com.aallam.openai.api.admin.ProjectDataRetentionUpdateRequest
import com.aallam.openai.api.admin.ProjectGroup
import com.aallam.openai.api.admin.ProjectGroupCreateRequest
import com.aallam.openai.api.admin.ProjectGroupId
import com.aallam.openai.api.admin.ProjectHostedToolPermissions
import com.aallam.openai.api.admin.ProjectHostedToolPermissionsUpdateRequest
import com.aallam.openai.api.admin.ProjectModelPermissions
import com.aallam.openai.api.admin.ProjectModelPermissionsUpdateRequest
import com.aallam.openai.api.admin.ProjectRateLimit
import com.aallam.openai.api.admin.ProjectRateLimitId
import com.aallam.openai.api.admin.ProjectRateLimitUpdateRequest
import com.aallam.openai.api.admin.ProjectRole
import com.aallam.openai.api.admin.ProjectRoleCreateRequest
import com.aallam.openai.api.admin.ProjectRoleId
import com.aallam.openai.api.admin.ProjectRoleUpdateRequest
import com.aallam.openai.api.admin.ProjectServiceAccount
import com.aallam.openai.api.admin.ProjectServiceAccountCreated
import com.aallam.openai.api.admin.ProjectServiceAccountCreateRequest
import com.aallam.openai.api.admin.ProjectServiceAccountId
import com.aallam.openai.api.admin.ProjectServiceAccountUpdateRequest
import com.aallam.openai.api.admin.ProjectSpendAlert
import com.aallam.openai.api.admin.ProjectSpendAlertCreateRequest
import com.aallam.openai.api.admin.ProjectSpendAlertId
import com.aallam.openai.api.admin.ProjectSpendAlertUpdateRequest
import com.aallam.openai.api.admin.ProjectSpendLimit
import com.aallam.openai.api.admin.ProjectSpendLimitUpdateRequest
import com.aallam.openai.api.admin.ProjectUser
import com.aallam.openai.api.admin.ProjectUserCreateRequest
import com.aallam.openai.api.admin.ProjectUserId
import com.aallam.openai.api.admin.ProjectUserUpdateRequest
import com.aallam.openai.api.core.DeleteResponse
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.client.Admin
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class AdminApi(private val requester: HttpRequester) : Admin {

    override suspend fun createProject(
        request: AdminProjectCreateRequest,
        requestOptions: RequestOptions?
    ): AdminProject {
        return requester.perform {
            it.post {
                url(path = ApiPath.AdminProjects)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun project(id: AdminProjectId, requestOptions: RequestOptions?): AdminProject {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AdminProjects}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateProject(
        id: AdminProjectId,
        request: AdminProjectUpdateRequest,
        requestOptions: RequestOptions?
    ): AdminProject {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.AdminProjects}/${id.id}")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun archiveProject(
        id: AdminProjectId,
        requestOptions: RequestOptions?
    ): AdminProject {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.AdminProjects}/${id.id}/archive")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projects(
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AdminProject> {
        return requester.perform {
            it.get {
                url(path = ApiPath.AdminProjects) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun adminUser(id: AdminUserId, requestOptions: RequestOptions?): AdminUser {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AdminUsers}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateAdminUser(
        id: AdminUserId,
        request: AdminUserUpdateRequest,
        requestOptions: RequestOptions?
    ): AdminUser {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.AdminUsers}/${id.id}")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteAdminUser(
        id: AdminUserId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.AdminUsers}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun adminUsers(
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AdminUser> {
        return requester.perform {
            it.get {
                url(path = ApiPath.AdminUsers) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createInvite(
        request: AdminInviteCreateRequest,
        requestOptions: RequestOptions?
    ): AdminInvite {
        return requester.perform {
            it.post {
                url(path = ApiPath.AdminInvites)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun invite(id: AdminInviteId, requestOptions: RequestOptions?): AdminInvite {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AdminInvites}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteInvite(
        id: AdminInviteId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.AdminInvites}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun invites(
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AdminInvite> {
        return requester.perform {
            it.get {
                url(path = ApiPath.AdminInvites) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createAdminApiKey(
        request: AdminApiKeyCreateRequest,
        requestOptions: RequestOptions?
    ): AdminApiKeyCreated {
        return requester.perform {
            it.post {
                url(path = ApiPath.AdminApiKeys)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun adminApiKey(
        id: AdminApiKeyId,
        requestOptions: RequestOptions?
    ): AdminApiKey {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AdminApiKeys}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteAdminApiKey(
        id: AdminApiKeyId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.AdminApiKeys}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun adminApiKeys(
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AdminApiKey> {
        return requester.perform {
            it.get {
                url(path = ApiPath.AdminApiKeys) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    // ---- Project sub-resources ----

    private fun projectPath(projectId: AdminProjectId, suffix: String) =
        "${ApiPath.AdminProjects}/${projectId.id}/$suffix"

    override suspend fun projectApiKeys(
        projectId: AdminProjectId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<ProjectApiKey> {
        return requester.perform {
            it.get {
                url(path = projectPath(projectId, "api_keys")) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectApiKey(
        projectId: AdminProjectId,
        keyId: ProjectApiKeyId,
        requestOptions: RequestOptions?
    ): ProjectApiKey {
        return requester.perform {
            it.get {
                url(path = projectPath(projectId, "api_keys/${keyId.id}"))
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteProjectApiKey(
        projectId: AdminProjectId,
        keyId: ProjectApiKeyId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = projectPath(projectId, "api_keys/${keyId.id}"))
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectServiceAccounts(
        projectId: AdminProjectId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<ProjectServiceAccount> {
        return requester.perform {
            it.get {
                url(path = projectPath(projectId, "service_accounts")) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectServiceAccount(
        projectId: AdminProjectId,
        id: ProjectServiceAccountId,
        requestOptions: RequestOptions?
    ): ProjectServiceAccount {
        return requester.perform {
            it.get {
                url(path = projectPath(projectId, "service_accounts/${id.id}"))
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createProjectServiceAccount(
        projectId: AdminProjectId,
        request: ProjectServiceAccountCreateRequest,
        requestOptions: RequestOptions?
    ): ProjectServiceAccountCreated {
        return requester.perform {
            it.post {
                url(path = projectPath(projectId, "service_accounts"))
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateProjectServiceAccount(
        projectId: AdminProjectId,
        id: ProjectServiceAccountId,
        request: ProjectServiceAccountUpdateRequest,
        requestOptions: RequestOptions?
    ): ProjectServiceAccount {
        return requester.perform {
            it.post {
                url(path = projectPath(projectId, "service_accounts/${id.id}"))
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteProjectServiceAccount(
        projectId: AdminProjectId,
        id: ProjectServiceAccountId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = projectPath(projectId, "service_accounts/${id.id}"))
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectUsers(
        projectId: AdminProjectId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<ProjectUser> {
        return requester.perform {
            it.get {
                url(path = projectPath(projectId, "users")) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectUser(
        projectId: AdminProjectId,
        userId: ProjectUserId,
        requestOptions: RequestOptions?
    ): ProjectUser {
        return requester.perform {
            it.get {
                url(path = projectPath(projectId, "users/${userId.id}"))
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createProjectUser(
        projectId: AdminProjectId,
        request: ProjectUserCreateRequest,
        requestOptions: RequestOptions?
    ): ProjectUser {
        return requester.perform {
            it.post {
                url(path = projectPath(projectId, "users"))
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateProjectUser(
        projectId: AdminProjectId,
        userId: ProjectUserId,
        request: ProjectUserUpdateRequest,
        requestOptions: RequestOptions?
    ): ProjectUser {
        return requester.perform {
            it.post {
                url(path = projectPath(projectId, "users/${userId.id}"))
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteProjectUser(
        projectId: AdminProjectId,
        userId: ProjectUserId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = projectPath(projectId, "users/${userId.id}"))
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectGroups(
        projectId: AdminProjectId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<ProjectGroup> {
        return requester.perform {
            it.get {
                url(path = projectPath(projectId, "groups")) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createProjectGroup(
        projectId: AdminProjectId,
        request: ProjectGroupCreateRequest,
        requestOptions: RequestOptions?
    ): ProjectGroup {
        return requester.perform {
            it.post {
                url(path = projectPath(projectId, "groups"))
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteProjectGroup(
        projectId: AdminProjectId,
        groupId: ProjectGroupId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = projectPath(projectId, "groups/${groupId.id}"))
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectRoles(
        projectId: AdminProjectId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<ProjectRole> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Projects}/${projectId.id}/roles") {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectRole(
        projectId: AdminProjectId,
        roleId: ProjectRoleId,
        requestOptions: RequestOptions?
    ): ProjectRole {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Projects}/${projectId.id}/roles/${roleId.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createProjectRole(
        projectId: AdminProjectId,
        request: ProjectRoleCreateRequest,
        requestOptions: RequestOptions?
    ): ProjectRole {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Projects}/${projectId.id}/roles")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateProjectRole(
        projectId: AdminProjectId,
        roleId: ProjectRoleId,
        request: ProjectRoleUpdateRequest,
        requestOptions: RequestOptions?
    ): ProjectRole {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Projects}/${projectId.id}/roles/${roleId.id}")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteProjectRole(
        projectId: AdminProjectId,
        roleId: ProjectRoleId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.Projects}/${projectId.id}/roles/${roleId.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectRateLimits(
        projectId: AdminProjectId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<ProjectRateLimit> {
        return requester.perform {
            it.get {
                url(path = projectPath(projectId, "rate_limits")) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateProjectRateLimit(
        projectId: AdminProjectId,
        rateLimitId: ProjectRateLimitId,
        request: ProjectRateLimitUpdateRequest,
        requestOptions: RequestOptions?
    ): ProjectRateLimit {
        return requester.perform {
            it.post {
                url(path = projectPath(projectId, "rate_limits/${rateLimitId.id}"))
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectModelPermissions(
        projectId: AdminProjectId,
        requestOptions: RequestOptions?
    ): ProjectModelPermissions {
        return requester.perform {
            it.get {
                url(path = projectPath(projectId, "model_permissions"))
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateProjectModelPermissions(
        projectId: AdminProjectId,
        request: ProjectModelPermissionsUpdateRequest,
        requestOptions: RequestOptions?
    ): ProjectModelPermissions {
        return requester.perform {
            it.post {
                url(path = projectPath(projectId, "model_permissions"))
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteProjectModelPermissions(
        projectId: AdminProjectId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = projectPath(projectId, "model_permissions"))
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectHostedToolPermissions(
        projectId: AdminProjectId,
        requestOptions: RequestOptions?
    ): ProjectHostedToolPermissions {
        return requester.perform {
            it.get {
                url(path = projectPath(projectId, "hosted_tool_permissions"))
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateProjectHostedToolPermissions(
        projectId: AdminProjectId,
        request: ProjectHostedToolPermissionsUpdateRequest,
        requestOptions: RequestOptions?
    ): ProjectHostedToolPermissions {
        return requester.perform {
            it.post {
                url(path = projectPath(projectId, "hosted_tool_permissions"))
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectSpendAlerts(
        projectId: AdminProjectId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<ProjectSpendAlert> {
        return requester.perform {
            it.get {
                url(path = projectPath(projectId, "spend_alerts")) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createProjectSpendAlert(
        projectId: AdminProjectId,
        request: ProjectSpendAlertCreateRequest,
        requestOptions: RequestOptions?
    ): ProjectSpendAlert {
        return requester.perform {
            it.post {
                url(path = projectPath(projectId, "spend_alerts"))
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateProjectSpendAlert(
        projectId: AdminProjectId,
        alertId: ProjectSpendAlertId,
        request: ProjectSpendAlertUpdateRequest,
        requestOptions: RequestOptions?
    ): ProjectSpendAlert {
        return requester.perform {
            it.post {
                url(path = projectPath(projectId, "spend_alerts/${alertId.id}"))
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteProjectSpendAlert(
        projectId: AdminProjectId,
        alertId: ProjectSpendAlertId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = projectPath(projectId, "spend_alerts/${alertId.id}"))
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectSpendLimit(
        projectId: AdminProjectId,
        requestOptions: RequestOptions?
    ): ProjectSpendLimit {
        return requester.perform {
            it.get {
                url(path = projectPath(projectId, "spend_limit"))
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateProjectSpendLimit(
        projectId: AdminProjectId,
        request: ProjectSpendLimitUpdateRequest,
        requestOptions: RequestOptions?
    ): ProjectSpendLimit {
        return requester.perform {
            it.post {
                url(path = projectPath(projectId, "spend_limit"))
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteProjectSpendLimit(
        projectId: AdminProjectId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = projectPath(projectId, "spend_limit"))
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectDataRetention(
        projectId: AdminProjectId,
        requestOptions: RequestOptions?
    ): ProjectDataRetention {
        return requester.perform {
            it.get {
                url(path = projectPath(projectId, "data_retention"))
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateProjectDataRetention(
        projectId: AdminProjectId,
        request: ProjectDataRetentionUpdateRequest,
        requestOptions: RequestOptions?
    ): ProjectDataRetention {
        return requester.perform {
            it.post {
                url(path = projectPath(projectId, "data_retention"))
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectCertificates(
        projectId: AdminProjectId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<ProjectCertificate> {
        return requester.perform {
            it.get {
                url(path = projectPath(projectId, "certificates")) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun activateProjectCertificate(
        projectId: AdminProjectId,
        certificateId: ProjectCertificateId,
        requestOptions: RequestOptions?
    ): ProjectCertificate {
        return requester.perform {
            it.post {
                url(path = projectPath(projectId, "certificates/activate"))
                setBody(mapOf("certificate_id" to certificateId.id))
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deactivateProjectCertificate(
        projectId: AdminProjectId,
        certificateId: ProjectCertificateId,
        requestOptions: RequestOptions?
    ): ProjectCertificate {
        return requester.perform {
            it.post {
                url(path = projectPath(projectId, "certificates/deactivate"))
                setBody(mapOf("certificate_id" to certificateId.id))
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    // ---- Organization sub-resources ----

    override suspend fun adminGroups(
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AdminGroup> {
        return requester.perform {
            it.get {
                url(path = ApiPath.AdminGroups) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun adminGroup(id: AdminGroupId, requestOptions: RequestOptions?): AdminGroup {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AdminGroups}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createAdminGroup(
        request: AdminGroupCreateRequest,
        requestOptions: RequestOptions?
    ): AdminGroup {
        return requester.perform {
            it.post {
                url(path = ApiPath.AdminGroups)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateAdminGroup(
        id: AdminGroupId,
        request: AdminGroupUpdateRequest,
        requestOptions: RequestOptions?
    ): AdminGroup {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.AdminGroups}/${id.id}")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteAdminGroup(
        id: AdminGroupId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.AdminGroups}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun adminCertificates(
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AdminCertificate> {
        return requester.perform {
            it.get {
                url(path = ApiPath.AdminCertificates) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun adminCertificate(
        id: AdminCertificateId,
        requestOptions: RequestOptions?
    ): AdminCertificate {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AdminCertificates}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createAdminCertificate(
        request: AdminCertificateCreateRequest,
        requestOptions: RequestOptions?
    ): AdminCertificate {
        return requester.perform {
            it.post {
                url(path = ApiPath.AdminCertificates)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateAdminCertificate(
        id: AdminCertificateId,
        request: AdminCertificateUpdateRequest,
        requestOptions: RequestOptions?
    ): AdminCertificate {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.AdminCertificates}/${id.id}")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteAdminCertificate(
        id: AdminCertificateId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.AdminCertificates}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun activateAdminCertificate(
        id: AdminCertificateId,
        requestOptions: RequestOptions?
    ): AdminCertificate {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.AdminCertificates}/activate")
                setBody(mapOf("certificate_id" to id.id))
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deactivateAdminCertificate(
        id: AdminCertificateId,
        requestOptions: RequestOptions?
    ): AdminCertificate {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.AdminCertificates}/deactivate")
                setBody(mapOf("certificate_id" to id.id))
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun adminRoles(
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AdminRole> {
        return requester.perform {
            it.get {
                url(path = ApiPath.AdminRoles) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun adminRole(id: AdminRoleId, requestOptions: RequestOptions?): AdminRole {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AdminRoles}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createAdminRole(
        request: AdminRoleCreateRequest,
        requestOptions: RequestOptions?
    ): AdminRole {
        return requester.perform {
            it.post {
                url(path = ApiPath.AdminRoles)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateAdminRole(
        id: AdminRoleId,
        request: AdminRoleUpdateRequest,
        requestOptions: RequestOptions?
    ): AdminRole {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.AdminRoles}/${id.id}")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteAdminRole(
        id: AdminRoleId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.AdminRoles}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun adminAuditLogs(
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AdminAuditLog> {
        return requester.perform {
            it.get {
                url(path = ApiPath.AdminAuditLogs) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun adminSpendAlerts(
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AdminSpendAlert> {
        return requester.perform {
            it.get {
                url(path = ApiPath.AdminSpendAlerts) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createAdminSpendAlert(
        request: AdminSpendAlertCreateRequest,
        requestOptions: RequestOptions?
    ): AdminSpendAlert {
        return requester.perform {
            it.post {
                url(path = ApiPath.AdminSpendAlerts)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateAdminSpendAlert(
        id: AdminSpendAlertId,
        request: AdminSpendAlertUpdateRequest,
        requestOptions: RequestOptions?
    ): AdminSpendAlert {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.AdminSpendAlerts}/${id.id}")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteAdminSpendAlert(
        id: AdminSpendAlertId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.AdminSpendAlerts}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun adminSpendLimit(requestOptions: RequestOptions?): AdminSpendLimit {
        return requester.perform {
            it.get {
                url(path = ApiPath.AdminSpendLimit)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateAdminSpendLimit(
        request: AdminSpendLimitUpdateRequest,
        requestOptions: RequestOptions?
    ): AdminSpendLimit {
        return requester.perform {
            it.post {
                url(path = ApiPath.AdminSpendLimit)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteAdminSpendLimit(requestOptions: RequestOptions?): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = ApiPath.AdminSpendLimit)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun adminDataRetention(requestOptions: RequestOptions?): AdminDataRetention {
        return requester.perform {
            it.get {
                url(path = ApiPath.AdminDataRetention)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateAdminDataRetention(
        request: AdminDataRetentionUpdateRequest,
        requestOptions: RequestOptions?
    ): AdminDataRetention {
        return requester.perform {
            it.post {
                url(path = ApiPath.AdminDataRetention)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun adminExternalStorages(
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AdminExternalStorage> {
        return requester.perform {
            it.get {
                url(path = ApiPath.AdminExternalStorage) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun adminExternalStorage(
        id: AdminExternalStorageId,
        requestOptions: RequestOptions?
    ): AdminExternalStorage {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AdminExternalStorage}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createAdminExternalStorage(
        request: AdminExternalStorageCreateRequest,
        requestOptions: RequestOptions?
    ): AdminExternalStorage {
        return requester.perform {
            it.post {
                url(path = ApiPath.AdminExternalStorage)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteAdminExternalStorage(
        id: AdminExternalStorageId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.AdminExternalStorage}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun validateAdminExternalStorage(
        id: AdminExternalStorageId,
        requestOptions: RequestOptions?
    ): AdminExternalStorage {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.AdminExternalStorage}/${id.id}/validate")
                requestOptions(requestOptions)
            }.body()
        }
    }

    /** Applies the shared usage-query filters to a request. */
    private fun HttpRequestBuilder.appendUsageQuery(category: String, query: AdminUsageQuery) {
        url(path = "${ApiPath.AdminUsage}/$category") {
            parameter("start_time", query.startTime)
            query.endTime?.let { value -> parameter("end_time", value) }
            query.bucketWidth?.let { value -> parameter("bucket_width", value) }
            query.groupBy?.forEach { value -> parameter("group_by[]", value) }
            query.limit?.let { value -> parameter("limit", value) }
            query.page?.let { value -> parameter("page", value) }
            query.projectIds?.forEach { value -> parameter("project_ids[]", value) }
            query.userIds?.forEach { value -> parameter("user_ids[]", value) }
            query.apiKeyIds?.forEach { value -> parameter("api_key_ids[]", value) }
            query.models?.forEach { value -> parameter("models[]", value) }
        }
    }

    override suspend fun usageCompletions(
        query: AdminUsageQuery,
        batch: Boolean?,
        requestOptions: RequestOptions?
    ): AdminCompletionsUsagePage {
        return requester.perform {
            it.get {
                appendUsageQuery("completions", query)
                batch?.let { value -> parameter("batch", value) }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun usageEmbeddings(
        query: AdminUsageQuery,
        requestOptions: RequestOptions?
    ): AdminEmbeddingsUsagePage {
        return requester.perform {
            it.get {
                appendUsageQuery("embeddings", query)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun usageModerations(
        query: AdminUsageQuery,
        requestOptions: RequestOptions?
    ): AdminModerationsUsagePage {
        return requester.perform {
            it.get {
                appendUsageQuery("moderations", query)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun usageImages(
        query: AdminUsageQuery,
        sizes: List<String>?,
        sources: List<String>?,
        requestOptions: RequestOptions?
    ): AdminImagesUsagePage {
        return requester.perform {
            it.get {
                appendUsageQuery("images", query)
                sizes?.forEach { value -> parameter("sizes", value) }
                sources?.forEach { value -> parameter("sources", value) }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun usageAudioSpeeches(
        query: AdminUsageQuery,
        requestOptions: RequestOptions?
    ): AdminAudioSpeechesUsagePage {
        return requester.perform {
            it.get {
                appendUsageQuery("audio_speeches", query)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun usageAudioTranscriptions(
        query: AdminUsageQuery,
        requestOptions: RequestOptions?
    ): AdminAudioTranscriptionsUsagePage {
        return requester.perform {
            it.get {
                appendUsageQuery("audio_transcriptions", query)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun usageVectorStores(
        query: AdminUsageQuery,
        requestOptions: RequestOptions?
    ): AdminVectorStoresUsagePage {
        return requester.perform {
            it.get {
                appendUsageQuery("vector_stores", query)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun usageCodeInterpreterSessions(
        query: AdminUsageQuery,
        requestOptions: RequestOptions?
    ): AdminCodeInterpreterSessionsUsagePage {
        return requester.perform {
            it.get {
                appendUsageQuery("code_interpreter_sessions", query)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun usageFileSearchCalls(
        query: AdminUsageQuery,
        requestOptions: RequestOptions?
    ): AdminFileSearchCallsUsagePage {
        return requester.perform {
            it.get {
                appendUsageQuery("file_search_calls", query)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun usageWebSearchCalls(
        query: AdminUsageQuery,
        requestOptions: RequestOptions?
    ): AdminWebSearchCallsUsagePage {
        return requester.perform {
            it.get {
                appendUsageQuery("web_search_calls", query)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun usageCosts(
        query: AdminUsageQuery,
        requestOptions: RequestOptions?
    ): AdminCostsUsagePage {
        return requester.perform {
            it.get {
                // Costs live at `organization/costs`, not under `organization/usage/`.
                url(path = ApiPath.AdminCosts) {
                    parameter("start_time", query.startTime)
                    query.endTime?.let { value -> parameter("end_time", value) }
                    query.bucketWidth?.let { value -> parameter("bucket_width", value) }
                    query.groupBy?.forEach { value -> parameter("group_by[]", value) }
                    query.limit?.let { value -> parameter("limit", value) }
                    query.page?.let { value -> parameter("page", value) }
                    query.projectIds?.forEach { value -> parameter("project_ids[]", value) }
                    query.apiKeyIds?.forEach { value -> parameter("api_key_ids[]", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    // ---- Role assignments ----

    override suspend fun groupRoles(
        groupId: AdminGroupId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AdminGroupRole> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AdminGroups}/${groupId.id}/roles") {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createGroupRole(
        groupId: AdminGroupId,
        request: AdminGroupRoleRequest,
        requestOptions: RequestOptions?
    ): AdminGroupRole {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.AdminGroups}/${groupId.id}/roles")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteGroupRole(
        groupId: AdminGroupId,
        roleId: AdminRoleId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.AdminGroups}/${groupId.id}/roles/${roleId.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun groupUsers(
        groupId: AdminGroupId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AdminGroupUser> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AdminGroups}/${groupId.id}/users") {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createGroupUser(
        groupId: AdminGroupId,
        request: AdminGroupUserRequest,
        requestOptions: RequestOptions?
    ): AdminGroupUser {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.AdminGroups}/${groupId.id}/users")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteGroupUser(
        groupId: AdminGroupId,
        userId: AdminUserId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.AdminGroups}/${groupId.id}/users/${userId.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun userRoles(
        userId: AdminUserId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AdminUserRole> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AdminUsers}/${userId.id}/roles") {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createUserRole(
        userId: AdminUserId,
        request: AdminUserRoleRequest,
        requestOptions: RequestOptions?
    ): AdminUserRole {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.AdminUsers}/${userId.id}/roles")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteUserRole(
        userId: AdminUserId,
        roleId: AdminRoleId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.AdminUsers}/${userId.id}/roles/${roleId.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun groupRole(
        groupId: AdminGroupId,
        roleId: AdminRoleId,
        requestOptions: RequestOptions?
    ): AdminGroupRole {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AdminGroups}/${groupId.id}/roles/${roleId.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun groupUser(
        groupId: AdminGroupId,
        userId: AdminUserId,
        requestOptions: RequestOptions?
    ): AdminGroupUser {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AdminGroups}/${groupId.id}/users/${userId.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun userRole(
        userId: AdminUserId,
        roleId: AdminRoleId,
        requestOptions: RequestOptions?
    ): AdminUserRole {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AdminUsers}/${userId.id}/roles/${roleId.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun adminSpendAlert(
        id: AdminSpendAlertId,
        requestOptions: RequestOptions?
    ): AdminSpendAlert {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AdminSpendAlerts}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectServiceAccountApiKeys(
        projectId: AdminProjectId,
        serviceAccountId: ProjectServiceAccountId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<ProjectApiKey> {
        return requester.perform {
            it.get {
                url(
                    path = projectPath(
                        projectId,
                        "service_accounts/${serviceAccountId.id}/api_keys",
                    )
                ) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectSpendAlert(
        projectId: AdminProjectId,
        alertId: ProjectSpendAlertId,
        requestOptions: RequestOptions?
    ): ProjectSpendAlert {
        return requester.perform {
            it.get {
                url(path = projectPath(projectId, "spend_alerts/${alertId.id}"))
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectGroup(
        projectId: AdminProjectId,
        groupId: ProjectGroupId,
        requestOptions: RequestOptions?
    ): ProjectGroup {
        return requester.perform {
            it.get {
                url(path = projectPath(projectId, "groups/${groupId.id}"))
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectGroupRoles(
        projectId: AdminProjectId,
        groupId: ProjectGroupId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AdminGroupRole> {
        return requester.perform {
            it.get {
                // Note: role assignments are served from `projects/...`, without the
                // `organization/` prefix used by the other project sub-resources.
                url(path = "${ApiPath.Projects}/${projectId.id}/groups/${groupId.id}/roles") {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun projectUserRoles(
        projectId: AdminProjectId,
        userId: ProjectUserId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AdminUserRole> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Projects}/${projectId.id}/users/${userId.id}/roles") {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }
}
