package com.benjamin538.profile;

// da loggigng
import com.benjamin538.util.Logging;

// file stuff
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

// j son😭
import org.json.JSONObject;
import org.json.JSONArray;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "add",
    description = "Add profile"
)
public class AddProfile implements Runnable {
    private Logging logger = new Logging();
    @Parameters(description = "New profile name")
    String name;
    @Parameters(description = "New profile location")
    String location;
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Override
    public void run() {
        if (!location.endsWith("GeometryDash.exe")) {
            logger.fatal("Path should end with Geometry Dash executable, not folder");
            return;
        }
        Path path = Paths.get(location);
        if (!Files.exists(path)) {
            logger.fatal("Geometry Dash executable not found");
            return;
        }
        Path configPath = Paths.get(System.getenv("LOCALAPPDATA"), "Geode", "config.json");
        if (!Files.exists(configPath)) {
            try {
                // this is BULLLLLSHITTTTT
                String os = System.getProperty("os.name").toLowerCase().replaceAll("[0-9]", "").replace(" ", "");
                OutputStream stream = Files.newOutputStream(configPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

                JSONObject profile = new JSONObject();
                profile.put("name", name);
                profile.put("gd-path", location);
                profile.put("platform", os);

                JSONArray profiles = new JSONArray();
                profiles.put(profile);

                JSONObject jsonObj = new JSONObject();
                jsonObj.put("current-profile", name);
                jsonObj.put("profiles", profiles);
                jsonObj.put("default-developer", name);
                jsonObj.put("sdk-nightly", false);
                jsonObj.put("sdk-version", JSONObject.NULL);
                jsonObj.put("index-token", JSONObject.NULL);
                jsonObj.put("index-url", "https://api.geode-sdk.org");

                stream.write(jsonObj.toString().getBytes(StandardCharsets.UTF_8));
                logger.done("A profile named '" + name + "' has been created");
            } catch(Exception ex) {
                logger.fatal("Failed to create profile: " + ex.getMessage());
            }
        } else {
            try {
                // im losing sanity
                String os = System.getProperty("os.name").toLowerCase().replaceAll("[0-9]", "").replace(" ", "");
                OutputStream stream = Files.newOutputStream(configPath, StandardOpenOption.WRITE);
                JSONObject profileJSON = new JSONObject(Files.readAllLines(configPath).get(0));
                JSONArray profileList = profileJSON.getJSONArray("profiles");
                
                for(int i = 0; i < profileList.length(); i++) {
                    if (profileList.getJSONObject(i).getString("name").equals(name)) {
                        logger.fatal("Profile '" + name + "'' is already exist");
                    }
                }

                JSONObject newProfile = new JSONObject();
                newProfile.put("name", name);
                newProfile.put("gd-path", location);
                newProfile.put("platform", os);

                profileList.put(newProfile);
                
                stream.write(profileJSON.toString().getBytes());
                logger.done("A profile named '" + name + "' has been created");
            } catch(Exception ex) {
                logger.fatal("Failed to create profile: " + ex.getMessage());
            }

            // absolute boilerplate
        }
    }
}
