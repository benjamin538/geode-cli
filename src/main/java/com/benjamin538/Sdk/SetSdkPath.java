package com.benjamin538.Sdk;

// file stuff
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// shell detector
import com.benjamin538.util.Shell;
import com.benjamin538.util.LinuxShellConfig;

// da logging
import com.benjamin538.util.Logging;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

@Command(
    name = "set-path",
    description = "Change SDK Path"
)
public class SetSdkPath implements Runnable {
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Parameters(description = "New SDK Path")
    String path;
    String plist = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\r\n" +
                "<plist version=\"1.0\">\r\n" +
                "<dict>\r\n" +
                "  <key>Label</key>\r\n" +
                "  <string>com.benjamin538.environment</string>\r\n" +
                "  <key>EnvironmentVariables</key>\r\n" +
                "  <dict>\r\n" +
                "    <key>GEODE_SDK</key>\r\n" + 
                "    <string>%s</string>\r\n" + 
                "  </dict>\r\n" +
                "  <key>RunAtLoad</key>\r\n" +
                "  <true/>\r\n" +
                "</dict>\r\n" +
                "</plist>";
    @Override
    public void run() {
        setPath(path);
    }

    public void setPath(String newPath) {
        String os = System.getProperty("os.name").toLowerCase().replaceAll("[0-9]", "").replace(" ", "");
        switch(os) {
            case "windows":
                try {
                    ProcessBuilder builder = new ProcessBuilder("setx", "GEODE_SDK", newPath);
                    Process process = builder.start();
                    process.waitFor();
                    return;
                } catch(Exception ex) {
                    logger.warn("Unable to set the GEODE_SDK enviroment to " + path);
                    return;
                }
            case "linux":
                LinuxShellConfig config = Shell.getShellData(Shell.getShell(), newPath);
                String backup;
                try {
                    try {
                        InputStream stream = Files.newInputStream(Paths.get(config.getProfile()));
                        backup = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                        stream.close();
                    } catch(FileNotFoundException ex) {
                        backup = "";
                    }
                    OutputStream outStream = Files.newOutputStream(Paths.get(config.getProfileBak()), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
                    outStream.write(backup.getBytes());
                } catch(IOException ex) {
                    logger.warn("Failed to write profile backup: " + ex.getMessage());
                    return;
                }
                try {
                    backup = backup.replaceAll(config.getRegexPattern(), config.getCommand());
                    OutputStream stream;
                    if (!backup.contains(config.getCommand())) {
                        stream = Files.newOutputStream(Paths.get(config.getProfile()), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                        stream.write(config.getCommand().getBytes());
                        stream.close();
                        return;
                    }
                    stream = Files.newOutputStream(Paths.get(config.getProfile()), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
                    stream.write(backup.getBytes());
                    stream.close();
                    return;
                } catch(IOException ex) {
                    logger.warn("Couldn't write profile file: " + ex.getMessage() + ". Please check if " + config.getProfile() + " is intact, otherwise apply the created backup");
                    return;
                }
            case "macos":
                // i dont have mac so i rely on stack overflow
                String home = System.getenv("HOME");
                Path envPath = Paths.get(home, "Library", "LaunchAgents", "enviroment.plist");
                try {
                    OutputStream stream = Files.newOutputStream(envPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
                    stream.write(String.format(plist, newPath).getBytes(StandardCharsets.UTF_8));
                    stream.close();
                    return;
                } catch(IOException ex) {
                    logger.warn("Couldn't write enviroment file: " + ex.getMessage());
                    return;
                }
        }
    }
}
