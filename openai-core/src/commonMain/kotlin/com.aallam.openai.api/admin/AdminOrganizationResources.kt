package com.aallam.openai.api.admin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlin.jvm.JvmInline

/** The organization group identifier. */
@JvmInline
@Serializable
public value class AdminGroupId(public val id: String)

/** The organization certificate identifier. */
@JvmInline
@Serializable
public value class AdminCertificateId(public val id: String)

/** The organization role identifier. */
@JvmInline
@Serializable
public value class AdminRoleId(public val id: String)

/** The organization spend alert identifier. */
@JvmInline
@Serializable
public value class AdminSpendAlertId(public val id: String)

/** The external storage identifier. */
@JvmInline
@Serializable
public value class AdminExternalStorageId(public val id: String)

/**
 * A group in the organization.
 */
@Serializable
public data class AdminGroup(
    @SerialName("id") public val id: AdminGroupId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("group_type") public val groupType: String? = null,
    @SerialName("is_scim_managed") public val isScimManaged: Boolean? = null,
    @SerialName("name") public val name: String? = null,
)

/**
 * Creates an organization group.
 */
@Serializable
public data class AdminGroupCreateRequest(
    @SerialName("name") public val name: String,
)

/**
 * Updates an organization group.
 */
@Serializable
public data class AdminGroupUpdateRequest(
    @SerialName("name") public val name: String,
)

/**
 * A certificate registered with the organization.
 */
@Serializable
public data class AdminCertificate(
    @SerialName("id") public val id: AdminCertificateId,
    @SerialName("active") public val active: Boolean? = null,
    @SerialName("certificate_details") public val certificateDetails: JsonObject? = null,
    @SerialName("content") public val content: String? = null,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("expires_at") public val expiresAt: Long? = null,
    @SerialName("name") public val name: String? = null,
)

/**
 * Creates an organization certificate.
 */
@Serializable
public data class AdminCertificateCreateRequest(
    @SerialName("content") public val content: String,
    @SerialName("name") public val name: String? = null,
)

/**
 * Updates an organization certificate.
 */
@Serializable
public data class AdminCertificateUpdateRequest(
    @SerialName("name") public val name: String? = null,
)

/**
 * A role defined in the organization.
 */
@Serializable
public data class AdminRole(
    @SerialName("id") public val id: AdminRoleId,
    @SerialName("description") public val description: String? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("permissions") public val permissions: List<String> = emptyList(),
    @SerialName("predefined_role") public val predefinedRole: Boolean? = null,
    @SerialName("resource_type") public val resourceType: String? = null,
)

/**
 * Creates an organization role.
 */
@Serializable
public data class AdminRoleCreateRequest(
    @SerialName("role_name") public val roleName: String,
    @SerialName("permissions") public val permissions: List<String>,
    @SerialName("description") public val description: String? = null,
)

/**
 * Updates an organization role.
 */
@Serializable
public data class AdminRoleUpdateRequest(
    @SerialName("role_name") public val roleName: String? = null,
    @SerialName("permissions") public val permissions: List<String>? = null,
    @SerialName("description") public val description: String? = null,
)

/**
 * An audit log entry.
 */
@Serializable
public data class AdminAuditLog(
    @SerialName("id") public val id: String,
    @SerialName("effective_at") public val effectiveAt: Long? = null,
    @SerialName("type") public val type: String? = null,
    @SerialName("actor") public val actor: JsonObject? = null,
)

/**
 * A spend alert configured for the organization.
 */
@Serializable
public data class AdminSpendAlert(
    @SerialName("id") public val id: AdminSpendAlertId,
    @SerialName("currency") public val currency: String? = null,
    @SerialName("interval") public val interval: String? = null,
    @SerialName("notification_channel") public val notificationChannel: String? = null,
    @SerialName("threshold_amount") public val thresholdAmount: Long? = null,
)

/**
 * Creates an organization spend alert.
 */
@Serializable
public data class AdminSpendAlertCreateRequest(
    @SerialName("currency") public val currency: String,
    @SerialName("interval") public val interval: String,
    @SerialName("notification_channel") public val notificationChannel: String? = null,
    @SerialName("threshold_amount") public val thresholdAmount: Long,
)

/**
 * Updates an organization spend alert.
 */
@Serializable
public data class AdminSpendAlertUpdateRequest(
    @SerialName("currency") public val currency: String? = null,
    @SerialName("interval") public val interval: String? = null,
    @SerialName("notification_channel") public val notificationChannel: String? = null,
    @SerialName("threshold_amount") public val thresholdAmount: Long? = null,
)

/**
 * The spend limit configured for the organization.
 */
@Serializable
public data class AdminSpendLimit(
    @SerialName("currency") public val currency: String? = null,
    @SerialName("enforcement") public val enforcement: String? = null,
    @SerialName("interval") public val interval: String? = null,
    @SerialName("threshold_amount") public val thresholdAmount: Long? = null,
)

/**
 * Updates the organization spend limit.
 */
@Serializable
public data class AdminSpendLimitUpdateRequest(
    @SerialName("currency") public val currency: String,
    @SerialName("interval") public val interval: String,
    @SerialName("threshold_amount") public val thresholdAmount: Long,
    @SerialName("enforcement") public val enforcement: String? = null,
)

/**
 * The data retention configuration of the organization.
 */
@Serializable
public data class AdminDataRetention(
    @SerialName("type") public val type: String? = null,
)

/**
 * Updates the organization data retention configuration.
 */
@Serializable
public data class AdminDataRetentionUpdateRequest(
    @SerialName("type") public val type: String,
)

/**
 * An external storage configuration.
 */
@Serializable
public data class AdminExternalStorage(
    @SerialName("id") public val id: AdminExternalStorageId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("geography") public val geography: String? = null,
    @SerialName("project_id") public val projectId: AdminProjectId? = null,
    @SerialName("provider") public val provider: String? = null,
    @SerialName("status") public val status: String? = null,
)

/**
 * Creates an external storage configuration.
 */
@Serializable
public data class AdminExternalStorageCreateRequest(
    @SerialName("name") public val name: String,
    @SerialName("type") public val type: String,
    @SerialName("config") public val config: JsonObject? = null,
)

/**
 * A role assigned to a group.
 */
@Serializable
public data class AdminGroupRole(
    @SerialName("group_id") public val groupId: AdminGroupId? = null,
    @SerialName("role_id") public val roleId: AdminRoleId? = null,
)

/**
 * Assigns a role to a group.
 */
@Serializable
public data class AdminGroupRoleRequest(
    @SerialName("role_id") public val roleId: AdminRoleId,
)

/**
 * A user belonging to a group.
 */
@Serializable
public data class AdminGroupUser(
    @SerialName("group_id") public val groupId: AdminGroupId? = null,
    @SerialName("user_id") public val userId: AdminUserId? = null,
)

/**
 * Adds a user to a group.
 */
@Serializable
public data class AdminGroupUserRequest(
    @SerialName("user_id") public val userId: AdminUserId,
)

/**
 * A role assigned to a user.
 */
@Serializable
public data class AdminUserRole(
    @SerialName("user_id") public val userId: AdminUserId? = null,
    @SerialName("role_id") public val roleId: AdminRoleId? = null,
)

/**
 * Assigns a role to a user.
 */
@Serializable
public data class AdminUserRoleRequest(
    @SerialName("role_id") public val roleId: AdminRoleId,
)
