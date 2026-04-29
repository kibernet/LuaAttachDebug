package com.kibernet.LuaAttachDebug.attach;

import com.google.gson.Gson;
import com.intellij.execution.process.ProcessInfo;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.xdebugger.XDebugSession;
import com.tang.intellij.lua.debugger.DebugLogger;
import com.tang.intellij.lua.debugger.LogConsoleType;
import com.tang.intellij.lua.debugger.emmy.*;

public final class LuaAttachDebugProcess extends EmmyDebugProcessBase {
    private final ProcessInfo processInfo;
    public LuaAttachDebugProcess(XDebugSession session, ProcessInfo processInfo) { super(session); this.processInfo = processInfo; }
    @Override protected void setupTransporter() {
        int pid = processInfo.getPid();
        if (!AttachSupport.attach(this, pid)) { getSession().stop(); return; }
        SocketClientTransporter transporter = new SocketClientTransporter("localhost", AttachSupport.portFromPid(pid));
        transporter.setHandler((ITransportHandler)this); transporter.setLogger((DebugLogger)this); setTransporter((Transporter)transporter); transporter.start();
    }
    @Override public void onReceiveMessage(MessageCMD cmd, String json) {
        if (cmd == MessageCMD.AttachedNotify) {
            AttachedNotify msg = new Gson().fromJson(json, AttachedNotify.class);
            println("Attached to lua state 0x" + Long.toString(msg.getState(), 16), LogConsoleType.NORMAL, ConsoleViewContentType.SYSTEM_OUTPUT);
        } else super.onReceiveMessage(cmd, json);
    }
}
