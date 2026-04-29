package com.kibernet.luaattachdebug.launch;

import com.google.gson.Gson;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.*;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.xdebugger.XDebugSession;
import com.kibernet.luaattachdebug.attach.AttachSupport;
import com.kibernet.luaattachdebug.util.FileUtils;
import com.tang.intellij.lua.debugger.DebugLogger;
import com.tang.intellij.lua.debugger.LogConsoleType;
import com.tang.intellij.lua.debugger.emmy.*;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.*;

public final class LuaLaunchDebugProcess extends EmmyDebugProcessBase {
    private Socket client;
    private final LuaLaunchDebugConfiguration configuration;
    @SuppressWarnings("unused") private final Project project;
    public LuaLaunchDebugProcess(XDebugSession session, LuaLaunchDebugConfiguration configuration, Project project) { super(session); this.configuration = configuration; this.project = project; }
    @Override protected void setupTransporter() {
        try { if (configuration.getUseWindowsTerminal()) runAndAttachUseWindowsTerminal(); else runAndAttach(); }
        catch (Exception e) { println(e.getMessage() == null ? e.toString() : e.getMessage(), LogConsoleType.NORMAL, ConsoleViewContentType.ERROR_OUTPUT); getSession().stop(); }
    }
    private void attachTo(int pid) {
        SocketClientTransporter transporter = new SocketClientTransporter("localhost", AttachSupport.portFromPid(pid));
        transporter.setHandler((ITransportHandler)this); transporter.setLogger((DebugLogger)this); setTransporter((Transporter)transporter); transporter.start();
    }
    private String detectArch() throws Exception {
        String tool = FileUtils.getPluginVirtualFile("debugger/emmy/windows/x64/emmy_tool.exe");
        GeneralCommandLine commandLine = new GeneralCommandLine(tool); commandLine.addParameters("arch_file", configuration.getProgram());
        Process process = commandLine.createProcess(); process.waitFor(); return process.exitValue() == 0 ? "x64" : "x86";
    }
    private void runAndAttachUseWindowsTerminal() throws Exception {
        int port = AttachSupport.portFromPid(ThreadLocalRandom.current().nextInt(10240) + 10240);
        String path = FileUtils.getNativeDirectory(detectArch());
        GeneralCommandLine commandLine = new GeneralCommandLine(); commandLine.setExePath("wt"); commandLine.setWorkDirectory(path);
        commandLine.addParameters("--title", displayName(configuration.getProgram()), "emmy_tool.exe", "run_and_attach", "-dll", "emmy_hook.dll", "-dir", quote(path), "-work", quote(configuration.getWorkingDirectory()), "-block-on-exit", "-exe", quote(configuration.getProgram()), "-debug-port", String.valueOf(port), "-listen-mode", "-args", quote(configuration.getParameter()));
        OSProcessHandler handler = new OSProcessHandler(commandLine); addConsoleForwarder(handler); handler.startNotify(); completeHandshake(port);
    }
    private void runAndAttach() throws Exception {
        int port = AttachSupport.portFromPid(ThreadLocalRandom.current().nextInt(10240) + 10240);
        String path = FileUtils.getNativeDirectory(detectArch());
        GeneralCommandLine commandLine = new GeneralCommandLine(); commandLine.setExePath(path + "/emmy_tool.exe"); commandLine.setWorkDirectory(path);
        commandLine.addParameters("run_and_attach", "-dll", "emmy_hook.dll", "-dir", quote(path), "-work", quote(configuration.getWorkingDirectory()), "-exe", quote(configuration.getProgram()), "-unitbuf", "-debug-port", String.valueOf(port), "-listen-mode", "-args", quote(configuration.getParameter()));
        ColoredProcessHandler handler = new ColoredProcessHandler(commandLine); addConsoleForwarder(handler); handler.startNotify(); completeHandshake(port);
    }
    private void completeHandshake(int port) throws Exception {
        client = new Socket("localhost", port); client.setSoTimeout(10000);
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream())); int pid = Integer.parseInt(reader.readLine());
        attachTo(pid); Thread.sleep(300L); BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream())); writer.write("connected"); writer.flush();
    }
    private void addConsoleForwarder(OSProcessHandler handler) {
        handler.addProcessListener(new ProcessAdapter() { @Override public void onTextAvailable(ProcessEvent event, Key outputType) {
            ConsoleViewContentType type = outputType == ProcessOutputTypes.STDERR ? ConsoleViewContentType.ERROR_OUTPUT : ConsoleViewContentType.SYSTEM_OUTPUT; print(event.getText(), LogConsoleType.NORMAL, type);
        }});
    }
    private static String quote(String value) { return '"' + (value == null ? "" : value) + '"'; }
    private static String displayName(String program) { Matcher matcher = Pattern.compile("[^/\\]+$").matcher(program == null ? "" : program); return matcher.find() ? matcher.group() : program; }
    @Override public void onDisconnect() { super.onDisconnect(); if (client != null) try { client.close(); } catch (Exception ignored) {} }
    @Override public void onReceiveMessage(MessageCMD cmd, String json) {
        if (cmd == MessageCMD.AttachedNotify) { AttachedNotify msg = new Gson().fromJson(json, AttachedNotify.class); println("Attached to lua state 0x" + Long.toString(msg.getState(), 16), LogConsoleType.NORMAL, ConsoleViewContentType.SYSTEM_OUTPUT); }
        else super.onReceiveMessage(cmd, json);
    }
}
