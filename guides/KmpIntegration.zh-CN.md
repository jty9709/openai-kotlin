# 在 KMP 工程中接入 OpenAI SDK

本文面向**已有 Kotlin Multiplatform 应用的接入方**。你在自己的工程中配置 Maven 仓库、给共享模块添加 SDK 依赖，然后在 `commonMain` 调用 API。

本文使用已发布产物 **`com.aallam.openai:openai-client:4.1.0-local.2`**，示例以 **Android＋iOS 的 `shared` 模块**为主线。模块名是 `composeApp` 或其他名称时，替换下文的 `shared` 即可；JVM、JS、Wasm 是可选扩展。

**当前仓库可用范围：该版本已发布到这台机器的 Maven Local。** 同一机器上的其他 KMP 工程可以直接接入。另一台开发机或 CI 需要 SDK 提供方给出包含该版本的远端 Maven 仓库地址；目前该版本尚未发布到 Maven Central。仓库地址和 SDK 版本应向提供方获取，接入方不需要编译、发布 SDK 源码。

## 阅读路径

1. [添加仓库和共享模块依赖](#1-在接入工程中添加依赖)。
2. [在 commonMain 创建客户端](#2-在-commonmain-中创建客户端)。
3. [封装共享业务服务并调用](#3-在共享模块中完成一次调用)。
4. [流式输出和取消](#4-responses-流式输出)、[多轮对话](#5-多轮对话)。
5. 按需阅读 [文件与多模态](#6-文件向量音频视频与分页)、[Realtime](#7-realtime会话配置sdp-和通话控制)、[异常处理](#11-超时异常与单次请求选项)。
6. [接入验收和排错](#13-在接入工程中验证)。Webhook、Admin、ChatKit 会话签发属于后端侧能力，移动端接入时按对应章节说明与后端协作。

## 1. 在接入工程中添加依赖

### 1.1 你需要修改哪些文件

以下路径全部属于**你的 KMP 应用工程**：

```text
your-kmp-app/
├── settings.gradle.kts             # 添加 Maven 仓库
├── build.gradle.kts                # 已有 Kotlin/Android 插件版本
├── gradle/libs.versions.toml       # 如使用版本目录，在这里统一版本
├── androidApp/                    # 已有 Android 应用模块（名称可能不同）
├── iosApp/                        # 已有 iOS 应用
└── shared/
    ├── build.gradle.kts            # commonMain 依赖 SDK，各平台添加 HTTP 引擎
    └── src/
        ├── commonMain/kotlin/com/example/shared/ai/
        │   ├── OpenAIClient.kt
        │   ├── Responses.kt
        │   ├── AiService.kt
        │   └── AiScreenActions.kt
        ├── androidMain/AndroidManifest.xml
        └── iosMain/kotlin/
```

本文配置的验证组合为 Kotlin **2.1.21**、Gradle **8.10.2**、JDK **17**。Android 示例使用 AGP **8.7.2**、compileSdk **35**、minSdk **24**；这些 Android 值是示例工程配置，已有工程按自己的兼容版本合并，不能据此认定 SDK 的最低 Android 版本。[Kotlin/Gradle/AGP 兼容范围](https://kotlinlang.org/docs/gradle-configure-project.html)

| 依赖 | 本文版本 | 配置位置 |
| --- | --- | --- |
| `com.aallam.openai:openai-client` | `4.1.0-local.2` | `commonMain` |
| `io.ktor:ktor-client-okhttp` | `3.0.0` | `androidMain`，可选 `jvmMain` |
| `io.ktor:ktor-client-darwin` | `3.0.0` | `iosMain` |
| `org.jetbrains.kotlinx:kotlinx-coroutines-core` | `1.9.0` | `commonMain`，供业务协程使用 |
| `org.jetbrains.kotlinx:kotlinx-serialization-json` | `1.7.3` | `commonMain`，供后文 JSON 示例使用 |

Ktor 各组件保持同一版本。`openai-core` 会随 client 自动引入，无需再添加一次。仅使用 SDK 请求类或 `JsonObject` 不需要 serialization 编译插件；自己的数据类使用 `@Serializable` 时，才需要接入方已有的 `kotlin("plugin.serialization")`。

### 1.2 在 settings.gradle.kts 添加 Maven 仓库

当前在已发布这份 SDK 的机器上接入，使用 `mavenLocal()`：

<!-- consumer-file: settings.gradle.kts -->
```kotlin
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal {
            content { includeGroup("com.aallam.openai") }
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "your-kmp-app"
include(":shared")
// 保留你工程已有的 include(":androidApp") 等模块声明。
```

将仓库片段合并到已有 settings，不要覆盖原来的模块列表、插件管理或仓库模式。Maven Local 默认位于当前用户的 `~/.m2/repository`；如果设置了自定义 `localRepository`，发布者和接入工程应使用同一个位置。

**团队远端仓库接入：** 获取提供方的真实 Maven 地址后，将上面的 `mavenLocal { ... }` 替换为以下片段，其余第三方依赖仓库保留：

```kotlin
maven {
    url = uri(providers.gradleProperty("openaiRepositoryUrl").get())
    content { includeGroup("com.aallam.openai") }
}
```

`openaiRepositoryUrl` 在接入方的 Gradle 属性或 CI 配置中设置为提供方给出的地址。若仓库需要认证，按团队已有的凭据管理方式添加 `credentials`。上面的属性名只是配置入口，并不意味着当前已有一个远端仓库可用。

### 1.3 在 shared/build.gradle.kts 添加依赖

**已有 KMP 模块只需合并下面的依赖块**，无需因为接入 SDK 重新创建 target：

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.aallam.openai:openai-client:4.1.0-local.2")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
        }
        androidMain.dependencies {
            implementation("io.ktor:ktor-client-okhttp:3.0.0")
        }
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:3.0.0")
        }
    }
}
```

完整的 Android＋iOS 共享模块配置如下，便于对照。已有工程继续使用原来的插件声明方式（例如 `alias(libs.plugins...)`），不要重复声明插件版本。

接入工程根目录 `build.gradle.kts`：

<!-- consumer-file: build.gradle.kts -->
```kotlin
plugins {
    kotlin("multiplatform") version "2.1.21" apply false
    id("com.android.library") version "8.7.2" apply false
}
```

`shared/build.gradle.kts`：

<!-- consumer-file: shared/build.gradle.kts -->
```kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    jvmToolchain(17)
    androidTarget {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }
    val appleTargets = listOf(iosArm64(), iosSimulatorArm64())
    appleTargets.forEach { target ->
        target.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation("com.aallam.openai:openai-client:4.1.0-local.2")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
        }
        androidMain.dependencies {
            implementation("io.ktor:ktor-client-okhttp:3.0.0")
        }
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:3.0.0")
        }
    }
}

android {
    namespace = "com.example.shared"
    compileSdk = 35
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
```

这里使用 Kotlin 2.1.21 搭配 AGP 8.x 的 `androidTarget` 配置。已使用更新 Android KMP 插件的工程应保留自己的 target DSL，再把依赖放到对应 source set，不能同时混用两套 Android 插件。

- `commonMain` 引入的是 **`openai-client` 根坐标**，Gradle 会为 Android 选择 JVM 构件、为 iOS 选择对应 KLIB。不要在 commonMain 使用 `openai-client-jvm` 或某个 `-iosarm64` 构件。
- `iosMain` 来自 Kotlin 默认层级模板。已有自定义 source set 层级时，把 Darwin 引擎加到实际共享的 Apple source set。[KMP 依赖与 source set](https://kotlinlang.org/docs/multiplatform/multiplatform-add-dependencies.html)
- `implementation` 足够供 shared 内部调用。本文封装对上层返回 `String`，不向外暴露 SDK 类型；Android UI 模块和 Swift 调用层都不需要再声明 SDK 依赖。Swift 侧通过工程已有的 `Shared.framework` / XCFramework 接入 shared。
- `framework` 块用于展示已有 iOS 工程的常见配置；如果项目已用 CocoaPods 或其他方式产出 framework，保留现有配置即可。

Android 网络权限可以声明在共享模块的清单中，并由应用合并：

`shared/src/androidMain/AndroidManifest.xml`：

<!-- consumer-file: shared/src/androidMain/AndroidManifest.xml -->
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET" />
</manifest>
```

### 1.4 已有版本目录时怎么写

如果你的工程用 `gradle/libs.versions.toml` 管理依赖，可把 1.3 的字符串依赖替换为别名。以下内容分别合并进已有区段，不要再次创建同名 `[versions]`、`[libraries]`：

```toml
[versions]
openai-sdk = "4.1.0-local.2"
ktor = "3.0.0"
coroutines = "1.9.0"
serialization = "1.7.3"

[libraries]
openai-client = { module = "com.aallam.openai:openai-client", version.ref = "openai-sdk" }
ktor-client-okhttp = { module = "io.ktor:ktor-client-okhttp", version.ref = "ktor" }
ktor-client-darwin = { module = "io.ktor:ktor-client-darwin", version.ref = "ktor" }
coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "coroutines" }
serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "serialization" }
```

```kotlin
commonMain.dependencies {
    implementation(libs.openai.client)
    implementation(libs.coroutines.core)
    implementation(libs.serialization.json)
}
androidMain.dependencies { implementation(libs.ktor.client.okhttp) }
iosMain.dependencies { implementation(libs.ktor.client.darwin) }
```

如果已有 `ktor` 等版本键，直接复用并确认解析后的版本一致。字符串依赖与版本目录别名选一种即可。

### 1.5 按需添加其他 KMP 目标

Android＋iOS 工程无需添加本节内容。已有 JVM、JS 或 Wasm 目标时，在同一个 shared 模块中补充对应引擎；commonMain 的 SDK 依赖保持一份。

```kotlin
kotlin {
    jvm()
    js(IR) { browser() } // Node 工程改成 nodejs()
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs { browser() }

    sourceSets {
        jvmMain.dependencies { implementation("io.ktor:ktor-client-okhttp:3.0.0") }
        jsMain.dependencies { implementation("io.ktor:ktor-client-js:3.0.0") }
        wasmJsMain.dependencies { implementation("io.ktor:ktor-client-js:3.0.0") }
    }
}
```

浏览器还需要后端允许 CORS。macOS 可在相应 source set 添加 Darwin 引擎；Linux/Windows Native 可添加 `ktor-client-curl:3.0.0` 并配置所需原生环境。引擎能力见 [Ktor 文档](https://ktor.io/docs/client-engines.html)。

## 2. 在 commonMain 中创建客户端

以下 Kotlin 文件全部放在接入工程的 `shared/src/commonMain/kotlin/com/example/shared/ai/`。每段都给出文件名、package 和完整 import，可按文件复制。示例辅助函数使用 `internal`，对外入口是第 3 节的 `AiService`。

HTTP 引擎由第 1 节的平台依赖自动选择，业务代码不需要写 expect/actual 客户端，也不需要把 OkHttp 或 Darwin 类导入 commonMain。

`OpenAIClient.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/OpenAIClient.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import kotlin.time.Duration.Companion.seconds

internal fun createOpenAI(token: String, baseUrl: String): OpenAI =
    OpenAI(OpenAIConfig(
        token = token,
        host = OpenAIHost(baseUrl = baseUrl),
        timeout = Timeout(connect = 30.seconds, socket = 120.seconds),
        logging = LoggingConfig(logLevel = LogLevel.None),
    ))
```

一个客户端可供多个协程复用；在应用服务或业务作用域结束后调用 `close()`。有路径前缀的 `baseUrl` 以 `/` 结尾。自定义网关需要兼容对应的 OpenAI 路径、鉴权和响应格式，不能直接填写普通聊天网页地址。

服务端从环境变量或密钥服务注入 token。移动端和浏览器使用自己的后端代理普通模型调用，避免把长期 API key 打包进应用；Realtime 可由后端签发临时 client secret。Admin key 和 Webhook signing secret 放在服务端。

## 3. 在共享模块中完成一次调用

`response()` 是挂起函数，需要在协程中调用。`model` 使用账户可访问的模型 ID，由业务配置传入。

`Responses.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/Responses.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.Response
import com.aallam.openai.api.response.ResponseInput
import com.aallam.openai.api.response.ResponseRequest
import com.aallam.openai.client.OpenAI

internal suspend fun ask(openAI: OpenAI, model: String, prompt: String): String {
    val response = openAI.response(ResponseRequest(
        model = ModelId(model),
        input = ResponseInput(prompt),
        instructions = "请用中文回答。",
    ))
    return responseText(response)
}

internal fun responseText(response: Response): String {
    check(response.status == "completed") {
        response.error?.message ?: "响应状态=${response.status}，详情=${response.incompleteDetails}"
    }
    val text = response.output
        .filter { it.type == "message" }
        .flatMap { it.content.orEmpty() }
        .filter { it.type == "output_text" }
        .joinToString("") { it.text.orEmpty() }
    check(text.isNotBlank()) { "没有文本输出，请检查拒绝信息、工具调用或其他输出项" }
    return text
}
```

文本位于 `output[].content[]`；不要假设原始响应必然存在顶层 `output_text`。拒绝、工具调用和非文本输出应分别处理。需要识别截断时，检查 `status == "incomplete"` 和 `incompleteDetails`。

### 3.1 给应用暴露共享业务服务

`AiService` 是接入工程自行添加的业务封装类，放在 shared 中，对上层只暴露业务参数和文本结果。Android 和 iOS 复用同一份实现；创建时传入登录态令牌、后端兼容接口地址及可用模型 ID。

`AiService.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/AiService.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.ResponseInput
import com.aallam.openai.api.response.ResponseRequest
import com.aallam.openai.api.response.ResponseStreamEventType
import kotlinx.coroutines.flow.collect

class AiService(
    accessToken: String,
    baseUrl: String,
    private val model: String,
) {
    private val client = createOpenAI(token = accessToken, baseUrl = baseUrl)

    @Throws(Exception::class)
    suspend fun answer(prompt: String): String = ask(client, model, prompt)

    @Throws(Exception::class)
    suspend fun stream(prompt: String, onText: (String) -> Unit) {
        var completed = false
        client.responseStream(ResponseRequest(
            model = ModelId(model),
            input = ResponseInput(prompt),
            instructions = "请用中文回答。",
        )).collect { event ->
            when (event.type) {
                ResponseStreamEventType.RESPONSE_OUTPUT_TEXT_DELTA -> event.delta?.let(onText)
                ResponseStreamEventType.RESPONSE_COMPLETED -> completed = true
                ResponseStreamEventType.ERROR,
                ResponseStreamEventType.RESPONSE_FAILED ->
                    error(event.message ?: "流式响应失败")
                ResponseStreamEventType.RESPONSE_INCOMPLETE ->
                    error("响应未完整生成")
                else -> Unit
            }
        }
        check(completed) { "流已结束，但没有收到 response.completed" }
    }

    fun close() {
        client.close()
    }
}
```

同一账户会话内复用 `AiService`，在所有使用它的请求停止、会话或依赖注入作用域结束后调用 `close()`。不要每次页面重绘都创建一个服务。若 token 或后端地址变化，结束旧实例的请求后再创建新实例。

`accessToken` 会作为 Bearer token 发送给 `baseUrl`。生产移动端使用自己的后端令牌，并由后端持有长期 OpenAI API key；前提是后端兼容这里用到的 OpenAI 路径和请求/响应协议。普通业务接口若使用不同协议，应通过自己的网络层对接。模型 ID 由后端或业务配置提供。

### 3.2 在应用协程作用域内调用

下面演示位于 shared 内的 Kotlin 页面逻辑。scope 由调用方提供，回调在该 scope 的上下文运行；更新 UI 时使用应用现有的 UI 作用域。

`AiScreenActions.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/AiScreenActions.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal fun requestAnswer(
    scope: CoroutineScope,
    service: AiService,
    prompt: String,
    onAnswer: (String) -> Unit,
    onFailure: (Exception) -> Unit,
): Job = scope.launch {
    try {
        onAnswer(service.answer(prompt))
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (failure: Exception) {
        onFailure(failure)
    }
}
```

Android 的已有 ViewModel 可以在 `viewModelScope.launch` 中直接调用 `service.answer(prompt)`；Compose Multiplatform 页面可以使用项目已有的共享 ViewModel/协程作用域。保留请求的 `Job` 以便取消，避免在 UI 主线程使用 `runBlocking`。

iOS 若使用共享 Kotlin UI，继续调用相同的挂起函数。Swift UI 则通过已有的 Shared framework 和协程桥接方式调用 `AiService`；类上的挂起方法已声明 `@Throws`，便于向 Swift 传递失败。Swift 页面沿用工程现有的生命周期和协程桥接约定。

### 3.3 请求参数和返回结果

`model`、`prompt` 由应用传入。`instructions` 是本轮指令；`maxOutputTokens` 控制输出预算，`temperature` 等参数只在所选模型支持时设置。需要这些配置时，在 shared 内调整 `ask()` 对应的 `ResponseRequest`。

本文的 `responseText()` 要求响应完整结束后才返回文本。拒绝、工具调用或非文本输出需要单独处理；若业务允许部分结果，可按 `incompleteDetails` 处理截断。`Response.usage` 可用于业务统计。

后续章节的辅助函数均放在 shared 内部，可以从 `AiService` 新增的业务方法调用，再向 UI 返回自己的数据类型；无需让 UI 层认识 SDK 的所有模型类。

### 3.4 工具调用：声明、执行、回传、继续生成

SDK 只收发工具定义和调用结果，实际执行由业务负责。下面声明只读的 `get_ticket_status` 工具；模型给出参数后，调用应用提供的 `lookupTicket`，把结果通过 `function_call_output` 回传，再取得最终答案。

`TicketTools.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/TicketTools.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import com.aallam.openai.api.core.Parameters
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.ResponseInput
import com.aallam.openai.api.response.ResponseRequest
import com.aallam.openai.api.response.ResponseTool
import com.aallam.openai.client.OpenAI
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

internal suspend fun answerWithTicketTool(
    openAI: OpenAI,
    model: String,
    question: String,
    lookupTicket: suspend (ticketId: String) -> String,
): String {
    val tool = ResponseTool(
        type = "function",
        name = "get_ticket_status",
        description = "查询用户有权查看的工单状态",
        strict = true,
        parameters = Parameters.fromJsonString("""
            {
              "type": "object",
              "properties": {"ticket_id": {"type": "string"}},
              "required": ["ticket_id"],
              "additionalProperties": false
            }
        """.trimIndent()),
    )
    val instructions = "查询工单时使用工具，根据工具结果回答，不要编造状态。"
    var response = openAI.response(ResponseRequest(
        model = ModelId(model),
        input = ResponseInput(question),
        instructions = instructions,
        tools = listOf(tool),
        store = true,
    ))

    // 限制工具轮数，防止无休止地请求。
    repeat(4) {
        check(response.status == "completed") {
            response.error?.message ?: "工具响应未完成：${response.status}"
        }
        val calls = response.output.filter { it.type == "function_call" }
        if (calls.isEmpty()) return responseText(response)

        val outputs = buildJsonArray {
            for (call in calls) {
                require(call.name == "get_ticket_status") { "不支持的工具：${call.name}" }
                val args = Json.parseToJsonElement(requireNotNull(call.arguments)).jsonObject
                val ticketId = requireNotNull(args["ticket_id"]?.jsonPrimitive?.content)
                require(ticketId.isNotBlank())
                // lookupTicket 必须在应用层校验当前用户对该工单的访问权限。
                val result = lookupTicket(ticketId)
                add(buildJsonObject {
                    put("type", "function_call_output")
                    put("call_id", requireNotNull(call.callId))
                    put("output", result)
                })
            }
        }
        response = openAI.response(ResponseRequest(
            model = ModelId(model),
            previousResponseId = response.id,
            input = ResponseInput(outputs),
            instructions = instructions,
            tools = listOf(tool),
            store = true,
        ))
    }
    check(response.output.none { it.type == "function_call" }) { "已达到工具调用轮数上限" }
    return responseText(response)
}
```

这里使用 `previousResponseId` 引用已保存的上一轮，避免手工丢失 reasoning/tool-call 上下文；每轮仍显式传入 instructions 和 tools。若业务不能使用服务端存储，需要维护完整的历史输入、输出项及工具结果，不能只传上一段可见文本。[官方工具循环说明](https://developers.openai.com/cookbook/examples/agent_optimization/optimizing_agents_for_cost_and_quality#optional-live-responses-api-tool-loop)

`ResponseInputItem` 当前没有完整的工具回传字段，因此示例用 `ResponseInput(JsonElement)` 明确构造 `call_id`/`output`。流式工具调用应先组装完整 arguments，再解析和执行，不能把每个增量都当作一个完整 JSON 请求。

### 3.5 Chat Completions 和结构化 JSON 输出

已有 Chat 协议的业务可以继续使用 `chatCompletion()`；`messages` 需由应用维护并在每轮发送。下面的严格 JSON Schema 示例使用 **Chat 的 `responseFormat`**：

`ChatCompletions.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/ChatCompletions.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatResponseFormat
import com.aallam.openai.api.chat.JsonSchema
import com.aallam.openai.api.core.FinishReason
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

internal suspend fun classifyTicket(openAI: OpenAI, model: String, text: String): JsonObject {
    val schema = Json.parseToJsonElement("""
        {
          "type": "object",
          "properties": {
            "category": {"type": "string", "enum": ["bug", "question", "other"]},
            "summary": {"type": "string"}
          },
          "required": ["category", "summary"],
          "additionalProperties": false
        }
    """.trimIndent()).jsonObject
    val completion = openAI.chatCompletion(ChatCompletionRequest(
        model = ModelId(model),
        messages = listOf(
            ChatMessage.System("对工单分类，用 JSON 返回 category 和中文 summary。"),
            ChatMessage.User(text),
        ),
        responseFormat = ChatResponseFormat.jsonSchema(
            JsonSchema(name = "ticket_classification", schema = schema, strict = true)
        ),
    ))
    val choice = completion.choices.firstOrNull() ?: error("没有返回候选结果")
    check(choice.finishReason == FinishReason.Stop) { "输出未正常结束：${choice.finishReason}" }
    val content = requireNotNull(choice.message.content) { "未返回文本，检查拒绝或工具调用" }
    return Json.parseToJsonElement(content).jsonObject
}

internal suspend fun streamChat(openAI: OpenAI, model: String, text: String, onText: (String) -> Unit) {
    openAI.chatCompletions(ChatCompletionRequest(
        model = ModelId(model),
        messages = listOf(ChatMessage.User(text)),
    )).collect { chunk ->
        chunk.choices.forEach { choice -> choice.delta?.content?.let(onText) }
    }
}
```

普通文本 Chat 可去掉 `responseFormat` 并读取 `choices.firstOrNull()?.message?.content`；需要多轮时，把上一轮的 user/assistant 消息加入新的 `messages`。

模型须支持相应的结构化输出模式。当前 `ChatResponseFormat.jsonSchema` 的 JSON 形状适配 Chat；**不要直接搬到 Responses 的 `text.format`**，两者的 schema 嵌套层级不同，当前模型尚未完整区分。Responses 的 JSON 对象模式可使用 `ResponseText(format = ChatResponseFormat.JsonObject)`，并在指令中明确要求 JSON；JSON 对象模式本身不保证符合业务 schema。

## 4. Responses 流式输出

`responseStream()` 返回冷 `Flow`：收集时才发送请求，再次收集会再次请求。它自动设置 `stream=true`。普通 `response()` 不要传 `stream=true`。

第 3 节的 `AiService.stream()` 已处理文本增量、完成事件和错误事件。调用时把每个增量追加到当前消息内容，避免把增量当成完整答案覆盖。


取消收集 Flow 的协程会结束本地流读取。未知事件仍保存在 `event.json` 中。`responseId` 从生命周期事件的 `response.id` 读取，`id` 从输出项事件的 `item.id` 读取；不是每个事件都包含这些字段。流开始后的错误可能通过事件返回，不能只捕获 HTTP 异常。已经收到部分输出后，不要无条件重放整次请求。[官方流式错误处理说明](https://developers.openai.com/api/docs/guides/rate-limits#update-existing-error-handlers)

### 4.1 绑定生命周期和取消生成

把流放入调用方的作用域，保留返回的 `Job`；用户点击“停止”时调用 `job.cancel()`。以下回调运行在所传 scope 的协程上下文中，UI 更新应由 UI 层选择正确 dispatcher。

`Streaming.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/Streaming.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal fun launchAnswer(
    scope: CoroutineScope,
    service: AiService,
    prompt: String,
    onText: (String) -> Unit,
    onFailure: (Exception) -> Unit,
    onComplete: () -> Unit,
): Job = scope.launch {
    try {
        service.stream(prompt, onText)
        onComplete()
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (failure: Exception) {
        onFailure(failure)
    }
}
```

Android 可传 `viewModelScope`，页面销毁后的取消由生命周期处理；不要在每个文本增量到来时重新创建客户端。iOS 应提供与页面/业务服务对应的协程作用域或桥接层，SDK 本身不把 `Flow` 自动转换成 Swift `AsyncSequence`。

取消本地读取与服务端资源操作是两个行为。`cancel(ResponseId(...))` 对应 Responses 的服务端取消端点，须满足该端点的可取消条件；不要假定取消一个本地协程会删除 Conversation、文件或已创建的生成任务。

## 5. 多轮对话

可以传 `previousResponseId` 继续上一条响应，也可以创建 Conversation 后传入 `conversation`。一次请求不要同时设置这两个字段。

`Conversations.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/Conversations.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import com.aallam.openai.api.conversation.ConversationId
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.ResponseInput
import com.aallam.openai.api.response.ResponseRequest
import com.aallam.openai.client.OpenAI

internal suspend fun startConversation(openAI: OpenAI): ConversationId =
    openAI.createConversation().id

internal suspend fun sendInConversation(openAI: OpenAI, model: String, id: ConversationId, text: String) =
    openAI.response(ResponseRequest(
        model = ModelId(model),
        conversation = id,
        input = ResponseInput(text),
    ))

internal suspend fun continueResponse(openAI: OpenAI, model: String, prompt: String) {
    val first = openAI.response(ResponseRequest(model = ModelId(model), input = ResponseInput(prompt)))
    openAI.response(ResponseRequest(
        model = ModelId(model),
        previousResponseId = first.id,
        input = ResponseInput("请举一个例子。"),
    ))
}
```

会话 ID 由应用持久保存并关联到当前用户。并发请求写同一会话时，应在业务层明确顺序，避免上下文交错。使用 `previousResponseId` 时，如后续仍需要同样的 instructions，应再次传入；不要认为上一轮 instructions 会自动继承。

当前有 Conversation 创建、读取、更新、删除接口；尚未提供 conversation items 子资源的完整操作。`responseInputItems()` 查询的是某个 Response 的输入项。

## 6. 文件、向量、音频、视频与分页

`FileSource` 接收 `kotlinx.io.RawSource`。共享代码可使用内存数据；JVM/Native 也可使用 `FileSource(Path(...))`。上传会消费并关闭 source，需要重用时重新创建。

`Files.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/Files.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.file.FileUpload
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.client.OpenAI
import kotlinx.io.Buffer

internal suspend fun uploadDocument(openAI: OpenAI, bytes: ByteArray) =
    openAI.file(FileUpload(
        file = FileSource(name = "document.txt", source = Buffer().apply { write(bytes) }),
        purpose = Purpose("user_data"),
    ))
```

分页接口不会自动获取下一页。例如 `videos(limit, after, order)` 返回 `PaginatedList`，检查 `hasMore`，用 `lastId` 作为下一次 `after`。组织 Usage 使用另一种分页方式：`hasMore` 与 `nextPage`，下一页放到 `AdminUsageQuery.page`。

### 6.1 将上传文件或图片用于 Responses

上传返回的是文件对象，后续请求引用它的 `id`。以下函数接收 `FileId`；把 `uploadDocument(...).id` 传入即可。实际文件格式需由所选模型和端点支持；如需 PDF，应上传真实 PDF 字节并使用 `.pdf` 文件名。

`FileAndImageInputs.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/FileAndImageInputs.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.ResponseInput
import com.aallam.openai.api.response.ResponseRequest
import com.aallam.openai.client.OpenAI
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal suspend fun askAboutFile(openAI: OpenAI, model: String, fileId: FileId, question: String): String {
    val input = buildJsonArray {
        add(buildJsonObject {
            put("role", "user")
            put("content", buildJsonArray {
                add(buildJsonObject {
                    put("type", "input_file")
                    put("file_id", fileId.id)
                })
                add(buildJsonObject {
                    put("type", "input_text")
                    put("text", question)
                })
            })
        })
    }
    return responseText(openAI.response(ResponseRequest(
        model = ModelId(model), input = ResponseInput(input),
    )))
}

internal suspend fun describeImage(openAI: OpenAI, model: String, imageUrl: String): String {
    val input = buildJsonArray {
        add(buildJsonObject {
            put("role", "user")
            put("content", buildJsonArray {
                add(buildJsonObject {
                    put("type", "input_text")
                    put("text", "请描述这张图片。")
                })
                add(buildJsonObject {
                    put("type", "input_image")
                    put("image_url", imageUrl)
                })
            })
        })
    }
    return responseText(openAI.response(ResponseRequest(
        model = ModelId(model), input = ResponseInput(input),
    )))
}
```

`imageUrl` 是服务端可读取的图片 URL 或端点允许的 data URL，不是本机路径。移动端录制/选择文件后，通过平台文件 API 获取字节或 source，再交给共享代码。

文件管理方法：`file(fileId)` 读取元数据，`download(fileId)` 读取内容字节，`delete(fileId)` 删除远端文件。根据业务生命周期清理文件，仍被其他会话引用的文件不要提前删除。

较大文件可以使用 Uploads 分片流程：`createUpload(UploadCreateRequest(...))` → 多次 `createUploadPart(uploadId, FileSource(...))` → 按原文件顺序把所有 part ID 交给 `completeUpload(uploadId, partIds)`。完成结果的 `file` 中包含可用于后续 API 的文件信息；中途放弃可 `cancelUpload(uploadId)`。`bytes` 应为总字节数，每片创建新的 source，分片大小和总量按服务端当前限制设置，不要为分片先把整个大文件读入内存。

### 6.2 Embeddings：批量文本转向量

`Embeddings.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/Embeddings.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI

internal suspend fun embedTexts(openAI: OpenAI, model: String, texts: List<String>): List<List<Double>> {
    require(texts.isNotEmpty())
    val result = openAI.embeddings(EmbeddingRequest(
        model = ModelId(model),
        input = texts,
    ))
    return result.embeddings.sortedBy { it.index }.map { it.embedding }
}
```

向量顺序按 `index` 对齐输入。建库和查询使用相同的模型及 dimensions；`dimensions` 仅在所选模型支持时传入。文本切分、向量存储和相似度检索属于应用层。

### 6.3 Audio：文本转语音、语音转文字

`Audio.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/Audio.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import com.aallam.openai.api.audio.AudioResponseFormat
import com.aallam.openai.api.audio.SpeechRequest
import com.aallam.openai.api.audio.SpeechResponseFormat
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.audio.Voice
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.io.Buffer
import kotlinx.serialization.json.Json

internal suspend fun synthesizeSpeech(openAI: OpenAI, model: String, voice: String, text: String): ByteArray =
    openAI.speech(SpeechRequest(
        model = ModelId(model),
        input = text,
        voice = Voice(voice),
        responseFormat = SpeechResponseFormat.Mp3,
    ))

internal suspend fun transcribeWav(openAI: OpenAI, model: String, wavBytes: ByteArray): String =
    openAI.transcription(TranscriptionRequest(
        audio = FileSource(name = "recording.wav", source = Buffer().apply { write(wavBytes) }),
        model = ModelId(model),
        responseFormat = AudioResponseFormat.Json,
        language = "zh",
    )).text
```

`speech()` 返回完整音频字节，把它保存为 `.mp3` 或交给平台播放器；该方法不是实时播放流。转录示例的输入必须是真实 WAV 编码，不能只把其他格式改名。模型、音色、语言、输出格式须相互兼容；录音权限、采集与播放由 Android/iOS/Web 调用层实现。

### 6.4 Videos：创建、查询进度、下载和分页

视频生成是异步作业。先保存 `createVideo()` 返回的 ID，再轮询已有作业，避免把“查询进度”写成重复创建。下面轮询函数在业务超时后停止等待，但不会假定服务端作业已被取消。

`Videos.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/Videos.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import com.aallam.openai.api.video.Video
import com.aallam.openai.api.video.VideoCreateRequest
import com.aallam.openai.api.video.VideoId
import com.aallam.openai.api.video.VideoModel
import com.aallam.openai.client.OpenAI
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout

internal suspend fun submitVideo(openAI: OpenAI, model: String, prompt: String): VideoId =
    openAI.createVideo(VideoCreateRequest(prompt = prompt, model = VideoModel(model))).id

internal suspend fun awaitVideo(openAI: OpenAI, id: VideoId, onProgress: (Long?) -> Unit): Video =
    withTimeout(10.minutes) {
        var video = openAI.video(id)
        while (video.status == "queued" || video.status == "in_progress") {
            onProgress(video.progress)
            delay(3.seconds)
            video = openAI.video(id)
        }
        check(video.status == "completed") {
            video.error?.message ?: "视频未完成：${video.status}"
        }
        video
    }

internal suspend fun forEachVideo(openAI: OpenAI, consume: suspend (Video) -> Unit) {
    var after: String? = null
    do {
        val page = openAI.videos(limit = 20, after = after)
        page.data.forEach { consume(it) }
        if (page.hasMore != true) break
        val next = requireNotNull(page.lastId) { "缺少下一页游标" }
        check(next != after) { "分页游标没有前进" }
        after = next
    } while (true)
}
```

完成后 `downloadVideoContent(id)` 返回视频字节；较大视频会占用相应内存，保存/播放应在平台层处理。`deleteVideo(id)` 删除视频资源。列表接口不自动翻页，示例使用 `lastId`/`after`，不要用本地数组下标代替游标。

## 7. Realtime：会话配置、SDP 和通话控制

使用 GA 的 `client_secrets` / `calls` 接口。`createRealtimeSession()` 和 `createRealtimeTranscriptionSession()` 保留了旧版接口形状，新接入按下面的 GA 流程使用。[官方 Realtime 接入与迁移说明](https://developers.openai.com/api/docs/guides/realtime)

`RealtimeSetup.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/RealtimeSetup.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import com.aallam.openai.api.realtime.RealtimeCallId
import com.aallam.openai.api.realtime.RealtimeCallRequest
import com.aallam.openai.api.realtime.RealtimeClientSecretRequest
import com.aallam.openai.client.OpenAI
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun realtimeConfig(model: String) = buildJsonObject {
    put("type", "realtime")
    put("model", model)
    put("instructions", "请用中文交流。")
}

internal suspend fun issueRealtimeSecret(openAI: OpenAI, model: String): String =
    requireNotNull(openAI.createRealtimeClientSecret(
        RealtimeClientSecretRequest(session = realtimeConfig(model))
    ).value)

internal suspend fun createWebRtcCall(openAI: OpenAI, model: String, offerSdp: String) =
    openAI.createRealtimeCall(RealtimeCallRequest(
        sdp = offerSdp,
        session = realtimeConfig(model),
    ))

internal suspend fun acceptIncomingCall(openAI: OpenAI, model: String, callId: String) {
    openAI.acceptRealtimeCall(RealtimeCallId(callId), request = realtimeConfig(model))
}
```

`issueRealtimeSecret()` 等使用长期 API key 的签发操作放在后端，移动端从后端取得临时密钥或 SDP answer。

`createWebRtcCall()` 返回 `RealtimeCall`：把 `sdp` 设置为 PeerConnection 的远端 answer；`location` 保留服务器响应头，`id` 在 Location 存在时解析得到，其他响应头在 `headers`。录音、播放、PeerConnection 和媒体轨道由应用的 WebRTC 层实现。

SDK 自动以 multipart 发送 SDP/session，不需要手动拼 multipart。`acceptRealtimeCall()` 的配置直接作为 JSON body，**不再额外包一层 `session`**。accept/reject/hangup/refer 成功返回 `Unit`。

支持带 Authorization 请求头的 WebSocket 引擎上，可调用 `connectRealtime(clientSecret, model)`；返回连接只收集一次 `events`，结束后调用 `close()`。事件类型是 `RealtimeEventType` 枚举；需要字符串时使用 `rawType`。浏览器原生 WebSocket 不支持任意握手请求头，本实现的该连接方式未验证浏览器兼容性，浏览器接入使用 WebRTC 流程。

Live 的 `createLiveSession()` 和 `forkLiveSession()` 均返回包含 `session`、`transport` 的 `LiveSessionCreated`；控制操作同样返回 `Unit`。创建时提供实际 `session` 配置和 WebRTC `transport`，fork 时提供新客户端的 SDP offer。

### 7.1 WebSocket 文本会话示例

以下示例适用于支持 Authorization 握手请求头的引擎，例如 JVM 的 OkHttp。传入后端签发的临时密钥，在同一连接内发送用户消息并请求文本响应，完成后关闭连接；没有重新创建 REST session。

`RealtimeText.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/RealtimeText.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import com.aallam.openai.api.realtime.RealtimeEvent
import com.aallam.openai.api.realtime.RealtimeEventType
import com.aallam.openai.client.OpenAI
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

internal suspend fun realtimeText(
    openAI: OpenAI,
    clientSecret: String,
    model: String,
    prompt: String,
    onText: (String) -> Unit,
) = withTimeout(120.seconds) {
    val connection = openAI.connectRealtime(clientSecret, model)
    try {
        connection.send(RealtimeEvent.of(RealtimeEventType.CONVERSATION_ITEM_CREATE, buildJsonObject {
            put("item", buildJsonObject {
                put("type", "message")
                put("role", "user")
                put("content", buildJsonArray {
                    add(buildJsonObject {
                        put("type", "input_text")
                        put("text", prompt)
                    })
                })
            })
        }))
        connection.send(RealtimeEvent.of(RealtimeEventType.RESPONSE_CREATE, buildJsonObject {
            put("response", buildJsonObject {
                put("output_modalities", buildJsonArray { add("text") })
            })
        }))
        val done = connection.events.first { event ->
            if (event.rawType == "error") error("Realtime 错误：${event.json["error"]}")
            if (event.type == RealtimeEventType.RESPONSE_OUTPUT_TEXT_DELTA) {
                event.delta?.let(onText)
            }
            event.type == RealtimeEventType.RESPONSE_DONE
        }
        val status = done.json["response"]?.jsonObject?.get("status")?.jsonPrimitive?.content
        check(status == "completed") { "Realtime 响应未完成：$status" }
    } finally {
        connection.close()
    }
}
```

真实语音会话还需向 `input_audio_buffer.append` 发送符合 session 音频配置的 Base64 音频，接收音频 delta 后解码播放。音频格式、采样率、VAD、打断播放和重连恢复由应用设计；上面只演示文本，不包含跨平台录音播放器。浏览器优先使用前面的 WebRTC SDP 流程。

## 8. 服务端协作：Webhook

本节供接入方的后端服务使用；Android/iOS 客户端不保存 Webhook signing secret。验证传入的原始 HTTP body，不要先解析后重新序列化。`verifySignature()` 仅校验签名，需与时间容差检查组合使用。`unwrapWebhookEvent()` 仅负责解析 JSON。

`Webhooks.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/Webhooks.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import com.aallam.openai.api.webhook.DEFAULT_TOLERANCE_SECONDS
import com.aallam.openai.api.webhook.WebhookEvent
import com.aallam.openai.api.webhook.isWithinTolerance
import com.aallam.openai.api.webhook.unwrapWebhookEvent
import com.aallam.openai.api.webhook.verifySignature

internal fun verifiedWebhook(
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

## 9. 服务端协作：Admin 与 Usage

本节仅在接入方后端运行。组织管理接口使用 Admin key 创建客户端，移动端通过自己的后端获取业务需要的汇总结果。

`AdminUsage.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/AdminUsage.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import com.aallam.openai.api.admin.AdminApiKeyCreateRequest
import com.aallam.openai.api.admin.AdminProjectId
import com.aallam.openai.api.admin.AdminUsageQuery
import com.aallam.openai.api.admin.AdminUserId
import com.aallam.openai.api.admin.ProjectUserCreateRequest
import com.aallam.openai.client.OpenAI

internal suspend fun addProjectMember(admin: OpenAI, projectId: String, userId: String) {
    admin.createProjectUser(
        AdminProjectId(projectId),
        ProjectUserCreateRequest(role = "member", userId = AdminUserId(userId)),
    )
}

internal suspend fun createAdminCredential(admin: OpenAI, saveSecret: (String) -> Unit) {
    val created = admin.createAdminApiKey(AdminApiKeyCreateRequest(name = "automation"))
    saveSecret(created.value) // 写入密钥存储，不要打印整个返回对象。
}

internal suspend fun readUsage(admin: OpenAI, startTime: Long, endTime: Long, projectId: String) {
    var cursor: String? = null
    do {
        val page = admin.usageCompletions(AdminUsageQuery(
            startTime = startTime,
            endTime = endTime,
            projectIds = listOf(projectId),
            groupBy = listOf("project_id", "model"),
            bucketWidth = "1d",
            limit = 7,
            page = cursor,
        ))
        for (bucket in page.data) {
            for (result in bucket.results) {
                println("${bucket.startTime}: ${result.model}, input=${result.inputTokens}, output=${result.outputTokens}")
            }
        }
        if (page.hasMore != true) break
        val next = requireNotNull(page.nextPage) { "缺少 Usage 下一页游标" }
        check(next != cursor) { "Usage 分页游标没有前进" }
        cursor = next
    } while (true)
}
```

上例的 `startTime`/`endTime` 使用 Unix **秒**，不是毫秒；`limit` 是时间桶的分页大小，并不代表 results 条数。Usage 使用 `nextPage`/`page`，与第 6 节的 `lastId`/`after` 不同。

`createProjectServiceAccount()` 返回 `ProjectServiceAccountCreated`，密钥在 `apiKey?.value`。服务器未返回密钥时该字段可以为空。Usage 的数据路径为 `page.data[].results[]`；Costs 金额在 `result.amount?.value`，币种在 `result.amount?.currency`。同一时间桶可包含多个分组，不能只读第一个结果。

`validateAdminExternalStorage(id)` 接收已创建的存储 ID，返回存储配置及 `status`。项目角色路径以及数组参数的 `[]` 编码由 SDK 处理。

## 10. Agents 与 ChatKit

Beta 版本头由 SDK 自动添加：Agents/Vaults 使用 `agents=v1`，ChatKit 使用 `chatkit_beta=v1`。可通过单次 `RequestOptions.headers` 覆盖。

`BetaSessions.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/BetaSessions.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import com.aallam.openai.api.beta.AgentId
import com.aallam.openai.api.beta.AgentSessionCreateRequest
import com.aallam.openai.api.beta.ChatKitSessionCreateRequest
import com.aallam.openai.api.beta.ChatKitWorkflow
import com.aallam.openai.client.OpenAI
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal suspend fun startAgentSession(openAI: OpenAI, agentId: String) =
    openAI.createAgentSession(AgentSessionCreateRequest(
        environment = buildJsonObject { put("type", "none") },
        agentId = AgentId(agentId),
    ))

internal suspend fun createChatKitSession(openAI: OpenAI, userId: String, workflowId: String) =
    openAI.createChatKitSession(ChatKitSessionCreateRequest(
        user = userId,
        workflow = ChatKitWorkflow(id = workflowId),
    ))
```

创建 ChatKit 会话等需要长期凭据的操作由后端执行，再把适用的临时会话信息交给移动端。Agent 的 `environment` 是必需的对象；示例为不使用执行环境，托管/自托管环境按相应 schema 传入。ChatKit 的 `workflow` 是对象，包含 `id`，可选 `version`、`stateVariables` 和 `tracing`。这些能力还取决于账户权限与服务端可用性。

## 11. 超时、异常与单次请求选项

HTTP 调用失败会抛出 `OpenAIException` 的子类，例如 `AuthenticationException`、`RateLimitException`、`InvalidRequestException`、`OpenAITimeoutException`。协程取消会继续传播；不要把 `CancellationException` 转换成普通业务失败。

`RequestOptionsExample.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/RequestOptionsExample.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.ResponseInput
import com.aallam.openai.api.response.ResponseRequest
import com.aallam.openai.client.OpenAI
import kotlin.time.Duration.Companion.seconds

internal suspend fun askWithOptions(openAI: OpenAI, model: String, prompt: String) =
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
| 找不到文档中的方法 | 核对实际解析的 Maven 版本，按第 13 节检查依赖 |

### 11.1 将错误转换成业务状态

下面仅在 SDK 失败时回调业务层，协程取消仍向上传播。应用可根据异常类型区分需要重新登录、修改请求、稍后重试或显示网络提示；详细信息写入受控日志，不要把内部服务错误原样展示给终端用户。

`ErrorHandling.kt`：

<!-- consumer-file: shared/src/commonMain/kotlin/com/example/shared/ai/ErrorHandling.kt -->
<!-- compile: common -->
```kotlin
package com.example.shared.ai

import com.aallam.openai.api.exception.AuthenticationException
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.exception.OpenAIException
import com.aallam.openai.api.exception.RateLimitException
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CancellationException

internal suspend fun askHandlingFailure(
    openAI: OpenAI,
    model: String,
    prompt: String,
    onFailure: (String) -> Unit,
): String? = try {
    ask(openAI, model, prompt)
} catch (cancelled: CancellationException) {
    throw cancelled
} catch (failure: AuthenticationException) {
    onFailure("服务凭据无效，请检查后端配置")
    null
} catch (failure: RateLimitException) {
    onFailure("请求频率或额度受限，请稍后重试")
    null
} catch (failure: InvalidRequestException) {
    onFailure("请求参数不受支持，请检查模型和输入")
    null
} catch (failure: OpenAIException) {
    onFailure("服务请求失败，请检查网络或稍后重试")
    null
}
```

这里不捕获所有业务异常，Responses 的 `failed/incomplete`、流事件错误以及 JSON 解析失败仍由更外层的业务逻辑处理。不要对包含上传、资源创建或已消费部分输出的操作无条件自动重放；根据业务幂等性和已经完成的步骤决定恢复方式。

## 12. 当前覆盖范围与迁移变化

| 能力 | 当前范围 |
| --- | --- |
| Responses | 创建、流式创建、读取、取消、删除、输入项、压缩、输入 token 计数；模型字段仍是官方能力的子集 |
| Conversations | 创建/读取/更新/删除；未完整覆盖 items 子资源 |
| Videos | 创建、读取、列表、删除、下载；不代表所有视频编辑等能力均已同步 |
| Evals | Eval CRUD/列表；未覆盖 runs/output_items 的完整执行流程 |
| Realtime / Live | 当前暴露的 REST 方法和 Realtime WebSocket；不包含跨平台音视频采集、播放和 WebRTC 栈 |
| Admin / Beta | 当前源码暴露的资源；部分官方方法和字段尚未覆盖 |
| 其他 | Chat、Files、Images、Audio、Embeddings、Batch 等参见 [现有使用手册](GettingStarted.md) 和对应接口源码 |

如果你的旧版依赖仍使用以下旧结构，迁移到当前产物时应注意：

- `acceptRealtimeCall(id, request)` 增加必传的 GA session JSON。
- Realtime/Live 的 accept/reject/hangup/refer 返回 `Unit`。
- `RealtimeCall` 改为读取 `sdp`、`location`、可空 `id` 和 `headers`。
- `forkLiveSession()` 返回 `LiveSessionCreated`，通过 `session`、`transport` 访问结果。
- `AgentSessionCreateRequest.environment` 必传；`ChatKitSessionCreateRequest` 必传 user 和 `ChatKitWorkflow`。
- Usage 从 `data[].指标` 改为 `data[].results[].指标`，Costs 金额为对象。
- 创建 Admin key/ServiceAccount 返回专门的创建响应类型；存储校验改为传 ID，读取配置状态。
- Webhook 签名使用 Base64；移除自定义的十六进制签名夹具。

## 13. 在接入工程中验证

### 13.1 编译自己的 shared 模块

在**接入工程根目录**运行，模块名按实际情况替换。示例验证使用 JDK 17，Gradle 和 Kotlin daemon 的最大堆内存均为 2 GiB；已有工程沿用自己的 Gradle 内存配置，链接较大的 Native framework 时确保内存充足。

```sh
# Android 共享库
./gradlew :shared:assembleDebug

# iOS：macOS + Xcode 环境下编译真机代码，并链接 Apple Silicon 模拟器 framework
./gradlew :shared:compileKotlinIosArm64 \
  :shared:linkDebugFrameworkIosSimulatorArm64

# 确认 Android 实际使用的 Maven 产物
./gradlew :shared:dependencyInsight \
  --dependency openai-client --configuration debugCompileClasspath
```

Android 依赖解析结果应包含 `com.aallam.openai:openai-client:4.1.0-local.2`，并选择其 JVM 变体；iOS 由 Gradle 选择对应的 Native 变体。若结果中出现 SDK 源码的 `project :openai-client`，应移除旧的源码替换配置，才能验证真实 Maven 接入。

采用 1.5 可选目标时，再执行：

```sh
./gradlew :shared:compileKotlinJvm :shared:compileKotlinJs :shared:compileKotlinWasmJs
```

最后在自己的应用中注入可用的 baseUrl、令牌和模型，实际验证一次 `AiService.answer()` 和一次流式调用；编译成功不代表后端权限、CORS、模型配置已经正确。

### 13.2 常见接入问题

| 现象 | 接入方处理方式 |
| --- | --- |
| 找不到 `4.1.0-local.2` | 同机检查 Maven Local 路径与 Gradle 仓库配置；异机/CI 向 SDK 提供方确认真实仓库地址及已发布版本 |
| 只添加 Maven Central 后解析失败 | 当前版本不在 Central，补充提供方的仓库；当前机器使用 1.2 的 Maven Local 配置 |
| 找到 client 却找不到 core、POM 或平台变体 | 将缺失的完整坐标反馈给 SDK 提供方，确认仓库中产物完整；接入方不需要修改 SDK 发布脚本 |
| `No matching variant` | commonMain 应使用根坐标；确认目标平台及 Kotlin/Gradle/AGP 版本相容 |
| `Unresolved reference: iosMain` | 检查工程已有的 source set 层级，将 Darwin 放到实际存在的 Apple source set |
| 找不到 HTTP engine | 为运行中的平台添加引擎；放在 androidMain 的 OkHttp 不会提供给 iOS |
| `Unsupported class file` 或 JVM target 冲突 | 当前本地产物由 JDK 17 构建；使用兼容的 Android 工具链，按示例对齐 Java/Kotlin target |
| Android 没有网络权限 | 检查最终合并清单是否包含 `android.permission.INTERNET` |
| iOS framework 链接失败 | 确认 macOS/Xcode 工具链、目标架构和 iOS 引擎依赖；先在 shared 执行 framework 链接任务定位 |
| 新依赖仍显示旧方法 | 用 dependencyInsight 核对版本；更新版本后同步 Gradle，必要时加 `--refresh-dependencies` |
| SDK 类型暴露到 UI/Swift 后找不到类型 | 优先像 AiService 一样在 shared 内封装；确需暴露依赖类型时再设计 `api`/framework export，避免直接导出整个 SDK |

### 13.3 本文配置的验证范围

本文的配置和 20 个 Kotlin 示例文件已用于生成独立的多模块 KMP 接入工程：根工程＋`:shared`，SDK 仅通过 `4.1.0-local.2` Maven 依赖解析。2026-09-22 完成以下验证：

| 接入方验证项 | 结果 |
| --- | --- |
| Android 共享模块 | `:shared:assembleDebug` 成功，生成 AAR |
| iOS 真机代码 | `:shared:compileKotlinIosArm64` 成功 |
| iOS 模拟器 framework | `:shared:linkDebugFrameworkIosSimulatorArm64` 成功；导出头中包含 AiService 的创建、answer、stream 和 close 方法 |
| 可选 KMP 目标 | JVM、JS、Wasm JS 的共享代码编译成功 |
| 依赖选择 | Android 的 dependencyInsight 确认解析到 `openai-client:4.1.0-local.2` 及 JVM 变体 |
| 业务封装调用 | 在同一 KMP 工程的 jvmTest 中使用本地 HTTP 测试服务，验证 AiService 普通文本请求、SSE 文本增量、完成事件及 Bearer 鉴权通过 |

这些检查未调用真实 OpenAI API，也未运行 Android/iOS 页面。应用账户、真实后端及 UI 生命周期仍按 13.1 在业务工程中联调。

SDK 提供方需要重新构建或发布时，参见 [SDK 维护与本地发布](MaintainerPublishing.zh-CN.md)。这些维护操作不是接入方的步骤。
