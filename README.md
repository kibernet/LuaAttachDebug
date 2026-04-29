# Lua Attach Debug

Lua Attach Debug 是面向 JetBrains IDE、VS Code 和 Cursor 的 Lua 进程附加调试插件工程，版本号为 `1.0.0`，包名为 `com.kibernet.luaattachdebug`。

- `intellij-plugin`: JetBrains IntelliJ Platform 插件源码，依赖 Lua 调试基础插件提供调试协议能力。
- `vscode-plugin`: VS Code/Cursor 插件源码，封装 Windows Emmy 调试工具。

## 构建

```bat
build.bat
```

构建产物：

- IntelliJ: `intellij-plugin/build/distributions/*.zip`
- VS Code: `vscode-plugin/luaattachdebug-1.0.0.vsix`

## 运行限制

当前原生调试工具仅支持 Windows。IntelliJ 版本复用外部 Lua 调试协议实现；VS Code 版本负责启动、注入并输出连接端口，完整断点、变量、调用栈体验需要 VS Code 侧已有兼容 Lua 调试协议的调试适配器配合。

## 开源协议

本工程使用 [Apache License 2.0](./LICENSE)。
