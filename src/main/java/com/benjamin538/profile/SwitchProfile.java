package com.benjamin538.profile;

// file stuff
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// json
import org.json.JSONArray;
import org.json.JSONObject;

import com.benjamin538.util.CheckProfileFile;
// da logging
import com.benjamin538.util.Logging;

// c
import com.benjamin538.config.ConfigPath;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "switch",
    description = "Switch main profile"
)
public class SwitchProfile implements Runnable {
    private Logging logger = new Logging();
    @Parameters(description = "New main profile")
    String profile;
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Override
    public void run() {
        Path path = ConfigPath.path();
        CheckProfileFile.checkFile();
        try {
            JSONObject profileJSON = new JSONObject(Files.readString(path));
            JSONArray profileArray = profileJSON.getJSONArray("profiles");
            for(Object jprofile : profileArray) {
                JSONObject JSONProfile = (JSONObject) jprofile;
                String name = JSONProfile.getString("name");
                if (name.equals(profile)) {
                    profileJSON.put("current-profile", profile);
                    Files.write(path, profileJSON.toString().getBytes());
                    logger.done("'" + profile + "' is now the current profile");
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
