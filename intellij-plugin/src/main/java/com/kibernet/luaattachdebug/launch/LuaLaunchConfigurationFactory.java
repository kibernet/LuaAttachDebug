package com.kibernet.luaattachdebug.launch;

import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.tang.intellij.lua.debugger.LuaConfigurationFactory;

public final class LuaLaunchConfigurationFactory extends LuaConfigurationFactory {
    public LuaLaunchConfigurationFactory(ConfigurationType type) { super(type); }
    @Override public RunConfiguration createTemplateConfiguration(Project project) { return new LuaLaunchDebugConfiguration(project, this); }
}
