package com.benjamin538.Sdk;

// file stuff
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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
        try {
            Path path = Paths.get(System.getenv("GEODE_SDK"), "VERSION");
            InputStream stream = Files.newInputStream(path);
            String version = new String(stream.readAllBytes(), StandardCharsets.UTF_8).replace("\r\n", " ");
            logger.info("Geode SDK Version: " + version);
            stream.close();
        } catch(Exception ex) {
            logger.fatal("Unable to get SDK version: " + ex.getMessage());
        }
    }
}
