package com.benjamin538.index;

// logging
import com.benjamin538.util.Logging;

// check file
import com.benjamin538.util.CheckProfileFile;

// file
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

// json
import org.json.JSONObject;
import org.json.JSONException;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "url",
    description = "Set the URL for the index (pass default to reset)"
)
public class IndexUrl implements Runnable {
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Option(names = {"-u", "--url"}, description = "URL to set")
    String url;
    @Override
    public void run() {
       CheckProfileFile.checkFile();
       Path path;
       if (System.getenv("LOCALAPPDATA") != null) {
           path = Paths.get(System.getenv("LOCALAPPDATA"), "Geode", "config.json");
       } else {
           path = Paths.get(System.getProperty("user.home"), ".local", "share", "Geode", "config.json");
       }
       JSONObject configJSON = new JSONObject();;
       try {
           configJSON = new JSONObject(Files.readString(path));
       } catch(IOException ex) {
           logger.fatal("Failed to read file: " + ex.getMessage());
       }
       try {
           if (url == null) {
               logger.info("Your current index URL is: " + configJSON.getString("index-url"));
               return;
           }
           if (url.equals("default")) {
               configJSON.put("index-url", "https://api.geode-sdk.org");
               logger.info("Index URL set to: " + configJSON.getString("index-url"));
           } else {
               configJSON.put("index-url", url);
               logger.info("Index URL set to: " + configJSON.getString("index-url"));
           }
       } catch(JSONException ex) {
           logger.fatal("Failed to get/set URL: " + ex.getMessage());
       }
       try {
           Files.write(path, configJSON.toString().getBytes());
       } catch(IOException ex) {
           logger.fatal("Failed to write config: " + ex.getMessage());
       }
    }
}
