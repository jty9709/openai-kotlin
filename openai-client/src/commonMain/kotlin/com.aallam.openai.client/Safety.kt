package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.safety.SafetyAlert
import com.aallam.openai.api.safety.SafetyAlertId
import com.aallam.openai.api.safety.SafetyCase
import com.aallam.openai.api.safety.SafetyCaseId

/**
 * Retrieve safety cases and alerts raised by the moderation pipeline.
 */
public interface Safety {

    /**
     * Retrieves a safety case by its identifier.
     */
    public suspend fun safetyCase(
        id: SafetyCaseId,
        requestOptions: RequestOptions? = null
    ): SafetyCase

    /**
     * Retrieves a safety alert by its identifier.
     */
    public suspend fun safetyAlert(
        id: SafetyAlertId,
        requestOptions: RequestOptions? = null
    ): SafetyAlert
}
