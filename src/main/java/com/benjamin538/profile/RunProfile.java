package com.benjamin538.profile;

import java.io.IOException;
// file stuff
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// lists for arguments
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// json
import org.json.JSONArray;
import org.json.JSONObject;

// check file
import com.benjamin538.util.CheckProfileFile;

// da logging
import com.benjamin538.util.Logging;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

@Command(
    name = "run",
    description = "Open Geometry Dash based on profile"
)
public class RunProfile implements Runnable {
    // this will be LOOONG file
    private Logging logger = new Logging();
    @Option(names = {"-b", "--background"}, description = "Run Geometry Dash in the background instead of the foreground")
    boolean background;
    @Option(names = {"-s", "--stay"}, description = "Do not exit CLI after Geometry Dash exits if running in foreground")
    boolean stay;
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Parameters(description = "Profile to run, uses default if none is provided", defaultValue = "")
    String profile;
    @Parameters(description = "Launch arguments for Geometry Dash")
    Set<String> arguments = new HashSet<>();
    @Override
    public void run() {
        try {
            if (stay && background) {
                logger.fatal("Impossible argument combination (background and stay)");
            }
            String os = System.getProperty("os.name").toLowerCase().replaceAll("[0-9]", "").replace(" ", "");
            Path path;
            if (System.getenv("LOCALAPPDATA") != null) {
                path = Paths.get(System.getenv("LOCALAPPDATA"), "Geode", "config.json");
            }
            else {
                path = Paths.get(System.getProperty("user.home"),".local", "share", "Geode", "config.json");
            }
            CheckProfileFile.checkFile();
            JSONObject profileJSON = new JSONObject(Files.readString(path));
            JSONArray profileArray = profileJSON.getJSONArray("profiles");
            String current = CurrentDeveloper.get();
            for (Object jprofile : profileArray) {
                JSONObject JSONProfile = (JSONObject) jprofile;
                String name = JSONProfile.getString("name");
                String gdPath = JSONProfile.getString("gd-path");
                if (profile.equals("") || profile.equals(current)) {
                    runGeometryDash(current, os, gdPath);
                    return;
                }
                if (name.equals(profile)) {
                    runGeometryDash(profile, os, gdPath);
                    return;
                }
            }
            logger.fatal("No profile found with name '" + profile + "'");
        } catch (Exception ex) {
            logger.fatal("Unable to start Geometry Dash: " + ex.getMessage());
        }
    }

    public void runGeometryDash(String profile, String os, String gdPath) throws IOException, InterruptedException {
        logger.info("Starting Geometry Dash");
        List<String> args = new ArrayList<>();
        if (os.startsWith("macos")) {
            args.add(Paths.get(gdPath, "Contents", "MacOS", "Geometry Dash").toString());
        } else {
            args.add(gdPath);
        }
        args.addAll(arguments);
        ProcessBuilder builder = new ProcessBuilder(args);
        Process process = builder.start();
        if (!background) {
            process.waitFor();
        }
        if (stay) {
            logger.info("Press any key");
            System.in.read();
        }
    }
}