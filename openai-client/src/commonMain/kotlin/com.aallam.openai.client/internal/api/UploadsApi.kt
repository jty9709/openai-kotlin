package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.upload.Upload
import com.aallam.openai.api.upload.UploadCompleteRequest
import com.aallam.openai.api.upload.UploadCreateRequest
import com.aallam.openai.api.upload.UploadId
import com.aallam.openai.api.upload.UploadPart
import com.aallam.openai.api.upload.UploadPartId
import com.aallam.openai.client.Uploads
import com.aallam.openai.client.internal.extension.appendFileSource
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

internal class UploadsApi(private val requester: HttpRequester) : Uploads {

    override suspend fun createUpload(
        request: UploadCreateRequest,
        requestOptions: RequestOptions?
    ): Upload {
        return requester.perform {
            it.post {
                url(path = ApiPath.Uploads)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun cancelUpload(
        uploadId: UploadId,
        requestOptions: RequestOptions?
    ): Upload {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Uploads}/${uploadId.id}/cancel")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun completeUpload(
        uploadId: UploadId,
        partIds: List<UploadPartId>,
        requestOptions: RequestOptions?
    ): Upload {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Uploads}/${uploadId.id}/complete")
                setBody(UploadCompleteRequest(partIds))
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createUploadPart(
        uploadId: UploadId,
        data: FileSource,
        requestOptions: RequestOptions?
    ): UploadPart {
        return requester.perform<UploadPart> {
            it.submitFormWithBinaryData(
                url = "${ApiPath.Uploads}/${uploadId.id}/parts",
                formData = formData { appendFileSource("data", data) },
            ) {
                requestOptions(requestOptions)
            }
        }
    }
}
