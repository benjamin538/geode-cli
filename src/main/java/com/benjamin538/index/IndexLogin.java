package com.benjamin538.index;

// anim
import com.benjamin538.LoadingAnim;

// logging
import com.benjamin538.util.Logging;

// json
import org.json.JSONObject;

// config
import com.benjamin538.config.ConfigSet;
import com.benjamin538.config.ConfigGet;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

// net
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.io.IOException;

@Command(
    name = "login",
    description = "Login via GitHub"
)
public class IndexLogin implements Runnable {
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Option(names = {"--token"}, description = "Existing access token to use")
    String token;
    @Option(names = {"--github-token"}, description = "Existing GitHub access token to use")
    String ghToken;
    @Override
    public void run() {
        if (token != null) {
            ConfigSet.setIndexToken(token);
            logger.info("Successfuly logged in with provided token");
            return;
        }
        if (!ConfigGet.getIndexToken().isEmpty()) {
            logger.warn("You are already logged in");
            logger.info("Your token is: " + ConfigGet.getIndexToken());
            return;
        }
        try {
            HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
            if (ghToken != null) {
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ConfigGet.getIndexUrl() + "/v1/login/github/token")).POST(HttpRequest.BodyPublishers.ofString("{\"token\": \""  + ghToken + "\"}")).header("User-Agent", "GeodeCLI").header("Content-Type", "application/json").build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    logger.fatal("Invalid GitHub token");
                }
                JSONObject json = new JSONObject(response.body());
                ConfigSet.setIndexToken(json.getString("payload"));
                logger.info("Successfuly logged in via GitHub token");
                return;
            }
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ConfigGet.getIndexUrl() + "/v1/login/github")).header("User-Agent", "GeodeCLI").header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString("{}")).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logger.fatal("Unable to connect to Geode Index");
            }
/*
{
  "payload": {
    "code": "code",
    "interval": 5,
    "uuid": uuid,
    "uri": "https://github.com/login/device"
  },                                                    "error": ""
}
*/
            JSONObject json = new JSONObject(response.body()).getJSONObject("payload");
            String code = json.getString("code");
            int interval = json.getInt("interval");
            String uuid = json.getString("uuid");
            String url = json.getString("uri");
            logger.info("You will need to complete process in browser");
            logger.info("Your code is: " + code);
            logger.info("Go to " + url + " and type code");
            LoadingAnim anim = new LoadingAnim();
            Thread.startVirtualThread(anim);
            while (true) {
                String token = poll(client, uuid);
                if (token == null) {
                     Thread.sleep(interval * 1000);
                     continue;
                } else {
                     anim.stop();
                     ConfigSet.setIndexToken(token);
                     logger.info("Login successfull");
                     return;
                }
            }
        } catch(Exception ex) {
            logger.fatal("Unable to connect to Geode Index :" + ex.getMessage());
        }
    }

    private String poll(HttpClient client, String uuid) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ConfigGet.getIndexUrl() + "/v1/login/github/poll")).header("Content-Type", "application/json").header("User-Agent", "GeodeCLI").POST(HttpRequest.BodyPublishers.ofString("{\"uuid\": \"" + uuid + "\"}")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }
        return new JSONObject(response.body()).getString("payload");
    }
}
