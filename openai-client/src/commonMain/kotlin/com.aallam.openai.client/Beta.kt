package com.aallam.openai.client

import com.aallam.openai.api.beta.Agent
import com.aallam.openai.api.beta.AgentArtifact
import com.aallam.openai.api.beta.AgentArtifactId
import com.aallam.openai.api.beta.AgentCreateRequest
import com.aallam.openai.api.beta.AgentEnvironment
import com.aallam.openai.api.beta.AgentEnvironmentId
import com.aallam.openai.api.beta.AgentId
import com.aallam.openai.api.beta.AgentSession
import com.aallam.openai.api.beta.AgentSessionCreateRequest
import com.aallam.openai.api.beta.AgentSessionId
import com.aallam.openai.api.beta.AgentItem
import com.aallam.openai.api.beta.AgentSessionEvent
import com.aallam.openai.api.beta.AgentSubagent
import com.aallam.openai.api.beta.AgentSubagentId
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

/**
 * Agents, environments, vaults and ChatKit.
 *
 * These endpoints are in beta and may change without a major version bump.
 */
public interface Beta {

    // ---- Agents ----

    /**
     * Lists agents.
     */
    public suspend fun agents(
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<Agent>

    /**
     * Creates an agent.
     */
    public suspend fun createAgent(
        request: AgentCreateRequest,
        requestOptions: RequestOptions? = null
    ): Agent

    /**
     * Retrieves an agent by its identifier.
     */
    public suspend fun agent(
        id: AgentId,
        requestOptions: RequestOptions? = null
    ): Agent

    /**
     * Updates an agent.
     */
    public suspend fun updateAgent(
        id: AgentId,
        request: AgentUpdateRequest,
        requestOptions: RequestOptions? = null
    ): Agent

    /**
     * Deletes an agent.
     */
    public suspend fun deleteAgent(
        id: AgentId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    // ---- Agent sessions ----

    /**
     * Lists agent sessions.
     */
    public suspend fun agentSessions(
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AgentSession>

    /**
     * Creates an agent session.
     */
    public suspend fun createAgentSession(
        request: AgentSessionCreateRequest,
        requestOptions: RequestOptions? = null
    ): AgentSession

    /**
     * Retrieves an agent session by its identifier.
     */
    public suspend fun agentSession(
        id: AgentSessionId,
        requestOptions: RequestOptions? = null
    ): AgentSession

    /**
     * Deletes an agent session.
     */
    public suspend fun deleteAgentSession(
        id: AgentSessionId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    // ---- Environments ----

    /**
     * Retrieves the environment an agent session runs in.
     */
    public suspend fun agentEnvironment(
        id: AgentEnvironmentId,
        requestOptions: RequestOptions? = null
    ): AgentEnvironment

    // ---- Vaults ----

    /**
     * Lists vaults.
     */
    public suspend fun vaults(
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<Vault>

    /**
     * Creates a vault.
     */
    public suspend fun createVault(
        request: VaultCreateRequest,
        requestOptions: RequestOptions? = null
    ): Vault

    /**
     * Retrieves a vault by its identifier.
     */
    public suspend fun vault(
        id: VaultId,
        requestOptions: RequestOptions? = null
    ): Vault

    /**
     * Deletes a vault.
     */
    public suspend fun deleteVault(
        id: VaultId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    // ---- ChatKit ----

    /**
     * Creates a ChatKit session.
     */
    public suspend fun createChatKitSession(
        request: ChatKitSessionCreateRequest,
        requestOptions: RequestOptions? = null
    ): ChatKitSession

    /**
     * Cancels a ChatKit session.
     */
    public suspend fun cancelChatKitSession(
        id: ChatKitSessionId,
        requestOptions: RequestOptions? = null
    ): ChatKitSession

    /**
     * Lists ChatKit threads.
     */
    public suspend fun chatKitThreads(
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<ChatKitThread>

    /**
     * Retrieves a ChatKit thread by its identifier.
     */
    public suspend fun chatKitThread(
        id: ChatKitThreadId,
        requestOptions: RequestOptions? = null
    ): ChatKitThread

    /**
     * Deletes a ChatKit thread.
     */
    public suspend fun deleteChatKitThread(
        id: ChatKitThreadId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    // ---- Vault credentials ----

    /**
     * Lists the credentials stored in a vault.
     */
    public suspend fun vaultCredentials(
        vaultId: VaultId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<VaultCredential>

    /**
     * Creates a credential in a vault.
     */
    public suspend fun createVaultCredential(
        vaultId: VaultId,
        request: VaultCredentialCreateRequest,
        requestOptions: RequestOptions? = null
    ): VaultCredential

    /**
     * Deletes a credential from a vault.
     */
    public suspend fun deleteVaultCredential(
        vaultId: VaultId,
        credentialId: VaultCredentialId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse

    // ---- Agent session resources ----

    /**
     * Lists the artifacts produced by an agent session.
     */
    public suspend fun agentSessionArtifacts(
        sessionId: AgentSessionId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AgentArtifact>

    /**
     * Lists the turns of an agent session.
     */
    public suspend fun agentSessionTurns(
        sessionId: AgentSessionId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AgentTurn>

    /**
     * Lists the items produced in an agent session.
     */
    public suspend fun agentSessionItems(
        sessionId: AgentSessionId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AgentItem>

    /**
     * Lists the events emitted during an agent session.
     */
    public suspend fun agentSessionEvents(
        sessionId: AgentSessionId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AgentSessionEvent>

    /**
     * Lists the subagents spawned within an agent session.
     */
    public suspend fun agentSessionSubagents(
        sessionId: AgentSessionId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AgentSubagent>

    /**
     * Retrieves a subagent spawned within an agent session.
     */
    public suspend fun agentSubagent(
        sessionId: AgentSessionId,
        subagentId: AgentSubagentId,
        requestOptions: RequestOptions? = null
    ): AgentSubagent

    /**
     * Lists the items produced by a subagent.
     */
    public suspend fun agentSubagentItems(
        sessionId: AgentSessionId,
        subagentId: AgentSubagentId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AgentItem>

    /**
     * Lists the turns of a subagent.
     */
    public suspend fun agentSubagentTurns(
        sessionId: AgentSessionId,
        subagentId: AgentSubagentId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AgentTurn>

    /**
     * Retrieves a turn of a subagent.
     */
    public suspend fun agentSubagentTurn(
        sessionId: AgentSessionId,
        subagentId: AgentSubagentId,
        turnId: AgentTurnId,
        requestOptions: RequestOptions? = null
    ): AgentTurn

    /**
     * Lists the items produced within a subagent turn.
     */
    public suspend fun agentSubagentTurnItems(
        sessionId: AgentSessionId,
        subagentId: AgentSubagentId,
        turnId: AgentTurnId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<AgentItem>

    /**
     * Retrieves an artifact produced by an agent session.
     */
    public suspend fun agentArtifact(
        sessionId: AgentSessionId,
        artifactId: AgentArtifactId,
        requestOptions: RequestOptions? = null
    ): AgentArtifact

    /**
     * Downloads the content of an artifact produced by an agent session.
     */
    public suspend fun agentArtifactContent(
        sessionId: AgentSessionId,
        artifactId: AgentArtifactId,
        requestOptions: RequestOptions? = null
    ): ByteArray

    // ---- Environment resources ----

    /**
     * Lists the files available inside an agent environment.
     */
    public suspend fun agentEnvironmentFiles(
        environmentId: AgentEnvironmentId,
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<EnvironmentFile>

    /**
     * Creates a file in an agent environment.
     */
    public suspend fun createAgentEnvironmentFile(
        environmentId: AgentEnvironmentId,
        request: EnvironmentFileCreateRequest,
        requestOptions: RequestOptions? = null
    ): EnvironmentFile

    /**
     * Lists environment templates.
     */
    public suspend fun environmentTemplates(
        limit: Int? = null,
        after: String? = null,
        requestOptions: RequestOptions? = null
    ): PaginatedList<EnvironmentTemplate>

    /**
     * Creates an environment template.
     */
    public suspend fun createEnvironmentTemplate(
        request: EnvironmentTemplateCreateRequest,
        requestOptions: RequestOptions? = null
    ): EnvironmentTemplate

    /**
     * Retrieves an environment template by its identifier.
     */
    public suspend fun environmentTemplate(
        id: EnvironmentTemplateId,
        requestOptions: RequestOptions? = null
    ): EnvironmentTemplate

    /**
     * Updates an environment template.
     */
    public suspend fun updateEnvironmentTemplate(
        id: EnvironmentTemplateId,
        request: EnvironmentTemplateUpdateRequest,
        requestOptions: RequestOptions? = null
    ): EnvironmentTemplate

    /**
     * Deletes an environment template.
     */
    public suspend fun deleteEnvironmentTemplate(
        id: EnvironmentTemplateId,
        requestOptions: RequestOptions? = null
    ): DeleteResponse
}
