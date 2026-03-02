package com.benjamin538.index;

// logging
import com.benjamin538.util.Logging;

// check file
import com.benjamin538.util.CheckProfileFile;

// config
import com.benjamin538.config.ConfigGet;
import com.benjamin538.config.ConfigSet;

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
