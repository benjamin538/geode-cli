package com.benjamin538.index;

// logginggngngngm
import com.benjamin538.util.Logging;

// net
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

// config
import com.benjamin538.config.ConfigGet;
import com.benjamin538.config.ConfigSet;

@Command(
    name = "invalidate",
    description = "Invalidate all existing access tokens (logout)"
)
public class IndexInvalidate implements Runnable {
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Override
    public void run() {
        if (!logger.askConfirm("Do you want to log out on all devices?", false)) return;
        String token = ConfigGet.getIndexToken();
        if (token.isEmpty()) {
            logger.fatal("You are not logged in");
        }
        try {
            HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ConfigGet.getIndexUrl() + "/v1/me/tokens")).DELETE().header("User-Agent", "GeodeCLI").header("Authorization", "Bearer " + token).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 401) {
                logger.fatal("Invalid token. Please login again.");
            } else if (response.statusCode() != 204) {
                logger.fatal("Unable to invalidate token");
            }
            ConfigSet.setIndexToken("");
            logger.done("Token invalidated");
        } catch (Exception ex) {
            logger.fatal("Failed to invalidate tokens: " + ex.getMessage());
        }
    }
}
