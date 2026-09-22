package com.aallam.openai.client

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

/**
 * Create and manage containers and the files stored in them.
 */
public interface Containers {

    /**
     * Creates a container.
     */
    public suspend fun createContainer(
        request: ContainerCreateRequest,
        requestOptions: RequestOptions? = null
    ): Container

    /**
     * Retrieves a container by its identifier.
     */
    public suspend fun container(
        id: ContainerId,
        requestOptions: RequestOptions? = null
    ): Container

    /**
     * Deletes a container.
     */
    public suspend fun deleteContainer(
        id: ContainerId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Lists containers.
     */
    public suspend fun containers(
        limit: Int? = null,
        after: String? = null,
        order: SortOrder? = null,
        name: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<Container>

    /**
     * Uploads a file into a container.
     */
    public suspend fun createContainerFile(
        containerId: ContainerId,
        request: ContainerFileCreateRequest,
        requestOptions: RequestOptions? = null
    ): ContainerFile

    /**
     * Retrieves a file stored in a container.
     */
    public suspend fun containerFile(
        containerId: ContainerId,
        fileId: ContainerFileId,
        requestOptions: RequestOptions? = null
    ): ContainerFile

    /**
     * Deletes a file stored in a container.
     */
    public suspend fun deleteContainerFile(
        containerId: ContainerId,
        fileId: ContainerFileId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    /**
     * Lists the files stored in a container.
     */
    public suspend fun containerFiles(
        containerId: ContainerId,
        limit: Int? = null,
        after: String? = null,
        order: SortOrder? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<ContainerFile>

    /**
     * Downloads the content of a file stored in a container.
     */
    public suspend fun containerFileContent(
        containerId: ContainerId,
        fileId: ContainerFileId,
        requestOptions: RequestOptions? = null
    ): ByteArray
}
