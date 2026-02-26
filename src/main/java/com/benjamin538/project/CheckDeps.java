package com.benjamin538.project;

// file stuff
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;

// json
import org.json.JSONObject;
import org.json.JSONException;

// logging
import com.benjamin538.util.Logging;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

// java utils
import java.util.Iterator;

@Command(
    name = "check",
    description = "Check & install the dependencies for this project"
)
public class CheckDeps implements Runnable {
    private Logging logger = new Logging();
    enum OS {
        windows,
        mac_os,
        mac_intel,
        mac_arm,
        android,
        android32,
        android64,
        ios
    }
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Option(names = {"-p", "--platform"}, description = "The platform checked used for platform-specific dependencies. If not specified, uses current host platform if possible")
    OS platform;
    @Override
    public void run() {
        if(platform == null) {
            platform = detectOS();
        }
        JSONObject modJSON = new JSONObject();
        try {
            Path modJSONpath = Paths.get("", "mod.json");
            modJSON = new JSONObject(Files.readString(modJSONpath));
        } catch(IOException ex) {
            logger.fatal("mod.json not found");
        }
        JSONObject dependencies = new JSONObject();
        try {
            dependencies = modJSON.getJSONObject("dependencies");
        } catch(JSONException ex) {
            logger.fatal("Dependencies not found");
        }
        try {
            Iterator<String> keys = dependencies.keys();
            while(keys.hasNext()) {
                String mod = keys.next();
                String version = dependencies.getString(mod);
                logger.info("Fetching " + mod);
                // TODO
            }
        } catch(JSONException ex) {

        }
    }

    private OS detectOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();

        if (osName.contains("win")) return OS.windows;
        if (osName.contains("mac")) {
            if (arch.contains("aarch64") || arch.contains("arm")) return OS.mac_arm;
            return OS.mac_intel;
        }
        if (osName.contains("android")) return OS.android;

        return OS.windows;
    }
}
