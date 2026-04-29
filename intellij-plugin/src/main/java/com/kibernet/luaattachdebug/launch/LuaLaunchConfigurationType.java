package com.kibernet.LuaAttachDebug.launch;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.tang.intellij.lua.lang.LuaIcons;
import javax.swing.Icon;

public final class LuaLaunchConfigurationType implements ConfigurationType {
    @Override public String getDisplayName() { return "LuaAttachDebug Launch"; }
    @Override public String getConfigurationTypeDescription() { return "LuaAttachDebug Launch"; }
    @Override public Icon getIcon() { return LuaIcons.FILE; }
    @Override public String getId() { return "LuaAttachDebug.launch.debugger"; }
    @Override public ConfigurationFactory[] getConfigurationFactories() { return new ConfigurationFactory[]{new LuaLaunchConfigurationFactory(this)}; }
}
