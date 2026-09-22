package com.aallam.openai.client

import com.aallam.openai.api.conversation.ConversationCreateRequest
import com.aallam.openai.api.conversation.ConversationId
import com.aallam.openai.api.conversation.ConversationUpdateRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.ResponseInput
import com.aallam.openai.api.response.ResponseRequest
import com.aallam.openai.api.video.VideoCreateRequest
import com.aallam.openai.api.video.VideoId
import com.aallam.openai.api.video.VideoModel
import com.aallam.openai.api.video.VideoSeconds
import com.aallam.openai.api.video.VideoSize
import kotlinx.coroutines.flow.Flow

/**
 * Compile-time checks that the newly added API surface is reachable from [OpenAI] without overload
 * ambiguity against the pre-existing interfaces, and that the snippets in the guides compile.
 *
 * These functions are never invoked; they only need to compile.
 */
@Suppress("unused")
private object ApiSurface {

    fun responseStreaming(openAI: OpenAI, request: ResponseRequest): Flow<*> =
        openAI.responseStream(request)

    /** A response can be scoped to a conversation, and the same request can be streamed. */
    fun responseStreamingWithinConversation(openAI: OpenAI): Flow<*> =
        openAI.responseStream(
            ResponseRequest(
                model = ModelId("gpt-4.1"),
                conversation = ConversationId("conv_123"),
                input = ResponseInput("What did I ask about earlier?"),
            )
        )

    suspend fun conversations(openAI: OpenAI) {
        openAI.createConversation(
            request = ConversationCreateRequest(metadata = mapOf("topic" to "weather"))
        )
        openAI.conversation(ConversationId("conv_123"))
        openAI.updateConversation(
            id = ConversationId("conv_123"),
            request = ConversationUpdateRequest(metadata = mapOf("topic" to "sports")),
        )
        openAI.deleteConversation(ConversationId("conv_123"))
    }

    suspend fun videos(openAI: OpenAI) {
        openAI.createVideo(
            request = VideoCreateRequest(
                prompt = "A calico cat playing a piano on stage",
                model = VideoModel("sora-2"),
                seconds = VideoSeconds("8"),
                size = VideoSize("1280x720"),
            )
        )
        openAI.video(VideoId("video_123"))
        openAI.videos(limit = 20)
        openAI.deleteVideo(VideoId("video_123"))
        openAI.downloadVideoContent(VideoId("video_123"))
    }

    suspend fun preExistingStillResolves(openAI: OpenAI) {
        openAI.response(ResponseRequest(model = ModelId("gpt-4.1"), input = ResponseInput("hi")))
        openAI.models()
    }
}
