package com.aallam.openai.api.skill

import com.aallam.openai.api.file.FileSource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The skill identifier.
 */
@JvmInline
@Serializable
public value class SkillId(public val id: String)

/**
 * A skill that can be attached to a container.
 *
 * @property id the identifier of the skill.
 * @property createdAt the Unix timestamp (in seconds) of when the skill was created.
 * @property defaultVersion the version used when the skill is invoked without an explicit version.
 * @property description the description of the skill.
 * @property latestVersion the most recently uploaded version.
 * @property name the name of the skill.
 */
@Serializable
public data class Skill(
    @SerialName("id") public val id: SkillId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("default_version") public val defaultVersion: String? = null,
    @SerialName("description") public val description: String? = null,
    @SerialName("latest_version") public val latestVersion: String? = null,
    @SerialName("name") public val name: String? = null,
)

/**
 * Creates a skill from a set of files.
 *
 * @property files the files composing the skill.
 */
public data class SkillCreateRequest(
    public val files: List<FileSource>,
)

/**
 * Updates a skill.
 *
 * @property defaultVersion the version to use when the skill is invoked without an explicit version.
 */
@Serializable
public data class SkillUpdateRequest(
    @SerialName("default_version") public val defaultVersion: String? = null,
)

/**
 * The result of deleting a skill.
 */
@Serializable
public data class DeletedSkill(
    @SerialName("id") public val id: SkillId,
    @SerialName("deleted") public val deleted: Boolean = false,
)

/**
 * The skill version identifier.
 */
@JvmInline
@Serializable
public value class SkillVersionId(public val id: String)

/**
 * A version of a skill.
 *
 * @property id the identifier of the version.
 * @property createdAt the Unix timestamp (in seconds) of when the version was created.
 * @property description the description of the version.
 * @property name the name of the version.
 * @property skillId the identifier of the owning skill.
 * @property version the version label.
 */
@Serializable
public data class SkillVersion(
    @SerialName("id") public val id: SkillVersionId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("description") public val description: String? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("skill_id") public val skillId: SkillId? = null,
    @SerialName("version") public val version: String? = null,
)

/**
 * Creates a skill version from a set of files.
 *
 * @property files the files composing the version.
 */
public data class SkillVersionCreateRequest(
    public val files: List<FileSource>,
)

/**
 * The result of deleting a skill version.
 */
@Serializable
public data class DeletedSkillVersion(
    @SerialName("id") public val id: SkillVersionId,
    @SerialName("deleted") public val deleted: Boolean = false,
    @SerialName("version") public val version: String? = null,
)
