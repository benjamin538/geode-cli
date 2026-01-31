package com.benjamin538.sdk;

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
            Path path = Paths.get(System.getenv("GEODE_SDK"));
            path = Paths.get(path + "\\VERSION");
            InputStream stream = Files.newInputStream(path);
            logger.info("Geode SDK Version: " + new String(stream.readAllBytes(), StandardCharsets.UTF_8));
            stream.close();
        } catch(Exception ex) {
            logger.fatal("Unable to get SDK version: " + ex.getMessage());
        }
    }
}
