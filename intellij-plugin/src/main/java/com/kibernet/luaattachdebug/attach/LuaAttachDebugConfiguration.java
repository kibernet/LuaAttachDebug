package com.kibernet.LuaAttachDebug.attach;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.SettingsEditorGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.tang.intellij.lua.debugger.LuaCommandLineState;
import com.tang.intellij.lua.debugger.LuaRunConfiguration;
import org.jdom.Element;
import java.util.Collection;
import java.util.Collections;

public final class LuaAttachDebugConfiguration extends LuaRunConfiguration implements RunConfigurationWithSuppressedDefaultRunAction {
    private LuaAttachMode attachMode = LuaAttachMode.Pid;
    private String pid = "0";
    private String processName = "";
    private String encoding = "gbk";
    public LuaAttachDebugConfiguration(Project project, ConfigurationFactory factory) { super(project, factory); }
    public LuaAttachMode getAttachMode() { return attachMode; }
    public void setAttachMode(LuaAttachMode attachMode) { this.attachMode = attachMode == null ? LuaAttachMode.Pid : attachMode; }
    public String getPid() { return pid; }
    public void setPid(String pid) { this.pid = pid == null ? "0" : pid; }
    public String getProcessName() { return processName; }
    public void setProcessName(String processName) { this.processName = processName == null ? "" : processName; }
    public String getEncoding() { return encoding; }
    public void setEncoding(String encoding) { this.encoding = encoding == null || encoding.isBlank() ? "gbk" : encoding; }
    @SuppressWarnings("unchecked")
    @Override public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        SettingsEditorGroup<RunConfiguration> group = new SettingsEditorGroup<>();
        group.addEditor("emmy", (SettingsEditor<RunConfiguration>) (SettingsEditor<?>) new LuaAttachDebugSettingsPanel());
        return group;
    }
    @Override public RunProfileState getState(Executor executor, ExecutionEnvironment environment) { return new LuaCommandLineState(environment); }
    @Override public Collection<Module> getValidModules() { return Collections.emptyList(); }
    @Override public void writeExternal(Element element) {
        super.writeExternal(element);
        JDOMExternalizerUtil.writeField(element, "AttachMode", String.valueOf(attachMode.ordinal()));
        JDOMExternalizerUtil.writeField(element, "Pid", pid);
        JDOMExternalizerUtil.writeField(element, "ProcessName", processName);
        JDOMExternalizerUtil.writeField(element, "Encoding", encoding);
    }
    @Override public void readExternal(Element element) {
        super.readExternal(element);
        String value = JDOMExternalizerUtil.readField(element, "AttachMode");
        if (value != null) {
            try { int index = Integer.parseInt(value); attachMode = index >= 0 && index < LuaAttachMode.values().length ? LuaAttachMode.values()[index] : LuaAttachMode.Pid; }
            catch (NumberFormatException ignored) { attachMode = LuaAttachMode.Pid; }
        }
        value = JDOMExternalizerUtil.readField(element, "Pid"); if (value != null) pid = value;
        value = JDOMExternalizerUtil.readField(element, "ProcessName"); if (value != null) processName = value;
        value = JDOMExternalizerUtil.readField(element, "Encoding"); if (value != null) encoding = value;
    }
}
