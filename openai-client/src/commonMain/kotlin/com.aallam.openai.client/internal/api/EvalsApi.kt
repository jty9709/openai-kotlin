package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.eval.Eval
import com.aallam.openai.api.eval.EvalCreateRequest
import com.aallam.openai.api.eval.EvalDeleted
import com.aallam.openai.api.eval.EvalId
import com.aallam.openai.api.eval.EvalUpdateRequest
import com.aallam.openai.client.Evals
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class EvalsApi(private val requester: HttpRequester) : Evals {

    override suspend fun createEval(
        request: EvalCreateRequest,
        requestOptions: RequestOptions?
    ): Eval {
        return requester.perform {
            it.post {
                url(path = ApiPath.Evals)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun eval(id: EvalId, requestOptions: RequestOptions?): Eval {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Evals}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateEval(
        id: EvalId,
        request: EvalUpdateRequest,
        requestOptions: RequestOptions?
    ): Eval {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Evals}/${id.id}")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteEval(id: EvalId, requestOptions: RequestOptions?): EvalDeleted {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.Evals}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun evals(
        limit: Int?,
        after: String?,
        order: SortOrder?,
        requestOptions: RequestOptions?
    ): PaginatedList<Eval> {
        return requester.perform {
            it.get {
                url(path = ApiPath.Evals) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                    order?.let { value -> parameter("order", value.order) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }
}
