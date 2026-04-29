package com.kibernet.luaattachdebug.attach;

public enum LuaAttachMode {
    Pid("Pid"), ProcessName("ProcessName");
    private final String description;
    LuaAttachMode(String description) { this.description = description; }
    @Override public String toString() { return description; }
}
