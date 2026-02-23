package com.benjamin538.profile;

// java stuff
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

// json
import org.json.JSONObject;

// file check
import com.benjamin538.util.CheckProfileFile;

// da logging
import com.benjamin538.util.Logging;

public abstract class CurrentDeveloper {
    public static String get() {
        Logging logger = new Logging();
        CheckProfileFile.checkFile();
        try {
            Path configPath;
            if (System.getenv("LOCALAPPDATA") != null) {
                configPath = Paths.get(System.getenv("LOCALAPPDATA"), "Geode", "config.json");
            }
            else {
                configPath = Paths.get(System.getProperty("user.home"),".local", "share", "Geode", "config.json");
            }
            JSONObject profileJSON = new JSONObject(Files.readString(configPath));
            String currentProfile = profileJSON.getString("current-profile");
            return currentProfile;
        } catch(Exception ex) {
            logger.fail("Failed to get current profile: " + ex.getMessage());
            return "";
        }

    }

    public static String getDev() {
        Logging logger = new Logging();
        CheckProfileFile.checkFile();
        try {
            Path path;
            if (System.getenv("LOCALAPPDATA") != null) {
                path = Paths.get(System.getenv("LOCALAPPDATA"), "Geode", "config.json");
            }
            else {
                path = Paths.get(System.getProperty("user.home"),".local", "share", "Geode", "config.json");
            }
            JSONObject profileJSON = new JSONObject(Files.readString(path));
            String currentProfile = profileJSON.getString("default-developer");
            return currentProfile;
        } catch(Exception ex) {
            logger.fail("Failed to get current profile: " + ex.getMessage());
            return "";
        }

    }
}
