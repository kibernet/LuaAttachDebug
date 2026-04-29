package com.kibernet.LuaAttachDebug.launch;

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

public final class LuaLaunchDebugConfiguration extends LuaRunConfiguration implements RunConfigurationWithSuppressedDefaultRunAction {
    private String program = "lua";
    private String workingDirectory = "";
    private String parameter = "";
    private boolean useWindowsTerminal;
    public LuaLaunchDebugConfiguration(Project project, ConfigurationFactory factory) { super(project, factory); }
    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program == null ? "" : program; }
    public String getWorkingDirectory() { return workingDirectory; }
    public void setWorkingDirectory(String workingDirectory) { this.workingDirectory = workingDirectory == null ? "" : workingDirectory; }
    public String getParameter() { return parameter; }
    public void setParameter(String parameter) { this.parameter = parameter == null ? "" : parameter; }
    public boolean getUseWindowsTerminal() { return useWindowsTerminal; }
    public void setUseWindowsTerminal(boolean useWindowsTerminal) { this.useWindowsTerminal = useWindowsTerminal; }
    @SuppressWarnings("unchecked")
    @Override public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        SettingsEditorGroup<RunConfiguration> group = new SettingsEditorGroup<>();
        group.addEditor("emmy", (SettingsEditor<RunConfiguration>) (SettingsEditor<?>) new LuaLaunchDebugSettingsPanel());
        return group;
    }
    @Override public RunProfileState getState(Executor executor, ExecutionEnvironment environment) { return new LuaCommandLineState(environment); }
    @Override public Collection<Module> getValidModules() { return Collections.emptyList(); }
    @Override public void writeExternal(Element element) {
        super.writeExternal(element);
        JDOMExternalizerUtil.writeField(element, "Program", program);
        JDOMExternalizerUtil.writeField(element, "WorkingDirectory", workingDirectory);
        JDOMExternalizerUtil.writeField(element, "Parameter", parameter);
        JDOMExternalizerUtil.writeField(element, "UseWindowsTerminal", String.valueOf(useWindowsTerminal));
    }
    @Override public void readExternal(Element element) {
        super.readExternal(element);
        String value = JDOMExternalizerUtil.readField(element, "Program"); if (value != null) program = value;
        value = JDOMExternalizerUtil.readField(element, "WorkingDirectory"); if (value != null) workingDirectory = value;
        value = JDOMExternalizerUtil.readField(element, "Parameter"); if (value != null) parameter = value;
        value = JDOMExternalizerUtil.readField(element, "UseWindowsTerminal"); if (value != null) useWindowsTerminal = Boolean.parseBoolean(value);
    }
}
