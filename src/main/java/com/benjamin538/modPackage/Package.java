package com.benjamin538.modPackage;

// picocli
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "package",
    description = "Options for working with .geode packages",
    subcommands = {
        InstallPackage.class
    }
)
public class Package implements Runnable {
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Override
    public void run() {
        CommandLine.usage(this, System.err);
    }
}
