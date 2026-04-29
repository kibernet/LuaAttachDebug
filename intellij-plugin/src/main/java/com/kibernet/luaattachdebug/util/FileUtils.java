package com.kibernet.luaattachdebug.util;

import com.intellij.openapi.application.PathManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class FileUtils {
    public static final String PLUGIN_ID = "com.kibernet.luaattachdebug";
    private static final Map<String, Path> EXTRACTED = new ConcurrentHashMap<>();
    private static final String[] NATIVE_FILES = {"EasyHook.dll", "emmy_core.dll", "emmy_hook.dll", "emmy_tool.exe"};
    private FileUtils() {}
    public static String getArchExeFile() { return getPluginVirtualFile("debugger/emmy/windows/x86/emmy_tool.exe"); }
    public static String getNativeDirectory(String arch) {
        for (String file : NATIVE_FILES) getPluginVirtualFile("debugger/emmy/windows/" + arch + "/" + file);
        return getExtractionRoot().resolve("debugger/emmy/windows/" + arch).toString();
    }
    public static String getPluginVirtualFile(String resourcePath) {
        Path existing = EXTRACTED.get(resourcePath);
        if (existing != null && Files.exists(existing)) return existing.toString();
        Path destination = getExtractionRoot().resolve(resourcePath.replace('/', java.io.File.separatorChar));
        try {
            Files.createDirectories(destination.getParent());
            try (InputStream stream = FileUtils.class.getResourceAsStream("/" + resourcePath)) {
                if (stream == null) return null;
                Files.copy(stream, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            destination.toFile().setExecutable(true, false);
            EXTRACTED.put(resourcePath, destination);
            return destination.toString();
        } catch (IOException e) { return null; }
    }
    private static Path getExtractionRoot() { return Path.of(PathManager.getSystemPath(), PLUGIN_ID, "native"); }
}
