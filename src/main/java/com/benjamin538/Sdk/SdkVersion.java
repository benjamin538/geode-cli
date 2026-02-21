package com.benjamin538.Sdk;

// file stuff
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

// da logging
import com.benjamin538.util.Logging;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "version",
    description = "Get SDK version"
)
public class SdkVersion implements Runnable {
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Override
    public void run() {
        if (getVersion().equals("")) {
            return;
        }
        logger.info("Geode SDK Version: " + getVersion());
    }

    public String getVersion() {
        try {
            Path path = Paths.get(System.getenv("GEODE_SDK"), "VERSION");
            return Files.readString(path);
        } catch(Exception ex) {
            logger.fail("Unable to get SDK version: " + ex.getMessage());
            return "";
        }
    }
}
