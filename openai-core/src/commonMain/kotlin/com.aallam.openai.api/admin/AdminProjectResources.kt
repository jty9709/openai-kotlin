package com.aallam.openai.api.admin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/** The project API key identifier. */
@JvmInline
@Serializable
public value class ProjectApiKeyId(public val id: String)

/** The project service account identifier. */
@JvmInline
@Serializable
public value class ProjectServiceAccountId(public val id: String)

/** The project user identifier. */
@JvmInline
@Serializable
public value class ProjectUserId(public val id: String)

/** The project group identifier. */
@JvmInline
@Serializable
public value class ProjectGroupId(public val id: String)

/** The project role identifier. */
@JvmInline
@Serializable
public value class ProjectRoleId(public val id: String)

/** The project rate limit identifier. */
@JvmInline
@Serializable
public value class ProjectRateLimitId(public val id: String)

/** The project certificate identifier. */
@JvmInline
@Serializable
public value class ProjectCertificateId(public val id: String)

/** The project spend alert identifier. */
@JvmInline
@Serializable
public value class ProjectSpendAlertId(public val id: String)

/**
 * An API key scoped to a project.
 */
@Serializable
public data class ProjectApiKey(
    @SerialName("id") public val id: ProjectApiKeyId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("last_used_at") public val lastUsedAt: Long? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("redacted_value") public val redactedValue: String? = null,
    @SerialName("expires_at") public val expiresAt: Long? = null,
)

/**
 * A service account belonging to a project.
 */
@Serializable
public data class ProjectServiceAccount(
    @SerialName("id") public val id: ProjectServiceAccountId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("role") public val role: String? = null,
)

/** A newly created service account, including its API key when returned by the server. */
@Serializable
public data class ProjectServiceAccountCreated(
    @SerialName("id") public val id: ProjectServiceAccountId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("role") public val role: String? = null,
    @SerialName("api_key") public val apiKey: ProjectServiceAccountApiKey? = null,
)

/** The secret returned when provisioning a service account. Store [value] securely. */
@Serializable
public data class ProjectServiceAccountApiKey(
    @SerialName("id") public val id: ProjectApiKeyId,
    @SerialName("value") public val value: String,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("expires_at") public val expiresAt: Long? = null,
)

/**
 * Creates a service account.
 */
@Serializable
public data class ProjectServiceAccountCreateRequest(
    @SerialName("name") public val name: String,
)

/**
 * Updates a service account.
 */
@Serializable
public data class ProjectServiceAccountUpdateRequest(
    @SerialName("name") public val name: String? = null,
    @SerialName("role") public val role: String? = null,
)

/**
 * A user with access to a project.
 */
@Serializable
public data class ProjectUser(
    @SerialName("id") public val id: ProjectUserId,
    @SerialName("added_at") public val addedAt: Long? = null,
    @SerialName("role") public val role: String? = null,
    @SerialName("email") public val email: String? = null,
    @SerialName("name") public val name: String? = null,
)

/**
 * Adds a user to a project.
 */
@Serializable
public data class ProjectUserCreateRequest(
    @SerialName("role") public val role: String,
    @SerialName("user_id") public val userId: AdminUserId? = null,
    @SerialName("email") public val email: String? = null,
)

/**
 * Updates a project user.
 */
@Serializable
public data class ProjectUserUpdateRequest(
    @SerialName("role") public val role: String? = null,
)

/**
 * A group with access to a project.
 */
@Serializable
public data class ProjectGroup(
    @SerialName("group_id") public val groupId: ProjectGroupId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("group_name") public val groupName: String? = null,
    @SerialName("group_type") public val groupType: String? = null,
    @SerialName("project_id") public val projectId: AdminProjectId? = null,
)

/**
 * Adds a group to a project.
 *
 * @property groupId the identifier of the group to add.
 * @property role the role to grant to the group.
 */
@Serializable
public data class ProjectGroupCreateRequest(
    @SerialName("group_id") public val groupId: ProjectGroupId,
    @SerialName("role") public val role: String,
)

/**
 * A role defined within a project.
 */
@Serializable
public data class ProjectRole(
    @SerialName("id") public val id: ProjectRoleId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("description") public val description: String? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("permissions") public val permissions: List<String> = emptyList(),
)

/**
 * Creates a project role.
 */
@Serializable
public data class ProjectRoleCreateRequest(
    @SerialName("role_name") public val roleName: String,
    @SerialName("permissions") public val permissions: List<String>,
    @SerialName("description") public val description: String? = null,
)

/**
 * Updates a project role.
 */
@Serializable
public data class ProjectRoleUpdateRequest(
    @SerialName("role_name") public val roleName: String? = null,
    @SerialName("permissions") public val permissions: List<String>? = null,
    @SerialName("description") public val description: String? = null,
)

/**
 * A rate limit applied to a project.
 */
@Serializable
public data class ProjectRateLimit(
    @SerialName("id") public val id: ProjectRateLimitId,
    @SerialName("model") public val model: String? = null,
    @SerialName("max_requests_per_1_minute") public val maxRequestsPerMinute: Long? = null,
    @SerialName("max_tokens_per_1_minute") public val maxTokensPerMinute: Long? = null,
)

/**
 * Updates a project rate limit.
 */
@Serializable
public data class ProjectRateLimitUpdateRequest(
    @SerialName("max_requests_per_1_minute") public val maxRequestsPerMinute: Long? = null,
    @SerialName("max_tokens_per_1_minute") public val maxTokensPerMinute: Long? = null,
)

/**
 * Model permissions granted to a project.
 *
 * @property mode the mode the model permissions operate in.
 * @property modelIds the models the permissions apply to.
 */
@Serializable
public data class ProjectModelPermissions(
    @SerialName("mode") public val mode: String? = null,
    @SerialName("model_ids") public val modelIds: List<String>? = null,
)

/**
 * Updates model permissions.
 */
@Serializable
public data class ProjectModelPermissionsUpdateRequest(
    @SerialName("mode") public val mode: String? = null,
    @SerialName("model_ids") public val modelIds: List<String>? = null,
)

/**
 * Hosted tool permissions granted to a project.
 */
@Serializable
public data class ProjectHostedToolPermissions(
    @SerialName("code_interpreter") public val codeInterpreter: Boolean? = null,
    @SerialName("file_search") public val fileSearch: Boolean? = null,
    @SerialName("image_generation") public val imageGeneration: Boolean? = null,
    @SerialName("mcp") public val mcp: Boolean? = null,
    @SerialName("web_search") public val webSearch: Boolean? = null,
)

/**
 * Updates hosted tool permissions.
 */
@Serializable
public data class ProjectHostedToolPermissionsUpdateRequest(
    @SerialName("code_interpreter") public val codeInterpreter: Boolean? = null,
    @SerialName("file_search") public val fileSearch: Boolean? = null,
    @SerialName("image_generation") public val imageGeneration: Boolean? = null,
    @SerialName("mcp") public val mcp: Boolean? = null,
    @SerialName("web_search") public val webSearch: Boolean? = null,
)

/**
 * A spend alert configured for a project.
 */
@Serializable
public data class ProjectSpendAlert(
    @SerialName("id") public val id: ProjectSpendAlertId,
    @SerialName("currency") public val currency: String? = null,
    @SerialName("interval") public val interval: String? = null,
    @SerialName("notification_channel") public val notificationChannel: String? = null,
    @SerialName("threshold_amount") public val thresholdAmount: Long? = null,
)

/**
 * Creates a spend alert.
 */
@Serializable
public data class ProjectSpendAlertCreateRequest(
    @SerialName("currency") public val currency: String,
    @SerialName("interval") public val interval: String,
    @SerialName("notification_channel") public val notificationChannel: String? = null,
    @SerialName("threshold_amount") public val thresholdAmount: Long,
)

/**
 * Updates a spend alert.
 */
@Serializable
public data class ProjectSpendAlertUpdateRequest(
    @SerialName("currency") public val currency: String? = null,
    @SerialName("interval") public val interval: String? = null,
    @SerialName("notification_channel") public val notificationChannel: String? = null,
    @SerialName("threshold_amount") public val thresholdAmount: Long? = null,
)

/**
 * The spend limit configured for a project.
 */
@Serializable
public data class ProjectSpendLimit(
    @SerialName("currency") public val currency: String? = null,
    @SerialName("enforcement") public val enforcement: String? = null,
    @SerialName("interval") public val interval: String? = null,
    @SerialName("threshold_amount") public val thresholdAmount: Long? = null,
)

/**
 * Updates the spend limit of a project.
 */
@Serializable
public data class ProjectSpendLimitUpdateRequest(
    @SerialName("currency") public val currency: String,
    @SerialName("interval") public val interval: String,
    @SerialName("threshold_amount") public val thresholdAmount: Long,
    @SerialName("enforcement") public val enforcement: String? = null,
)

/**
 * The data retention configuration of a project.
 */
@Serializable
public data class ProjectDataRetention(
    @SerialName("type") public val type: String? = null,
)

/**
 * Updates the data retention configuration.
 */
@Serializable
public data class ProjectDataRetentionUpdateRequest(
    @SerialName("type") public val type: String,
)

/**
 * A certificate belonging to a project.
 */
@Serializable
public data class ProjectCertificate(
    @SerialName("id") public val id: ProjectCertificateId,
    @SerialName("active") public val active: Boolean? = null,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("expires_at") public val expiresAt: Long? = null,
    @SerialName("valid_at") public val validAt: Long? = null,
)
