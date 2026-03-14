package com.benjamin538.index.mods;

// logging
import com.benjamin538.util.Logging;

// for cache
import java.nio.file.Paths;
import java.time.Instant;
import java.nio.file.Path;
import java.nio.file.Files;

// config
import com.benjamin538.config.ConfigGet;

// http
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;

// map
import java.util.ArrayList;
import java.util.List;

// json
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public abstract class GetMods {
    public static void getMods(HttpClient client, Logging logger, boolean published) {
        String status = (published ? "accepted" : "pending");
        try {
            JSONArray responseJSON = new JSONArray();
            logger.clearTerminal();
            Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"), status + ".json");
            if (Files.exists(tempPath)) {
                if (!(Instant.now().getEpochSecond() > new JSONObject(Files.readString(tempPath)).getInt("expires_in"))) {
                    responseJSON = new JSONObject(Files.readString(tempPath)).getJSONArray("payload");
                } else {
                    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ConfigGet.getIndexUrl() + "/v1/me/mods?status=" + status)).GET().header("User-Agent", "GeodeCLI").header("Authorization", "Bearer " + ConfigGet.getIndexToken()).build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    responseJSON = new JSONObject(response.body()).getJSONArray("payload");
                }
            } else {
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ConfigGet.getIndexUrl() + "/v1/me/mods")).GET().header("User-Agent", "GeodeCLI").header("Authorization", "Bearer " + ConfigGet.getIndexToken()).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                responseJSON = new JSONObject(response.body()).getJSONArray("payload");
            }
            JSONObject resp = new JSONObject();
            if (Files.exists(tempPath)) resp = new JSONObject(Files.readString(tempPath));
            resp.put("payload", responseJSON);
            try {
                resp.getInt("expires_in");
            } catch(JSONException ex) {
                resp.put("expires_in", 0);
            }
            if (Instant.now().getEpochSecond() > resp.getInt("expires_in")) resp.put("expires_in", Instant.now().getEpochSecond() + 300);
            Files.write(tempPath, resp.toString().getBytes());
            if (responseJSON.isEmpty()) {
                logger.done("You have no " + status + " mods");
                Thread.sleep(1500);
                return;
            }
            List<Mod> mods = new ArrayList<>();
            for (int i = 0; i < responseJSON.length(); i++) {
                JSONObject modJSON = responseJSON.getJSONObject(i);
                Mod mod = new Mod(modJSON);
                mods.add(mod);
                System.out.println(i + ". " + mod.getID());
            }
            String index = logger.askValue("Select mod to view (q to back)", "", true);
            if (index.equals("q")) return;
            try {
                int intIndex = Integer.parseInt(index);
                if (intIndex < 0 || intIndex > mods.size()) {
                    logger.fatal("Wrong number");
                }
                GetMod.getMod(client, logger, mods.get(intIndex));
            } catch(NumberFormatException ex) {
                logger.fail("Not a number");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            logger.fatal("Something went wrong: " + ex.getMessage());
        }
    } 
}
