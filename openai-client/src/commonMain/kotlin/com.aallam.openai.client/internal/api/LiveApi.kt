package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.live.LiveSessionAcceptRequest
import com.aallam.openai.api.live.LiveSessionCreateRequest
import com.aallam.openai.api.live.LiveSessionCreated
import com.aallam.openai.api.live.LiveSessionForkRequest
import com.aallam.openai.api.live.LiveSessionId
import com.aallam.openai.api.live.LiveSessionReferRequest
import com.aallam.openai.api.live.LiveSessionRejectRequest
import com.aallam.openai.client.Live
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class LiveApi(private val requester: HttpRequester) : Live {

    override suspend fun createLiveSession(
        request: LiveSessionCreateRequest,
        requestOptions: RequestOptions?
    ): LiveSessionCreated {
        return requester.perform {
            it.post {
                url(path = ApiPath.LiveSessions)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun acceptLiveSession(
        id: LiveSessionId,
        request: LiveSessionAcceptRequest,
        requestOptions: RequestOptions?
    ) {
        requester.perform<Unit> {
            it.post {
                url(path = "${ApiPath.LiveSessions}/${id.id}/accept")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun rejectLiveSession(
        id: LiveSessionId,
        request: LiveSessionRejectRequest,
        requestOptions: RequestOptions?
    ) {
        requester.perform<Unit> {
            it.post {
                url(path = "${ApiPath.LiveSessions}/${id.id}/reject")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun hangupLiveSession(
        id: LiveSessionId,
        requestOptions: RequestOptions?
    ) {
        requester.perform<Unit> {
            it.post {
                url(path = "${ApiPath.LiveSessions}/${id.id}/hangup")
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun referLiveSession(
        id: LiveSessionId,
        request: LiveSessionReferRequest,
        requestOptions: RequestOptions?
    ) {
        requester.perform<Unit> {
            it.post {
                url(path = "${ApiPath.LiveSessions}/${id.id}/refer")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun forkLiveSession(
        id: LiveSessionId,
        request: LiveSessionForkRequest,
        requestOptions: RequestOptions?
    ): LiveSessionCreated {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.LiveSessions}/${id.id}/fork")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun downloadLiveRecording(
        id: LiveSessionId,
        requestOptions: RequestOptions?
    ): ByteArray {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.LiveSessions}/${id.id}/content")
                requestOptions(requestOptions)
            }
        }
    }
}
