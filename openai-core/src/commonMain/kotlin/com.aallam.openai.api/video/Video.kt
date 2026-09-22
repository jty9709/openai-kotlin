package com.aallam.openai.api.video

import com.aallam.openai.api.file.FileSource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The video identifier.
 */
@JvmInline
@Serializable
public value class VideoId(public val id: String)

/**
 * The model used to generate a video.
 */
@JvmInline
@Serializable
public value class VideoModel(public val id: String)

/**
 * The duration of a generated video.
 */
@JvmInline
@Serializable
public value class VideoSeconds(public val seconds: String)

/**
 * The resolution of a generated video.
 */
@JvmInline
@Serializable
public value class VideoSize(public val size: String)

/**
 * A video object.
 *
 * @property id the identifier of the video, which can be referenced in API endpoints.
 * @property status the current status of the video generation.
 */
@Serializable
public data class Video(
    @SerialName("id") public val id: VideoId,
    @SerialName("object") public val objectType: String? = null,
    @SerialName("completed_at") public val completedAt: Long? = null,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("error") public val error: VideoCreateError? = null,
    @SerialName("expires_at") public val expiresAt: Long? = null,
    @SerialName("model") public val model: VideoModel? = null,
    @SerialName("progress") public val progress: Long? = null,
    @SerialName("prompt") public val prompt: String? = null,
    @SerialName("remixed_from_video_id") public val remixedFromVideoId: String? = null,
    @SerialName("seconds") public val seconds: VideoSeconds? = null,
    @SerialName("size") public val size: VideoSize? = null,
    @SerialName("status") public val status: String? = null,
)

/**
 * Error attached to a failed video generation.
 */
@Serializable
public data class VideoCreateError(
    @SerialName("code") public val code: String? = null,
    @SerialName("message") public val message: String? = null,
)

/**
 * The result of deleting a video.
 */
@Serializable
public data class VideoDeleted(
    @SerialName("id") public val id: VideoId,
    @SerialName("object") public val objectType: String? = null,
    @SerialName("deleted") public val deleted: Boolean = false,
)

/**
 * Creates a video generation request.
 *
 * @property prompt the prompt describing the video to generate.
 * @property model the model to use for this video generation.
 * @property seconds the duration of the generated video.
 * @property size the resolution of the generated video.
 * @property inputReference an optional image used as the starting frame.
 */
public data class VideoCreateRequest(
    public val prompt: String,
    public val model: VideoModel? = null,
    public val seconds: VideoSeconds? = null,
    public val size: VideoSize? = null,
    public val inputReference: FileSource? = null,
)
