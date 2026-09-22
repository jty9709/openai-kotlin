package com.aallam.openai.client.internal.api

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
import com.aallam.openai.client.Skills
import com.aallam.openai.client.internal.extension.appendFileSource
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

internal class SkillsApi(private val requester: HttpRequester) : Skills {

    override suspend fun createSkill(
        request: SkillCreateRequest,
        requestOptions: RequestOptions?
    ): Skill {
        return requester.perform<Skill> {
            it.submitFormWithBinaryData(
                url = ApiPath.Skills,
                formData = formData {
                    request.files.forEach { file -> appendFileSource("files", file) }
                },
            ) {
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun skill(id: SkillId, requestOptions: RequestOptions?): Skill {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Skills}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateSkill(
        id: SkillId,
        request: SkillUpdateRequest,
        requestOptions: RequestOptions?
    ): Skill {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Skills}/${id.id}")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteSkill(id: SkillId, requestOptions: RequestOptions?): DeletedSkill {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.Skills}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun skills(
        limit: Int?,
        after: String?,
        order: SortOrder?,
        requestOptions: RequestOptions?
    ): PaginatedList<Skill> {
        return requester.perform {
            it.get {
                url(path = ApiPath.Skills) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                    order?.let { value -> parameter("order", value.order) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun skillContent(id: SkillId, requestOptions: RequestOptions?): ByteArray {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Skills}/${id.id}/content")
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun skillVersions(
        id: SkillId,
        limit: Int?,
        after: String?,
        order: SortOrder?,
        requestOptions: RequestOptions?
    ): PaginatedList<SkillVersion> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Skills}/${id.id}/versions") {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                    order?.let { value -> parameter("order", value.order) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun skillVersion(
        id: SkillId,
        version: String,
        requestOptions: RequestOptions?
    ): SkillVersion {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Skills}/${id.id}/versions/$version")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createSkillVersion(
        id: SkillId,
        request: SkillVersionCreateRequest,
        requestOptions: RequestOptions?
    ): SkillVersion {
        return requester.perform<SkillVersion> {
            it.submitFormWithBinaryData(
                url = "${ApiPath.Skills}/${id.id}/versions",
                formData = formData {
                    request.files.forEach { file -> appendFileSource("files", file) }
                },
            ) {
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun deleteSkillVersion(
        id: SkillId,
        version: String,
        requestOptions: RequestOptions?
    ): DeletedSkillVersion {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.Skills}/${id.id}/versions/$version")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun skillVersionContent(
        id: SkillId,
        version: String,
        requestOptions: RequestOptions?
    ): ByteArray {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Skills}/${id.id}/versions/$version/content")
                requestOptions(requestOptions)
            }
        }
    }
}
