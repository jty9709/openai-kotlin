package com.aallam.openai.api.beta

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlin.jvm.JvmInline

// region Agents

/** The agent identifier. */
@JvmInline
@Serializable
public value class AgentId(public val id: String)

/**
 * An agent definition.
 *
 * @property id the identifier of the agent.
 * @property createdAt the Unix timestamp (in seconds) of when the agent was created.
 * @property instructions system instructions for the agent.
 * @property metadata key-value pairs attached to the agent.
 * @property model the model backing the agent.
 * @property name the name of the agent.
 * @property serviceTier the service tier the agent runs on.
 */
@Serializable
public data class Agent(
    @SerialName("id") public val id: AgentId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("instructions") public val instructions: String? = null,
    @SerialName("metadata") public val metadata: Map<String, String>? = null,
    @SerialName("model") public val model: String? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("service_tier") public val serviceTier: String? = null,
)

/**
 * Creates an agent.
 */
@Serializable
public data class AgentCreateRequest(
    @SerialName("model") public val model: String,
    @SerialName("name") public val name: String? = null,
    @SerialName("instructions") public val instructions: String? = null,
    @SerialName("metadata") public val metadata: Map<String, String>? = null,
    @SerialName("service_tier") public val serviceTier: String? = null,
)

/**
 * Updates an agent.
 */
@Serializable
public data class AgentUpdateRequest(
    @SerialName("name") public val name: String? = null,
    @SerialName("instructions") public val instructions: String? = null,
    @SerialName("metadata") public val metadata: Map<String, String>? = null,
    @SerialName("service_tier") public val serviceTier: String? = null,
)

/** The agent session identifier. */
@JvmInline
@Serializable
public value class AgentSessionId(public val id: String)

/**
 * A session in which an agent runs.
 *
 * @property id the identifier of the session.
 * @property createdAt the Unix timestamp (in seconds) of when the session was created.
 * @property agentId the identifier of the agent the session belongs to.
 * @property status the current status of the session.
 */
@Serializable
public data class AgentSession(
    @SerialName("id") public val id: AgentSessionId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("agent_id") public val agentId: AgentId? = null,
    @SerialName("status") public val status: String? = null,
)

/**
 * Creates an agent session.
 */
@Serializable
public data class AgentSessionCreateRequest(
    /** Execution environment, for example `{"type":"none"}`. */
    @SerialName("environment") public val environment: JsonObject,
    @SerialName("agent_id") public val agentId: AgentId? = null,
)

// endregion

// region Environments

/** The environment identifier. */
@JvmInline
@Serializable
public value class AgentEnvironmentId(public val id: String)

/**
 * An environment an agent session runs in.
 *
 * @property id the identifier of the environment.
 * @property skills the skills enabled in the environment.
 */
@Serializable
public data class AgentEnvironment(
    @SerialName("id") public val id: AgentEnvironmentId,
    @SerialName("skills") public val skills: List<String>? = null,
)

// endregion

// region Vaults

/** The vault identifier. */
@JvmInline
@Serializable
public value class VaultId(public val id: String)

/**
 * A vault holding credentials available to agent sessions.
 *
 * @property id the identifier of the vault.
 * @property createdAt the Unix timestamp (in seconds) of when the vault was created.
 * @property name the name of the vault.
 * @property status the current status of the vault.
 */
@Serializable
public data class Vault(
    @SerialName("id") public val id: VaultId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("status") public val status: String? = null,
)

/**
 * Creates a vault.
 */
@Serializable
public data class VaultCreateRequest(
    @SerialName("name") public val name: String,
)

// endregion

// region ChatKit

/** The ChatKit session identifier. */
@JvmInline
@Serializable
public value class ChatKitSessionId(public val id: String)

/**
 * A ChatKit session.
 *
 * @property id the identifier of the session.
 * @property clientSecret the secret used to authenticate the ChatKit client.
 * @property expiresAt the Unix timestamp (in seconds) of when the session expires.
 */
@Serializable
public data class ChatKitSession(
    @SerialName("id") public val id: ChatKitSessionId,
    @SerialName("client_secret") public val clientSecret: String? = null,
    @SerialName("expires_at") public val expiresAt: Long? = null,
)

/**
 * Creates a ChatKit session.
 *
 * @property user the user the session is issued for.
 * @property workflow the workflow the session runs.
 */
@Serializable
public data class ChatKitSessionCreateRequest(
    @SerialName("user") public val user: String,
    @SerialName("workflow") public val workflow: ChatKitWorkflow,
)

/** Workflow reference and optional overrides for a ChatKit session. */
@Serializable
public data class ChatKitWorkflow(
    @SerialName("id") public val id: String,
    @SerialName("version") public val version: String? = null,
    @SerialName("state_variables") public val stateVariables: JsonObject? = null,
    @SerialName("tracing") public val tracing: JsonObject? = null,
)

/** The ChatKit thread identifier. */
@JvmInline
@Serializable
public value class ChatKitThreadId(public val id: String)

/**
 * A ChatKit conversation thread.
 *
 * @property id the identifier of the thread.
 * @property createdAt the Unix timestamp (in seconds) of when the thread was created.
 * @property status the current status of the thread.
 * @property title the title of the thread.
 * @property user the user the thread belongs to.
 */
@Serializable
public data class ChatKitThread(
    @SerialName("id") public val id: ChatKitThreadId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("status") public val status: String? = null,
    @SerialName("title") public val title: String? = null,
    @SerialName("user") public val user: String? = null,
)

// endregion

// region Vault credentials

/** The vault credential identifier. */
@JvmInline
@Serializable
public value class VaultCredentialId(public val id: String)

/**
 * A credential stored in a vault.
 *
 * @property id the identifier of the credential.
 * @property createdAt the Unix timestamp (in seconds) of when the credential was created.
 * @property name the name of the credential.
 */
@Serializable
public data class VaultCredential(
    @SerialName("id") public val id: VaultCredentialId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("name") public val name: String? = null,
)

/**
 * Creates a credential in a vault.
 */
@Serializable
public data class VaultCredentialCreateRequest(
    @SerialName("name") public val name: String,
    @SerialName("value") public val value: String,
)

// endregion

// region Agent session artifacts and turns

/** The agent artifact identifier. */
@JvmInline
@Serializable
public value class AgentArtifactId(public val id: String)

/**
 * An artifact produced by an agent session.
 */
@Serializable
public data class AgentArtifact(
    @SerialName("id") public val id: AgentArtifactId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("name") public val name: String? = null,
)

/** The agent turn identifier. */
@JvmInline
@Serializable
public value class AgentTurnId(public val id: String)

/**
 * A turn within an agent session.
 */
@Serializable
public data class AgentTurn(
    @SerialName("id") public val id: AgentTurnId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("status") public val status: String? = null,
)

/**
 * An item produced within an agent session.
 */
@Serializable
public data class AgentItem(
    @SerialName("id") public val id: String? = null,
    @SerialName("type") public val type: String? = null,
    @SerialName("created_at") public val createdAt: Long? = null,
)

/**
 * An event emitted during an agent session.
 */
@Serializable
public data class AgentSessionEvent(
    @SerialName("id") public val id: String? = null,
    @SerialName("type") public val type: String? = null,
    @SerialName("created_at") public val createdAt: Long? = null,
)

/** The subagent identifier. */
@JvmInline
@Serializable
public value class AgentSubagentId(public val id: String)

/**
 * A subagent spawned within an agent session.
 */
@Serializable
public data class AgentSubagent(
    @SerialName("id") public val id: AgentSubagentId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("status") public val status: String? = null,
)

// endregion

// region Environment files and templates

/** The environment file identifier. */
@JvmInline
@Serializable
public value class EnvironmentFileId(public val id: String)

/**
 * A file available inside an agent environment.
 */
@Serializable
public data class EnvironmentFile(
    @SerialName("id") public val id: EnvironmentFileId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("path") public val path: String? = null,
)

/** The environment template identifier. */
@JvmInline
@Serializable
public value class EnvironmentTemplateId(public val id: String)

/**
 * A reusable environment template.
 */
@Serializable
public data class EnvironmentTemplate(
    @SerialName("id") public val id: EnvironmentTemplateId,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("name") public val name: String? = null,
)

/**
 * Creates a file in an agent environment.
 *
 * @property path the path the file is mounted at.
 * @property content the content of the file.
 */
@Serializable
public data class EnvironmentFileCreateRequest(
    @SerialName("path") public val path: String,
    @SerialName("content") public val content: String? = null,
)

/**
 * Creates an environment template.
 */
@Serializable
public data class EnvironmentTemplateCreateRequest(
    @SerialName("name") public val name: String,
)

/**
 * Updates an environment template.
 */
@Serializable
public data class EnvironmentTemplateUpdateRequest(
    @SerialName("name") public val name: String? = null,
)

// endregion
