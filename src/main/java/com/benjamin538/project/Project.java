package com.benjamin538.project;

// picocli
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "project",
    description = "Tools for working with the current mod project",
    subcommands = {
        CreateMod.class,
        CheckDeps.class
    }
)
public class Project implements Runnable {
    @Option(names = {"-h", "--help"}, description = "Print help")
    boolean help;
    @Override
    public void run() {
        CommandLine.usage(this, System.err);
    }
}
