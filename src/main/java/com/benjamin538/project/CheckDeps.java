package com.benjamin538.project;
// net stuff
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

// file stuff
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

// json
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

// getting url
import com.benjamin538.config.ConfigGet;

// logging
import com.benjamin538.util.Logging;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.Enumeration;
// java utils
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Command(
    name = "check",
    description = "Check & install the dependencies for this project"
)
public class CheckDeps implements Runnable {
    private Logging logger = new Logging();
    enum OS {
        windows,
        mac_os,
        mac_intel,
        mac_arm,
        android,
        android32,
        android64,
        ios
    }
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Option(names = {"-p", "--platform"}, description = "The platform checked used for platform-specific dependencies. If not specified, uses current host platform if possible")
    void setPlatform(String s) {
        platform = OS.valueOf(s.replace("-", "_"));
    }
    OS platform;
    @Override
    public void run() {
        if(platform == null) {
            platform = detectOS();
        }
        JSONObject modJSON = new JSONObject();
        try {
            Path modJSONpath = Paths.get("", "mod.json");
            modJSON = new JSONObject(Files.readString(modJSONpath));
        } catch(IOException ex) {
            logger.fatal("mod.json not found");
        }
        JSONObject dependencies = new JSONObject();
        try {
            dependencies = modJSON.getJSONObject("dependencies");
        } catch(JSONException ex) {
            logger.fatal("Dependencies not found");
        }
        try {
            Iterator<String> keys = dependencies.keys();
            HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
            while(keys.hasNext()) {
                String mod = keys.next();
                Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"), mod + ".geode");
                Path buildPath = Paths.get("build", "geode-deps", mod);
                // netttttt
                String url = ConfigGet.getIndexURL() + "/v1/mods/" + mod + "/versions";
                if(!Files.exists(tempPath)) {
                    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("User-Agent", "GeodeCLI").GET().build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() != 200) {
                        logger.fatal("Bad status code on " + url + " : " + response.statusCode());
                    }
                    JSONArray respJSON = new JSONObject(response.body()).getJSONObject("payload").getJSONArray("data");
                    if(respJSON.isEmpty()) {
                        logger.fatal("Dependency not found");
                    }
                    logger.info("Downloading " + mod);
                    String downloadLink = respJSON.getJSONObject(0).getString("download_link");
                    HttpRequest downloadReq = HttpRequest.newBuilder().uri(URI.create(downloadLink)).header("User-Agent", "GeodeCLI").GET().build();
                    Files.createDirectories(buildPath);
                    client.send(downloadReq, HttpResponse.BodyHandlers.ofFile(tempPath));
                    logger.done(mod + " installed");
                }
                else {
                    logger.info("Dependency '" + mod + "' found in cache");
                }
                
                ZipFile zipFile = new ZipFile(tempPath.toFile());
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    Path newPath = buildPath.resolve(entry.getName()).normalize();

                    if (entry.isDirectory()) {
                        Files.createDirectories(newPath);
                    } else {
                        if (newPath.getParent() != null) {
                            Files.createDirectories(newPath.getParent());
                        }
                        InputStream stream = zipFile.getInputStream(entry);
                        Files.copy(stream, newPath, StandardCopyOption.REPLACE_EXISTING);
                        stream.close();
                    }
                }
                zipFile.close();
            }
            client.close();
            logger.done("All dependencies resolved");
        } catch(JSONException ex) {
            logger.fatal("JSON failed: " + ex.getMessage());
        } catch(InterruptedException ex) {
            logger.fail("Interrupted");
        } catch(IOException ex) {
            logger.fatal("Client error: " + ex.getMessage());
        }
        // i cant believe that it IS working ❤️‍🩹
    }

    private OS detectOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();

        if (osName.contains("win")) return OS.windows;
        if (osName.contains("mac")) {
            if (arch.contains("aarch64") || arch.contains("arm")) return OS.mac_arm;
            return OS.mac_intel;
        }
        if (osName.contains("android")) return OS.android;

        return OS.windows;
    }
}
