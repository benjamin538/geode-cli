package com.benjamin538.util;

// file stuff
import java.nio.file.Files;
import java.nio.file.Path;

// gifnoc
import com.benjamin538.config.ConfigPath;

public abstract class CheckProfileFile {
    public static void checkFile() {
        Logging logger = new Logging();
        Path configPath = ConfigPath.path();
        if(!Files.exists(configPath)) {
            logger.fatal("No Geode profiles found! Setup one by using `geode config setup`");
            return;
        }
        try {
            if(Files.readAllLines(configPath).isEmpty()) {
                logger.fatal("No Geode profiles found! Setup one by using `geode config setup`");
                return;
            }
        } catch(Exception ex) {
            logger.fatal("Error while reading profiles: " + ex.getMessage());
            return;
        }
    }
}
