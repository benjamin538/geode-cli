package com.benjamin538.profile;

// java stuff
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

// json
import org.json.JSONObject;

// config path
import com.benjamin538.config.ConfigPath;

// file check
import com.benjamin538.util.CheckProfileFile;

// da logging
import com.benjamin538.util.Logging;

public abstract class CurrentDeveloper {
    public static String get() {
        Logging logger = new Logging();
        CheckProfileFile.checkFile();
        try {
            Path configPath = ConfigPath.path();
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
            Path path = ConfigPath.path();
            JSONObject profileJSON = new JSONObject(Files.readString(path));
            String currentProfile = profileJSON.getString("default-developer");
            return currentProfile;
        } catch(Exception ex) {
            logger.fail("Failed to get current profile: " + ex.getMessage());
            return "";
        }

    }
}
