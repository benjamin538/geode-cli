package com.benjamin538.util;

public class LinuxShellConfig {
    private String profile;
    private String profileBak;
    private String command;

    public LinuxShellConfig(String newProfile, String newProfileBak, String newCommand) {
        profile = newProfile;
        profileBak = newProfileBak;
        command = newCommand;
    }

    public String getProfile() {
        return profile;
    }

    public String getProfileBak() {
        return profileBak;
    }

    public String getCommand() {
        return command;
    }
}
