package com.benjamin538.Sdk;

// da logging
import com.benjamin538.util.Logging;

// file stuff
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.nio.file.Files;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
            String version = new String(stream.readAllBytes(), StandardCharsets.UTF_8).replace("\r\n", " ");
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
                logger.fatal("No binaries found for " + os);
            }
            path = Paths.get(System.getenv("GEODE_SDK"), "bin", version.replace(" ", ""));
            Files.createDirectories(path);
            request = HttpRequest.newBuilder().uri(URI.create(downloadUrl)).build();
            Path tempzip = Paths.get(path.toAbsolutePath().toString(), "tempzip.zip");
            client.send(request, HttpResponse.BodyHandlers.ofFile(tempzip));
            client.close();
            InputStream readZipStream = Files.newInputStream(tempzip);
            ZipInputStream zipStream = new ZipInputStream(readZipStream);
            ZipEntry zipEntry = zipStream.getNextEntry();
            byte[] buffer = new byte[1024];
            while (zipEntry != null) {
                File newFile = new File(path.toAbsolutePath().toString(), zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zipStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipStream.closeEntry();
                zipEntry = zipStream.getNextEntry();
            }
            Files.delete(tempzip);
            zipStream.close();
            anim.stop();
            logger.info("Binaries installed");
        } catch(Exception ex) {
            anim.stop();
            logger.fatal("Unable to install binaries: " + ex.getMessage());
        }
    }
}
