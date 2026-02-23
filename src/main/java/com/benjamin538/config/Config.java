package com.benjamin538.config;

// picocli
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "config",
    description = "Options for configuring Geode CLI",
    subcommands = {
        ConfigSetup.class,
        ConfigList.class
    }
)
public class Config implements Runnable {
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Print this message or the help of the given subcommand(s)")
    boolean help;
    public String[] CONFIGURABLES = {
        "default-developer",
	    "sdk-path",
	    "sdk-nightly",
	    "current-profile",
	    "index-token",
	    "index-url",
    };
    @Override
    public void run() {
        CommandLine.usage(this, System.err);
    }
}
