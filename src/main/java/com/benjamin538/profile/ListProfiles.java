package com.benjamin538.profile;

import com.benjamin538.util.Colors;
// da loggign
import com.benjamin538.util.Logging;

// file stuff
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

// json
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

@Command(
    name = "list",
    description = "List profiles"
)
public class ListProfiles implements Runnable {
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help")
    boolean help;
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
            String currentProfile = profileJSON.getString("current-profile");
            for(Object profile : profileArray) {
                JSONObject JSONProfile = (JSONObject) profile;
                String name = JSONProfile.getString("name");
                String gdPath = JSONProfile.getString("gd-path");
                System.out.println(Colors.BRIGHT_CYAN + (currentProfile.equals(name) ? " * " : "") + name + Colors.RESET + " [ path = " + Colors.BRIGHT_GREEN + gdPath + Colors.RESET + " ]");
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
