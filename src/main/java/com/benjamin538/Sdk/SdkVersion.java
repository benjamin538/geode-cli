package com.benjamin538.Sdk;

// file stuff
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;

// json
import org.json.JSONObject;

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
            return Files.readString(path).replace("\n", "");
        } catch(Exception ex) {
            logger.fail("Unable to get SDK version: " + ex.getMessage());
            return "";
        }
    }

    public void setVersion(String version) {
        version = version.replace("\n", "");
        Path path;
        if (System.getenv("LOCALAPPDATA") != null) {
            path = Paths.get(System.getenv("LOCALAPPDATA"), "Geode", "config.json");
        }
        else {
            path = Paths.get(System.getProperty("user.home"),".local", "share", "Geode", "config.json");
        }
        if (!Files.exists(path)) {
            return;
        }
        try {
            JSONObject profileJSON = new JSONObject(Files.readString(path));
            profileJSON.put("sdk-version", version);
            Files.write(path, profileJSON.toString().getBytes());
        } catch(IOException ex) {
            return;
        }
    }
}
