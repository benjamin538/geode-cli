package com.benjamin538.modPackage;

// path
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.nio.file.Files;

// config path
import com.benjamin538.config.ConfigPath;

// get profile
import com.benjamin538.profile.CurrentDeveloper;

// check file
import com.benjamin538.util.CheckProfileFile;

// logging
import com.benjamin538.util.Logging;

// json
import org.json.JSONObject;
import org.json.JSONArray;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "install",
    description = "Install a .geode package to the current profile"
)
public class InstallPackage implements Runnable {
    private static Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Parameters(description = "Location of the .geode package to install")
    Path path;
    @Override
    public void run() {
        installPackage(path);
    }

    public static void installPackage(Path path) {
        CheckProfileFile.checkFile();
        String current = CurrentDeveloper.get();
        if(current.equals("")) {
            logger.fatal("Current profile is missing, use `geode profile switch`");
        }
        Path configPath = ConfigPath.path();
        if (!Files.exists(path)) {
            logger.fatal(".geode file not found.");
        }
        try {
            JSONObject profileJSON = new JSONObject(Files.readString(configPath));
            JSONArray profiles = profileJSON.getJSONArray("profiles");
            for(Object profile : profiles) {
                JSONObject _profile = (JSONObject) profile;
                String name = _profile.getString("name");
                if(name.equals(current)) {
                    String gdPath = Paths.get(_profile.getString("gd-path"), "..").normalize().toString();
                    Path modDir = Paths.get(gdPath, "geode", "mods");
                    if (!Files.exists(modDir)) {
                        logger.warn("Path " + modDir.toAbsolutePath().toString() + "does not exists, creating one");
                        logger.warn("(Did you installed Geode?");
                        Files.createDirectories(modDir);
                    }
                    Files.move(path, Paths.get(gdPath, "geode", "mods", path.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
                    logger.done("Installed " + path.getFileName().toString());
                }
            }
        } catch(Exception ex) {
            logger.fatal("Failed to install package: " + ex.getMessage());
        }
    }
}
