package com.aallam.openai.client.internal.api

import com.aallam.openai.api.conversation.Conversation
import com.aallam.openai.api.conversation.ConversationCreateRequest
import com.aallam.openai.api.conversation.ConversationDeleted
import com.aallam.openai.api.conversation.ConversationId
import com.aallam.openai.api.conversation.ConversationUpdateRequest
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.client.Conversations
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class ConversationsApi(private val requester: HttpRequester) : Conversations {

    override suspend fun createConversation(
        request: ConversationCreateRequest,
        requestOptions: RequestOptions?
    ): Conversation {
        return requester.perform {
            it.post {
                url(path = ApiPath.Conversations)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun conversation(
        id: ConversationId,
        requestOptions: RequestOptions?
    ): Conversation {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Conversations}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateConversation(
        id: ConversationId,
        request: ConversationUpdateRequest,
        requestOptions: RequestOptions?
    ): Conversation {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Conversations}/${id.id}")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteConversation(
        id: ConversationId,
        requestOptions: RequestOptions?
    ): ConversationDeleted {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.Conversations}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }
}
