package com.benjamin538.index;

// anim
import com.benjamin538.LoadingAnim;

// config
import com.benjamin538.config.ConfigGet;

// mods
import com.benjamin538.index.mods.GetMods;

// developer
import com.benjamin538.index.mods.Developer;

// logging
import com.benjamin538.util.Logging;

// files
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// json
import org.json.JSONObject;

// net
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "profile",
    description = "Edit your developer profile"
)
public class IndexProfile implements Runnable {
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Override
    public void run() {
        if (ConfigGet.getIndexToken().isEmpty()) {
            logger.fatal("You are not logged in");
        }
        LoadingAnim anim = new LoadingAnim();
        try {
            HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(ConfigGet.getIndexUrl() + "/v1/me")).header("User-Agent", "GeodeCLI").header("Authorization", "Bearer " + ConfigGet.getIndexToken()).build();
            Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"), "profile.json");
            JSONObject profile = new JSONObject();
            if (!Files.exists(tempPath)) {
                Thread.startVirtualThread(anim);
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 401) {
                    logger.fatal("Invalid token. Please re-login and try again");
                } else if (response.statusCode() != 200) {
                    logger.fatal("Failed request: api returned " + response.statusCode());
                }
                profile = new JSONObject(response.body()).getJSONObject("payload");
                anim.stop();
            } else {
                profile = new JSONObject(Files.readString(tempPath));
            }
            String username = profile.getString("username");
            int id = profile.getInt("id");
            String displayName = profile.getString("display_name");
            boolean isVerified = profile.getBoolean("verified");
            boolean isAdmin = profile.getBoolean("admin");
            Developer dev = new Developer(username, displayName, id, isVerified, isAdmin);
            anim.stop();
            while (true) {
                Files.write(tempPath, dev.exportJSON().toString().getBytes());
                logger.clearTerminal();
                System.out.println("Your profile:");
                System.out.println("---------------");
                System.out.println("Username: " + username);
                System.out.println("Display name: " + displayName);
                if (!isVerified) {
                    System.out.print("NOT ");
                }
                System.out.println("Verified");
                if (!isAdmin) {
                    System.out.print("NOT ");
                }
                System.out.println("Admin");
                System.out.println("---------------");
                System.out.println("Actions:\n1.Change display name\n2.View your pending mods\n3.View your published mods");
                System.out.println("---------------");
                String action = logger.askValue("Type action (q to exit)", "", true);
                if (action.toLowerCase().equals("q")) break;
                try {
                    int act = Integer.parseInt(action);
                    switch(act) {
                        case 1:
                            String newName = logger.askValue("Type new display name", "", true);
                            HttpRequest nameRequest = HttpRequest.newBuilder().uri(URI.create(ConfigGet.getIndexUrl() + "/v1/me")).PUT(HttpRequest.BodyPublishers.ofString("{\"display_name\": \"" + newName + "\"}")).header("User-Agent", "GeodeCLI").header("Authorization", "Bearer " + ConfigGet.getIndexToken()).header("Content-Type", "application/json").build();
                            HttpResponse<String> nameResponse = client.send(nameRequest, HttpResponse.BodyHandlers.ofString());
                            if (nameResponse.statusCode() != 200) logger.fatal("Unable to change name: status code " + nameResponse.statusCode());
                            dev.setDisplayName(newName);
                            logger.done("Changed display name to " + newName);
                            Thread.sleep(1500);
                            break;
                        case 2:
                            GetMods.getMods(client, logger, false);
                            break;
                        case 3:
                            GetMods.getMods(client, logger, true);
                            break;
                        default:
                            logger.warn("Wrong action");
                            Thread.sleep(1500);
                            break;
                    }
                } catch(Exception ex) {
                    logger.warn("Wrong action");
                    Thread.sleep(1500);
                }
            }
        } catch(Exception ex) {
            anim.stop();
            logger.fatal("Something went wrong: " + ex.getMessage());
        }
    }
}
