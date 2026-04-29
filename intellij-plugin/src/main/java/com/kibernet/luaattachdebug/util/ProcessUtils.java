package com.kibernet.LuaAttachDebug.util;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessInfo;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.execution.util.ExecUtil;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ProcessUtils {
    private static final int MAX_DISPLAY_LEN = 60;
    private ProcessUtils() {}
    public static String getDisplayName(ProcessInfo processInfo, ProcessDetailInfo detailInfo) {
        String name = detailInfo.getTitle().isEmpty() ? processInfo.getExecutableName() : processInfo.getExecutableName() + " - " + detailInfo.getTitle();
        return name.length() > MAX_DISPLAY_LEN ? name.substring(0, MAX_DISPLAY_LEN) + "..." : name;
    }
    public static Map<Integer, ProcessDetailInfo> listProcesses() {
        Map<Integer, ProcessDetailInfo> processMap = new LinkedHashMap<>();
        for (ProcessDetailInfo process : listProcessesByEncoding(null)) processMap.put(process.getPid(), process);
        return processMap;
    }
    public static List<ProcessDetailInfo> listProcessesByEncoding(String encoding) {
        List<ProcessDetailInfo> processes = new ArrayList<>();
        String archExe = FileUtils.getArchExeFile();
        if (archExe == null) return processes;
        try {
            GeneralCommandLine commandLine = new GeneralCommandLine(archExe);
            if (encoding != null && !encoding.isBlank()) commandLine.setCharset(Charset.forName(encoding));
            commandLine.addParameter("list_processes");
            ProcessOutput output = ExecUtil.execAndGetOutput(commandLine);
            String[] lines = output.getStdout().split("\\R");
            for (int i = 0; i + 2 < lines.length; i += 4) {
                int pid = Integer.parseInt(lines[i].trim());
                processes.add(new ProcessDetailInfo(pid, lines[i + 2], lines[i + 1]));
            }
        } catch (Exception ignored) {}
        return processes;
    }
}
