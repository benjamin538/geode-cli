package com.benjamin538.index;

// net
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

// meth
import java.math.BigInteger;

// file stuff
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// anim
import com.benjamin538.LoadingAnim;

// json
import org.json.JSONArray;
import org.json.JSONObject;

// config
import com.benjamin538.config.ConfigPath;

// hash
import java.security.MessageDigest;

// current profile
import com.benjamin538.profile.CurrentDeveloper;

// check file
import com.benjamin538.util.CheckProfileFile;

// logging
import com.benjamin538.util.Logging;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "install",
    description = "Install a mod from the index to the current profile"
)
public class IndexInstall implements Runnable {
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help")
    boolean help;
    @Parameters(description = "Mod ID to install")
    String id;
    @Parameters(description = "Mod version to install, defaults to latest", defaultValue = "latest")
    String version;
    private HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    @Override
    public void run() {
        JSONArray versions = GetVersions.getVersions(client, id);
        LoadingAnim anim = new LoadingAnim();
        try {
            JSONArray profiles = new JSONObject(Files.readString(ConfigPath.path())).getJSONArray("profiles");
            String current = CurrentDeveloper.get();
            CheckProfileFile.checkFile();
            for (Object mod : versions) {
                JSONObject modJSON = (JSONObject) mod;
                if (version.equals("latest") || version.equals(modJSON.getString("version"))) {
                    String downloadLink = modJSON.getString("download_link");
                    String modHash = modJSON.getString("hash");
                    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(downloadLink)).GET().build();
                    for(Object profile : profiles) {
                        JSONObject _profile = (JSONObject) profile;
                        String name = _profile.getString("name");
                        if(name.equals(current)) {
                            String gdPath = Paths.get(_profile.getString("gd-path"), "..").normalize().toString();
                            Path modDir = Paths.get(gdPath, "geode", "mods");
                            Path modFile = modDir.resolve(id + ".geode");
                            if (!Files.exists(modDir)) {
                                logger.warn("Path " + modDir.toAbsolutePath().toString() + "does not exists, creating one");
                                logger.warn("(Did you installed Geode?");
                                Files.createDirectories(modDir);
                            }
                            if (Files.exists(modFile)) {
                                Files.delete(modFile);
                            }
                            Thread.startVirtualThread(anim);
                            client.send(request, HttpResponse.BodyHandlers.ofFile(modFile));
                            byte[] data = Files.readAllBytes(modFile);
                            byte[] hash = MessageDigest.getInstance("SHA-256").digest(data);
                            String localHash = String.format("%064x", new BigInteger(1, hash));
                            if (modHash != localHash) {
                                Files.delete(modFile);
                                logger.fatal("Downloaded file doesn't match expected hash\n" + modHash + "\nvs\n" + localHash + "\nTry again, if this issue persists, open issue on GitHub:\nhttps://github.com/benjamin538/geode-cli/issues/new");
                            }
                            logger.done("Mod " + id + " installed");
                            return;
                        }
                    }
                }
            }
        } catch(Exception ex) {
            anim.stop();
            logger.fatal("Failed to get mod from index: " + ex.getMessage());
        }
    }
}
