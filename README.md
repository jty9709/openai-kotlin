# OpenAI API client for Kotlin

[![Maven Central](https://img.shields.io/maven-central/v/com.aallam.openai/openai-client?color=blue&label=Download)](https://central.sonatype.com/namespace/com.aallam.openai)
[![License](https://img.shields.io/github/license/Aallam/openai-kotlin?color=yellow)](LICENSE.md)
[![Documentation](https://img.shields.io/badge/docs-api-a97bff.svg?logo=kotlin)](https://mouaad.aallam.com/openai-kotlin/)

Kotlin client for [OpenAI's API](https://platform.openai.com/docs/api-reference) with multiplatform and coroutines
capabilities.

中文文档：[在 KMP 工程中接入 OpenAI SDK](guides/KmpIntegration.zh-CN.md)。面向接入方的 Android/iOS 共享模块，包含 Maven 仓库配置、commonMain 依赖、平台引擎、共享业务封装和详细调用示例。

## 📦 KMP Setup

当前产物为 **`com.aallam.openai:openai-client:4.1.0-local.2`**，由 Kotlin **2.1.21** 构建，已发布到当前机器的 Maven Local。同机的其他 KMP 工程可以直接使用；其他机器/CI 需要 SDK 提供方提供实际可访问的 Maven 仓库地址和版本。

在**接入工程**的 `settings.gradle.kts` 合并仓库配置：

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenLocal { content { includeGroup("com.aallam.openai") } }
        google()
        mavenCentral()
    }
}
```

在已有 KMP 共享模块的 `build.gradle.kts` 合并依赖：

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.aallam.openai:openai-client:4.1.0-local.2")
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

Gradle 自动为各目标选择 SDK 变体，业务调用放在 `commonMain`。完整 Android/iOS 模块配置、版本目录写法及 JVM/JS/Wasm 可选目标见[接入指南](guides/KmpIntegration.zh-CN.md)。SDK 提供方的发布命令见[维护文档](guides/MaintainerPublishing.zh-CN.md)。

## ⚡️ Getting Started

> [!NOTE]
> OpenAI encourages using environment variables for the API key.
> [Read more](https://help.openai.com/en/articles/5112595-best-practices-for-api-key-safety).

Create an instance of `OpenAI` client:

```kotlin
val openai = OpenAI(
    token = "your-api-key",
    timeout = Timeout(socket = 60.seconds),
    // additional configurations...
)
```

Or you can create an instance of `OpenAI` using a pre-configured `OpenAIConfig`:

```kotlin
val config = OpenAIConfig(
    token = apiKey,
    timeout = Timeout(socket = 60.seconds),
    // additional configurations...
)

val openAI = OpenAI(config)
```

Use your `OpenAI` instance to make API requests. [Learn more](guides/GettingStarted.md).

### Supported features

- [Responses](guides/GettingStarted.md#responses)
- [Conversations](guides/GettingStarted.md#conversations)
- [Models](guides/GettingStarted.md#models)
- [Chat](guides/GettingStarted.md#chat)
- [Images](guides/GettingStarted.md#images)
- [Videos](guides/GettingStarted.md#videos)
- [Embeddings](guides/GettingStarted.md#embeddings)
- [Files](guides/GettingStarted.md#files)
- [Fine-tuning](guides/GettingStarted.md#fine-tuning)
- [Moderations](guides/GettingStarted.md#moderations)
- [Audio](guides/GettingStarted.md#audio)
- [Batch](guides/GettingStarted.md#batch)
- [Evals](guides/GettingStarted.md#evals)
- [Uploads](guides/GettingStarted.md#uploads)
- [Webhooks](guides/GettingStarted.md#webhooks)
- [Containers](guides/GettingStarted.md#containers)
- [Skills](guides/GettingStarted.md#skills)
- [Safety](guides/GettingStarted.md#safety)
- [Live](guides/GettingStarted.md#live)
- [Realtime](guides/GettingStarted.md#realtime)
- [Admin](guides/GettingStarted.md#admin)

#### Beta

- [Assistants](guides/GettingStarted.md#assistants)
- [Threads](guides/GettingStarted.md#threads)
- [Messages](guides/GettingStarted.md#messages)
- [Runs](guides/GettingStarted.md#runs)
- [Vector Stores](guides/GettingStarted.md#vector-stores)
- [Agents](guides/GettingStarted.md#agents)
- [Vaults](guides/GettingStarted.md#vaults)
- [ChatKit](guides/GettingStarted.md#chatkit)

#### Deprecated
- [Completions](guides/GettingStarted.md#completions)
- [Fine-tunes](guides/GettingStarted.md#fine-tunes)
- [Edits](guides/GettingStarted.md#edits)

*Looking for a tokenizer? Try [ktoken](https://github.com/aallam/ktoken), a Kotlin library for tokenizing text.*

## 📚 Guides

Get started and understand more about how to use OpenAI API client for Kotlin with these guides:

- [Getting Started](guides/GettingStarted.md)
- [Chat & Tool Calls](guides/ChatToolCalls.md)
- [FileSource Guide](guides/FileSource.md)
- [Assistants](guides/Assistants.md)

## ℹ️ Sample apps

Sample apps are available under `sample`, please check the [README](sample/README.md) for running instructions.

## 🔒 ProGuard / R8

The specific rules are [already bundled](openai-core/src/jvmMain/resources/META-INF/proguard/openai.pro) into the Jar which can be interpreted by R8 automatically.

## 📸 Snapshots

[![Snapshot](https://img.shields.io/badge/dynamic/xml?url=https://central.sonatype.com/repository/maven-snapshots/com/aallam/openai/openai-client/maven-metadata.xml&label=snapshot&color=red&query=.//versioning/latest)](https://central.sonatype.com/repository/maven-snapshots/com/aallam/openai/openai-client/)

<details>
 <summary>Learn how to import snapshot version</summary>

To import snapshot versions into your project, add the following code snippet to your gradle file:

```groovy
repositories {
   //...
   maven { url 'https://central.sonatype.com/repository/maven-snapshots/' }
}
```

</details>

## 🛠️ Troubleshooting

For common issues and their solutions, check the [Troubleshooting Guide](TROUBLESHOOTING.md).

## 🧪 Testing

`openai-client` tests are live integration tests and can generate billable API traffic.

- Default (non-billable): live tests are disabled.
- Opt-in live tests: set `OPENAI_LIVE_TESTS=1` and `OPENAI_API_KEY`.

Examples:

```bash
# Free/offline checks
./gradlew :openai-core:jvmTest :openai-core:jsTest :openai-core:wasmJsTest :openai-core:apiCheck :openai-client:apiCheck

# Live smoke (billable)
OPENAI_LIVE_TESTS=1 OPENAI_API_KEY=... ./gradlew :openai-client:jvmTest --tests "*.TestModels"
```

## ⭐️ Support

Appreciate the project? Here's how you can help:

1. **Star**: Give it a star at the top right. It means a lot!
2. **Contribute**: Found an issue or have a feature idea? Submit a PR.
3. **Feedback**: Have suggestions? Open an issue or start a discussion.

## 📄 License

OpenAI Kotlin API Client is an open-sourced software licensed under the [MIT license](LICENSE.md).
**This is an unofficial library, it is not affiliated with nor endorsed by OpenAI**. Contributions are welcome.
