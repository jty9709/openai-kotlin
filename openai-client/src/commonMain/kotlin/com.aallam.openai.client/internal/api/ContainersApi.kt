package com.aallam.openai.client.internal.api

import com.aallam.openai.api.container.Container
import com.aallam.openai.api.container.ContainerCreateRequest
import com.aallam.openai.api.container.ContainerFile
import com.aallam.openai.api.container.ContainerFileCreateRequest
import com.aallam.openai.api.container.ContainerFileId
import com.aallam.openai.api.container.ContainerId
import com.aallam.openai.api.core.DeleteResponse
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.client.Containers
import com.aallam.openai.client.internal.extension.appendFileSource
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

internal class ContainersApi(private val requester: HttpRequester) : Containers {

    override suspend fun createContainer(
        request: ContainerCreateRequest,
        requestOptions: RequestOptions?
    ): Container {
        return requester.perform {
            it.post {
                url(path = ApiPath.Containers)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun container(id: ContainerId, requestOptions: RequestOptions?): Container {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Containers}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteContainer(
        id: ContainerId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.Containers}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun containers(
        limit: Int?,
        after: String?,
        order: SortOrder?,
        name: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<Container> {
        return requester.perform {
            it.get {
                url(path = ApiPath.Containers) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                    order?.let { value -> parameter("order", value.order) }
                    name?.let { value -> parameter("name", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createContainerFile(
        containerId: ContainerId,
        request: ContainerFileCreateRequest,
        requestOptions: RequestOptions?
    ): ContainerFile {
        return requester.perform<ContainerFile> {
            it.submitFormWithBinaryData(
                url = "${ApiPath.Containers}/${containerId.id}/files",
                formData = formData {
                    appendFileSource("file", request.file)
                    request.path?.let { path -> append(key = "path", value = path) }
                },
            ) {
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun containerFile(
        containerId: ContainerId,
        fileId: ContainerFileId,
        requestOptions: RequestOptions?
    ): ContainerFile {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Containers}/${containerId.id}/files/${fileId.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteContainerFile(
        containerId: ContainerId,
        fileId: ContainerFileId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.Containers}/${containerId.id}/files/${fileId.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun containerFiles(
        containerId: ContainerId,
        limit: Int?,
        after: String?,
        order: SortOrder?,
        requestOptions: RequestOptions?
    ): PaginatedList<ContainerFile> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Containers}/${containerId.id}/files") {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                    order?.let { value -> parameter("order", value.order) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun containerFileContent(
        containerId: ContainerId,
        fileId: ContainerFileId,
        requestOptions: RequestOptions?
    ): ByteArray {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Containers}/${containerId.id}/files/${fileId.id}/content")
                requestOptions(requestOptions)
            }
        }
    }
}
