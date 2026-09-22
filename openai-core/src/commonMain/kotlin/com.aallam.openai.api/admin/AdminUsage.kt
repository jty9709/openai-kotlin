package com.aallam.openai.api.admin

/**
 * Filters shared by every organization usage query.
 *
 * @property startTime the start of the query window, as a Unix timestamp in seconds. Required.
 * @property endTime the end of the query window, as a Unix timestamp in seconds.
 * @property bucketWidth the width of each time bucket, e.g. `1d`.
 * @property groupBy the fields to group results by.
 * @property limit the number of buckets to return.
 * @property page the cursor returned by a previous query.
 * @property projectIds only include the given projects.
 * @property userIds only include the given users.
 * @property apiKeyIds only include the given API keys.
 * @property models only include the given models.
 */
public data class AdminUsageQuery(
    public val startTime: Long,
    public val endTime: Long? = null,
    public val bucketWidth: String? = null,
    public val groupBy: List<String>? = null,
    public val limit: Int? = null,
    public val page: String? = null,
    public val projectIds: List<String>? = null,
    public val userIds: List<String>? = null,
    public val apiKeyIds: List<String>? = null,
    public val models: List<String>? = null,
)
