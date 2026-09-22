package com.aallam.openai.client

import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.video.Video
import com.aallam.openai.api.video.VideoCreateRequest
import com.aallam.openai.api.video.VideoDeleted
import com.aallam.openai.api.video.VideoId

/**
 * Generate and manage videos.
 */
public interface Videos {

    /**
     * Creates a video generation request.
     *
     * The video is generated asynchronously: poll [video] until the returned video reaches a
     * terminal status, then fetch the result with [downloadVideoContent].
     */
    public suspend fun createVideo(
        request: VideoCreateRequest,
        requestOptions: RequestOptions? = null
    ): Video

    /**
     * Retrieves a video by its identifier.
     */
    public suspend fun video(
        videoId: VideoId,
        requestOptions: RequestOptions? = null
    ): Video

    /**
     * Lists videos.
     */
    public suspend fun videos(
        limit: Int? = null,
        after: String? = null,
        order: SortOrder? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<Video>

    /**
     * Deletes a video.
     */
    public suspend fun deleteVideo(
        videoId: VideoId,
        requestOptions: RequestOptions? = null
    ): VideoDeleted

    /**
     * Downloads the content of a generated video.
     */
    public suspend fun downloadVideoContent(
        videoId: VideoId,
        requestOptions: RequestOptions? = null
    ): ByteArray
}
