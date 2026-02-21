package com.benjamin538.profile;

// file stuff
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// json
import org.json.JSONArray;
import org.json.JSONObject;

// checking
import com.benjamin538.util.CheckProfileFile;

// da logging
import com.benjamin538.util.Logging;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

@Command(
    name = "rename",
    description = "Rename profile"
)
public class RenameProfile implements Runnable {
    private Logging logger = new Logging();
    @Parameters(description = "Profile to rename")
    String old;
    @Parameters(description = "New name")
    String newName;
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Override
    public void run() {
        try {
            Path path = Paths.get(System.getenv("LOCALAPPDATA"), "Geode", "config.json");
            CheckProfileFile.checkFile();
            JSONObject profileJSON = new JSONObject(Files.readString(path));
            JSONArray profileArray = profileJSON.getJSONArray("profiles");
            String current = CurrentDeveloper.get();
            for(Object jprofile : profileArray) {
                JSONObject JSONProfile = (JSONObject) jprofile;
                String name = JSONProfile.getString("name");
                if (name.equals(newName)) {
                    logger.fatal("Profile with name '" + newName + "' already exists!");
                }
                if (name.equals(old)) {
                    JSONProfile.put("name", newName);
                    if (current.equals(old)) {
                        profileJSON.put("current-profile", newName);
                    }
                    Files.write(path, profileJSON.toString().getBytes());
                    logger.done("Successfully renamed '" + old + "' to '" + newName + "'");
                    return;
                }
            }
            logger.fatal("No profile found with name " + old);
        } catch(Exception ex) {
            logger.fatal("Error while reading profiles: " + ex.getMessage());
            return;
        }
    }
}
