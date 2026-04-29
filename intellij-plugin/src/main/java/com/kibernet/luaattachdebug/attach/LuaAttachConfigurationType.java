package com.kibernet.luaattachdebug.attach;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.tang.intellij.lua.lang.LuaIcons;
import javax.swing.Icon;

public final class LuaAttachConfigurationType implements ConfigurationType {
    @Override public String getDisplayName() { return "Lua Attach Debugger"; }
    @Override public String getConfigurationTypeDescription() { return "Lua Attach Debugger"; }
    @Override public Icon getIcon() { return LuaIcons.FILE; }
    @Override public String getId() { return "luaattachdebug.attach.debugger"; }
    @Override public ConfigurationFactory[] getConfigurationFactories() { return new ConfigurationFactory[]{new LuaAttachConfigurationFactory(this)}; }
}
