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

// config
import com.benjamin538.config.ConfigGet;
import com.benjamin538.config.ConfigSet;
import com.benjamin538.config.ConfigPath;

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
       Path path = ConfigPath.path();
       if (url == null) {
            logger.info("Your current index URL is: " + ConfigGet.getIndexUrl());
            return;
        }
        if (url.equals("default")) {
            ConfigSet.setIndexUrl("https://api.geode-sdk.org");
            logger.info("Index URL set to: " + "https://api.geode-sdk.org");
        } else {
            ConfigSet.setIndexUrl(url);
            logger.info("Index URL set to: " + url);
        }
    }
}
