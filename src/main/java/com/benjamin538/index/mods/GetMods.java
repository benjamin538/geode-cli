package com.benjamin538.index.mods;

// logging
import com.benjamin538.util.Logging;

// config
import com.benjamin538.config.ConfigGet;

// http
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;

// json
import org.json.JSONObject;
import org.json.JSONArray;

public abstract class GetMods {
    public static void getMods(HttpClient client, Logging logger, boolean published) {
        String status = (published ? "accepted" : "pending");
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ConfigGet.getIndexUrl() + "/v1/me/mods?status=" + status)).GET().header("User-Agent", "GeodeCLI").header("Authorization", "Bearer " + ConfigGet.getIndexToken()).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray responseJSON = new JSONObject(response.body()).getJSONArray("payload");
            logger.clearTerminal();
            if (responseJSON.isEmpty()) {
                logger.done("You have no " + status + " mods");
                Thread.sleep(1500);
                return;
            }
            logger.askValue("Press any key to leave", "", false);
        } catch(Exception ex) {
            logger.fatal("Something went wrong: " + ex.getMessage());
        }
    } 
}
