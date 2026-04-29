package com.kibernet.LuaAttachDebug.util;

import java.util.Objects;

public final class ProcessDetailInfo {
    private int pid;
    private String path;
    private String title;

    public ProcessDetailInfo() { this(0, "", ""); }
    public ProcessDetailInfo(int pid, String path, String title) {
        this.pid = pid;
        this.path = path == null ? "" : path;
        this.title = title == null ? "" : title;
    }
    public int getPid() { return pid; }
    public void setPid(int pid) { this.pid = pid; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path == null ? "" : path; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title == null ? "" : title; }
    @Override public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ProcessDetailInfo that)) return false;
        return pid == that.pid && Objects.equals(path, that.path) && Objects.equals(title, that.title);
    }
    @Override public int hashCode() { return Objects.hash(pid, path, title); }
    @Override public String toString() { return "ProcessDetailInfo(pid=" + pid + ", path=" + path + ", title=" + title + ")"; }
}
