package com.benjamin538.project;

public abstract class DetectOS {
    public static OS detectOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();

        if (osName.contains("win")) return OS.windows;
        if (osName.contains("mac")) {
            if (arch.contains("aarch64") || arch.contains("arm")) return OS.mac_arm;
            return OS.mac_intel;
        }
        if (osName.contains("android")) return OS.android;

        return OS.windows;
    }
}
