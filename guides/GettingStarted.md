# Getting Started

For the current source integration and migration notes, see [KMP SDK 接入与使用指南](KmpIntegration.zh-CN.md).

Create an instance of `OpenAI` client:

```kotlin
val openAI = OpenAI(apiKey)
```

> ℹ️ OpenAI encourages using environment variables for the API
> key. [Read more](https://help.openai.com/en/articles/5112595-best-practices-for-api-key-safety).

Use your `OpenAI` instance to make API requests.

- [Responses](#responses)
  - [Create response](#create-response)
  - [Retrieve a response](#retrieve-a-response)
  - [Cancel a response](#cancel-a-response)
  - [Delete a response](#delete-a-response)
  - [List response input items](#list-response-input-items)
- [Models](#models)
  - [List models](#list-models)
  - [Retrieve a model](#retrieve-a-model)
- [Chat](#chat)
  - [Create chat completion](#create-chat-completion)
- [Images](#images)
  - [Create image](#create-image)
  - [Edit images](#edit-images)
  - [Create image variation](#create-image-variation)
- [Embeddings](#embeddings)
  - [Create embeddings](#create-embeddings)
- [Fine-tuning](#fine-tuning)
  - [Create fine-tuning job](#create-fine-tuning-job)
  - [List fine-tuning jobs](#list-fine-tuning-jobs)
  - [Retrieve fine-tuning job](#retrieve-fine-tuning-job)
  - [Cancel fine-tuning](#cancel-fine-tuning)
  - [List fine-tuning events](#list-fine-tuning-events)
- [Audio](#audio)
  - [Create speech](#create-speech)
  - [Create transcription](#create-transcription)
  - [Create translation](#create-translation)
- [Files](#files)
  - [List files](#list-files)
  - [Upload file](#upload-file)
  - [Delete file](#delete-file)
  - [Retrieve file](#retrieve-file)
  - [Retrieve file content](#retrieve-file-content)
- [Moderations](#moderations)
  - [Create moderation](#create-moderation)
- [Batch](#batch)
  - [Create batch](#create-batch)
  - [Retrieve batch](#retrieve-batch)
  - [List batches](#list-batches)
  - [Cancel batch](#cancel-batch)
- [Vector stores](#vector-stores)
  - [Create vector store](#create-vector-store)
  - [List vector stores](#list-vector-stores)
  - [Retrieve vector store](#retrieve-vector-store)
  - [Update vector store](#update-vector-store)
  - [Delete vector store](#delete-vector-store)
  - [Attach file to vector store](#attach-file-to-vector-store)
  - [Batch files into vector store](#batch-files-into-vector-store)
- [Hosts](#hosts)
  - [Azure](#azure)
  - [Other hosts](#other-hosts)

#### Beta

- [Assistants](#assistants)
  - [Create assistant](#create-assistant)
  - [Retrieve assistant](#retrieve-assistant)
  - [Modify assistant](#modify-assistant)
  - [Delete assistant](#delete-assistant)
  - [List assistants](#list-assistants)
- [Threads](#threads)
  - [Create thread](#create-thread)
  - [Retrieve thread](#retrieve-thread)
  - [Modify thread](#modify-thread)
  - [Delete thread](#delete-thread)
- [Messages](#messages)
  - [Create message](#create-message)
  - [Retrieve message](#retrieve-message)
  - [Modify message](#modify-message)
  - [List messages](#list-messages)
- [Runs](#runs)
  - [Create run](#create-run)
  - [Retrieve run](#retrieve-run)
  - [Modify run](#modify-run)
  - [List runs](#list-runs)
  - [Cancel run](#cancel-run)
  - [Create thread and run](#create-thread-and-run)
  - [Retrieve a run step](#retrieve-a-run-step)
  - [List run steps](#list-run-steps)
  - [Event streaming](#event-streaming)

#### Deprecated

- [Completions](#completions)
  - [Create completion](#create-completion-legacy)
- [Fine-tunes](#fine-tunes)
  - [Create fine-tune](#create-fine-tune)
  - [List fine-tunes](#list-fine-tunes)
  - [Retrieve fine-tune](#retrieve-fine-tune)
  - [Cancel fine-tune](#cancel-fine-tune)
  - [List fine-tune events](#list-fine-tune-events)
  - [Delete fine-tune model](#delete-fine-tune-model)
- [Edits](#edits)
  - [Create edits](#create-edits-deprecated)

## Responses

Create model responses with the Responses API.

### Create response

```kotlin
val response = openAI.response(
    request = ResponseRequest(
        model = ModelId("gpt-4.1"),
        input = ResponseInput("Write a haiku about Kotlin.")
    )
)

println(response.outputText)
```

### Stream a response

Streaming emits events as the response is generated. Collect the flow and switch on the event type;
each `ResponseStreamEvent` also exposes the raw payload through `json`/`raw`.

```kotlin
openAI.responseStream(
    request = ResponseRequest(
        model = ModelId("gpt-4.1"),
        input = ResponseInput("Write a haiku about Kotlin.")
    )
).collect { event ->
    when (event.type) {
        ResponseStreamEventType.RESPONSE_OUTPUT_TEXT_DELTA -> print(event.delta)
        ResponseStreamEventType.RESPONSE_COMPLETED -> println()
        else -> Unit
    }
}
```

### Retrieve a response

```kotlin
val responseId = ResponseId("resp_123")
val response = openAI.response(responseId)
```

### Cancel a response

```kotlin
val responseId = ResponseId("resp_123")
val cancelled = openAI.cancel(responseId)
```

### Delete a response

```kotlin
val responseId = ResponseId("resp_123")
val deleted = openAI.delete(responseId)
```

### List response input items

```kotlin
val responseId = ResponseId("resp_123")
val inputItems = openAI.responseInputItems(id = responseId, limit = 20)
```

## Conversations

Create and manage conversations, which persist message history that later responses can continue from.

### Create a conversation

```kotlin
val conversation = openAI.createConversation(
    request = ConversationCreateRequest(
        metadata = mapOf("topic" to "weather")
    )
)
```

### Retrieve a conversation

```kotlin
val conversation = openAI.conversation(ConversationId("conv_123"))
```

### Update a conversation

```kotlin
val conversation = openAI.updateConversation(
    id = ConversationId("conv_123"),
    request = ConversationUpdateRequest(metadata = mapOf("topic" to "sports"))
)
```

### Delete a conversation

```kotlin
val deleted = openAI.deleteConversation(ConversationId("conv_123"))
```

### Continue a conversation with a response

Pass the conversation to a response request: its items are prepended to `input`, and the response's
input and output items are added back to the conversation once it completes. Note that `conversation`
cannot be combined with `previousResponseId`.

```kotlin
val response = openAI.response(
    request = ResponseRequest(
        model = ModelId("gpt-4.1"),
        conversation = ConversationId("conv_123"),
        input = ResponseInput("What did I ask about earlier?")
    )
)

println(response.outputText)
```

## Evals

Create and manage evaluations. The data source config and testing criteria are passed through as
JSON, so newer grader and data source variants remain usable without a client release.

### Create an eval

```kotlin
val eval = openAI.createEval(
    request = EvalCreateRequest(
        name = "Sentiment accuracy",
        dataSourceConfig = buildJsonObject {
            put("type", "custom")
            putJsonObject("item_schema") {
                put("type", "object")
                putJsonArray("properties") {
                    addJsonObject {
                        put("name", "input")
                        put("type", "string")
                    }
                }
            }
        },
        metadata = mapOf("env" to "test")
    )
)
```

### Retrieve an eval

```kotlin
val eval = openAI.eval(EvalId("eval_123"))
```

### Update an eval

```kotlin
val eval = openAI.updateEval(
    id = EvalId("eval_123"),
    request = EvalUpdateRequest(name = "Renamed eval")
)
```

### List evals

```kotlin
val evals = openAI.evals(limit = 20)
```

### Delete an eval

```kotlin
val deleted = openAI.deleteEval(EvalId("eval_123"))
```

## Models

List and describe the various [models](https://platform.openai.com/docs/models) available in the API.
You can refer to the Models documentation to understand what models are available and the differences between them.

### List models

Lists the currently available models, and provides basic information about each one such as the owner and availability.

```kotlin
val models: List<Model> = openAI.models()
```

### Retrieve a model

Retrieves a model instance, providing basic information about the model such as the owner and permissioning.

```kotlin
val id = ModelId("gpt-4.1")
val model: Model = openAI.model(id)
```

## Chat

Given a chat conversation, the model will return a chat completion response.

### Create chat completion

Creates a completion for the chat message.

```kotlin
val request = ChatCompletionRequest(
    model = ModelId("gpt-4o-mini"),
    messages = listOf(
        ChatMessage(
            role = ChatRole.System,
            content = "You are a helpful assistant!"
        ),
        ChatMessage(
            role = ChatRole.User,
            content = "Hello!"
        )
    )
)

val completion: ChatCompletion = openAI.chatCompletion(request)

// Or stream as a Flow
val chunks: Flow<ChatCompletionChunk> = openAI.chatCompletions(request)
```

## Images

Given a prompt and/or an input image, the model will generate a new image.

### Create image

Creates an image given a prompt.

```kotlin
val images = openAI.imageURL( // or openAI.imageJSON
    creation = ImageCreation(
        prompt = "A cute baby sea otter",
        model = ModelId("dall-e-3"),
        n = 1,
        size = ImageSize.is1024x1024
    )
)
```

### Edit images

Creates an edited or extended image given an original image and a prompt.

```kotlin
val images = openAI.imageURL( // or openAI.imageJSON
    edit = ImageEdit(
        image = FileSource(path = Path("image.png")),
        model = ModelId("dall-e-2"),
        mask = FileSource(path = Path("mask.png")),
        prompt = "a sunlit indoor lounge area with a pool containing a flamingo",
        n = 1,
        size = ImageSize.is1024x1024
    )
)
```

### Create image variation

Creates a variation of a given image.

```kotlin
val images = openAI.imageURL( // or openAI.imageJSON
    variation = ImageVariation(
        image = FileSource(path = Path("image.png")),
        model = ModelId("dall-e-2"),
        n = 1,
        size = ImageSize.is1024x1024
    )
)
```

## Videos

Generate videos with the Sora models. Generation is asynchronous: create a request, poll until the
video reaches a terminal status, then download the content.

### Create a video

```kotlin
val video = openAI.createVideo(
    request = VideoCreateRequest(
        prompt = "A calico cat playing a piano on stage",
        model = VideoModel("sora-2"),
        seconds = VideoSeconds("8"),
        size = VideoSize("1280x720")
    )
)
```

### Retrieve a video

Poll until `status` reaches a terminal state, and read `progress` for the completion percentage.

```kotlin
val video = openAI.video(VideoId("video_123"))
println(video.status)
```

### List videos

```kotlin
val videos = openAI.videos(limit = 20)
```

### Download video content

```kotlin
val content = openAI.downloadVideoContent(VideoId("video_123"))
```

### Delete a video

```kotlin
val deleted = openAI.deleteVideo(VideoId("video_123"))
```

## Embeddings

Get a vector representation of a given input that can be easily consumed by machine learning models and algorithms.

### Create embeddings

Creates an embedding vector representing the input text.

```kotlin
val embeddings = openAI.embeddings(
    request = EmbeddingRequest(
        model = ModelId("text-embedding-3-small"),
        input = listOf("The food was delicious and the waiter was very friendly.")
    )
)
```

## Fine-tuning

Manage fine-tuning jobs to tailor a model to your specific training data.

### Create fine-tuning job

Creates a job that fine-tunes a specified model from a given dataset.

#### No Hyperparameters

```kotlin
val request = FineTuningRequest(
    trainingFile = FileId("file-abc123"),
    model = ModelId("gpt-3.5-turbo"),
)
val fineTuningJob = openAI.fineTuningJob(request)
```

#### Hyperparameters

```kotlin
val request = FineTuningRequest(
    trainingFile = FileId("file-abc123"),
    model = ModelId("gpt-3.5-turbo"),
    hyperparameters = Hyperparameters(nEpochs = 2),
)
val fineTuningJob = openAI.fineTuningJob(request)
```

#### Validation File

```kotlin
val request = FineTuningRequest(
    trainingFile = FileId("file-abc123"),
    validationFile = FileId("file-def345"),
    model = ModelId("gpt-3.5-turbo"),
)
val fineTuningJob = openAI.fineTuningJob(request)
```

### List fine-tuning jobs

List your organization's fine-tuning jobs.

```kotlin
val fineTuningJobs = openAI.fineTuningJobs(limit = 20)
```

### Retrieve fine-tuning job

Get info about a fine-tuning job.

```kotlin
val id = FineTuningId("ftjob-abc123")
val fineTuningJob = openAI.fineTuningJob(id)
```

### Cancel fine-tuning

Immediately cancel a fine-tuning job.

```kotlin
val id = FineTuningId("ftjob-abc123")
openAI.cancel(id)
```

### List fine-tuning events

Get status updates for a fine-tuning job.

```kotlin
val id = FineTuningId("ftjob-abc123")
val fineTuningEvents = openAI.fineTuningEvents(id)
```

## Audio

Learn how to turn audio into text.

### Create speech

Generates audio from the input text.

```kotlin
val rawAudio = openAI.speech(
    request = SpeechRequest(
        model = ModelId("tts-1"),
        input = "The quick brown fox jumped over the lazy dog.",
        voice = Voice.Alloy,
    )
)
```

### Create transcription

Transcribes audio into the input language.

```kotlin
val request = TranscriptionRequest(
    audio = FileSource(path = Path("micro-machines.wav")),
    model = ModelId("whisper-1"),
)
val transcription = openAI.transcription(request)
```

### Create translation

Translates audio into English.

```kotlin
val request = TranslationRequest(
    audio = FileSource(path = Path("multilingual.wav")),
    model = ModelId("whisper-1"),
)
val translation = openAI.translation(request)
```

## Files

Files are used to upload documents that can be used across features.

### List files

Returns a list of files that belong to the user's organization.

```kotlin
val files = openAI.files()
```

### Upload file

Upload a file that contains document(s) to be used across various endpoints/features.

```kotlin
val file = openAI.file(
    request = FileUpload(
        file = FileSource(path = Path("training.jsonl")),
        purpose = Purpose("fine-tune")
    )
)
```

### Delete file

Delete a file.

```kotlin
openAI.delete(file.id)
```

### Retrieve file

Returns information about a specific file.

```kotlin
val file = openAI.file(FileId("file-abc123"))
```

### Retrieve file content

Returns the contents of the specified file.

```kotlin
val bytes = openAI.download(FileId("file-abc123"))
```

## Moderations

Given an input text, outputs if the model classifies it as violating OpenAI's content policy.

### Create moderation

Classifies if text violates OpenAI's content policy.

```kotlin
val moderation = openAI.moderations(
    request = ModerationRequest(
        model = ModerationModel.Latest,
        input = "I want to kill them."
    )
)
```

## Batch

Create and manage asynchronous batches.

### Create batch

```kotlin
val request = BatchRequest(
    inputFileId = FileId("file-abc123"),
    endpoint = Endpoint.Completions,
    completionWindow = CompletionWindow.TwentyFourHours
)

val batch = openAI.batch(request)
```

### Retrieve batch

```kotlin
val batch = openAI.batch(BatchId("batch_abc123"))
```

### List batches

```kotlin
val batches = openAI.batches(limit = 20)
```

### Cancel batch

```kotlin
val cancelled = openAI.cancel(BatchId("batch_abc123"))
```

## Vector Stores

Store and index files for `file_search` use cases.

### Create vector store

```kotlin
val vectorStore = openAI.createVectorStore(
    request = VectorStoreRequest(name = "Support FAQ")
)
```

### List vector stores

```kotlin
val vectorStores = openAI.vectorStores(limit = 20)
```

### Retrieve vector store

```kotlin
val vectorStore = openAI.vectorStore(VectorStoreId("vs_abc123"))
```

### Update vector store

```kotlin
val updated = openAI.updateVectorStore(
    id = VectorStoreId("vs_abc123"),
    request = VectorStoreRequest(name = "Support FAQ v2")
)
```

### Delete vector store

```kotlin
val deleted = openAI.delete(VectorStoreId("vs_abc123"))
```

### Attach file to vector store

```kotlin
val vectorStoreFile = openAI.createVectorStoreFile(
    id = VectorStoreId("vs_abc123"),
    request = VectorStoreFileRequest(fileId = FileId("file-abc123"))
)

val files = openAI.vectorStoreFiles(id = VectorStoreId("vs_abc123"))
val removed = openAI.delete(id = VectorStoreId("vs_abc123"), fileId = FileId("file-abc123"))
```

### Batch files into vector store

```kotlin
val batch = openAI.createVectorStoreFilesBatch(
    id = VectorStoreId("vs_abc123"),
    request = FileBatchRequest(fileIds = listOf(FileId("file-abc123"), FileId("file-def456")))
)

val retrieved = openAI.vectorStoreFileBatch(
    vectorStoreId = VectorStoreId("vs_abc123"),
    batchId = batch.id
)

val batchFiles = openAI.vectorStoreFilesBatches(
    vectorStoreId = VectorStoreId("vs_abc123"),
    batchId = batch.id
)

val cancelled = openAI.cancel(
    vectorStoreId = VectorStoreId("vs_abc123"),
    batchId = batch.id
)
```

## Uploads

Upload files larger than the regular file limit in parts: create an upload session, upload each
part, then complete the session with the ordered part identifiers.

### Create an upload

```kotlin
val upload = openAI.createUpload(
    request = UploadCreateRequest(
        filename = "data.jsonl",
        bytes = 5_000_000_000,
        mimeType = "application/jsonl",
        purpose = "batch"
    )
)
```

### Upload a part

```kotlin
val part = openAI.createUploadPart(
    uploadId = upload.id,
    data = FileSource(path = Path("part-0.bin"))
)
```

### Complete an upload

```kotlin
val completed = openAI.completeUpload(
    uploadId = upload.id,
    partIds = listOf(part.id)
)
```

### Cancel an upload

```kotlin
val cancelled = openAI.cancelUpload(upload.id)
```

## Webhooks

Manage the endpoints that receive event notifications.

### Create a webhook endpoint

The signing secret is only returned here and when the secret is rotated.

```kotlin
val endpoint = openAI.createWebhookEndpoint(
    request = WebhookEndpointCreateRequest(
        eventTypes = listOf("response.completed", "batch.completed"),
        name = "prod",
        url = "https://example.com/hooks/openai"
    )
)

println(endpoint.signingSecret)
```

### List event types

```kotlin
val eventTypes = openAI.webhookEventTypes()
```

### Rotate a signing secret

```kotlin
val rotated = openAI.rotateWebhookSecret(WebhookEndpointId("wh_1"))
```

### Delete a webhook endpoint

```kotlin
val deleted = openAI.deleteWebhookEndpoint(WebhookEndpointId("wh_1"))
```

### Verify an incoming delivery

Verification is a pure function, so it works anywhere — including on a server that never holds an
API key. Pass `nowSeconds` to also reject replayed deliveries.

```kotlin
val valid = verifySignature(
    webhookId = headers["webhook-id"],
    webhookTimestamp = headers["webhook-timestamp"],
    body = rawBody,
    signatureHeader = headers["webhook-signature"],
    secret = signingSecret,
) && isWithinTolerance(headers["webhook-timestamp"], DEFAULT_TOLERANCE_SECONDS, nowSeconds)

if (valid) {
    val event = unwrapWebhookEvent(rawBody)
    when (event.type) {
        "response.completed" -> println(event.data)
        else -> Unit
    }
}
```

## Containers

Containers run code for tool calls, and hold the files that code operates on.

### Create a container

```kotlin
val container = openAI.createContainer(ContainerCreateRequest(name = "sandbox"))
```

### Upload a file

```kotlin
val file = openAI.createContainerFile(
    containerId = container.id,
    request = ContainerFileCreateRequest(
        file = FileSource(path = Path("data.csv")),
        path = "/data.csv"
    )
)
```

### Download a file

```kotlin
val content = openAI.containerFileContent(container.id, file.id)
```

## Skills

### List skills

```kotlin
val skills = openAI.skills(limit = 20)
```

### Retrieve a skill

```kotlin
val skill = openAI.skill(SkillId("skill_1"))
```

## Safety

### Retrieve a safety case

```kotlin
val case = openAI.safetyCase(SafetyCaseId("case_1"))
```

### Retrieve a safety alert

```kotlin
val alert = openAI.safetyAlert(SafetyAlertId("alert_1"))
```

## Live

Live sessions are realtime conversations established over a WebRTC transport.

### Create a session

```kotlin
val created = openAI.createLiveSession(LiveSessionCreateRequest(
    session = buildJsonObject { put("model", liveModel) },
    transport = LiveTransport(type = "webrtc", sdp = offerSdp)
))
println(created.transport?.sdp)
```

### Hang up a session

```kotlin
openAI.hangupLiveSession(LiveSessionId("sess_1"))
```

## Realtime

Realtime conversations run over a WebSocket connection opened with a short-lived client secret.

### Create a client secret and connect

```kotlin
val secret = openAI.createRealtimeClientSecret(RealtimeClientSecretRequest(
    session = buildJsonObject { put("type", "realtime"); put("model", realtimeModel) }
))
val connection = openAI.connectRealtime(secret.value ?: error("missing secret"))

connection.use {
    // Client events always carry their `type`.
    connection.send(RealtimeEvent.of("session.update", buildJsonObject {
        put("session", buildJsonObject {
            put("type", "realtime")
            put("instructions", "You are a helpful assistant.")
        })
    }))

    connection.events.collect { event ->
        when (event.type) {
            RealtimeEventType.RESPONSE_OUTPUT_AUDIO_DELTA -> handleAudio(event.delta)
            RealtimeEventType.RESPONSE_DONE -> println("turn complete")
            else -> Unit
        }
    }
}
```

For new integrations, use the GA `client_secrets` / `calls` endpoints shown here. The legacy
`createRealtimeSession` and `createRealtimeTranscriptionSession` methods retain the older API shape.

### Accept an incoming call

```kotlin
openAI.acceptRealtimeCall(
    RealtimeCallId("call_1"),
    request = buildJsonObject { put("type", "realtime"); put("model", realtimeModel) }
)
```

## Admin

Manage organization projects, users, invites and admin API keys. These endpoints require an admin
key rather than a regular project key.

### List projects

```kotlin
val projects = openAI.projects(limit = 20)
```

### Create a project

```kotlin
val project = openAI.createProject(AdminProjectCreateRequest(name = "staging"))
```

### Invite a user

```kotlin
val invite = openAI.createInvite(
    AdminInviteCreateRequest(email = "dev@example.com", role = "reader")
)
```

### List admin API keys

```kotlin
val keys = openAI.adminApiKeys()
```

### Project-scoped resources

Most organization resources also exist per project. Pass the project identifier to scope them.

```kotlin
val projectId = AdminProjectId("proj_1")

val keys = openAI.projectApiKeys(projectId)
val serviceAccount = openAI.createProjectServiceAccount(
    projectId,
    ProjectServiceAccountCreateRequest(name = "ci")
)
val role = openAI.createProjectRole(
    projectId,
    ProjectRoleCreateRequest(roleName = "reader", permissions = listOf("api.read"))
)
```

### Organization resources

```kotlin
val group = openAI.createAdminGroup(AdminGroupCreateRequest(name = "engineering"))
val role = openAI.createAdminRole(
    AdminRoleCreateRequest(roleName = "reader", permissions = listOf("api.read"))
)

// Assign the role to the group.
openAI.createGroupRole(group.id, AdminGroupRoleRequest(role.id))

// Usage is addressed by category.
val usage = openAI.usageCompletions(AdminUsageQuery(startTime = 1_700_000_000))
for (bucket in usage.data) {
    for (result in bucket.results) println(result.inputTokens)
}
```

## Agents

Agents and the sessions they run in. These endpoints are in beta.

### Create an agent

```kotlin
val agent = openAI.createAgent(
    AgentCreateRequest(
        model = "gpt-4.1",
        name = "researcher",
        instructions = "You research topics thoroughly."
    )
)
```

### Start a session

```kotlin
val session = openAI.createAgentSession(
    AgentSessionCreateRequest(
        environment = buildJsonObject { put("type", "none") },
        agentId = agent.id
    )
)
```

### Clean up

```kotlin
openAI.deleteAgentSession(session.id)
openAI.deleteAgent(agent.id)
```

## Vaults

Vaults hold credentials that agent sessions can use.

```kotlin
val vault = openAI.createVault(VaultCreateRequest(name = "prod"))

val vaults = openAI.vaults(limit = 20)

openAI.deleteVault(vault.id)
```

## ChatKit

```kotlin
val session = openAI.createChatKitSession(ChatKitSessionCreateRequest(
    user = "user_1",
    workflow = ChatKitWorkflow(id = "wf_1")
))

val threads = openAI.chatKitThreads(limit = 20)
val thread = openAI.chatKitThread(ChatKitThreadId("thread_1"))

openAI.cancelChatKitSession(session.id)
```

## Hosts

This library supports custom OpenAI-compatible hosts. The default host is `https://api.openai.com/v1/`.

### Azure

To connect to an Azure-hosted instance, use `OpenAIHost.azure`:

```kotlin
val host = OpenAIHost.azure(
    resourceName = "your-resource-name",
    deploymentId = "your-deployment-id",
    apiVersion = "2024-10-21",
)

val config = OpenAIConfig(
    host = host,
    token = "your-api-token",
)

val openAI = OpenAI(config)
```

### Other hosts

You can connect to any compatible host by constructing your own `OpenAIHost` instance.

```kotlin
val host = OpenAIHost(
    baseUrl = "http://localhost:8080/v1/",
)

val config = OpenAIConfig(
    host = host,
    token = "your-api-token",
)

val openAI = OpenAI(config)
```

---

## Assistants

Build assistants that can call models and use tools to perform tasks.

### Create assistant

Create an assistant with a model and instructions.

```kotlin
val assistant = openAI.assistant(
    request = AssistantRequest(
        name = "Math Tutor",
        tools = listOf(AssistantTool.CodeInterpreter),
        model = ModelId("gpt-4o-mini")
    )
)
```

### Retrieve assistant

Retrieves an assistant.

```kotlin
val assistant = openAI.assistant(id = AssistantId("asst_abc123"))
```

### Modify assistant

Modifies an assistant.

```kotlin
val assistant = openAI.assistant(
    id = AssistantId("asst_abc123"),
    request = AssistantRequest(
        instructions = "You are an HR bot. Use file search to answer policy questions.",
        tools = listOf(AssistantTool.FileSearch),
        toolResources = ToolResources(
            fileSearch = FileSearchResources(vectorStoreIds = listOf(VectorStoreId("vs_abc123")))
        ),
        model = ModelId("gpt-4o-mini"),
    )
)
```

### Delete assistant

Delete an assistant.

```kotlin
openAI.delete(id = AssistantId("asst_abc123"))
```

### List assistants

Returns a list of assistants.

```kotlin
val assistants = openAI.assistants()
```

## Threads

Create threads that assistants can interact with.

### Create thread

Create a thread with optional initial messages.

```kotlin
val thread = openAI.thread()
```

### Retrieve thread

Retrieves a thread.

```kotlin
val thread = openAI.thread(id = ThreadId("thread_abc123"))
```

### Modify thread

Modifies a thread.

```kotlin
val thread = openAI.thread(
    id = ThreadId("thread_abc123"),
    metadata = mapOf(
        "modified" to "true",
        "user" to "abc123"
    )
)
```

### Delete thread

Delete a thread.

```kotlin
openAI.delete(id = ThreadId("thread_abc123"))
```

## Messages

Create messages within threads.

### Create message

Create a message.

```kotlin
val message = openAI.message(
    threadId = ThreadId("thread_abc123"),
    request = MessageRequest(
        role = Role.User,
        content = "How does AI work? Explain it in simple terms.",
    )
)
```

### Retrieve message

Retrieve a message.

```kotlin
val message = openAI.message(
    threadId = ThreadId("thread_abc123"),
    messageId = MessageId("msg_abc123")
)
```

### Modify message

Modifies a message.

```kotlin
val message = openAI.message(
    threadId = ThreadId("thread_abc123"),
    messageId = MessageId("msg_abc123"),
    metadata = mapOf(
        "modified" to "true",
        "user" to "abc123"
    )
)
```

### List messages

Returns a list of messages for a given thread.

```kotlin
val messages = openAI.messages(threadId = ThreadId("thread_abc123"))
```

## Runs

Represents an execution run on a thread.

### Create run

Create a run.

```kotlin
val run = openAI.createRun(
    threadId = ThreadId("thread_abc123"),
    request = RunRequest(assistantId = AssistantId("asst_abc123")),
)
```

### Retrieve run

Retrieves a run.

```kotlin
val run = openAI.getRun(
    threadId = ThreadId("thread_abc123"),
    runId = RunId("run_abc123")
)
```

### Modify run

Modifies a run.

```kotlin
val run = openAI.updateRun(
    threadId = ThreadId("thread_abc123"),
    runId = RunId("run_abc123"),
    metadata = mapOf("user_id" to "user_abc123")
)
```

### List runs

Returns a list of runs belonging to a thread.

```kotlin
val runs = openAI.runs(threadId = ThreadId("thread_abc123"))
```

### Cancel run

Cancel a run that is `Status.InProgress`.

```kotlin
val cancelled = openAI.cancel(
    threadId = ThreadId("thread_abc123"),
    runId = RunId("run_abc123")
)
```

### Create thread and run

Create a thread and run it in one request.

```kotlin
val run = openAI.createThreadRun(
    request = ThreadRunRequest(
        assistantId = AssistantId("asst_abc123"),
        thread = ThreadRequest(
            messages = listOf(
                ThreadMessage(
                    role = Role.User,
                    content = "Explain deep learning to a 5 year old."
                )
            )
        ),
    )
)
```

### Retrieve a run step

Retrieves a run step.

```kotlin
val runStep = openAI.runStep(
    threadId = ThreadId("thread_abc123"),
    runId = RunId("run_abc123"),
    stepId = RunStepId("step_abc123")
)
```

### List run steps

Returns a list of run steps belonging to a run.

```kotlin
val runSteps = openAI.runSteps(
    threadId = ThreadId("thread_abc123"),
    runId = RunId("run_abc123")
)
```

### Event streaming

Create a thread+run and process streaming events.

```kotlin
openAI
    .createStreamingThreadRun(
        request = ThreadRunRequest(
            assistantId = AssistantId("asst_abc123"),
            thread = ThreadRequest(
                messages = listOf(
                    ThreadMessage(
                        role = Role.User,
                        content = "Explain deep learning to a 5 year old."
                    )
                )
            )
        )
    )
    .onEach { event -> println(event.type) }
    .collect()
```

Get typed data from an `AssistantStreamEvent`:

```kotlin
when (assistantStreamEvent.type) {
    AssistantStreamEventType.THREAD_CREATED -> {
        val thread = assistantStreamEvent.getData<Thread>()
    }
    AssistantStreamEventType.THREAD_MESSAGE_CREATED -> {
        val message = assistantStreamEvent.getData<Message>()
    }
    AssistantStreamEventType.UNKNOWN -> {
        val raw = assistantStreamEvent.data
    }
    else -> Unit
}
```

If a new event type is released before the library is updated, you can deserialize custom payloads with your own serializer:

```kotlin
if (assistantStreamEvent.type == AssistantStreamEventType.UNKNOWN) {
    val data = assistantStreamEvent.getData(myCustomSerializer)
}
```

---

## Completions

Given a prompt, the model will return one or more predicted completions, and can also return token probabilities.

### Create completion `legacy`

```kotlin
val request = CompletionRequest(
    model = ModelId("text-ada-001"),
    prompt = "Somebody once told me the world is gonna roll me",
    echo = true
)

val completion: TextCompletion = openAI.completion(request)

// Or stream as Flow
val completions: Flow<TextCompletion> = openAI.completions(request)
```

---

## Fine-tunes

Legacy fine-tunes API.

### Create fine-tune

```kotlin
val fineTune = openAI.fineTune(
    request = FineTuneRequest(
        trainingFile = FileId("file-abc123"),
        model = ModelId("ada")
    )
)
```

### List fine-tunes

```kotlin
val fineTunes = openAI.fineTunes()
```

### Retrieve fine-tune

```kotlin
val fineTune = openAI.fineTune(FineTuneId("ft-abc123"))
```

### Cancel fine-tune

```kotlin
val fineTune = openAI.cancel(FineTuneId("ft-abc123"))
```

### List fine-tune events

```kotlin
val events: List<FineTuneEvent> = openAI.fineTuneEvents(FineTuneId("ft-abc123"))

// Or stream as Flow
val eventsFlow: Flow<FineTuneEvent> = openAI.fineTuneEventsFlow(FineTuneId("ft-abc123"))
```

### Delete fine-tune model

```kotlin
val deleted = openAI.delete(ModelId("ft:gpt-3.5-turbo:org:custom:abc123"))
```

## Edits

Given a prompt and an instruction, the model returns an edited version of the prompt.

### Create edits `Deprecated`

```kotlin
val edit = openAI.edit(
    request = EditsRequest(
        model = ModelId("text-davinci-edit-001"),
        input = "What day of the wek is it?",
        instruction = "Fix the spelling mistakes"
    )
)
```
