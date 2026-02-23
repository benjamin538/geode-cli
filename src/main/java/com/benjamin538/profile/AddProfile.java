package com.benjamin538.profile;

// da loggigng
import com.benjamin538.util.Logging;

// file stuff
import java.nio.file.Paths;
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
        addProfile();
    }

    public void addProfile() {
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
                Files.createDirectories(Paths.get(System.getenv("LOCALAPPDATA"), "Geode"));
                // this is BULLLLLSHITTTTT
                String os = System.getProperty("os.name").toLowerCase().replaceAll("[0-9]", "").replace(" ", "");

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

                Files.write(configPath, jsonObj.toString().getBytes());
                logger.done("A profile named '" + name + "' has been created");
            } catch(Exception ex) {
                logger.fatal("Failed to create profile: " + ex.getMessage());
            }
        } else {
            try {
                // im losing sanity
                String os = System.getProperty("os.name").toLowerCase().replaceAll("[0-9]", "").replace(" ", "");
                JSONObject profileJSON = new JSONObject(Files.readString(configPath));
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
                profileJSON.put("profiles", profileList);
                Files.write(configPath, profileJSON.toString().getBytes());
                logger.done("A profile named '" + name + "' has been created");
            } catch(Exception ex) {
                logger.fatal("Failed to create profile: " + ex.getMessage());
            }

            // absolute boilerplate
        }
    }

    public void addProfile(String name, String location, String os) {
        if (!location.endsWith("GeometryDash.exe")) {
            logger.fatal("Path should end with Geometry Dash executable, not folder");
            return;
        }
        Path path = Paths.get(location);
        if (!Files.exists(path)) {
            logger.fatal("Geometry Dash executable not found");
            return;
        }
        Path configPath;
        if (System.getenv("LOCALAPPDATA") != null) {
            configPath = Paths.get(System.getenv("LOCALAPPDATA"), "Geode", "config.json");
        }
        else {
            configPath = Paths.get(System.getProperty("user.home"),".local", "share", "Geode", "config.json");
        }
        if (!Files.exists(configPath)) {
            try {
                Files.createDirectories(Paths.get(System.getenv("LOCALAPPDATA"), "Geode"));
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

                Files.write(configPath, jsonObj.toString().getBytes());
                logger.done("A profile named '" + name + "' has been created");
            } catch(Exception ex) {
                logger.fatal("Failed to create profile: " + ex.getMessage());
            }
        } else {
            try {
                JSONObject profileJSON = new JSONObject(Files.readString(configPath));
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
                profileJSON.put("profiles", profileList);
                Files.write(configPath, profileJSON.toString().getBytes());
                logger.done("A profile named '" + name + "' has been created");
            } catch(Exception ex) {
                logger.fatal("Failed to create profile: " + ex.getMessage());
            }

            // absolute boilerplate x2
        }
    }
}
