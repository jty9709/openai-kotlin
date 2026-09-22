package com.aallam.openai.api.admin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The organization project identifier.
 */
@JvmInline
@Serializable
public value class AdminProjectId(public val id: String)

/**
 * The organization user identifier.
 */
@JvmInline
@Serializable
public value class AdminUserId(public val id: String)

/**
 * The organization invite identifier.
 */
@JvmInline
@Serializable
public value class AdminInviteId(public val id: String)

/**
 * The admin API key identifier.
 */
@JvmInline
@Serializable
public value class AdminApiKeyId(public val id: String)

/**
 * An organization project.
 *
 * @property id the identifier of the project.
 * @property createdAt the Unix timestamp (in seconds) of when the project was created.
 * @property archivedAt the Unix timestamp (in seconds) of when the project was archived.
 * @property externalKeyId the external key referenced by the project, if any.
 * @property name the name of the project.
 * @property residency the data residency region of the project.
 * @property status the status of the project.
 */
@Serializable
public data class AdminProject(
    @SerialName("id") public val id: AdminProjectId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("archived_at") public val archivedAt: Long? = null,
    @SerialName("external_key_id") public val externalKeyId: String? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("residency") public val residency: String? = null,
    @SerialName("status") public val status: String? = null,
)

/**
 * Creates an organization project.
 */
@Serializable
public data class AdminProjectCreateRequest(
    @SerialName("name") public val name: String,
    @SerialName("external_key_id") public val externalKeyId: String? = null,
    @SerialName("geography") public val geography: String? = null,
)

/**
 * Updates an organization project.
 */
@Serializable
public data class AdminProjectUpdateRequest(
    @SerialName("name") public val name: String? = null,
    @SerialName("external_key_id") public val externalKeyId: String? = null,
    @SerialName("geography") public val geography: String? = null,
)

/**
 * An organization user.
 *
 * @property id the identifier of the user.
 * @property addedAt the Unix timestamp (in seconds) of when the user was added.
 * @property email the email address of the user.
 * @property name the name of the user.
 * @property role the role granted to the user in the organization.
 */
@Serializable
public data class AdminUser(
    @SerialName("id") public val id: AdminUserId,
    @SerialName("added_at") public val addedAt: Long? = null,
    @SerialName("email") public val email: String? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("role") public val role: String? = null,
    @SerialName("is_service_account") public val isServiceAccount: Boolean? = null,
)

/**
 * Updates an organization user.
 */
@Serializable
public data class AdminUserUpdateRequest(
    @SerialName("role") public val role: String? = null,
)

/**
 * An invitation to join an organization.
 *
 * @property id the identifier of the invite.
 * @property createdAt the Unix timestamp (in seconds) of when the invite was created.
 * @property email the email address the invite was sent to.
 * @property role the role granted by the invite.
 * @property status the status of the invite.
 * @property acceptedAt the Unix timestamp (in seconds) of when the invite was accepted.
 * @property expiresAt the Unix timestamp (in seconds) of when the invite expires.
 */
@Serializable
public data class AdminInvite(
    @SerialName("id") public val id: AdminInviteId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("email") public val email: String? = null,
    @SerialName("role") public val role: String? = null,
    @SerialName("status") public val status: String? = null,
    @SerialName("accepted_at") public val acceptedAt: Long? = null,
    @SerialName("expires_at") public val expiresAt: Long? = null,
)

/**
 * Creates an organization invite.
 */
@Serializable
public data class AdminInviteCreateRequest(
    @SerialName("email") public val email: String,
    @SerialName("role") public val role: String,
)

/**
 * An admin API key.
 *
 * @property id the identifier of the key.
 * @property createdAt the Unix timestamp (in seconds) of when the key was created.
 * @property expiresAt the Unix timestamp (in seconds) of when the key expires.
 * @property name the name of the key.
 * @property redactedValue the redacted representation of the key.
 * @property lastUsedAt the Unix timestamp (in seconds) of the last use.
 */
@Serializable
public data class AdminApiKey(
    @SerialName("id") public val id: AdminApiKeyId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("expires_at") public val expiresAt: Long? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("redacted_value") public val redactedValue: String? = null,
    @SerialName("last_used_at") public val lastUsedAt: Long? = null,
)

/** A newly created admin API key. Store [value] securely; retrieval only returns a redacted value. */
@Serializable
public data class AdminApiKeyCreated(
    @SerialName("id") public val id: AdminApiKeyId,
    @SerialName("value") public val value: String,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("expires_at") public val expiresAt: Long? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("redacted_value") public val redactedValue: String? = null,
    @SerialName("last_used_at") public val lastUsedAt: Long? = null,
)

/**
 * Creates an admin API key.
 *
 * @property name the name of the key.
 * @property expiresInSeconds how long the key should remain valid.
 */
@Serializable
public data class AdminApiKeyCreateRequest(
    @SerialName("name") public val name: String,
    @SerialName("expires_in_seconds") public val expiresInSeconds: Long? = null,
)
