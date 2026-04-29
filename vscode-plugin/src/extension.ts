import * as path from 'path';
import * as vscode from 'vscode';
import { NativeTools, ProcessDetailInfo } from './nativeTools';

let output: vscode.OutputChannel;

export function activate(context: vscode.ExtensionContext) {
    output = vscode.window.createOutputChannel('LuaAttachDebug');
    const tools = new NativeTools(context, output);
    context.subscriptions.push(output);

    context.subscriptions.push(vscode.commands.registerCommand('luaAttachDebug.pickAndAttach', async () => {
        ensureWindows();
        const encoding = vscode.workspace.getConfiguration('luaAttachDebug').get<string>('encoding', 'gbk') as BufferEncoding;
        const processes = await tools.listProcesses(encoding);
        const picked = await vscode.window.showQuickPick(processes.map(toQuickPickItem), { placeHolder: 'Select a Windows process to attach' });
        if (picked) await attachAndReport(tools, picked.process.pid);
    }));

    context.subscriptions.push(vscode.commands.registerCommand('luaAttachDebug.attachByPid', async () => {
        ensureWindows();
        const value = await vscode.window.showInputBox({ prompt: 'Process ID', validateInput: text => /^\d+$/.test(text) ? undefined : 'PID must be a number.' });
        if (value) await attachAndReport(tools, Number.parseInt(value, 10));
    }));

    context.subscriptions.push(vscode.commands.registerCommand('luaAttachDebug.launchAndAttach', async () => {
        ensureWindows();
        const config = vscode.workspace.getConfiguration('luaAttachDebug');
        const program = await vscode.window.showInputBox({ prompt: 'Lua executable', value: config.get<string>('luaProgram', 'lua') });
        if (!program) return;
        const args = await vscode.window.showInputBox({ prompt: 'Lua arguments', value: config.get<string>('luaArguments', '') }) ?? '';
        const defaultCwd = config.get<string>('workingDirectory', '${workspaceFolder}');
        const cwd = expandWorkspaceFolder(await vscode.window.showInputBox({ prompt: 'Working directory', value: defaultCwd }) ?? defaultCwd);
        output.show(true);
        const result = await vscode.window.withProgress({ location: vscode.ProgressLocation.Notification, title: 'Launching Lua process with Lua debug hook' }, () => tools.launchAndAttach(program, args, cwd));
        output.appendLine(`Launched PID ${result.pid}; Lua debugger port is ${result.port}.`);
        void vscode.window.showInformationMessage(`Lua process ${result.pid} launched. Lua debugger port: ${result.port}.`);
    }));
}

export function deactivate() {}

async function attachAndReport(tools: NativeTools, pid: number) {
    output.show(true);
    const port = await vscode.window.withProgress({ location: vscode.ProgressLocation.Notification, title: `Attaching Lua hook to PID ${pid}` }, () => tools.attach(pid));
    output.appendLine(`Attached to PID ${pid}; Lua debugger port is ${port}.`);
    void vscode.window.showInformationMessage(`Lua process ${pid} attached. Lua debugger port: ${port}.`);
}

function toQuickPickItem(process: ProcessDetailInfo): vscode.QuickPickItem & { process: ProcessDetailInfo } {
    return { label: `${process.pid}: ${process.title || path.basename(process.executablePath)}`, description: process.executablePath, process };
}

function ensureWindows() {
    if (process.platform !== 'win32') throw new Error('LuaAttachDebug native tools are Windows-only.');
}

function expandWorkspaceFolder(value: string): string {
    const folder = vscode.workspace.workspaceFolders?.[0]?.uri.fsPath ?? '';
    return value.replace('${workspaceFolder}', folder);
}
