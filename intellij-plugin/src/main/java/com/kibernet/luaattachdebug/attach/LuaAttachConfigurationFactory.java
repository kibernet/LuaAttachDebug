package com.kibernet.luaattachdebug.attach;

import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.tang.intellij.lua.debugger.LuaConfigurationFactory;

public final class LuaAttachConfigurationFactory extends LuaConfigurationFactory {
    public LuaAttachConfigurationFactory(ConfigurationType type) { super(type); }
    @Override public RunConfiguration createTemplateConfiguration(Project project) { return new LuaAttachDebugConfiguration(project, this); }
}
