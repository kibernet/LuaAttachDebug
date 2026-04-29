package com.kibernet.LuaAttachDebug.attach;

import com.google.gson.Gson;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.xdebugger.XDebugSession;
import com.kibernet.LuaAttachDebug.util.ProcessDetailInfo;
import com.kibernet.LuaAttachDebug.util.ProcessUtils;
import com.tang.intellij.lua.debugger.DebugLogger;
import com.tang.intellij.lua.debugger.LogConsoleType;
import com.tang.intellij.lua.debugger.emmy.*;
import java.util.*;
import java.util.stream.Collectors;

public final class LuaConfigAttachDebugProcess extends EmmyDebugProcessBase {
    private int pid;
    private final LuaAttachDebugConfiguration configuration;
    public LuaConfigAttachDebugProcess(XDebugSession session, LuaAttachDebugConfiguration configuration) { super(session); this.configuration = configuration; }
    @Override public void sessionInitialized() {
        if (configuration.getAttachMode() == LuaAttachMode.Pid) { pid = Integer.parseInt(configuration.getPid().trim()); super.sessionInitialized(); return; }
        List<ProcessDetailInfo> matches = ProcessUtils.listProcessesByEncoding(configuration.getEncoding()).stream().filter(info -> info.getTitle().contains(configuration.getProcessName()) || info.getPath().contains(configuration.getProcessName())).collect(Collectors.toList());
        if (matches.size() == 1) { pid = matches.get(0).getPid(); super.sessionInitialized(); return; }
        Map<String, ProcessDetailInfo> displayMap = new LinkedHashMap<>(); for (ProcessDetailInfo info : matches) displayMap.put(info.getPid() + ":" + info.getTitle(), info);
        JBPopupFactory.getInstance().createPopupChooserBuilder(displayMap.keySet().stream().toList()).setTitle("choose best match process").setMovable(true).setItemChosenCallback(value -> { ProcessDetailInfo info = displayMap.get(value); if (info != null) pid = info.getPid(); super.sessionInitialized(); }).createPopup().showInFocusCenter();
    }
    @Override protected void setupTransporter() {
        if (!AttachSupport.attach(this, pid)) { getSession().stop(); return; }
        SocketClientTransporter transporter = new SocketClientTransporter("localhost", AttachSupport.portFromPid(pid));
        transporter.setHandler((ITransportHandler)this); transporter.setLogger((DebugLogger)this); setTransporter((Transporter)transporter); transporter.start();
    }
    @Override public void onReceiveMessage(MessageCMD cmd, String json) {
        if (cmd == MessageCMD.AttachedNotify) { AttachedNotify msg = new Gson().fromJson(json, AttachedNotify.class); println("Attached to lua state 0x" + Long.toString(msg.getState(), 16), LogConsoleType.NORMAL, ConsoleViewContentType.SYSTEM_OUTPUT); }
        else super.onReceiveMessage(cmd, json);
    }
}
