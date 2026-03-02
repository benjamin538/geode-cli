package com.benjamin538.index;

// logging
// import com.benjamin538.util.Logging;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "install",
    description = "Install a mod from the index to the current profile"
)
public class IndexInstall implements Runnable {
    // private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help")
    boolean help;
    @Parameters(description = "Mod ID to install")
    String id;
    @Parameters(description = "Mod version to install, defaults to latest", defaultValue = "latest")
    String version;
    @Override
    public void run() {
        // TODO: ts
    }
}
