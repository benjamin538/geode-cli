package com.benjamin538.Sdk;

// file stuff
import java.nio.file.Path;
import java.nio.file.Paths;

// da logging
import com.benjamin538.Util.Logging;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = "set-path",
    description = "Change SDK Path"
)
public class SetSdkPath implements Runnable {
    private Logging logger = new Logging();
    @Parameters(description = "New SDK Path")
    String path;
    @Override
    public void run() {
        String os = System.getProperty("os.name").toLowerCase();
        if(os.startsWith("windows")) {
            try {
                Path newPath = Paths.get(path);
                path = newPath.normalize().toString();
                ProcessBuilder builder = new ProcessBuilder("setx", "GEODE_SDK " + path);
                Process process = builder.start();
                process.waitFor();
            } catch(Exception ex) {
                logger.warn("Unable to set the GEODE_SDK enviroment to " + path);
            }
        } else {
            // TODO: Linux support
            logger.info("Currently supporting only Windows");
        }
    }
}
