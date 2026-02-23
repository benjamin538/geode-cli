package com.benjamin538.profile;

// file check
import com.benjamin538.util.CheckProfileFile;

// colors !!!
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

@Command(
    name = "list",
    description = "List profiles"
)
public class ListProfiles implements Runnable {
    private Logging logger = new Logging();
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
            String currentProfile = CurrentDeveloper.get();
            for(Object profile : profileArray) {
                JSONObject JSONProfile = (JSONObject) profile;
                String name = JSONProfile.getString("name");
                String gdPath = JSONProfile.getString("gd-path");
                System.out.println(Colors.BRIGHT_CYAN + (currentProfile.equals(name) ? " * " : "") + name + Colors.RESET + " [ path = " + Colors.BRIGHT_GREEN + gdPath + Colors.RESET + " ]");
            }
        } catch(Exception ex) {
            logger.fatal("Error while reading profiles: " + ex.getMessage());
            return;
        }
    }
}
