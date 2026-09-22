package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.video.Video
import com.aallam.openai.api.video.VideoCreateRequest
import com.aallam.openai.api.video.VideoDeleted
import com.aallam.openai.api.video.VideoId
import com.aallam.openai.client.Videos
import com.aallam.openai.client.internal.extension.appendFileSource
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

internal class VideosApi(private val requester: HttpRequester) : Videos {

    override suspend fun createVideo(
        request: VideoCreateRequest,
        requestOptions: RequestOptions?
    ): Video {
        return requester.perform<Video> {
            it.submitFormWithBinaryData(
                url = ApiPath.Videos,
                formData = videoCreateRequest(request),
            ) {
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun video(videoId: VideoId, requestOptions: RequestOptions?): Video {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Videos}/${videoId.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun videos(
        limit: Int?,
        after: String?,
        order: SortOrder?,
        requestOptions: RequestOptions?
    ): PaginatedList<Video> {
        return requester.perform {
            it.get {
                url(path = ApiPath.Videos) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                    order?.let { value -> parameter("order", value.order) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteVideo(videoId: VideoId, requestOptions: RequestOptions?): VideoDeleted {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.Videos}/${videoId.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun downloadVideoContent(
        videoId: VideoId,
        requestOptions: RequestOptions?
    ): ByteArray {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Videos}/${videoId.id}/content")
                requestOptions(requestOptions)
            }
        }
    }

    /**
     * Build video creation request.
     */
    private fun videoCreateRequest(request: VideoCreateRequest) = formData {
        append(key = "prompt", value = request.prompt)
        request.model?.let { model -> append(key = "model", value = model.id) }
        request.seconds?.let { seconds -> append(key = "seconds", value = seconds.seconds) }
        request.size?.let { size -> append(key = "size", value = size.size) }
        request.inputReference?.let { source -> appendFileSource("input_reference", source) }
    }
}
