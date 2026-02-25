package com.benjamin538.config;

// files
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

// check file
import com.benjamin538.util.CheckProfileFile;

// colors!!!!!!!!!!!!
import com.benjamin538.util.Colors;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

// json
import org.json.JSONObject;

// da logging
import com.benjamin538.util.Logging;

@Command(
    name = "get",
    description = "Get value"
)
public class ConfigGet implements Runnable {
    private static Logging logger = new Logging();
    @Parameters(description = "Field to get")
    String field;
    @Option(names = {"-r", "--raw"}, description = "Output raw value")
    boolean raw;
    @Override
    public void run() {
        try {
            Path path;
            if (System.getenv("LOCALAPPDATA") != null) {
                path = Paths.get(System.getenv("LOCALAPPDATA"), "Geode", "config.json");
            }
            else {
                path = Paths.get(System.getProperty("user.home"),".local", "share", "Geode", "config.json");
            }
            CheckProfileFile.checkFile();
            JSONObject json = new JSONObject(Files.readString(path));
            for(String _field : Config.CONFIGURABLES) {
                if(_field.equals(field)) {
                    if(raw) {
                        System.out.println(json.get(field));
                        return;
                    }
                    System.out.println(Colors.BRIGHT_CYAN + field + Colors.RESET + " = " + Colors.BRIGHT_GREEN + json.get(field) + Colors.RESET);
                    return;
                }
            }
            logger.fatal("Unknown field" + field);
        } catch(IOException ex) {
            logger.fatal("No Geode profiles found! Setup one by using `geode config setup`");
        }
    }

    public static final boolean getSdkNightly() {
        try {
            Path path;
            if (System.getenv("LOCALAPPDATA") != null) {
                path = Paths.get(System.getenv("LOCALAPPDATA"), "Geode", "config.json");
            }
            else {
                path = Paths.get(System.getProperty("user.home"),".local", "share", "Geode", "config.json");
            }
            if(!Files.exists(path)) {
                logger.fail("No Geode profiles found! Setup one by using `geode config setup`");
                return false;
            }
            return new JSONObject(Files.readString(path)).getBoolean("sdk-nightly");
        } catch(Exception ex) {
            logger.fail("No Geode profiles found! Setup one by using `geode config setup`");
            return false;
        }
    }
}
