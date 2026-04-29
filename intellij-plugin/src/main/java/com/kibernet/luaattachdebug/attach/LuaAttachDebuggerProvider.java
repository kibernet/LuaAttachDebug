package com.kibernet.LuaAttachDebug.attach;

import com.intellij.execution.process.ProcessInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.SystemInfoRt;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.xdebugger.attach.XAttachDebuggerProvider;
import com.intellij.xdebugger.attach.XAttachHost;
import com.intellij.xdebugger.attach.XLocalAttachDebugger;
import com.kibernet.LuaAttachDebug.util.ProcessDetailInfo;
import com.kibernet.LuaAttachDebug.util.ProcessUtils;
import java.util.*;

public final class LuaAttachDebuggerProvider implements XAttachDebuggerProvider {
    public static final Key<Map<Integer, ProcessDetailInfo>> DETAIL_KEY = Key.create("LuaAttachDebug.ProcessDetail.key");
    @Override public boolean isAttachHostApplicable(XAttachHost attachHost) { return true; }
    @Override public List<XLocalAttachDebugger> getAvailableDebuggers(Project project, XAttachHost attachHost, ProcessInfo processInfo, UserDataHolder userDataHolder) {
        if (!SystemInfoRt.isWindows) return Collections.emptyList();
        Map<Integer, ProcessDetailInfo> details = userDataHolder.getUserData(DETAIL_KEY);
        if (details == null) { details = ProcessUtils.listProcesses(); userDataHolder.putUserData(DETAIL_KEY, details); }
        String executableName = processInfo.getExecutableName();
        if (executableName == null || !executableName.toLowerCase(Locale.ROOT).endsWith(".exe")) return Collections.emptyList();
        ProcessDetailInfo info = details.get(processInfo.getPid());
        if (info == null || info.getPath().isBlank()) return Collections.emptyList();
        return Collections.singletonList(new LuaProcessAttachDebugger(processInfo, info));
    }
    @Override public LuaAttachProcessGroup getPresentationGroup() { return LuaAttachProcessGroup.getInstance(); }
}
