package com.aallam.openai.client

import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.skill.DeletedSkill
import com.aallam.openai.api.skill.DeletedSkillVersion
import com.aallam.openai.api.skill.Skill
import com.aallam.openai.api.skill.SkillCreateRequest
import com.aallam.openai.api.skill.SkillId
import com.aallam.openai.api.skill.SkillUpdateRequest
import com.aallam.openai.api.skill.SkillVersion
import com.aallam.openai.api.skill.SkillVersionCreateRequest

/**
 * Create and manage skills.
 */
public interface Skills {

    /**
     * Creates a skill.
     */
    public suspend fun createSkill(
        request: SkillCreateRequest,
        requestOptions: RequestOptions? = null
    ): Skill

    /**
     * Retrieves a skill by its identifier.
     */
    public suspend fun skill(
        id: SkillId,
        requestOptions: RequestOptions? = null
    ): Skill

    /**
     * Updates a skill.
     */
    public suspend fun updateSkill(
        id: SkillId,
        request: SkillUpdateRequest,
        requestOptions: RequestOptions? = null
    ): Skill

    /**
     * Deletes a skill.
     */
    public suspend fun deleteSkill(
        id: SkillId,
        requestOptions: RequestOptions? = null
    ): DeletedSkill

    /**
     * Lists skills.
     */
    public suspend fun skills(
        limit: Int? = null,
        after: String? = null,
        order: SortOrder? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<Skill>

    /**
     * Downloads the content of a skill.
     */
    public suspend fun skillContent(
        id: SkillId,
        requestOptions: RequestOptions? = null
    ): ByteArray

    /**
     * Lists the versions of a skill.
     */
    public suspend fun skillVersions(
        id: SkillId,
        limit: Int? = null,
        after: String? = null,
        order: SortOrder? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<SkillVersion>

    /**
     * Retrieves a version of a skill.
     */
    public suspend fun skillVersion(
        id: SkillId,
        version: String,
        requestOptions: RequestOptions? = null
    ): SkillVersion

    /**
     * Creates a new version of a skill.
     */
    public suspend fun createSkillVersion(
        id: SkillId,
        request: SkillVersionCreateRequest,
        requestOptions: RequestOptions? = null
    ): SkillVersion

    /**
     * Deletes a version of a skill.
     */
    public suspend fun deleteSkillVersion(
        id: SkillId,
        version: String,
        requestOptions: RequestOptions? = null
    ): DeletedSkillVersion

    /**
     * Downloads the content of a skill version.
     */
    public suspend fun skillVersionContent(
        id: SkillId,
        version: String,
        requestOptions: RequestOptions? = null
    ): ByteArray
}
