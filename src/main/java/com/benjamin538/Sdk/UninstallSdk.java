package com.benjamin538.Sdk;

// file stuff
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream; // for recursive deletion

// da logging
import com.benjamin538.Util.Logging;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "uninstall",
    description = "Uninstall SDK"
)
public class UninstallSdk implements Runnable{
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Override
    public void run() {
        try {
            Path path = Paths.get(System.getenv("GEODE_SDK"));
            if (!Files.isDirectory(path)) {
                throw new IOException("GEODE_SDK doesn't point to a directory");
            }
            if (!logger.askConfirm("Are you sure you want to uninstall Geode SDK?", false)) {
                logger.fail("Aboritng");
                return;
            }
            Stream<Path> walk = Files.walk(path);
            walk.forEach(file -> {
                try {
                    Files.delete(file);
                } catch(IOException ex) {
                    ;
                }
            });
            walk.close();
        } catch(IOException ex) {
            logger.fatal("Unable to uninstall SDK: " + ex.getMessage());
        } catch(NullPointerException ex) {
            logger.fatal("Unable to install SDK: Unable to find GEODE_SDK");
        }
    }
}