package com.aallam.openai.client

import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.eval.Eval
import com.aallam.openai.api.eval.EvalCreateRequest
import com.aallam.openai.api.eval.EvalDeleted
import com.aallam.openai.api.eval.EvalId
import com.aallam.openai.api.eval.EvalUpdateRequest

/**
 * Create and manage evaluations.
 */
public interface Evals {

    /**
     * Creates an evaluation.
     */
    public suspend fun createEval(
        request: EvalCreateRequest,
        requestOptions: RequestOptions? = null
    ): Eval

    /**
     * Retrieves an evaluation by its identifier.
     */
    public suspend fun eval(
        id: EvalId,
        requestOptions: RequestOptions? = null
    ): Eval

    /**
     * Updates an evaluation.
     */
    public suspend fun updateEval(
        id: EvalId,
        request: EvalUpdateRequest,
        requestOptions: RequestOptions? = null
    ): Eval

    /**
     * Deletes an evaluation.
     */
    public suspend fun deleteEval(
        id: EvalId,
        requestOptions: RequestOptions? = null
    ): EvalDeleted

    /**
     * Lists evaluations.
     */
    public suspend fun evals(
        limit: Int? = null,
        after: String? = null,
        order: SortOrder? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<Eval>
}
