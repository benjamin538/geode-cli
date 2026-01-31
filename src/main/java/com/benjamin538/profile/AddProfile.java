package com.benjamin538.profile;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "add",
    description = "Add profile"
)
public class AddProfile implements Runnable {
    @Parameters(description = "New profile location")
    String location;
    @Parameters(description = "Platfor of the target")
    String platform;
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Override
    public void run() {
        
    }
}
