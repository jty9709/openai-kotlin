package com.aallam.openai.api.upload

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlin.jvm.JvmInline

/**
 * The upload identifier.
 */
@JvmInline
@Serializable
public value class UploadId(public val id: String)

/**
 * The upload part identifier.
 */
@JvmInline
@Serializable
public value class UploadPartId(public val id: String)

/**
 * An upload session for a large file.
 *
 * @property id the identifier of the upload.
 * @property bytes the intended size of the file, in bytes.
 * @property createdAt the Unix timestamp (in seconds) of when the upload was created.
 * @property expiresAt the Unix timestamp (in seconds) of when the upload expires.
 * @property filename the name of the file being uploaded.
 * @property purpose the intended purpose of the uploaded file.
 * @property status the current status of the upload.
 * @property file the resulting file, once the upload has been completed.
 */
@Serializable
public data class Upload(
    @SerialName("id") public val id: UploadId,
    @SerialName("bytes") public val bytes: Long? = null,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("expires_at") public val expiresAt: Long? = null,
    @SerialName("filename") public val filename: String? = null,
    @SerialName("purpose") public val purpose: String? = null,
    @SerialName("status") public val status: String? = null,
    @SerialName("file") public val file: JsonObject? = null,
)

/**
 * A single part of a multipart upload.
 */
@Serializable
public data class UploadPart(
    @SerialName("id") public val id: UploadPartId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("upload_id") public val uploadId: UploadId? = null,
)

/**
 * Creates an upload session.
 *
 * @property filename the name of the file to upload.
 * @property bytes the intended size of the file, in bytes.
 * @property mimeType the MIME type of the file.
 * @property purpose the intended purpose of the uploaded file.
 * @property expiresAfter how long the upload should remain valid.
 */
@Serializable
public data class UploadCreateRequest(
    @SerialName("filename") public val filename: String,
    @SerialName("bytes") public val bytes: Long,
    @SerialName("mime_type") public val mimeType: String,
    @SerialName("purpose") public val purpose: String? = null,
    @SerialName("expires_after") public val expiresAfter: UploadExpiresAfter? = null,
)

/**
 * Expiry configuration of an upload session.
 *
 * @property anchor when the expiry window starts.
 * @property seconds the number of seconds the upload remains valid.
 */
@Serializable
public data class UploadExpiresAfter(
    @SerialName("anchor") public val anchor: String = "created_at",
    @SerialName("seconds") public val seconds: Long,
)

/**
 * Completes an upload session.
 *
 * @property partIds the ordered identifiers of the uploaded parts.
 */
@Serializable
public data class UploadCompleteRequest(
    @SerialName("part_ids") public val partIds: List<UploadPartId>,
)
