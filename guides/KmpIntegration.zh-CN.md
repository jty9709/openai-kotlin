# KMP SDK 接入与使用指南

本文对应本仓库当前源码，协议修复对照 `openai-java` 4.65.0。SDK 使用 `com.aallam.openai` 包、Kotlin 协程和 Ktor，在 `commonMain` 中编写共享业务代码，在各平台选择 HTTP 引擎。

**本次同步改动尚未发布。** 仓库的 `VERSION_NAME` 仍是 `4.1.0`，直接从 Maven Central 引入同名版本不会自动获得这些改动。开发阶段按下文接入本地源码；团队发布后，再替换成实际发布的版本号。

## 1. 接入当前源码

本仓库版本目录声明 Kotlin `2.0.20`、Ktor `3.0.0`、kotlinx.serialization `1.7.3`、coroutines `1.8.1`，Gradle Wrapper 为 `8.10.2`。本次本地构建使用 JDK 17。Ktor 等传递依赖可能提升最终解析版本，排查冲突时以 Gradle dependencyInsight 为准。先保持 Ktor 各组件版本一致，再单独验证依赖升级。

在使用方的 `settings.gradle.kts` 添加组合构建，路径指向本仓库：

```kotlin
includeBuild("../openai-kotlin") {
    dependencySubstitution {
        substitute(module("com.aallam.openai:openai-client"))
            .using(project(":openai-client"))
        substitute(module("com.aallam.openai:openai-core"))
            .using(project(":openai-core"))
    }
}
```

使用方需在仓库配置中启用 `mavenCentral()` 和 `google()`。以下为现有 KMP 模块的依赖配置示例；只保留项目实际启用的 target/source set：

```kotlin
kotlin {
    jvm()
    iosArm64()
    iosSimulatorArm64()
    js(IR) { nodejs() }
    @OptIn(org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl::class)
    wasmJs { nodejs() }

    sourceSets {
        commonMain.dependencies {
            // 上面的 dependencySubstitution 会把此依赖替换成本地源码。
            implementation("com.aallam.openai:openai-client:4.1.0")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
        }
        jvmMain.dependencies {
            implementation("io.ktor:ktor-client-okhttp:3.0.0")
        }
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:3.0.0")
        }
        jsMain.dependencies {
            implementation("io.ktor:ktor-client-js:3.0.0")
        }
        wasmJsMain.dependencies {
            implementation("io.ktor:ktor-client-js:3.0.0")
        }
    }
}
```

`iosMain` 依赖 Kotlin 默认层级模板；已有自定义 source set 层级的项目，应把 Darwin 引擎放到相应的 Apple source set。仅调用 SDK 无需额外启用 serialization 编译插件；给自己的数据类添加 `@Serializable` 时再启用。

| 使用方平台 | 引擎依赖 | 接入说明 |
| --- | --- | --- |
| JVM / Android | `ktor-client-okhttp:3.0.0` | Android 项目放到 `androidMain`；普通 Android 模块放到 `dependencies` |
| iOS / macOS | `ktor-client-darwin:3.0.0` | Native 编译需要对应宿主和工具链 |
| JS / Wasm JS | `ktor-client-js:3.0.0` | 选择 Node 或浏览器运行环境；浏览器还受 CORS 限制 |
| Linux / Windows Native | `ktor-client-curl:3.0.0` | 对应原生环境需要可用的 curl 依赖；此处没有验证其 WebSocket 能力 |

引擎能力参见 [Ktor 官方说明](https://ktor.io/docs/client-engines.html)；本文依赖版本按本仓库的 `3.0.0` 固定。

Android 应用还需在清单中声明 `android.permission.INTERNET`。本 SDK 仓库本身没有 Android Gradle 模块，Android 使用方消费其 JVM 产物。

## 2. 创建并复用客户端

下面的函数放在 `commonMain`。HTTP 引擎可以由平台依赖自动选择，也可以通过 `OpenAIConfig.engine` 显式传入。

<!-- compile: common -->
```kotlin
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import kotlin.time.Duration.Companion.seconds

fun createOpenAI(token: String, baseUrl: String = "https://api.openai.com/v1/"): OpenAI =
    OpenAI(OpenAIConfig(
        token = token,
        host = OpenAIHost(baseUrl = baseUrl),
        timeout = Timeout(connect = 30.seconds, socket = 120.seconds),
        logging = LoggingConfig(logLevel = LogLevel.None),
    ))
```

一个客户端可供多个协程复用；在应用服务或业务作用域结束后调用 `close()`。有路径前缀的 `baseUrl` 以 `/` 结尾。自定义网关需要兼容对应的 OpenAI 路径、鉴权和响应格式，不能直接填写普通聊天网页地址。

服务端从环境变量或密钥服务注入 token。移动端和浏览器使用自己的后端代理普通模型调用，避免把长期 API key 打包进应用；Realtime 可由后端签发临时 client secret。Admin key 和 Webhook signing secret 放在服务端。

## 3. 一次完整的 Responses 调用

`response()` 是挂起函数，需要在协程中调用。`model` 使用账户可访问的模型 ID，由业务配置传入。

<!-- compile: common -->
```kotlin
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.ResponseInput
import com.aallam.openai.api.response.ResponseRequest

suspend fun ask(openAI: OpenAI, model: String, prompt: String): String {
    val response = openAI.response(ResponseRequest(
        model = ModelId(model),
        input = ResponseInput(prompt),
        instructions = "请用中文回答。",
    ))
    check(response.status != "failed") { response.error?.message ?: "响应失败" }
    return response.output
        .filter { it.type == "message" }
        .flatMap { it.content.orEmpty() }
        .filter { it.type == "output_text" }
        .joinToString("") { it.text.orEmpty() }
}
```

文本位于 `output[].content[]`；不要假设原始响应必然存在顶层 `output_text`。拒绝、工具调用和非文本输出应分别处理。需要识别截断时，检查 `status == "incomplete"` 和 `incompleteDetails`。

在 JVM 应用中调用上述共享函数：

<!-- compile: jvm -->
```kotlin
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val token = requireNotNull(System.getenv("OPENAI_API_KEY"))
    val model = requireNotNull(System.getenv("OPENAI_MODEL"))
    val openAI = createOpenAI(token)
    try {
        println(ask(openAI, model, "解释一下 Kotlin 协程。"))
    } finally {
        openAI.close()
    }
}
```

Android 在 `viewModelScope` 等生命周期作用域调用挂起函数，不要在主线程使用 `runBlocking`；iOS 由调用层桥接协程结果或 Flow。

## 4. Responses 流式输出

`responseStream()` 返回冷 `Flow`：收集时才发送请求，再次收集会再次请求。它自动设置 `stream=true`。普通 `response()` 不要传 `stream=true`。

<!-- compile: common -->
```kotlin
import com.aallam.openai.api.response.ResponseStreamEventType
import kotlinx.coroutines.flow.collect

suspend fun streamAnswer(
    openAI: OpenAI,
    model: String,
    prompt: String,
    onText: (String) -> Unit,
    onResponseId: (String) -> Unit,
) {
    openAI.responseStream(ResponseRequest(
        model = ModelId(model),
        input = ResponseInput(prompt),
    )).collect { event ->
        when (event.type) {
            ResponseStreamEventType.RESPONSE_OUTPUT_TEXT_DELTA ->
                event.delta?.let(onText)
            ResponseStreamEventType.RESPONSE_CREATED ->
                event.responseId?.let(onResponseId)
            ResponseStreamEventType.ERROR,
            ResponseStreamEventType.RESPONSE_FAILED ->
                error(event.message ?: "流式响应失败：${event.raw}")
            ResponseStreamEventType.RESPONSE_INCOMPLETE ->
                error("响应未完整生成，请检查 response.incomplete_details")
            else -> Unit
        }
    }
}
```

取消收集 Flow 的协程会结束本地流读取。未知事件仍保存在 `event.json` 中。`responseId` 从生命周期事件的 `response.id` 读取，`id` 从输出项事件的 `item.id` 读取；不是每个事件都包含这些字段。流开始后的错误可能通过事件返回，不能只捕获 HTTP 异常。已经收到部分输出后，不要无条件重放整次请求。[官方流式错误处理说明](https://developers.openai.com/api/docs/guides/rate-limits#update-existing-error-handlers)

## 5. 多轮对话

可以传 `previousResponseId` 继续上一条响应，也可以创建 Conversation 后传入 `conversation`。一次请求不要同时设置这两个字段。

<!-- compile: common -->
```kotlin
import com.aallam.openai.api.conversation.ConversationId

suspend fun startConversation(openAI: OpenAI): ConversationId =
    openAI.createConversation().id

suspend fun sendInConversation(openAI: OpenAI, model: String, id: ConversationId, text: String) =
    openAI.response(ResponseRequest(
        model = ModelId(model),
        conversation = id,
        input = ResponseInput(text),
    ))

suspend fun continueResponse(openAI: OpenAI, model: String, prompt: String) {
    val first = openAI.response(ResponseRequest(model = ModelId(model), input = ResponseInput(prompt)))
    openAI.response(ResponseRequest(
        model = ModelId(model),
        previousResponseId = first.id,
        input = ResponseInput("请举一个例子。"),
    ))
}
```

当前有 Conversation 创建、读取、更新、删除接口；尚未提供 conversation items 子资源的完整操作。`responseInputItems()` 查询的是某个 Response 的输入项。

## 6. 文件上传与分页

`FileSource` 接收 `kotlinx.io.RawSource`。共享代码可使用内存数据；JVM/Native 也可使用 `FileSource(Path(...))`。上传会消费并关闭 source，需要重用时重新创建。

<!-- compile: common -->
```kotlin
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.file.FileUpload
import com.aallam.openai.api.file.Purpose
import kotlinx.io.Buffer

suspend fun uploadDocument(openAI: OpenAI, bytes: ByteArray) =
    openAI.file(FileUpload(
        file = FileSource(name = "document.txt", source = Buffer().apply { write(bytes) }),
        purpose = Purpose("user_data"),
    ))
```

分页接口不会自动获取下一页。例如 `videos(limit, after, order)` 返回 `PaginatedList`，检查 `hasMore`，用 `lastId` 作为下一次 `after`。组织 Usage 使用另一种分页方式：`hasMore` 与 `nextPage`，下一页放到 `AdminUsageQuery.page`。

## 7. Realtime：会话配置、SDP 和通话控制

使用 GA 的 `client_secrets` / `calls` 接口。`createRealtimeSession()` 和 `createRealtimeTranscriptionSession()` 保留了旧版接口形状，新接入按下面的 GA 流程使用。[官方 Realtime 接入与迁移说明](https://developers.openai.com/api/docs/guides/realtime)

<!-- compile: common -->
```kotlin
import com.aallam.openai.api.realtime.RealtimeCallId
import com.aallam.openai.api.realtime.RealtimeCallRequest
import com.aallam.openai.api.realtime.RealtimeClientSecretRequest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

fun realtimeConfig(model: String) = buildJsonObject {
    put("type", "realtime")
    put("model", model)
    put("instructions", "请用中文交流。")
}

suspend fun issueRealtimeSecret(openAI: OpenAI, model: String): String =
    requireNotNull(openAI.createRealtimeClientSecret(
        RealtimeClientSecretRequest(session = realtimeConfig(model))
    ).value)

suspend fun createWebRtcCall(openAI: OpenAI, model: String, offerSdp: String) =
    openAI.createRealtimeCall(RealtimeCallRequest(
        sdp = offerSdp,
        session = realtimeConfig(model),
    ))

suspend fun acceptIncomingCall(openAI: OpenAI, model: String, callId: String) {
    openAI.acceptRealtimeCall(RealtimeCallId(callId), request = realtimeConfig(model))
}
```

`createWebRtcCall()` 返回 `RealtimeCall`：把 `sdp` 设置为 PeerConnection 的远端 answer；`location` 保留服务器响应头，`id` 在 Location 存在时解析得到，其他响应头在 `headers`。录音、播放、PeerConnection 和媒体轨道由应用的 WebRTC 层实现。

SDK 自动以 multipart 发送 SDP/session，不需要手动拼 multipart。`acceptRealtimeCall()` 的配置直接作为 JSON body，**不再额外包一层 `session`**。accept/reject/hangup/refer 成功返回 `Unit`。

支持带 Authorization 请求头的 WebSocket 引擎上，可调用 `connectRealtime(clientSecret, model)`；返回连接只收集一次 `events`，结束后调用 `close()`。事件类型是 `RealtimeEventType` 枚举；需要字符串时使用 `rawType`。浏览器原生 WebSocket 不支持任意握手请求头，本实现的该连接方式未验证浏览器兼容性，浏览器接入使用 WebRTC 流程。

Live 的 `createLiveSession()` 和 `forkLiveSession()` 均返回包含 `session`、`transport` 的 `LiveSessionCreated`；控制操作同样返回 `Unit`。创建时提供实际 `session` 配置和 WebRTC `transport`，fork 时提供新客户端的 SDP offer。

## 8. Webhook：先校验，再解析

验证传入的原始 HTTP body，不要先解析后重新序列化。`verifySignature()` 仅校验签名，需与时间容差检查组合使用。`unwrapWebhookEvent()` 仅负责解析 JSON。

<!-- compile: common -->
```kotlin
import com.aallam.openai.api.webhook.DEFAULT_TOLERANCE_SECONDS
import com.aallam.openai.api.webhook.WebhookEvent
import com.aallam.openai.api.webhook.isWithinTolerance
import com.aallam.openai.api.webhook.unwrapWebhookEvent
import com.aallam.openai.api.webhook.verifySignature

fun verifiedWebhook(
    id: String,
    timestamp: String,
    signature: String,
    rawBody: String,
    signingSecret: String,
    nowSeconds: Long,
): WebhookEvent {
    require(isWithinTolerance(timestamp, DEFAULT_TOLERANCE_SECONDS, nowSeconds)) { "Webhook 已过期" }
    require(verifySignature(id, timestamp, rawBody, signature, signingSecret)) { "Webhook 签名不匹配" }
    return unwrapWebhookEvent(rawBody)
}
```

参数分别取自 `webhook-id`、`webhook-timestamp`、`webhook-signature` 请求头。服务端还应按事件 ID 做业务幂等处理，避免重复投递重复执行。签名和 `whsec_` 密钥使用官方 Base64 规则。

## 9. Admin 与 Usage

组织管理接口使用独立的 Admin key 创建客户端。项目 key 与 Admin key 不要混用。

<!-- compile: common -->
```kotlin
import com.aallam.openai.api.admin.AdminApiKeyCreateRequest
import com.aallam.openai.api.admin.AdminProjectId
import com.aallam.openai.api.admin.AdminUsageQuery
import com.aallam.openai.api.admin.AdminUserId
import com.aallam.openai.api.admin.ProjectUserCreateRequest

suspend fun addProjectMember(admin: OpenAI, projectId: String, userId: String) {
    admin.createProjectUser(
        AdminProjectId(projectId),
        ProjectUserCreateRequest(role = "member", userId = AdminUserId(userId)),
    )
}

suspend fun createAdminCredential(admin: OpenAI, saveSecret: (String) -> Unit) {
    val created = admin.createAdminApiKey(AdminApiKeyCreateRequest(name = "automation"))
    saveSecret(created.value) // 写入密钥存储，不要打印整个返回对象。
}

suspend fun readUsage(admin: OpenAI, startTime: Long, projectId: String) {
    val page = admin.usageCompletions(AdminUsageQuery(
        startTime = startTime,
        projectIds = listOf(projectId),
        groupBy = listOf("project_id", "model"),
    ))
    for (bucket in page.data) {
        for (result in bucket.results) {
            println("${bucket.startTime}: ${result.model}, input=${result.inputTokens}, output=${result.outputTokens}")
        }
    }
    // page.hasMore == true 时，把 page.nextPage 放入下一次查询的 page 参数。
}
```

`createProjectServiceAccount()` 返回 `ProjectServiceAccountCreated`，密钥在 `apiKey?.value`。服务器未返回密钥时该字段可以为空。Usage 的数据路径为 `page.data[].results[]`；Costs 金额在 `result.amount?.value`，币种在 `result.amount?.currency`。同一时间桶可包含多个分组，不能只读第一个结果。

`validateAdminExternalStorage(id)` 接收已创建的存储 ID，返回存储配置及 `status`。项目角色路径以及数组参数的 `[]` 编码由 SDK 处理。

## 10. Agents 与 ChatKit

Beta 版本头由 SDK 自动添加：Agents/Vaults 使用 `agents=v1`，ChatKit 使用 `chatkit_beta=v1`。可通过单次 `RequestOptions.headers` 覆盖。

<!-- compile: common -->
```kotlin
import com.aallam.openai.api.beta.AgentId
import com.aallam.openai.api.beta.AgentSessionCreateRequest
import com.aallam.openai.api.beta.ChatKitSessionCreateRequest
import com.aallam.openai.api.beta.ChatKitWorkflow

suspend fun startAgentSession(openAI: OpenAI, agentId: String) =
    openAI.createAgentSession(AgentSessionCreateRequest(
        environment = buildJsonObject { put("type", "none") },
        agentId = AgentId(agentId),
    ))

suspend fun createChatKitSession(openAI: OpenAI, userId: String, workflowId: String) =
    openAI.createChatKitSession(ChatKitSessionCreateRequest(
        user = userId,
        workflow = ChatKitWorkflow(id = workflowId),
    ))
```

Agent 的 `environment` 是必需的对象；示例为不使用执行环境，托管/自托管环境按相应 schema 传入。ChatKit 的 `workflow` 是对象，包含 `id`，可选 `version`、`stateVariables` 和 `tracing`。这些能力还取决于账户权限与服务端可用性。

## 11. 超时、异常与单次请求选项

HTTP 调用失败会抛出 `OpenAIException` 的子类，例如 `AuthenticationException`、`RateLimitException`、`InvalidRequestException`、`OpenAITimeoutException`。协程取消会继续传播；不要把 `CancellationException` 转换成普通业务失败。

<!-- compile: common -->
```kotlin
import com.aallam.openai.api.core.RequestOptions

suspend fun askWithOptions(openAI: OpenAI, model: String, prompt: String) =
    openAI.response(
        request = ResponseRequest(model = ModelId(model), input = ResponseInput(prompt)),
        requestOptions = RequestOptions(
            headers = mapOf("OpenAI-Project" to "proj_your_project"),
            timeout = Timeout(socket = 180.seconds),
        ),
    )
```

`socket` 表示读取空闲超时；`request` 是整个请求的时限。长流式请求按业务设置，不要给所有流设置很短的总时限。当前内置重试处理 HTTP 429，默认最多重试 3 次；它不等同于官方 SDK 的全部重试策略，也不会自动重放流事件错误。

| 现象 | 检查项 |
| --- | --- |
| 提示找不到 HTTP engine | 对应平台 source set 是否添加了 Ktor engine |
| 401 / 403 | token 类型、账户权限、project/organization 配置 |
| 404 | 模型与端点可用性、网关 baseUrl；带路径的 baseUrl 以 `/` 结尾 |
| 浏览器请求失败 | 网关 CORS、是否误用了依赖握手请求头的 WebSocket 连接方式 |
| 流处理中断 | socket/request 超时、网络断开、协程生命周期、错误事件 |
| Webhook 校验失败 | 原始 body、请求头、签名密钥和秒级时间戳 |
| 已替换源码但找不到新方法 | 检查组合构建替换规则是否生效，避免解析到远端 4.1.0 |

## 12. 当前覆盖范围与迁移变化

| 能力 | 当前范围 |
| --- | --- |
| Responses | 创建、流式创建、读取、取消、删除、输入项、压缩、输入 token 计数；模型字段仍是官方能力的子集 |
| Conversations | 创建/读取/更新/删除；未完整覆盖 items 子资源 |
| Videos | 创建、读取、列表、删除、下载；不代表所有视频编辑等能力均已同步 |
| Evals | Eval CRUD/列表；未覆盖 runs/output_items 的完整执行流程 |
| Realtime / Live | 当前暴露的 REST 方法和 Realtime WebSocket；不包含跨平台音视频采集、播放和 WebRTC 栈 |
| Admin / Beta | 当前源码暴露的资源；此次修复 15 类已确认协议问题，未声称覆盖官方全部方法和字段 |
| 其他 | Chat、Files、Images、Audio、Embeddings、Batch 等参见 [现有使用手册](GettingStarted.md) 和对应接口源码 |

从本次修复前的手动同步代码迁移时：

- `acceptRealtimeCall(id, request)` 增加必传的 GA session JSON。
- Realtime/Live 的 accept/reject/hangup/refer 返回 `Unit`。
- `RealtimeCall` 改为读取 `sdp`、`location`、可空 `id` 和 `headers`。
- `forkLiveSession()` 返回 `LiveSessionCreated`，通过 `session`、`transport` 访问结果。
- `AgentSessionCreateRequest.environment` 必传；`ChatKitSessionCreateRequest` 必传 user 和 `ChatKitWorkflow`。
- Usage 从 `data[].指标` 改为 `data[].results[].指标`，Costs 金额为对象。
- 创建 Admin key/ServiceAccount 返回专门的创建响应类型；存储校验改为传 ID，读取配置状态。
- Webhook 签名使用 Base64；移除自定义的十六进制签名夹具。

## 13. 本地验证

在 SDK 仓库根目录执行：

```sh
./gradlew :openai-core:jvmTest :openai-client:jvmTest \
  :openai-core:apiCheck :openai-client:apiCheck

./gradlew :openai-core:jsTest :openai-client:jsTest \
  :openai-core:wasmJsTest :openai-client:wasmJsTest

# macOS 主机的 Native 验证
./gradlew :openai-core:macosArm64Test :openai-client:macosArm64Test \
  :openai-client:compileKotlinIosSimulatorArm64
```

JS/Wasm 测试链接较大的客户端代码时需要足够堆内存，仓库已为 Gradle 和 Kotlin daemon 配置各 2 GiB；内存较紧张时可加 `--max-workers=2`。

默认客户端测试仅运行离线测试，不需要 API key。`OPENAI_LIVE_TESTS=1` 才会放开真实 API 测试；该测试可能产生费用和远端资源，按需要显式选择测试类。修改公共 API 后运行 `:openai-core:apiDump :openai-client:apiDump` 更新快照，再执行 `apiCheck`。

协议回归位于 `TestSdkContracts`、`TestSdkContractModels` 和 `TestWebhookSignature`。本指南的共享 Kotlin 示例可与 JVM main 一起编译校验；真实调用需要使用方提供凭据和可用模型。
