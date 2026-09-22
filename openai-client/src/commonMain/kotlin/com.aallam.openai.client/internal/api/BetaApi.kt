package com.aallam.openai.client.internal.api

import com.aallam.openai.api.beta.Agent
import com.aallam.openai.api.beta.AgentArtifact
import com.aallam.openai.api.beta.AgentArtifactId
import com.aallam.openai.api.beta.AgentCreateRequest
import com.aallam.openai.api.beta.AgentItem
import com.aallam.openai.api.beta.AgentSessionEvent
import com.aallam.openai.api.beta.AgentSubagent
import com.aallam.openai.api.beta.AgentSubagentId
import com.aallam.openai.api.beta.AgentEnvironment
import com.aallam.openai.api.beta.AgentEnvironmentId
import com.aallam.openai.api.beta.AgentId
import com.aallam.openai.api.beta.AgentSession
import com.aallam.openai.api.beta.AgentSessionCreateRequest
import com.aallam.openai.api.beta.AgentSessionId
import com.aallam.openai.api.beta.AgentTurn
import com.aallam.openai.api.beta.AgentTurnId
import com.aallam.openai.api.beta.AgentUpdateRequest
import com.aallam.openai.api.beta.ChatKitSession
import com.aallam.openai.api.beta.ChatKitSessionCreateRequest
import com.aallam.openai.api.beta.ChatKitSessionId
import com.aallam.openai.api.beta.ChatKitThread
import com.aallam.openai.api.beta.ChatKitThreadId
import com.aallam.openai.api.beta.EnvironmentFile
import com.aallam.openai.api.beta.EnvironmentFileCreateRequest
import com.aallam.openai.api.beta.EnvironmentTemplate
import com.aallam.openai.api.beta.EnvironmentTemplateCreateRequest
import com.aallam.openai.api.beta.EnvironmentTemplateId
import com.aallam.openai.api.beta.EnvironmentTemplateUpdateRequest
import com.aallam.openai.api.beta.Vault
import com.aallam.openai.api.beta.VaultCreateRequest
import com.aallam.openai.api.beta.VaultCredential
import com.aallam.openai.api.beta.VaultCredentialCreateRequest
import com.aallam.openai.api.beta.VaultCredentialId
import com.aallam.openai.api.beta.VaultId
import com.aallam.openai.api.core.DeleteResponse
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.client.Beta
import com.aallam.openai.client.internal.extension.beta
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class BetaApi(private val requester: HttpRequester) : Beta {

    // ---- Agents ----

    override suspend fun agents(
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<Agent> {
        return requester.perform {
            it.get {
                url(path = ApiPath.Agents) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createAgent(
        request: AgentCreateRequest,
        requestOptions: RequestOptions?
    ): Agent {
        return requester.perform {
            it.post {
                url(path = ApiPath.Agents)
                setBody(request)
                contentType(ContentType.Application.Json)
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun agent(id: AgentId, requestOptions: RequestOptions?): Agent {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Agents}/${id.id}")
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateAgent(
        id: AgentId,
        request: AgentUpdateRequest,
        requestOptions: RequestOptions?
    ): Agent {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Agents}/${id.id}")
                setBody(request)
                contentType(ContentType.Application.Json)
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteAgent(id: AgentId, requestOptions: RequestOptions?): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.Agents}/${id.id}")
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    // ---- Agent sessions ----

    override suspend fun agentSessions(
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AgentSession> {
        return requester.perform {
            it.get {
                url(path = ApiPath.AgentSessions) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createAgentSession(
        request: AgentSessionCreateRequest,
        requestOptions: RequestOptions?
    ): AgentSession {
        return requester.perform {
            it.post {
                url(path = ApiPath.AgentSessions)
                setBody(request)
                contentType(ContentType.Application.Json)
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun agentSession(
        id: AgentSessionId,
        requestOptions: RequestOptions?
    ): AgentSession {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AgentSessions}/${id.id}")
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteAgentSession(
        id: AgentSessionId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.AgentSessions}/${id.id}")
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    // ---- Environments ----

    override suspend fun agentEnvironment(
        id: AgentEnvironmentId,
        requestOptions: RequestOptions?
    ): AgentEnvironment {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AgentEnvironments}/${id.id}")
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    // ---- Vaults ----

    override suspend fun vaults(
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<Vault> {
        return requester.perform {
            it.get {
                url(path = ApiPath.Vaults) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createVault(
        request: VaultCreateRequest,
        requestOptions: RequestOptions?
    ): Vault {
        return requester.perform {
            it.post {
                url(path = ApiPath.Vaults)
                setBody(request)
                contentType(ContentType.Application.Json)
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun vault(id: VaultId, requestOptions: RequestOptions?): Vault {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Vaults}/${id.id}")
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteVault(id: VaultId, requestOptions: RequestOptions?): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.Vaults}/${id.id}")
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    // ---- ChatKit ----

    override suspend fun createChatKitSession(
        request: ChatKitSessionCreateRequest,
        requestOptions: RequestOptions?
    ): ChatKitSession {
        return requester.perform {
            it.post {
                url(path = ApiPath.ChatKitSessions)
                setBody(request)
                contentType(ContentType.Application.Json)
                beta("chatkit_beta", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun cancelChatKitSession(
        id: ChatKitSessionId,
        requestOptions: RequestOptions?
    ): ChatKitSession {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.ChatKitSessions}/${id.id}/cancel")
                beta("chatkit_beta", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun chatKitThreads(
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<ChatKitThread> {
        return requester.perform {
            it.get {
                url(path = ApiPath.ChatKitThreads) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                beta("chatkit_beta", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun chatKitThread(
        id: ChatKitThreadId,
        requestOptions: RequestOptions?
    ): ChatKitThread {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.ChatKitThreads}/${id.id}")
                beta("chatkit_beta", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteChatKitThread(
        id: ChatKitThreadId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.ChatKitThreads}/${id.id}")
                beta("chatkit_beta", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    // ---- Vault credentials ----

    override suspend fun vaultCredentials(
        vaultId: VaultId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<VaultCredential> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Vaults}/${vaultId.id}/credentials") {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createVaultCredential(
        vaultId: VaultId,
        request: VaultCredentialCreateRequest,
        requestOptions: RequestOptions?
    ): VaultCredential {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.Vaults}/${vaultId.id}/credentials")
                setBody(request)
                contentType(ContentType.Application.Json)
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteVaultCredential(
        vaultId: VaultId,
        credentialId: VaultCredentialId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.Vaults}/${vaultId.id}/credentials/${credentialId.id}")
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    // ---- Agent session resources ----

    override suspend fun agentSessionArtifacts(
        sessionId: AgentSessionId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AgentArtifact> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AgentSessions}/${sessionId.id}/artifacts") {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun agentSessionTurns(
        sessionId: AgentSessionId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AgentTurn> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AgentSessions}/${sessionId.id}/turns") {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun agentSessionItems(
        sessionId: AgentSessionId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AgentItem> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AgentSessions}/${sessionId.id}/items") {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun agentSessionEvents(
        sessionId: AgentSessionId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AgentSessionEvent> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AgentSessions}/${sessionId.id}/events") {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun agentSessionSubagents(
        sessionId: AgentSessionId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AgentSubagent> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AgentSessions}/${sessionId.id}/subagents") {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun agentSubagent(
        sessionId: AgentSessionId,
        subagentId: AgentSubagentId,
        requestOptions: RequestOptions?
    ): AgentSubagent {
        return requester.perform {
            it.get {
                url(path = subagentPath(sessionId, subagentId))
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun agentSubagentItems(
        sessionId: AgentSessionId,
        subagentId: AgentSubagentId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AgentItem> {
        return requester.perform {
            it.get {
                url(path = "${subagentPath(sessionId, subagentId)}/items") {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun agentSubagentTurns(
        sessionId: AgentSessionId,
        subagentId: AgentSubagentId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AgentTurn> {
        return requester.perform {
            it.get {
                url(path = "${subagentPath(sessionId, subagentId)}/turns") {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun agentSubagentTurn(
        sessionId: AgentSessionId,
        subagentId: AgentSubagentId,
        turnId: AgentTurnId,
        requestOptions: RequestOptions?
    ): AgentTurn {
        return requester.perform {
            it.get {
                url(path = "${subagentPath(sessionId, subagentId)}/turns/${turnId.id}")
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun agentSubagentTurnItems(
        sessionId: AgentSessionId,
        subagentId: AgentSubagentId,
        turnId: AgentTurnId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<AgentItem> {
        return requester.perform {
            it.get {
                url(path = "${subagentPath(sessionId, subagentId)}/turns/${turnId.id}/items") {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    private fun subagentPath(sessionId: AgentSessionId, subagentId: AgentSubagentId) =
        "${ApiPath.AgentSessions}/${sessionId.id}/subagents/${subagentId.id}"

    override suspend fun agentArtifact(
        sessionId: AgentSessionId,
        artifactId: AgentArtifactId,
        requestOptions: RequestOptions?
    ): AgentArtifact {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AgentSessions}/${sessionId.id}/artifacts/${artifactId.id}")
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun agentArtifactContent(
        sessionId: AgentSessionId,
        artifactId: AgentArtifactId,
        requestOptions: RequestOptions?
    ): ByteArray {
        return requester.perform {
            it.get {
                url(
                    path = "${ApiPath.AgentSessions}/${sessionId.id}/artifacts/${artifactId.id}/content"
                )
                beta("agents", 1)
                requestOptions(requestOptions)
            }
        }
    }

    // ---- Environment resources ----

    override suspend fun createAgentEnvironmentFile(
        environmentId: AgentEnvironmentId,
        request: EnvironmentFileCreateRequest,
        requestOptions: RequestOptions?
    ): EnvironmentFile {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.AgentEnvironments}/${environmentId.id}/files")
                setBody(request)
                contentType(ContentType.Application.Json)
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun agentEnvironmentFiles(
        environmentId: AgentEnvironmentId,
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<EnvironmentFile> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.AgentEnvironments}/${environmentId.id}/files") {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun environmentTemplates(
        limit: Int?,
        after: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<EnvironmentTemplate> {
        return requester.perform {
            it.get {
                url(path = ApiPath.EnvironmentTemplates) {
                    limit?.let { value -> parameter("limit", value) }
                    after?.let { value -> parameter("after", value) }
                }
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun createEnvironmentTemplate(
        request: EnvironmentTemplateCreateRequest,
        requestOptions: RequestOptions?
    ): EnvironmentTemplate {
        return requester.perform {
            it.post {
                url(path = ApiPath.EnvironmentTemplates)
                setBody(request)
                contentType(ContentType.Application.Json)
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun environmentTemplate(
        id: EnvironmentTemplateId,
        requestOptions: RequestOptions?
    ): EnvironmentTemplate {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.EnvironmentTemplates}/${id.id}")
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun updateEnvironmentTemplate(
        id: EnvironmentTemplateId,
        request: EnvironmentTemplateUpdateRequest,
        requestOptions: RequestOptions?
    ): EnvironmentTemplate {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.EnvironmentTemplates}/${id.id}")
                setBody(request)
                contentType(ContentType.Application.Json)
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteEnvironmentTemplate(
        id: EnvironmentTemplateId,
        requestOptions: RequestOptions?
    ): DeleteResponse {
        return requester.perform {
            it.delete {
                url(path = "${ApiPath.EnvironmentTemplates}/${id.id}")
                beta("agents", 1)
                requestOptions(requestOptions)
            }.body()
        }
    }
}
