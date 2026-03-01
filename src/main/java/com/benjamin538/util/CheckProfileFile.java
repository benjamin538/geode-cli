package com.benjamin538.util;

// file stuff
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// json exception
import org.json.JSONException;
import org.json.JSONObject;

public abstract class CheckProfileFile {
    public static void checkFile() {
        Logging logger = new Logging();
        Path configPath;
        if (System.getenv("LOCALAPPDATA") != null) {
            configPath = Paths.get(System.getenv("LOCALAPPDATA"), "Geode", "config.json");
        }
        else {
            configPath = Paths.get(System.getProperty("user.home"),".local", "share", "Geode", "config.json");
        }
        if(!Files.exists(configPath)) {
            logger.fatal("No Geode profiles found! Setup one by using `geode config setup`");
            return;
        }
        try {
            if(Files.readAllLines(configPath).isEmpty()) {
                logger.fatal("No Geode profiles found! Setup one by using `geode config setup`");
                return;
            }
            JSONObject profileJSON = new JSONObject(Files.readString(configPath));
        } catch(JSONException ex) {
            logger.fatal("Profiles file invalid");
            return;
        } catch(Exception ex) {
            logger.fatal("Error while reading profiles: " + ex.getMessage());
            return;
        }
    }
}
