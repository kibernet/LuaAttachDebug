# LuaAttachDebug for IntelliJ Platform

版本：`1.0.0`

LuaAttachDebug 为 JetBrains IntelliJ Platform 提供 Windows Lua 进程附加调试能力。插件依赖外部 Lua 调试基础能力提供调试协议、运行配置基类和 UI 图标。

## 功能

- 在运行配置中提供 `LuaAttachDebug`。
- 在运行配置中提供 `LuaAttachDebug Launch`。
- 在 `Attach to Process` 中显示可附加的 Windows 进程。
- 自动选择 x86/x64 调试工具并注入 Lua 调试 Hook。

## 构建

```bat
gradlew.bat --no-daemon --console=plain clean buildPlugin
```
