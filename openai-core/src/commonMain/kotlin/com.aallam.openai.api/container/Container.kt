package com.aallam.openai.api.container

import com.aallam.openai.api.file.FileSource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The container identifier.
 */
@JvmInline
@Serializable
public value class ContainerId(public val id: String)

/**
 * The container file identifier.
 */
@JvmInline
@Serializable
public value class ContainerFileId(public val id: String)

/**
 * A container that runs code for tool calls.
 *
 * @property id the identifier of the container.
 * @property createdAt the Unix timestamp (in seconds) of when the container was created.
 * @property name the name of the container.
 * @property status the current status of the container.
 * @property lastActiveAt the Unix timestamp (in seconds) of the last activity.
 */
@Serializable
public data class Container(
    @SerialName("id") public val id: ContainerId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("status") public val status: String? = null,
    @SerialName("last_active_at") public val lastActiveAt: Long? = null,
)

/**
 * Creates a container.
 *
 * @property name the name of the container.
 * @property fileIds identifiers of files to seed the container with.
 */
@Serializable
public data class ContainerCreateRequest(
    @SerialName("name") public val name: String,
    @SerialName("file_ids") public val fileIds: List<String>? = null,
)

/**
 * A file stored in a container.
 *
 * @property id the identifier of the file.
 * @property bytes the size of the file, in bytes.
 * @property containerId the identifier of the containing container.
 * @property createdAt the Unix timestamp (in seconds) of when the file was created.
 * @property path the path of the file within the container.
 */
@Serializable
public data class ContainerFile(
    @SerialName("id") public val id: ContainerFileId,
    @SerialName("bytes") public val bytes: Long? = null,
    @SerialName("container_id") public val containerId: ContainerId? = null,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("path") public val path: String? = null,
)

/**
 * Creates a file in a container.
 *
 * @property file the file to upload.
 * @property path the path to store the file under within the container.
 */
public data class ContainerFileCreateRequest(
    public val file: FileSource,
    public val path: String? = null,
)
