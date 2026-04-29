package com.kibernet.LuaAttachDebug.attach;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.tang.intellij.lua.lang.LuaIcons;
import javax.swing.Icon;

public final class LuaAttachConfigurationType implements ConfigurationType {
    @Override public String getDisplayName() { return "LuaAttachDebug"; }
    @Override public String getConfigurationTypeDescription() { return "LuaAttachDebug"; }
    @Override public Icon getIcon() { return LuaIcons.FILE; }
    @Override public String getId() { return "LuaAttachDebug.attach.debugger"; }
    @Override public ConfigurationFactory[] getConfigurationFactories() { return new ConfigurationFactory[]{new LuaAttachConfigurationFactory(this)}; }
}
