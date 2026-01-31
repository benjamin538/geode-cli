package com.benjamin538.Sdk;

// animation
import com.benjamin538.LoadingAnim;

// da logging
import com.benjamin538.Util.Logging;

// file stuff
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "install",
    description = "Install SDK"
)
public class InstallSdk implements Runnable {
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Option(names = {"--reinstall"}, description = "Uninstall exising SDK and reinstall")
    boolean reinstall;
    @Option(names = {"--force"}, description = "Force install, even if another location exists")
    boolean force;
    @Parameters(description = "Path to install")
    String path;
    @Override
    public void run() {
        Path newPath = Paths.get(path);
        try {
            Files.createDirectories(newPath);
        } catch(IOException ex) {
            logger.fatal("Could not install SDK: " + ex.getClass().getSimpleName());
        }
        try {
            logger.info("Downloading SDK");
            ProcessBuilder builder = new ProcessBuilder("git", "clone", "https://github.com/geode-sdk/geode.git", path);
            Process process = builder.start();
            LoadingAnim anim = new LoadingAnim();
            Thread.startVirtualThread(anim);
            int code = process.waitFor();
            anim.stop();
            if (code != 0) {
                logger.fatal("Could not install SDK: git exit code is " + code);
            }
            logger.done("Successfully installed SDK");
            logger.info("Please restart your command line to have the GEODE_SDK enviroment variable set.");
            logger.info("Use `geode sdk install-binaries` to install pre-built binaries");
        } catch(Exception ex) {
            logger.fatal("Could not install SDK: " + ex.getMessage());
        }
    }
}
