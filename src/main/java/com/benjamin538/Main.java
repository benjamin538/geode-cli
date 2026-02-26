package com.benjamin538;

// commands
import com.benjamin538.Sdk.Sdk;
import com.benjamin538.index.Index;
import com.benjamin538.profile.Profile;
import com.benjamin538.profile.RunProfile;
import com.benjamin538.project.CreateMod;
import com.benjamin538.config.Config;
import com.benjamin538.project.Project;

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
        CreateMod.class,
        Config.class,
        Project.class,
        picocli.AutoComplete.GenerateCompletion.class,
        Index.class
    },
    version = "geode java 0.31",
    mixinStandardHelpOptions = true
)
public class Main {
    public static void main(String[] args) {
        new CommandLine(new Main()).execute(args);
    }
}