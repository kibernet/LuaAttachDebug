package com.kibernet.LuaAttachDebug.attach;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.util.Key;
import com.kibernet.LuaAttachDebug.util.FileUtils;
import com.tang.intellij.lua.debugger.LogConsoleType;
import com.tang.intellij.lua.debugger.emmy.EmmyDebugProcessBase;

public final class AttachSupport {
    private AttachSupport() {}
    public static boolean attach(EmmyDebugProcessBase process, int pid) {
        String arch = detectArchByPid(pid);
        String path = FileUtils.getNativeDirectory(arch);
        GeneralCommandLine commandLine = new GeneralCommandLine(path + "/emmy_tool.exe");
        commandLine.addParameters("attach", "-p", String.valueOf(pid), "-dir", path, "-dll", "emmy_hook.dll");
        try {
            OSProcessHandler handler = new OSProcessHandler(commandLine);
            handler.addProcessListener(new ProcessListener() {
                @Override public void processTerminated(ProcessEvent event) {}
                @Override public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {}
                @Override public void startNotified(ProcessEvent event) {}
                @Override public void onTextAvailable(ProcessEvent event, Key outputType) {
                    ConsoleViewContentType type = outputType == ProcessOutputTypes.STDERR ? ConsoleViewContentType.ERROR_OUTPUT : ConsoleViewContentType.SYSTEM_OUTPUT;
                    process.print(event.getText(), LogConsoleType.NORMAL, type);
                }
            });
            handler.startNotify(); handler.waitFor();
            Integer exitCode = handler.getExitCode();
            return exitCode != null && exitCode == 0;
        } catch (Exception e) {
            process.println(e.getMessage() == null ? e.toString() : e.getMessage(), LogConsoleType.NORMAL, ConsoleViewContentType.ERROR_OUTPUT);
            return false;
        }
    }
    public static String detectArchByPid(int pid) {
        String tool = FileUtils.getPluginVirtualFile("debugger/emmy/windows/x86/emmy_tool.exe");
        try {
            GeneralCommandLine commandLine = new GeneralCommandLine(tool); commandLine.addParameters("arch_pid", String.valueOf(pid));
            Process p = commandLine.createProcess(); p.waitFor(); return p.exitValue() == 0 ? "x64" : "x86";
        } catch (Exception ignored) { return "x86"; }
    }
    public static int portFromPid(int pid) { int port = pid; while (port > 65535) port -= 65535; while (port < 1024) port += 1024; return port; }
}
