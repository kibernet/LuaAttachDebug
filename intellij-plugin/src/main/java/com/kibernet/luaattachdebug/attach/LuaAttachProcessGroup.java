package com.kibernet.LuaAttachDebug.attach;

import com.intellij.execution.process.ProcessInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.xdebugger.attach.XAttachProcessPresentationGroup;
import com.kibernet.LuaAttachDebug.util.ProcessDetailInfo;
import com.kibernet.LuaAttachDebug.util.ProcessUtils;
import com.tang.intellij.lua.lang.LuaIcons;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.Locale;
import java.util.Map;

public final class LuaAttachProcessGroup implements XAttachProcessPresentationGroup {
    private static final LuaAttachProcessGroup INSTANCE = new LuaAttachProcessGroup();
    private LuaAttachProcessGroup() {}
    public static LuaAttachProcessGroup getInstance() { return INSTANCE; }
    @Override public String getGroupName() { return "LuaAttachDebug"; }
    @Override public int getOrder() { return 0; }
    @Override public int compare(ProcessInfo a, ProcessInfo b) { return a.getExecutableName().toLowerCase(Locale.ROOT).compareTo(b.getExecutableName().toLowerCase(Locale.ROOT)); }
    @Override public String getItemDisplayText(Project project, ProcessInfo processInfo, UserDataHolder userDataHolder) { ProcessDetailInfo detail = getDetail(processInfo, userDataHolder); return detail == null ? processInfo.getExecutableName() : ProcessUtils.getDisplayName(processInfo, detail); }
    @Override public Icon getItemIcon(Project project, ProcessInfo processInfo, UserDataHolder userDataHolder) {
        ProcessDetailInfo detail = getDetail(processInfo, userDataHolder);
        if (detail != null) { File file = new File(detail.getPath()); if (file.exists()) return FileSystemView.getFileSystemView().getSystemIcon(file); }
        return LuaIcons.FILE;
    }
    private ProcessDetailInfo getDetail(ProcessInfo processInfo, UserDataHolder userDataHolder) { Map<Integer, ProcessDetailInfo> map = userDataHolder.getUserData(LuaAttachDebuggerProvider.DETAIL_KEY); return map == null ? null : map.get(processInfo.getPid()); }
}
