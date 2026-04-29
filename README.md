# LuaAttachDebug 插件

LuaAttachDebug 是一个面向 JetBrains IDE、VS Code 和 Cursor 的 Windows Lua 进程附加调试工具。它可以从 IDE/编辑器中选择目标进程、按 PID 附加，或启动 Lua 程序后自动注入调试 Hook，适合本地 Lua 程序调试和工具链集成。

本项目采用多模块结构，同时维护在两套主流生态下运行的独立插件：

- **IntelliJ 插件 (`intellij-plugin`)**: 支持 JetBrains 平台产品（IDEA, WebStorm, PyCharm, etc.）
- **VS Code 插件 (`vscode-plugin`)**: 支持 VS Code、Cursor 等基于 VS Code 架构的编辑器

## 📥 插件市场 (Marketplace)

### 1. JetBrains IDEA (IntelliJ 平台)
https://plugins.jetbrains.com/author/me

### 2. Visual Studio Code
https://marketplace.visualstudio.com/manage/publishers/kibernet

### 3. Cursor
https://open-vsx.org/

## 📄 开源协议
本插件基于 [Apache License 2.0](./intellij-plugin/LICENSE) 开源。
