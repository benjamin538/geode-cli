package com.benjamin538.config;

// path
import java.nio.file.Paths;
import java.nio.file.Path;

public abstract class ConfigPath {
    public static Path path() {
        if (System.getenv("LOCALAPPDATA") != null) {
            return Paths.get(System.getenv("LOCALAPPDATA"), "Geode", "config.json");
        } else {
            return Paths.get(System.getProperty("user.home"), ".local", "share", "Geode", "config.json");
        }
    }
}
