package com.benjamin538.profile;

import picocli.CommandLine;
// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.HelpCommand;

@Command(
    name = "profile",
    description = "Options for managing profiles (installations of Geode)",
    subcommands = {
        HelpCommand.class,
        AddProfile.class,
        ListProfiles.class,
        ProfilePath.class,
        SwitchProfile.class,
        RenameProfile.class,
        DeleteProfile.class
    }
)
public class Profile implements Runnable {
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Override
    public void run() {
        CommandLine.usage(this, System.err);
    }
}
