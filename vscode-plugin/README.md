# Lua Attach Debug

VS Code/Cursor 版本的 Lua Attach Debug，版本 `1.0.0`。

插件包含 Windows 平台的原生调试工具：`emmy_tool.exe`、`emmy_hook.dll`、`emmy_core.dll`、`EasyHook.dll`。

## 命令

- `Lua Attach Debug: Pick Process and Attach`
- `Lua Attach Debug: Attach by PID`
- `Lua Attach Debug: Launch and Attach`

注入成功后，输出面板会显示 Lua 调试端口。完整断点、变量、调用栈能力需要 VS Code 中已有兼容 Lua 调试协议的调试适配器。
