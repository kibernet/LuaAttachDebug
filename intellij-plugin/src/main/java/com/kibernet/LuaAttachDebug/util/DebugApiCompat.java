package com.kibernet.LuaAttachDebug.util;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class DebugApiCompat {
    private DebugApiCompat() {}

    public static XDebugSession startSession(Project project, ExecutionEnvironment environment, XDebugProcessStarter starter) throws ExecutionException {
        XDebuggerManager manager = XDebuggerManager.getInstance(project);
        try {
            Method method = XDebuggerManager.class.getMethod("startSession", ExecutionEnvironment.class, XDebugProcessStarter.class);
            return (XDebugSession) method.invoke(manager, environment, starter);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ExecutionException executionException) {
                throw executionException;
            }
            throw new ExecutionException(cause == null ? e.getMessage() : cause.getMessage(), cause);
        } catch (ReflectiveOperationException e) {
            throw new ExecutionException("Failed to start debug session via compatibility bridge.", e);
        }
    }

    public static XDebugSession startSessionAndShowTab(Project project, String sessionName, RunContentDescriptor contentToReuse, XDebugProcessStarter starter) throws ExecutionException {
        XDebuggerManager manager = XDebuggerManager.getInstance(project);
        try {
            Method method = XDebuggerManager.class.getMethod("startSessionAndShowTab", String.class, RunContentDescriptor.class, XDebugProcessStarter.class);
            return (XDebugSession) method.invoke(manager, sessionName, contentToReuse, starter);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ExecutionException executionException) {
                throw executionException;
            }
            throw new ExecutionException(cause == null ? e.getMessage() : cause.getMessage(), cause);
        } catch (ReflectiveOperationException e) {
            throw new ExecutionException("Failed to open debug tab via compatibility bridge.", e);
        }
    }

    public static RunContentDescriptor resolveDescriptor(XDebugSession session) {
        try {
            Method createdMethod = session.getClass().getMethod("getRunContentDescriptorIfCreated");
            Object descriptor = createdMethod.invoke(session);
            if (descriptor instanceof RunContentDescriptor runContentDescriptor) {
                return runContentDescriptor;
            }
        } catch (ReflectiveOperationException ignored) {
            // Fall back to legacy accessor below.
        }
        try {
            Method method = session.getClass().getMethod("getRunContentDescriptor");
            Object descriptor = method.invoke(session);
            if (descriptor instanceof RunContentDescriptor runContentDescriptor) {
                return runContentDescriptor;
            }
        } catch (ReflectiveOperationException ignored) {
            // Return null when no descriptor accessor is available.
        }
        return null;
    }
}
