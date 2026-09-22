package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.upload.Upload
import com.aallam.openai.api.upload.UploadCreateRequest
import com.aallam.openai.api.upload.UploadId
import com.aallam.openai.api.upload.UploadPart
import com.aallam.openai.api.upload.UploadPartId

/**
 * Upload large files in parts.
 *
 * A large file is uploaded by creating an upload session, uploading each part, then completing the
 * session with the ordered part identifiers.
 */
public interface Uploads {

    /**
     * Creates an upload session.
     */
    public suspend fun createUpload(
        request: UploadCreateRequest,
        requestOptions: RequestOptions? = null
    ): Upload

    /**
     * Cancels an upload session.
     */
    public suspend fun cancelUpload(
        uploadId: UploadId,
        requestOptions: RequestOptions? = null
    ): Upload

    /**
     * Completes an upload session and assembles the uploaded parts into a file.
     *
     * @param partIds the ordered identifiers of the uploaded parts.
     */
    public suspend fun completeUpload(
        uploadId: UploadId,
        partIds: List<UploadPartId>,
        requestOptions: RequestOptions? = null
    ): Upload

    /**
     * Uploads a single part of an upload session.
     */
    public suspend fun createUploadPart(
        uploadId: UploadId,
        data: FileSource,
        requestOptions: RequestOptions? = null
    ): UploadPart
}
