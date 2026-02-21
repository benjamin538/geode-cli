package com.benjamin538;

import com.benjamin538.Sdk.Sdk;
import com.benjamin538.modManagement.CreateMod;
import com.benjamin538.profile.Profile;
import com.benjamin538.profile.RunProfile;

// picocli
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;

@Command(
    name = "geode",
    description = "Command-line interface for Geode",
    subcommands = {
        HelpCommand.class,
        Sdk.class,
        Profile.class,
        RunProfile.class,
        CreateMod.class
    },
    version = "geode java 0.25",
    mixinStandardHelpOptions = true
)
public class Main {
    public static void main(String[] args) {
        new CommandLine(new Main()).execute(args);
    }
}