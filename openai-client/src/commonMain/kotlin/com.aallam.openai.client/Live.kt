package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.live.LiveSessionAcceptRequest
import com.aallam.openai.api.live.LiveSessionCreateRequest
import com.aallam.openai.api.live.LiveSessionCreated
import com.aallam.openai.api.live.LiveSessionForkRequest
import com.aallam.openai.api.live.LiveSessionId
import com.aallam.openai.api.live.LiveSessionReferRequest
import com.aallam.openai.api.live.LiveSessionRejectRequest

/**
 * Create and manage live sessions.
 */
public interface Live {

    /**
     * Creates a live session, returning the session together with the transport to connect with.
     */
    public suspend fun createLiveSession(
        request: LiveSessionCreateRequest,
        requestOptions: RequestOptions? = null
    ): LiveSessionCreated

    /**
     * Accepts an incoming live session.
     */
    public suspend fun acceptLiveSession(
        id: LiveSessionId,
        request: LiveSessionAcceptRequest = LiveSessionAcceptRequest(),
        requestOptions: RequestOptions? = null
    )

    /**
     * Rejects an incoming live session.
     */
    public suspend fun rejectLiveSession(
        id: LiveSessionId,
        request: LiveSessionRejectRequest = LiveSessionRejectRequest(),
        requestOptions: RequestOptions? = null
    )

    /**
     * Hangs up a live session.
     */
    public suspend fun hangupLiveSession(
        id: LiveSessionId,
        requestOptions: RequestOptions? = null
    )

    /**
     * Refers a live session to another target.
     */
    public suspend fun referLiveSession(
        id: LiveSessionId,
        request: LiveSessionReferRequest,
        requestOptions: RequestOptions? = null
    )

    /**
     * Forks a live session so another client can join it.
     */
    public suspend fun forkLiveSession(
        id: LiveSessionId,
        request: LiveSessionForkRequest = LiveSessionForkRequest(),
        requestOptions: RequestOptions? = null
    ): LiveSessionCreated

    /**
     * Downloads the recording of a live session.
     */
    public suspend fun downloadLiveRecording(
        id: LiveSessionId,
        requestOptions: RequestOptions? = null
    ): ByteArray
}
