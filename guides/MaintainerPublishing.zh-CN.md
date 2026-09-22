# SDK 维护与本地发布

本文面向 SDK 维护者，所有命令在 SDK 源码仓库根目录执行。第三方 KMP 工程直接使用 Maven 依赖，见 [KMP 接入指南](KmpIntegration.zh-CN.md)。

当前本地发布版本为 `4.1.0-local.2`，Kotlin 为 `2.1.21`。下列记录对应本次发布时的 SDK 回归验证。

## 发布到 Maven Local

进入本仓库根目录执行：

```sh
./gradlew :openai-core:publishToMavenLocal \
  :openai-client:publishToMavenLocal \
  :openai-client-bom:publishToMavenLocal \
  -PVERSION_NAME=4.1.0-local.2 \
  -PsignAllPublications=false \
  -PmavenCentralPublishing=false \
  --max-workers=2 --console=plain
```

三个任务分别发布数据模型、客户端和 BOM。客户端依赖 core；不要只发布 client，也不要只复制一个 JAR 或 KLIB，否则其他平台可能缺少变体或传递依赖。KMP 根模块、平台模块、POM 和 Gradle `.module` 元数据应一起保留。

命令中的 `-P` 仅覆盖本次构建属性，不修改仓库默认版本。`publishToMavenLocal` 不需要 GitHub、Maven Central 凭据或 GPG 密钥。首次构建仍需下载 Gradle、Kotlin/Native 工具链及第三方依赖。

默认输出位置：

```text
~/.m2/repository/com/aallam/openai/
├── openai-core/4.1.0-local.2/
├── openai-client/4.1.0-local.2/
├── openai-client-bom/4.1.0-local.2/
├── openai-core-jvm/4.1.0-local.2/
├── openai-client-jvm/4.1.0-local.2/
├── openai-client-iosarm64/4.1.0-local.2/
├── openai-client-iossimulatorarm64/4.1.0-local.2/
└── …其他平台产物
```

如果 Maven `settings.xml` 设置了 `localRepository`，以自定义路径为准。当前构建脚本只在 macOS 上声明 Apple targets；要发布 iOS/macOS/tvOS/watchOS 产物，应在 macOS 上运行上述命令并准备好 Apple 工具链。

**mavenLocal 只供当前机器使用。** Git push 不会上传这些包，另一台机器也不会自动获得它们；其他开发者可检出同一代码自行执行发布命令，团队共享则应发布到团队 Maven 仓库。再次修改 SDK 时建议改成 `4.1.0-local.3`，并同步修改使用方版本，避免覆盖同版本后的缓存混淆。

## 发布回归验证

### 发布时验证记录

2026-09-22，在 macOS ARM64 / JDK 17 环境中完成：

| 验证项 | 结果 |
| --- | --- |
| Kotlin 升级回归 | Kotlin `2.1.21` 下 17 个 SDK 平台目标编译通过，JVM API 快照与格式检查通过 |
| 离线测试 | core/client 在 JVM、JS、Wasm JS、macOS ARM64 上共 312 项通过；3 项原有 Wasm 测试跳过 |
| 仓库样例 | JVM、JS、Native 的 `assemble` 均通过，Native 包括 debug/release 可执行文件链接 |
| Maven Local 发布 | core、client 的根模块和 17 个平台模块，以及 BOM，共 37 个模块，版本均为 `4.1.0-local.2` |
| 发布元数据 | 所有 SDK 间依赖版本一致；校验了变体指向的文件和 SHA-256 |
| KMP 独立使用方 | 从发布时的指南提取 21 段 Kotlin 示例，只使用 mavenLocal/远端第三方依赖，没有 includeBuild |
| 示例编译 | JVM、JS、Wasm JS、iOS ARM64、iOS Simulator ARM64 均通过 |
| JVM BOM | 无版本的 client/OkHttp 正确解析，默认引擎创建及 MockEngine 请求测试通过 |
| Maven POM 依赖 | 使用发布时的 JVM pom 坐标、禁用 Gradle 元数据后，JVM 示例编译和同一离线请求测试通过；此项由 Gradle 的 POM 解析执行，未运行 Maven CLI |

验证没有发送真实 OpenAI API 请求，也没有运行 Android/iOS 应用。编译通过证明本文示例与已发布 SDK 的 API、依赖相容；账号权限、模型可用性、录音播放和真实网络行为需在业务环境中联调。

### SDK 源码回归命令

在 SDK 仓库根目录执行：

```sh
./gradlew :openai-core:jvmTest :openai-client:jvmTest \
  :openai-core:apiCheck :openai-client:apiCheck

./gradlew :openai-core:jsTest :openai-client:jsTest \
  :openai-core:wasmJsTest :openai-client:wasmJsTest

# macOS 主机的 Native 验证
./gradlew :openai-core:macosArm64Test :openai-client:macosArm64Test \
  :openai-client:compileKotlinIosSimulatorArm64

# 样例工程打包（Native 样例在当前宿主上构建）
./gradlew :sample:jvm:assemble :sample:js:assemble :sample:native:assemble
```

JS/Wasm 测试链接较大的客户端代码时需要足够堆内存，仓库已为 Gradle 和 Kotlin daemon 配置各 2 GiB；内存较紧张时可加 `--max-workers=2`。

从旧 Kotlin 版本升级后，若 JS/Wasm 构建提示 `Lock file was changed`，先运行 `./gradlew kotlinUpgradeYarnLock` 更新本机 Yarn 锁文件，再重新执行测试。不要关闭锁文件校验来绕过依赖变更。

默认客户端测试仅运行离线测试，不需要 API key。`OPENAI_LIVE_TESTS=1` 才会放开真实 API 测试；该测试可能产生费用和远端资源，按需要显式选择测试类。修改公共 API 后运行 `:openai-core:apiDump :openai-client:apiDump` 更新快照，再执行 `apiCheck`。

协议回归位于 `TestSdkContracts`、`TestSdkContractModels` 和 `TestWebhookSignature`。接入方最新的文档配置与验证结果见 [KMP 接入指南](KmpIntegration.zh-CN.md)。
