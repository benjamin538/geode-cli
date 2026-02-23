package com.benjamin538.Sdk;

// da logging
import com.benjamin538.util.Logging;

// file stuff
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.nio.file.Files;
import java.io.InputStream;

// net stuff
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

// json
import org.json.JSONObject;

// animation
import com.benjamin538.LoadingAnim;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "install-binaries",
    description = "Install prebuilt binaries for SDK"
)
public class InstallBinaries implements Runnable {
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Override
    public void run() {
        LoadingAnim anim = new LoadingAnim();
        try {
            String os = System.getProperty("os.name").toLowerCase().replaceAll("[0-9]", "").replace(" ", "");
            Path path = Paths.get(System.getenv("GEODE_SDK"), "VERSION");
            InputStream stream = Files.newInputStream(path);
            String version = new SdkVersion().getVersion();
            logger.info("Installing binaries for "  + os + " " + version);
            stream.close();
            Thread.startVirtualThread(anim);
            HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.github.com/repos/geode-sdk/geode/releases/tags/v" + version.replace(" ", ""))).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());
            String downloadUrl = "";
            for (Object asset : json.getJSONArray("assets")) {
                JSONObject item = (JSONObject) asset;
                String name = item.getString("name");
                if (name.toLowerCase().contains("installer") || !name.toLowerCase().contains("geode")) {
                    continue;
                }
                switch (os) {
                    case "windows":
                    case "win":
                    case "linux":
                        if (name.toLowerCase().contains("-win")) {
                            downloadUrl = item.getString("browser_download_url");
                            break;
                        }
                    default:
                        if (name.toLowerCase().contains(String.format("-{}", os))) {
                            downloadUrl = item.getString("browser_download_url");
                            break;
                        }
                }
                if (name.toLowerCase().contains(String.format("-{}", os))) {
                    downloadUrl = item.getString("browser_download_url");
                    break;
                }
            }

            if (downloadUrl.isEmpty()) {
                anim.stop();
                logger.fail("No binaries found for " + os);
                if (logger.askConfirm("Print JSON?", true)) {
                    System.out.println(json.toString(4));
                    System.exit(1);
                }
            }
            path = Paths.get(System.getenv("GEODE_SDK"), "bin", version.replace(" ", ""));
            Files.createDirectories(path);
            request = HttpRequest.newBuilder().uri(URI.create(downloadUrl)).build();
            Path tempzip = Paths.get(path.toAbsolutePath().toString(), "tempzip.zip");
            client.send(request, HttpResponse.BodyHandlers.ofFile(tempzip));
            client.close();
            InputStream readZipStream = Files.newInputStream(tempzip);
            ZipInputStream zipStream = new ZipInputStream(readZipStream);
            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                Path newPath = Paths.get(path.toAbsolutePath().toString()).resolve(entry.getName());

                if (entry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    if (newPath.getParent() != null) {
                        Files.createDirectories(newPath.getParent());
                    }
                    Files.copy(zipStream, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipStream.closeEntry();
            }
            Files.delete(tempzip);
            zipStream.close();
            anim.stop();
            logger.done("Binaries installed");
        } catch(Exception ex) {
            anim.stop();
            logger.fatal("Unable to install binaries: " + ex.getMessage());
        }
    }
}
