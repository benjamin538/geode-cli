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

// getting versions
import com.benjamin538.index.GetVersions;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// semver
import org.semver4j.Semver;

@Command(
    name = "check",
    description = "Check & install the dependencies for this project"
)
public class CheckDeps implements Runnable {
    private Logging logger = new Logging();
    private boolean errors = false;
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
        if (modJSON.has("gd")) {
            if (modJSON.get("gd") instanceof JSONObject) {
                JSONObject gdVer = modJSON.getJSONObject("gd");
                switch (platform) {
                    case OS.win:
                    case OS.windows:
                    case OS.linux:
                        if (!(gdVer.get("win") instanceof String)) {
                            logger.fatal("Geometry Dash version not specified for Windows, please specify one in mod.json");
                        }
                        break;
                    case OS.mac_os:
                    case OS.mac_intel:
                    case OS.mac_arm:
                        if (!(gdVer.get("mac") instanceof String)) {
                            logger.fatal("Geometry Dash version not specified for macOS, please specify one in mod.json");
                        }
                        break;
                    case OS.android:
                    case OS.android64:
                    case OS.android32:
                        if (!(gdVer.get("android") instanceof String)) {
                            logger.fatal("Geometry Dash version not specified for Android, please specify one in mod.json");
                        }
                        break;
                    case OS.ios:
                        if (!(gdVer.get("ios") instanceof String)) {
                            logger.fatal("Geometry Dash version not specified for iOS, please specify one in mod.json");
                        }
                        break;
                }
            }
        }
        if (dependencies.isEmpty()) {
            return;
        }
        Path buildFolder = Paths.get(folder, "geode-deps");
        Map<String, String> externalMap = new HashMap<>();
        for(String ext : externals) {
            if(ext.contains(":")) {
                String[] parts = ext.split(":");
                externalMap.put(parts[0], parts[1]);
            } else {
                externalMap.put(ext, null);
            }
        }
        try {
            if (!Files.exists(buildFolder)) {
                Files.createDirectories(buildFolder);
            }
            Iterator<String> keys = dependencies.keys();
            HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
            while(keys.hasNext()) {
                String mod = keys.next();
                String modVersion = dependencies.getString(mod);
                if(externalMap.containsKey(mod)) {
                    String extVersion = externalMap.get(mod);
                    if (extVersion != null) {
                        try {
                            boolean matches = new Semver(extVersion.replace("v", "")).satisfies(modVersion.replace("v", ""));
                            if(!matches) {
                                logger.fail("External dependency '" + mod + "' version '" + extVersion + "' does not match required '" + modVersion + "'");
                                errors = true;
                            } else {
                                logger.info("Dependency '" + mod + "' found as external");
                            }
                        } catch(Exception ex) {
                            logger.fatal("Unable to get version: " + ex.getMessage());
                            if(!extVersion.equals(modVersion)) {
                                logger.fail("External dependency '" + mod + "' version '" + extVersion + "' does not match required '" + modVersion + "' (note: optionality is ignored when verifying external dependencies)");
                                errors = true;
                            } else {
                                logger.info("Dependency '" + mod + "' found as external");
                            }
                        }
                        continue;
                    } else {
                        logger.info("Dependency '" + mod + "' marked as external");
                        continue;
                    }
                }
                Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"), mod + ".geode");
                Path buildPath = Paths.get(folder, "geode-deps", mod);
                if(!Files.exists(tempPath)) {
                    JSONArray versions = GetVersions.getVersions(client, mod);
                    JSONObject selectedVersion = null;
                    for(int i = 0; i < versions.length(); i++) {
                        JSONObject ver = versions.getJSONObject(i);
                        if(new Semver(ver.getString("version")).satisfies(modVersion)) {
                            selectedVersion = ver;
                            break;
                        }
                    }
                    if(selectedVersion == null) {
                        logger.fail("Dependency '" + mod + "' version '" + modVersion + "' not found");
                        errors = true;
                        continue;
                    }
                    String downloadLink = selectedVersion.getString("download_link");
                    logger.info("Downloading " + mod);
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
            if (!errors) {
                logger.done("All dependencies resolved");
            } else {
                logger.fatal("Some dependencies were not resolved");
            }
        } catch(JSONException ex) {
            logger.fatal("JSON failed: " + ex.getMessage());
        } catch(InterruptedException ex) {
            logger.fail("Interrupted");
        } catch(IOException ex) {
            logger.fatal("Client error: " + ex.getMessage());
        } catch(Exception ex) {
            logger.fatal("Unknown error: " + ex.getMessage());
        }
        // i cant believe that it IS working ❤️‍🩹
    }
}
