package com.benjamin538.Sdk;

// file stuff
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
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
    @Override
    public void run() {
        setPath(path);
    }

    public void setPath(String newPath) {
        String os = System.getProperty("os.name").toLowerCase().replaceAll("[0-9]", "").replace(" ", "");;
        switch(os) {
            case "windows":
                try {
                    ProcessBuilder builder = new ProcessBuilder("setx", "GEODE_SDK " + newPath);
                    Process process = builder.start();
                    process.waitFor();
                } catch(Exception ex) {
                    logger.warn("Unable to set the GEODE_SDK enviroment to " + path);
                }
            case "linux":
                LinuxShellConfig config = Shell.getShellData(Shell.getShell(), newPath);
                try {
                    InputStream stream = Files.newInputStream(Paths.get(config.getProfile()));
                    OutputStream outStream = Files.newOutputStream(Paths.get(config.getProfileBak()), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    outStream.write(stream.readAllBytes());
                    stream.close();
                } catch(IOException ex) {
                    logger.warn("Failed to write profile backup: " + ex.getMessage());
                }
                try {
                    OutputStream stream = Files.newOutputStream(Paths.get(config.getProfile()), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    stream.write(config.getCommand().getBytes());
                    stream.close();
                } catch(IOException ex) {
                    logger.warn("Couldn't write profile file: " + ex.getMessage() + ". Please check if " + config.getProfile() + " is intact, otherwise apply the created backup");
                }
            case "macos":
                // TODO
        }
    }
}
