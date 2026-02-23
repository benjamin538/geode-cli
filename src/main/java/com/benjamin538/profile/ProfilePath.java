package com.benjamin538.profile;

// file stuff
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// json
import org.json.JSONArray;
import org.json.JSONObject;

// file check
import com.benjamin538.util.CheckProfileFile;

// da logging
import com.benjamin538.util.Logging;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "path",
    description = "Get the GD path for a profile"
)
public class ProfilePath implements Runnable {
    private Logging logger = new Logging();
    @Parameters(description = "The profile to get a path for, or none for default", defaultValue = "")
    String profile;
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Override
    public void run() {
        Path path;
        if (System.getenv("LOCALAPPDATA") != null) {
            path = Paths.get(System.getenv("LOCALAPPDATA"), "Geode", "config.json");
        }
        else {
            path = Paths.get(System.getProperty("user.home"),".local", "share", "Geode", "config.json");
        }
        CheckProfileFile.checkFile();
        try {
            JSONObject profileJSON = new JSONObject(Files.readString(path));
            JSONArray profileArray = profileJSON.getJSONArray("profiles");
            if (profile.equals("")) {
                profile = CurrentDeveloper.get();
            }
            for(Object jprofile : profileArray) {
                JSONObject JSONProfile = (JSONObject) jprofile;
                String name = JSONProfile.getString("name");
                String gdPath = JSONProfile.getString("gd-path");
                if (name.equals(profile)) {
                    System.out.println(gdPath);
                    return;
                }
            }
            logger.fatal("No profile found with name " + profile);
        } catch(Exception ex) {
            logger.fatal("Error while reading profiles: " + ex.getMessage());
            return;
        }
    }
}
