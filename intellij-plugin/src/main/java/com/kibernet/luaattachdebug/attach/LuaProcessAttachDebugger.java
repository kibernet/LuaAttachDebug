package com.kibernet.LuaAttachDebug.attach;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessInfo;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.*;
import com.intellij.xdebugger.attach.XLocalAttachDebugger;
import com.kibernet.LuaAttachDebug.util.ProcessDetailInfo;
import com.kibernet.LuaAttachDebug.util.ProcessUtils;
import com.kibernet.LuaAttachDebug.util.DebugApiCompat;
import org.jetbrains.annotations.NotNull;

public final class LuaProcessAttachDebugger implements XLocalAttachDebugger {
    private final ProcessInfo processInfo;
    private final ProcessDetailInfo detailInfo;
    public LuaProcessAttachDebugger(ProcessInfo processInfo, ProcessDetailInfo detailInfo) { this.processInfo = processInfo; this.detailInfo = detailInfo; }
    @Override public void attachDebugSession(Project project, ProcessInfo processInfo) throws ExecutionException {
        String displayName = "PID:" + processInfo.getPid() + '(' + getDebuggerDisplayName() + ')';
        DebugApiCompat.startSessionAndShowTab(project, displayName, null, new XDebugProcessStarter() {
            @Override public @NotNull XDebugProcess start(@NotNull XDebugSession session) { return new LuaAttachDebugProcess(session, processInfo); }
        });
    }
    @Override public String getDebuggerDisplayName() { return ProcessUtils.getDisplayName(processInfo, detailInfo); }
}
