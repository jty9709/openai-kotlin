package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.safety.SafetyAlert
import com.aallam.openai.api.safety.SafetyAlertId
import com.aallam.openai.api.safety.SafetyCase
import com.aallam.openai.api.safety.SafetyCaseId
import com.aallam.openai.client.Safety
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*

internal class SafetyApi(private val requester: HttpRequester) : Safety {

    override suspend fun safetyCase(id: SafetyCaseId, requestOptions: RequestOptions?): SafetyCase {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.SafetyCases}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun safetyAlert(
        id: SafetyAlertId,
        requestOptions: RequestOptions?
    ): SafetyAlert {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.SafetyAlerts}/${id.id}")
                requestOptions(requestOptions)
            }.body()
        }
    }
}
