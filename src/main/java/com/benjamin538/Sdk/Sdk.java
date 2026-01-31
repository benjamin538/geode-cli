package com.benjamin538.sdk;

// picocli
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;

@Command(
    name = "sdk",
    description = "Options for installing & managing the Geode SDK",
    subcommands = {
        HelpCommand.class,
        UninstallSdk.class,
        InstallSdk.class,
        SetSdkPath.class,
        SdkVersion.class,
        UpdateSdk.class
    }
)
public class Sdk implements Runnable {
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Print this message or the help of the given subcommand(s)")
    boolean help;
    @Override
    public void run() {
        CommandLine.usage(this, System.err);
    }
}