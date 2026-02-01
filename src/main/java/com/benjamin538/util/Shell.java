package com.benjamin538.util;

public abstract class Shell {
    public static String getShell() {
        String shell = System.getenv("SHELL");
        if(shell.endsWith("bash")) {
            return "bash";
        } else if(shell.endsWith("zsh")) {
            return "zsh";
        } else if(shell.endsWith("fish")) {
            return "fish";
        }
        return null;
    }

    public static LinuxShellConfig getShellData(String shell, String path) {
        String home = System.getenv("HOME");
        switch(shell) {
            case "bash":
                return new LinuxShellConfig(home + "/.bash_profile", home + "/.bash_profile.bak", "export GEODE_SDK=" + path);
            case "zsh":
                return new LinuxShellConfig(home + "/.zshenv", home + "/.zshenv.bak", "export GEODE_SDK=" + path);
            case "fish":
                return new LinuxShellConfig(home + "/.config/fish/conf.d/geode.fish", home + "/.config/fish/conf.d/geode.fish.bak", "set -gx GEODE_SDK=" + path);
        }
        return null;
    }
}
