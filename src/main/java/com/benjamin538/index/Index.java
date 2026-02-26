package com.benjamin538.index;

import picocli.CommandLine;
// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "index",
    description = "Tools for interacting with the Geode mod index"
)
public class Index implements Runnable {
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Override
    public void run() {
        CommandLine.usage(this, System.err);
    }
}
