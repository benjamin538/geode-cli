package com.benjamin538.util;

// file stuff
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// json exception
import org.json.JSONException;

public abstract class CheckProfileFile {
    public static void checkFile() {
        Logging logger = new Logging();
        Path path = Paths.get(System.getenv("LOCALAPPDATA"), "Geode", "config.json");
        if(!Files.exists(path)) {
            logger.fatal("No Geode profiles found! Setup one by using `geode config setup`");
            return;
        }
        try {
            if(Files.readAllLines(path).isEmpty()) {
                logger.fatal("No Geode profiles found! Setup one by using `geode config setup`");
                return;
            }
        } catch(JSONException ex) {
            logger.fatal("Profiles file invalid");
            return;
        } catch(Exception ex) {
            logger.fatal("Error while reading profiles: " + ex.getMessage());
            return;
        }
    }
}
