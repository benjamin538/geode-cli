package com.benjamin538.index;

// net
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;

// config
import com.benjamin538.config.ConfigGet;

// json
import org.json.JSONObject;
import org.json.JSONArray;

public class GetVersions {
    public static JSONArray getVersions(HttpClient client, String modid) {
        try {
            String url = ConfigGet.getIndexUrl() + "/v1/mods/" + modid + "/versions";
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("User-Agent", "GeodeCLI").GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return null;
            }
            return new JSONObject(response.body()).getJSONObject("payload").getJSONArray("data");
        } catch (Exception ex) {
            return null;
        }
    }
}
