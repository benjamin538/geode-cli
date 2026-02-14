package com.benjamin538.profile;

// file stuff
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// json
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// da logging
import com.benjamin538.util.Logging;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = "path",
    description = "Get the GD path for a profile"
)
public class ProfilePath implements Runnable {
    private Logging logger = new Logging();
    @Parameters(description = "The profile to get a path for, or none for default", defaultValue = "default")
    String profile;
    @Override
    public void run() {
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
            JSONObject profileJSON = new JSONObject(Files.readAllLines(path).get(0));
            JSONArray profileArray = profileJSON.getJSONArray("profiles");
            if (profile == "default") {
                profile = profileJSON.getString("current-profile");
            }
            for(Object jprofile : profileArray) {
                JSONObject JSONProfile = (JSONObject) jprofile;
                String name = JSONProfile.getString("name");
                String gdPath = JSONProfile.getString("gd-path");
                if (name == profile) {
                    System.out.println(gdPath);
                    return;
                }
            }
            logger.fail("No profile found with name " + profile);
        } catch(JSONException ex) {
            logger.fatal("Profiles file invalid");
            return;
        } catch(Exception ex) {
            logger.fatal("Error while reading profiles: " + ex.getMessage());
            return;
        }
    }
}
