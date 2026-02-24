package com.benjamin538.config;

import java.io.IOException;
// file stuff
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

// json
import org.json.JSONObject;

// file check
import com.benjamin538.util.CheckProfileFile;

// logging
import com.benjamin538.util.Logging;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

@Command(
    name = "set",
    description = "Set value"
)
public class ConfigSet implements Runnable {
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Parameters(description = "Field to get")
    String field;
    @Parameters(description = "New value")
    String value;
    @Override
    public void run() {
        CheckProfileFile.checkFile();
        Path configPath;
        if (System.getenv("LOCALAPPDATA") != null) {
            configPath = Paths.get(System.getenv("LOCALAPPDATA"), "Geode", "config.json");
        }
        else {
            configPath = Paths.get(System.getProperty("user.home"),".local", "share", "Geode", "config.json");
        }
        try {
            JSONObject profileJSON = new JSONObject(Files.readString(configPath));
            for(String _field : Config.CONFIGURABLES) {
                if (_field.equals(field)) {
                    if (field.equals("sdk-nightly")) {
                        profileJSON.put(field, Boolean.parseBoolean(value));
                    } else if(field.equals("sdk-path")) {
                        logger.fatal("Set SDK path using `geode sdk set-path`");
                    }
                     else {
                        profileJSON.put(field, value);
                    }
                    Files.write(configPath, profileJSON.toString().getBytes());
                    logger.done("Set " + field + " to " + profileJSON.get(field));
                }
            }
        } catch (IOException ex) {
            logger.fatal("Cant write config.json: " + ex.getMessage());
        }
        logger.fatal("Unknown field aaa");
    }
}
