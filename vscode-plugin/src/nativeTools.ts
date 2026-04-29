import * as cp from 'child_process';
import * as fs from 'fs';
import * as net from 'net';
import * as path from 'path';
import * as vscode from 'vscode';

export interface ProcessDetailInfo {
    pid: number;
    title: string;
    executablePath: string;
}

export class NativeTools {
    constructor(private readonly context: vscode.ExtensionContext, private readonly output: vscode.OutputChannel) {}

    async listProcesses(encoding: BufferEncoding = 'utf8'): Promise<ProcessDetailInfo[]> {
        const stdout = await this.execFile(this.toolPath('x86'), ['list_processes'], { encoding });
        const lines = stdout.split(/\r?\n/);
        const result: ProcessDetailInfo[] = [];
        for (let i = 0; i + 2 < lines.length; i += 4) {
            const pid = Number.parseInt(lines[i], 10);
            if (!Number.isFinite(pid)) continue;
            result.push({ pid, title: lines[i + 1] ?? '', executablePath: lines[i + 2] ?? '' });
        }
        return result;
    }

    async attach(pid: number): Promise<number> {
        const arch = await this.detectArchByPid(pid);
        const dir = this.nativeDir(arch);
        await this.execFile(this.toolPath(arch), ['attach', '-p', String(pid), '-dir', dir, '-dll', 'emmy_hook.dll'], { cwd: dir });
        return this.portFromPid(pid);
    }

    async launchAndAttach(program: string, args: string, workingDirectory: string): Promise<{ pid: number; port: number }> {
        const arch = await this.detectArchByFile(program);
        const dir = this.nativeDir(arch);
        const port = this.portFromPid(Math.floor(Math.random() * 10240) + 10240);
        const child = cp.spawn(this.toolPath(arch), ['run_and_attach', '-dll', 'emmy_hook.dll', '-dir', dir, '-work', workingDirectory, '-exe', program, '-unitbuf', '-debug-port', String(port), '-listen-mode', '-args', args], { cwd: dir, windowsHide: false });
        child.stdout.on('data', data => this.output.append(data.toString()));
        child.stderr.on('data', data => this.output.append(data.toString()));
        const pid = await this.waitForLaunchHandshake(port);
        return { pid, port: this.portFromPid(pid) };
    }

    private async waitForLaunchHandshake(port: number): Promise<number> {
        return new Promise((resolve, reject) => {
            const socket = net.createConnection({ host: '127.0.0.1', port });
            const timer = setTimeout(() => { socket.destroy(); reject(new Error('Timed out waiting for emmy_tool launch handshake.')); }, 10000);
            socket.once('data', data => {
                const pid = Number.parseInt(data.toString().trim(), 10);
                socket.write('connected'); clearTimeout(timer); socket.end();
                Number.isFinite(pid) ? resolve(pid) : reject(new Error(`Invalid launch PID: ${data.toString()}`));
            });
            socket.once('error', error => { clearTimeout(timer); reject(error); });
        });
    }

    private async detectArchByPid(pid: number): Promise<'x86' | 'x64'> {
        try { await this.execFile(this.toolPath('x86'), ['arch_pid', String(pid)]); return 'x64'; } catch { return 'x86'; }
    }

    private async detectArchByFile(file: string): Promise<'x86' | 'x64'> {
        try { await this.execFile(this.toolPath('x64'), ['arch_file', file]); return 'x64'; } catch { return 'x86'; }
    }

    private execFile(file: string, args: string[], options: { cwd?: string; encoding?: BufferEncoding } = {}): Promise<string> {
        return new Promise((resolve, reject) => {
            cp.execFile(file, args, { cwd: options.cwd, encoding: options.encoding ?? 'utf8', windowsHide: true }, (error, stdout, stderr) => {
                if (stdout) this.output.append(stdout.toString());
                if (stderr) this.output.append(stderr.toString());
                error ? reject(error) : resolve(stdout.toString());
            });
        });
    }

    private toolPath(arch: 'x86' | 'x64'): string { return path.join(this.nativeDir(arch), 'emmy_tool.exe'); }

    private nativeDir(arch: 'x86' | 'x64'): string {
        const dir = path.join(this.context.extensionPath, 'resources', 'debugger', 'emmy', 'windows', arch);
        if (!fs.existsSync(dir)) throw new Error(`Native debugger directory not found: ${dir}`);
        return dir;
    }

    private portFromPid(pid: number): number {
        let port = pid;
        while (port > 65535) port -= 65535;
        while (port < 1024) port += 1024;
        return port;
    }
}
