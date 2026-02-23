package com.benjamin538.profile;

// file stuff
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// json
import org.json.JSONObject;
import org.json.JSONArray;

// file check
import com.benjamin538.util.CheckProfileFile;

// da logging
import com.benjamin538.util.Logging;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "remove",
    description = "Remove profile"
)
public class DeleteProfile implements Runnable {
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Parameters(description = "Profile to remove")
    String profile;
    @Override
    public void run() {
        try {
            int index = 0;
            Path path;
            if (System.getenv("LOCALAPPDATA") != null) {
                path = Paths.get(System.getenv("LOCALAPPDATA"), "Geode", "config.json");
            }
            else {
                path = Paths.get(System.getProperty("user.home"),".local", "share", "Geode", "config.json");
            }
            CheckProfileFile.checkFile();
            JSONObject profileJSON = new JSONObject(Files.readString(path));
            JSONArray profileArray = profileJSON.getJSONArray("profiles");
            String current = CurrentDeveloper.get();
            for(Object jprofile : profileArray) {
                JSONObject JSONProfile = (JSONObject) jprofile;
                String name = JSONProfile.getString("name");
                if (name.equals(profile)) {
                    if (name.equals(current)) {
                        boolean confirm = logger.askConfirm("Seems that profile you want to delete is in use. Are you sure you want to delete it?", false);
                        if (!confirm) {
                            logger.fatal("Aborting");
                        }
                        profileJSON.put("current-profile", "");
                    }
                    profileArray.remove(index);
                    profileJSON.put("profiles", profileArray);
                    Files.write(path, profileJSON.toString().getBytes());
                    logger.done("Successfully deleted profile '" + profile + "'");
                    return;
                }
                index++;
            }
            logger.fatal("No profile found with name '" + profile + "'");
        } catch(Exception ex) {
            logger.fatal("Error while reading profiles: " + ex.getMessage());
        }
    }
}
