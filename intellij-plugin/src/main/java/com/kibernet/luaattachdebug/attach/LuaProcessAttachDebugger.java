package com.kibernet.luaattachdebug.attach;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessInfo;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.*;
import com.intellij.xdebugger.attach.XLocalAttachDebugger;
import com.kibernet.luaattachdebug.util.ProcessDetailInfo;
import com.kibernet.luaattachdebug.util.ProcessUtils;
import org.jetbrains.annotations.NotNull;

public final class LuaProcessAttachDebugger implements XLocalAttachDebugger {
    private final ProcessInfo processInfo;
    private final ProcessDetailInfo detailInfo;
    public LuaProcessAttachDebugger(ProcessInfo processInfo, ProcessDetailInfo detailInfo) { this.processInfo = processInfo; this.detailInfo = detailInfo; }
    @Override public void attachDebugSession(Project project, ProcessInfo processInfo) throws ExecutionException {
        String displayName = "PID:" + processInfo.getPid() + '(' + getDebuggerDisplayName() + ')';
        XDebuggerManager.getInstance(project).startSessionAndShowTab(displayName, null, new XDebugProcessStarter() {
            @Override public @NotNull XDebugProcess start(@NotNull XDebugSession session) { return new LuaAttachDebugProcess(session, processInfo); }
        });
    }
    @Override public String getDebuggerDisplayName() { return ProcessUtils.getDisplayName(processInfo, detailInfo); }
}
