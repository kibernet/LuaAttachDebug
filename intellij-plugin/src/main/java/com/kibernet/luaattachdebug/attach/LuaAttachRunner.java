package com.kibernet.LuaAttachDebug.attach;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.*;
import com.kibernet.LuaAttachDebug.util.DebugApiCompat;
import com.tang.intellij.lua.debugger.LuaRunner;
import org.jetbrains.annotations.NotNull;

public final class LuaAttachRunner extends LuaRunner {
    public static final String ID = "LuaAttachDebug.attach.runner";
    private LuaAttachDebugConfiguration configuration;
    @Override public String getRunnerId() { return ID; }
    @Override public boolean canRun(String executorId, RunProfile runProfile) {
        if ("Debug".equals(executorId) && runProfile instanceof LuaAttachDebugConfiguration attachConfiguration) { configuration = attachConfiguration; return true; }
        return false;
    }
    @Override protected RunContentDescriptor doExecute(RunProfileState state, ExecutionEnvironment environment) throws ExecutionException {
        Project project = environment.getProject();
        XDebugSession session = DebugApiCompat.startSession(project, environment, new XDebugProcessStarter() {
            @Override public @NotNull XDebugProcess start(@NotNull XDebugSession session) { return new LuaConfigAttachDebugProcess(session, configuration); }
        });
        return DebugApiCompat.resolveDescriptor(session);
    }
}
