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
import picocli.CommandLine.Parameters;

// java utils
import java.util.Set;
import java.util.HashSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Command(
    name = "check",
    description = "Check & install the dependencies for this project"
)
public class CheckDeps implements Runnable {
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Option(names = {"-p", "--platform"}, description = "The platform checked used for platform-specific dependencies. If not specified, uses current host platform if possible")
    void setPlatform(String s) {
        platform = OS.valueOf(s.replace("-", "_"));
    }
    OS platform;
    @Parameters(description = "Where to install the dependencies; usually the project's build directory. A directory called geode-deps will be created inside the specified installation directory. If not specified, \"build\" is assumed", defaultValue = "build")
    String folder;
    @Option(names = {"--externals"}, description = "Any external dependencies as a list in the form of `mod.id:version`. An external dependency is one that the CLI will not verify exists in any way; it will just assume you have it installed through some other means (usually through building it as part of the same project)")
    Set<String> externals = new HashSet<>();
    @Override
    public void run() {
        if(platform == null) {
            platform = DetectOS.detectOS();
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
        if(dependencies.isEmpty()) {
            return;
        }
        try {
            Iterator<String> keys = dependencies.keys();
            HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
            while(keys.hasNext()) {
                String mod = keys.next();
                String modVersion = dependencies.getString(mod);
                if(externals.contains(mod + ":" + modVersion)) {
                    continue;
                }
                Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"), mod + ".geode");
                Path buildPath = Paths.get(folder, "geode-deps", mod);
                // netttttt
                String url = ConfigGet.getIndexUrl() + "/v1/mods/" + mod + "/versions";
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
                Path depOptionsPath = Paths.get("build", "geode-deps", mod, "geode-dep-options.json");
                JSONObject depJSON = new JSONObject();
                depJSON.put("required", true);
                Files.write(depOptionsPath, depJSON.toString().getBytes());
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
}
