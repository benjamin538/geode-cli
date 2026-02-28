package com.benjamin538.profile;

// path
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;

// json
import org.json.JSONObject;
import org.json.JSONArray;


// since this isnt a command, im not gonna import so much stuff

public abstract class InstallToProfile {
    public static void install(Path pathToGeode, String filename) {
        String current = CurrentDeveloper.get();
        if(current.equals("")) {
            return;
        }
        Path path;
        if (System.getenv("LOCALAPPDATA") != null) {
            path = Paths.get(System.getenv("LOCALAPPDATA"), "Geode", "config.json");
        }
        else {
            path = Paths.get(System.getProperty("user.home"),".local", "share", "Geode", "config.json");
        }
        try {
            JSONObject profileJSON = new JSONObject(Files.readString(path));
            JSONArray profiles = profileJSON.getJSONArray("profiles");
            for(Object profile : profiles) {
                JSONObject _profile = (JSONObject) profile;
                String name = _profile.getString("name");
                if(name.equals(current)) {
                    String gdPath = _profile.getString("gd-path");
                    Files.copy(pathToGeode, Paths.get(gdPath, "geode", "mods", filename));
                }
            }
        } catch(Exception ex) {
            return;
        }
    }
}
