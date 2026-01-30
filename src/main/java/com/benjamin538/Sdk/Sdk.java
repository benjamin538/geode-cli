package com.benjamin538.Sdk;

// picocli
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;

// commands
import com.benjamin538.Sdk.UninstallSdk;
import com.benjamin538.Sdk.InstallSdk;

@Command(
    name = "sdk",
    description = "Options for installing & managing the Geode SDK",
    subcommands = {
        HelpCommand.class,
        UninstallSdk.class,
        InstallSdk.class
    }
)
public class Sdk implements Runnable {
    @Option(names = {"-h", "--help"}, usageHelp = true)
    boolean help;
    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }
}
