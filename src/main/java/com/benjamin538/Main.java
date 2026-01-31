package com.benjamin538;

import com.benjamin538.Sdk.Sdk;

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
    },
    version = "geode 3.7.2",
    mixinStandardHelpOptions = true
)
public class Main {
    public static void main(String[] args) {
        new CommandLine(new Main()).execute(args);
    }
}