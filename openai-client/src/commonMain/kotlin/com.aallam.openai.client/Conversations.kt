package com.aallam.openai.client

import com.aallam.openai.api.conversation.Conversation
import com.aallam.openai.api.conversation.ConversationCreateRequest
import com.aallam.openai.api.conversation.ConversationDeleted
import com.aallam.openai.api.conversation.ConversationId
import com.aallam.openai.api.conversation.ConversationUpdateRequest
import com.aallam.openai.api.core.RequestOptions

/**
 * Create and manage conversations.
 */
public interface Conversations {

    /**
     * Creates a conversation.
     */
    public suspend fun createConversation(
        request: ConversationCreateRequest = ConversationCreateRequest(),
        requestOptions: RequestOptions? = null
    ): Conversation

    /**
     * Retrieves a conversation by its identifier.
     */
    public suspend fun conversation(
        id: ConversationId,
        requestOptions: RequestOptions? = null
    ): Conversation

    /**
     * Updates a conversation.
     */
    public suspend fun updateConversation(
        id: ConversationId,
        request: ConversationUpdateRequest,
        requestOptions: RequestOptions? = null
    ): Conversation

    /**
     * Deletes a conversation.
     */
    public suspend fun deleteConversation(
        id: ConversationId,
        requestOptions: RequestOptions? = null
    ): ConversationDeleted
}
