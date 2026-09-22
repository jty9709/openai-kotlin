package com.aallam.openai.client

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

/**
 * Manage organization projects, users, invites and admin API keys.
 */
public interface Admin {

    /**
     * Creates an organization project.
     */
    public suspend fun createProject(
        request: AdminProjectCreateRequest,
        requestOptions: RequestOptions? = null
    ): AdminProject

    /**
     * Retrieves an organization project by its identifier.
     */
    public suspend fun project(
        id: AdminProjectId,
        requestOptions: RequestOptions? = null
    ): AdminProject

    /**
     * Updates an organization project.
     */
    public suspend fun updateProject(
        id: AdminProjectId,
        request: AdminProjectUpdateRequest,
        requestOptions: RequestOptions? = null
    ): AdminProject

    /**
     * Archives an organization project.
     */
    public suspend fun archiveProject(
        id: AdminProjectId,
        requestOptions: RequestOptions? = null
    ): AdminProject

    /**
     * Lists organization projects.
     */
    public suspend fun projects(
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AdminProject>

    /**
     * Retrieves an organization user by their identifier.
     */
    public suspend fun adminUser(
        id: AdminUserId,
        requestOptions: RequestOptions? = null
    ): AdminUser

    /**
     * Updates an organization user.
     */
    public suspend fun updateAdminUser(
        id: AdminUserId,
        request: AdminUserUpdateRequest,
        requestOptions: RequestOptions? = null
    ): AdminUser

    /**
     * Removes a user from the organization.
     */
    public suspend fun deleteAdminUser(
        id: AdminUserId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Lists organization users.
     */
    public suspend fun adminUsers(
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AdminUser>

    /**
     * Invites a user to the organization.
     */
    public suspend fun createInvite(
        request: AdminInviteCreateRequest,
        requestOptions: RequestOptions? = null
    ): AdminInvite

    /**
     * Retrieves an invite by its identifier.
     */
    public suspend fun invite(
        id: AdminInviteId,
        requestOptions: RequestOptions? = null
    ): AdminInvite

    /**
     * Deletes an invite.
     */
    public suspend fun deleteInvite(
        id: AdminInviteId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Lists invites.
     */
    public suspend fun invites(
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AdminInvite>

    /**
     * Creates an admin API key.
     */
    public suspend fun createAdminApiKey(
        request: AdminApiKeyCreateRequest,
        requestOptions: RequestOptions? = null
    ): AdminApiKeyCreated

    /**
     * Retrieves an admin API key by its identifier.
     */
    public suspend fun adminApiKey(
        id: AdminApiKeyId,
        requestOptions: RequestOptions? = null
    ): AdminApiKey

    /**
     * Deletes an admin API key.
     */
    public suspend fun deleteAdminApiKey(
        id: AdminApiKeyId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Lists admin API keys.
     */
    public suspend fun adminApiKeys(
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AdminApiKey>

    /**
     * Lists the API keys of a project.
     */
    public suspend fun projectApiKeys(
        projectId: AdminProjectId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<ProjectApiKey>

    /**
     * Retrieves a project API key.
     */
    public suspend fun projectApiKey(
        projectId: AdminProjectId,
        keyId: ProjectApiKeyId,
        requestOptions: RequestOptions? = null
    ): ProjectApiKey

    /**
     * Deletes a project API key.
     */
    public suspend fun deleteProjectApiKey(
        projectId: AdminProjectId,
        keyId: ProjectApiKeyId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Lists the API keys belonging to a project service account.
     */
    public suspend fun projectServiceAccountApiKeys(
        projectId: AdminProjectId,
        serviceAccountId: ProjectServiceAccountId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<ProjectApiKey>

    /**
     * Retrieves a project spend alert.
     */
    public suspend fun projectSpendAlert(
        projectId: AdminProjectId,
        alertId: ProjectSpendAlertId,
        requestOptions: RequestOptions? = null
    ): ProjectSpendAlert

    /**
     * Retrieves a group belonging to a project.
     */
    public suspend fun projectGroup(
        projectId: AdminProjectId,
        groupId: ProjectGroupId,
        requestOptions: RequestOptions? = null
    ): ProjectGroup

    /**
     * Lists the roles granted to a group within a project.
     */
    public suspend fun projectGroupRoles(
        projectId: AdminProjectId,
        groupId: ProjectGroupId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AdminGroupRole>

    /**
     * Lists the roles granted to a user within a project.
     */
    public suspend fun projectUserRoles(
        projectId: AdminProjectId,
        userId: ProjectUserId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AdminUserRole>

    /**
     * Lists the service accounts of a project.
     */
    public suspend fun projectServiceAccounts(
        projectId: AdminProjectId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<ProjectServiceAccount>

    /**
     * Retrieves a project service account.
     */
    public suspend fun projectServiceAccount(
        projectId: AdminProjectId,
        id: ProjectServiceAccountId,
        requestOptions: RequestOptions? = null
    ): ProjectServiceAccount

    /**
     * Creates a service account in a project.
     */
    public suspend fun createProjectServiceAccount(
        projectId: AdminProjectId,
        request: ProjectServiceAccountCreateRequest,
        requestOptions: RequestOptions? = null
    ): ProjectServiceAccountCreated

    /**
     * Updates a project service account.
     */
    public suspend fun updateProjectServiceAccount(
        projectId: AdminProjectId,
        id: ProjectServiceAccountId,
        request: ProjectServiceAccountUpdateRequest,
        requestOptions: RequestOptions? = null
    ): ProjectServiceAccount

    /**
     * Deletes a project service account.
     */
    public suspend fun deleteProjectServiceAccount(
        projectId: AdminProjectId,
        id: ProjectServiceAccountId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Lists the users of a project.
     */
    public suspend fun projectUsers(
        projectId: AdminProjectId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<ProjectUser>

    /**
     * Retrieves a project user.
     */
    public suspend fun projectUser(
        projectId: AdminProjectId,
        userId: ProjectUserId,
        requestOptions: RequestOptions? = null
    ): ProjectUser

    /**
     * Adds a user to a project.
     */
    public suspend fun createProjectUser(
        projectId: AdminProjectId,
        request: ProjectUserCreateRequest,
        requestOptions: RequestOptions? = null
    ): ProjectUser

    /**
     * Updates a project user.
     */
    public suspend fun updateProjectUser(
        projectId: AdminProjectId,
        userId: ProjectUserId,
        request: ProjectUserUpdateRequest,
        requestOptions: RequestOptions? = null
    ): ProjectUser

    /**
     * Removes a user from a project.
     */
    public suspend fun deleteProjectUser(
        projectId: AdminProjectId,
        userId: ProjectUserId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Lists the groups of a project.
     */
    public suspend fun projectGroups(
        projectId: AdminProjectId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<ProjectGroup>

    /**
     * Adds a group to a project.
     */
    public suspend fun createProjectGroup(
        projectId: AdminProjectId,
        request: ProjectGroupCreateRequest,
        requestOptions: RequestOptions? = null
    ): ProjectGroup

    /**
     * Removes a group from a project.
     */
    public suspend fun deleteProjectGroup(
        projectId: AdminProjectId,
        groupId: ProjectGroupId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Lists the roles of a project.
     */
    public suspend fun projectRoles(
        projectId: AdminProjectId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<ProjectRole>

    /**
     * Retrieves a project role.
     */
    public suspend fun projectRole(
        projectId: AdminProjectId,
        roleId: ProjectRoleId,
        requestOptions: RequestOptions? = null
    ): ProjectRole

    /**
     * Creates a role in a project.
     */
    public suspend fun createProjectRole(
        projectId: AdminProjectId,
        request: ProjectRoleCreateRequest,
        requestOptions: RequestOptions? = null
    ): ProjectRole

    /**
     * Updates a project role.
     */
    public suspend fun updateProjectRole(
        projectId: AdminProjectId,
        roleId: ProjectRoleId,
        request: ProjectRoleUpdateRequest,
        requestOptions: RequestOptions? = null
    ): ProjectRole

    /**
     * Deletes a project role.
     */
    public suspend fun deleteProjectRole(
        projectId: AdminProjectId,
        roleId: ProjectRoleId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Lists the rate limits of a project.
     */
    public suspend fun projectRateLimits(
        projectId: AdminProjectId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<ProjectRateLimit>

    /**
     * Updates a project rate limit.
     */
    public suspend fun updateProjectRateLimit(
        projectId: AdminProjectId,
        rateLimitId: ProjectRateLimitId,
        request: ProjectRateLimitUpdateRequest,
        requestOptions: RequestOptions? = null
    ): ProjectRateLimit

    /**
     * Retrieves the model permissions of a project.
     */
    public suspend fun projectModelPermissions(
        projectId: AdminProjectId,
        requestOptions: RequestOptions? = null
    ): ProjectModelPermissions

    /**
     * Updates the model permissions of a project.
     */
    public suspend fun updateProjectModelPermissions(
        projectId: AdminProjectId,
        request: ProjectModelPermissionsUpdateRequest,
        requestOptions: RequestOptions? = null
    ): ProjectModelPermissions

    /**
     * Deletes the model permissions override of a project.
     */
    public suspend fun deleteProjectModelPermissions(
        projectId: AdminProjectId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Retrieves the hosted tool permissions of a project.
     */
    public suspend fun projectHostedToolPermissions(
        projectId: AdminProjectId,
        requestOptions: RequestOptions? = null
    ): ProjectHostedToolPermissions

    /**
     * Updates the hosted tool permissions of a project.
     */
    public suspend fun updateProjectHostedToolPermissions(
        projectId: AdminProjectId,
        request: ProjectHostedToolPermissionsUpdateRequest,
        requestOptions: RequestOptions? = null
    ): ProjectHostedToolPermissions

    /**
     * Lists the spend alerts of a project.
     */
    public suspend fun projectSpendAlerts(
        projectId: AdminProjectId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<ProjectSpendAlert>

    /**
     * Creates a spend alert in a project.
     */
    public suspend fun createProjectSpendAlert(
        projectId: AdminProjectId,
        request: ProjectSpendAlertCreateRequest,
        requestOptions: RequestOptions? = null
    ): ProjectSpendAlert

    /**
     * Updates a project spend alert.
     */
    public suspend fun updateProjectSpendAlert(
        projectId: AdminProjectId,
        alertId: ProjectSpendAlertId,
        request: ProjectSpendAlertUpdateRequest,
        requestOptions: RequestOptions? = null
    ): ProjectSpendAlert

    /**
     * Deletes a project spend alert.
     */
    public suspend fun deleteProjectSpendAlert(
        projectId: AdminProjectId,
        alertId: ProjectSpendAlertId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Retrieves the spend limit of a project.
     */
    public suspend fun projectSpendLimit(
        projectId: AdminProjectId,
        requestOptions: RequestOptions? = null
    ): ProjectSpendLimit

    /**
     * Updates the spend limit of a project.
     */
    public suspend fun updateProjectSpendLimit(
        projectId: AdminProjectId,
        request: ProjectSpendLimitUpdateRequest,
        requestOptions: RequestOptions? = null
    ): ProjectSpendLimit

    /**
     * Deletes the spend limit of a project.
     */
    public suspend fun deleteProjectSpendLimit(
        projectId: AdminProjectId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Retrieves the data retention configuration of a project.
     */
    public suspend fun projectDataRetention(
        projectId: AdminProjectId,
        requestOptions: RequestOptions? = null
    ): ProjectDataRetention

    /**
     * Updates the data retention configuration of a project.
     */
    public suspend fun updateProjectDataRetention(
        projectId: AdminProjectId,
        request: ProjectDataRetentionUpdateRequest,
        requestOptions: RequestOptions? = null
    ): ProjectDataRetention

    /**
     * Lists the certificates of a project.
     */
    public suspend fun projectCertificates(
        projectId: AdminProjectId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<ProjectCertificate>

    /**
     * Activates a project certificate.
     */
    public suspend fun activateProjectCertificate(
        projectId: AdminProjectId,
        certificateId: ProjectCertificateId,
        requestOptions: RequestOptions? = null
    ): ProjectCertificate

    /**
     * Deactivates a project certificate.
     */
    public suspend fun deactivateProjectCertificate(
        projectId: AdminProjectId,
        certificateId: ProjectCertificateId,
        requestOptions: RequestOptions? = null
    ): ProjectCertificate

    // ---- Organization sub-resources ----

    /**
     * Lists the groups of the organization.
     */
    public suspend fun adminGroups(
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AdminGroup>

    /**
     * Retrieves an organization group.
     */
    public suspend fun adminGroup(
        id: AdminGroupId,
        requestOptions: RequestOptions? = null
    ): AdminGroup

    /**
     * Creates an organization group.
     */
    public suspend fun createAdminGroup(
        request: AdminGroupCreateRequest,
        requestOptions: RequestOptions? = null
    ): AdminGroup

    /**
     * Updates an organization group.
     */
    public suspend fun updateAdminGroup(
        id: AdminGroupId,
        request: AdminGroupUpdateRequest,
        requestOptions: RequestOptions? = null
    ): AdminGroup

    /**
     * Deletes an organization group.
     */
    public suspend fun deleteAdminGroup(
        id: AdminGroupId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Lists the certificates of the organization.
     */
    public suspend fun adminCertificates(
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AdminCertificate>

    /**
     * Retrieves an organization certificate.
     */
    public suspend fun adminCertificate(
        id: AdminCertificateId,
        requestOptions: RequestOptions? = null
    ): AdminCertificate

    /**
     * Creates an organization certificate.
     */
    public suspend fun createAdminCertificate(
        request: AdminCertificateCreateRequest,
        requestOptions: RequestOptions? = null
    ): AdminCertificate

    /**
     * Updates an organization certificate.
     */
    public suspend fun updateAdminCertificate(
        id: AdminCertificateId,
        request: AdminCertificateUpdateRequest,
        requestOptions: RequestOptions? = null
    ): AdminCertificate

    /**
     * Deletes an organization certificate.
     */
    public suspend fun deleteAdminCertificate(
        id: AdminCertificateId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Activates an organization certificate.
     */
    public suspend fun activateAdminCertificate(
        id: AdminCertificateId,
        requestOptions: RequestOptions? = null
    ): AdminCertificate

    /**
     * Deactivates an organization certificate.
     */
    public suspend fun deactivateAdminCertificate(
        id: AdminCertificateId,
        requestOptions: RequestOptions? = null
    ): AdminCertificate

    /**
     * Lists the roles of the organization.
     */
    public suspend fun adminRoles(
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AdminRole>

    /**
     * Retrieves an organization role.
     */
    public suspend fun adminRole(
        id: AdminRoleId,
        requestOptions: RequestOptions? = null
    ): AdminRole

    /**
     * Creates an organization role.
     */
    public suspend fun createAdminRole(
        request: AdminRoleCreateRequest,
        requestOptions: RequestOptions? = null
    ): AdminRole

    /**
     * Updates an organization role.
     */
    public suspend fun updateAdminRole(
        id: AdminRoleId,
        request: AdminRoleUpdateRequest,
        requestOptions: RequestOptions? = null
    ): AdminRole

    /**
     * Deletes an organization role.
     */
    public suspend fun deleteAdminRole(
        id: AdminRoleId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Lists the audit logs of the organization.
     */
    public suspend fun adminAuditLogs(
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AdminAuditLog>

    /**
     * Lists the spend alerts of the organization.
     */
    public suspend fun adminSpendAlerts(
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AdminSpendAlert>

    /**
     * Retrieves an organization spend alert.
     */
    public suspend fun adminSpendAlert(
        id: AdminSpendAlertId,
        requestOptions: RequestOptions? = null
    ): AdminSpendAlert

    /**
     * Creates an organization spend alert.
     */
    public suspend fun createAdminSpendAlert(
        request: AdminSpendAlertCreateRequest,
        requestOptions: RequestOptions? = null
    ): AdminSpendAlert

    /**
     * Updates an organization spend alert.
     */
    public suspend fun updateAdminSpendAlert(
        id: AdminSpendAlertId,
        request: AdminSpendAlertUpdateRequest,
        requestOptions: RequestOptions? = null
    ): AdminSpendAlert

    /**
     * Deletes an organization spend alert.
     */
    public suspend fun deleteAdminSpendAlert(
        id: AdminSpendAlertId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Retrieves the spend limit of the organization.
     */
    public suspend fun adminSpendLimit(
        requestOptions: RequestOptions? = null
    ): AdminSpendLimit

    /**
     * Updates the spend limit of the organization.
     */
    public suspend fun updateAdminSpendLimit(
        request: AdminSpendLimitUpdateRequest,
        requestOptions: RequestOptions? = null
    ): AdminSpendLimit

    /**
     * Deletes the spend limit of the organization.
     */
    public suspend fun deleteAdminSpendLimit(
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Retrieves the data retention configuration of the organization.
     */
    public suspend fun adminDataRetention(
        requestOptions: RequestOptions? = null
    ): AdminDataRetention

    /**
     * Updates the data retention configuration of the organization.
     */
    public suspend fun updateAdminDataRetention(
        request: AdminDataRetentionUpdateRequest,
        requestOptions: RequestOptions? = null
    ): AdminDataRetention

    /**
     * Lists the external storage configurations of the organization.
     */
    public suspend fun adminExternalStorages(
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AdminExternalStorage>

    /**
     * Retrieves an external storage configuration.
     */
    public suspend fun adminExternalStorage(
        id: AdminExternalStorageId,
        requestOptions: RequestOptions? = null
    ): AdminExternalStorage

    /**
     * Creates an external storage configuration.
     */
    public suspend fun createAdminExternalStorage(
        request: AdminExternalStorageCreateRequest,
        requestOptions: RequestOptions? = null
    ): AdminExternalStorage

    /**
     * Deletes an external storage configuration.
     */
    public suspend fun deleteAdminExternalStorage(
        id: AdminExternalStorageId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Validates an external storage configuration.
     */
    public suspend fun validateAdminExternalStorage(
        id: AdminExternalStorageId,
        requestOptions: RequestOptions? = null
    ): AdminExternalStorage

    /**
     * Retrieves completions usage for the organization.
     *
     * @param batch only include batch requests when set.
     */
    public suspend fun usageCompletions(
        query: AdminUsageQuery,
        batch: Boolean? = null,
        requestOptions: RequestOptions? = null
    ): AdminCompletionsUsagePage

    /**
     * Retrieves embeddings usage for the organization.
     */
    public suspend fun usageEmbeddings(
        query: AdminUsageQuery,
        requestOptions: RequestOptions? = null
    ): AdminEmbeddingsUsagePage

    /**
     * Retrieves moderations usage for the organization.
     */
    public suspend fun usageModerations(
        query: AdminUsageQuery,
        requestOptions: RequestOptions? = null
    ): AdminModerationsUsagePage

    /**
     * Retrieves images usage for the organization.
     *
     * @param sizes only include the given image sizes.
     * @param sources only include the given image sources.
     */
    public suspend fun usageImages(
        query: AdminUsageQuery,
        sizes: List<String>? = null,
        sources: List<String>? = null,
        requestOptions: RequestOptions? = null
    ): AdminImagesUsagePage

    /**
     * Retrieves audio speeches usage for the organization.
     */
    public suspend fun usageAudioSpeeches(
        query: AdminUsageQuery,
        requestOptions: RequestOptions? = null
    ): AdminAudioSpeechesUsagePage

    /**
     * Retrieves audio transcriptions usage for the organization.
     */
    public suspend fun usageAudioTranscriptions(
        query: AdminUsageQuery,
        requestOptions: RequestOptions? = null
    ): AdminAudioTranscriptionsUsagePage

    /**
     * Retrieves vector stores usage for the organization.
     */
    public suspend fun usageVectorStores(
        query: AdminUsageQuery,
        requestOptions: RequestOptions? = null
    ): AdminVectorStoresUsagePage

    /**
     * Retrieves code interpreter sessions usage for the organization.
     */
    public suspend fun usageCodeInterpreterSessions(
        query: AdminUsageQuery,
        requestOptions: RequestOptions? = null
    ): AdminCodeInterpreterSessionsUsagePage

    /**
     * Retrieves file search calls usage for the organization.
     */
    public suspend fun usageFileSearchCalls(
        query: AdminUsageQuery,
        requestOptions: RequestOptions? = null
    ): AdminFileSearchCallsUsagePage

    /**
     * Retrieves web search calls usage for the organization.
     */
    public suspend fun usageWebSearchCalls(
        query: AdminUsageQuery,
        requestOptions: RequestOptions? = null
    ): AdminWebSearchCallsUsagePage

    /**
     * Retrieves costs for the organization.
     */
    public suspend fun usageCosts(
        query: AdminUsageQuery,
        requestOptions: RequestOptions? = null
    ): AdminCostsUsagePage

    // ---- Role assignments ----

    /**
     * Lists the roles assigned to a group.
     */
    public suspend fun groupRoles(
        groupId: AdminGroupId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AdminGroupRole>

    /**
     * Retrieves a role assigned to a group.
     */
    public suspend fun groupRole(
        groupId: AdminGroupId,
        roleId: AdminRoleId,
        requestOptions: RequestOptions? = null
    ): AdminGroupRole

    /**
     * Assigns a role to a group.
     */
    public suspend fun createGroupRole(
        groupId: AdminGroupId,
        request: AdminGroupRoleRequest,
        requestOptions: RequestOptions? = null
    ): AdminGroupRole

    /**
     * Removes a role from a group.
     */
    public suspend fun deleteGroupRole(
        groupId: AdminGroupId,
        roleId: AdminRoleId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Lists the users of a group.
     */
    public suspend fun groupUsers(
        groupId: AdminGroupId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AdminGroupUser>

    /**
     * Retrieves a user belonging to a group.
     */
    public suspend fun groupUser(
        groupId: AdminGroupId,
        userId: AdminUserId,
        requestOptions: RequestOptions? = null
    ): AdminGroupUser

    /**
     * Adds a user to a group.
     */
    public suspend fun createGroupUser(
        groupId: AdminGroupId,
        request: AdminGroupUserRequest,
        requestOptions: RequestOptions? = null
    ): AdminGroupUser

    /**
     * Removes a user from a group.
     */
    public suspend fun deleteGroupUser(
        groupId: AdminGroupId,
        userId: AdminUserId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Lists the roles assigned to a user.
     */
    public suspend fun userRoles(
        userId: AdminUserId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AdminUserRole>

    /**
     * Retrieves a role assigned to a user.
     */
    public suspend fun userRole(
        userId: AdminUserId,
        roleId: AdminRoleId,
        requestOptions: RequestOptions? = null
    ): AdminUserRole

    /**
     * Assigns a role to a user.
     */
    public suspend fun createUserRole(
        userId: AdminUserId,
        request: AdminUserRoleRequest,
        requestOptions: RequestOptions? = null
    ): AdminUserRole

    /**
     * Removes a role from a user.
     */
    public suspend fun deleteUserRole(
        userId: AdminUserId,
        roleId: AdminRoleId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse
}
