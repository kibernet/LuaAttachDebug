package com.kibernet.luaattachdebug.launch;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.tang.intellij.lua.lang.LuaIcons;
import javax.swing.Icon;

public final class LuaLaunchConfigurationType implements ConfigurationType {
    @Override public String getDisplayName() { return "Lua Launch Debugger"; }
    @Override public String getConfigurationTypeDescription() { return "Lua Launch Debugger"; }
    @Override public Icon getIcon() { return LuaIcons.FILE; }
    @Override public String getId() { return "luaattachdebug.launch.debugger"; }
    @Override public ConfigurationFactory[] getConfigurationFactories() { return new ConfigurationFactory[]{new LuaLaunchConfigurationFactory(this)}; }
}
