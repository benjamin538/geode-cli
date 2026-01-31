package com.benjamin538.sdk;

// animation
import com.benjamin538.LoadingAnim;
import com.benjamin538.util.Logging;

// file stuff
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.File;

// git
import org.eclipse.jgit.api.Git;

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
            logger.fatal("Could not install SDK: " + ex.getMessage());
        }
        LoadingAnim anim = new LoadingAnim();
        try {
            logger.info("Downloading SDK");
            Thread.startVirtualThread(anim);
            Git.cloneRepository().setURI("https://github.com/geode-sdk/geode.git").setDirectory(new File(path)).call();
            SetSdkPath setsdk = new SetSdkPath();
            setsdk.setPath(newPath.toAbsolutePath().toString());
            anim.stop();
            logger.done("Successfully installed SDK");
            logger.info("Please restart your command line to have the GEODE_SDK enviroment variable set.");
            logger.info("Use `geode sdk install-binaries` to install pre-built binaries");
        } catch(Exception ex) {
            logger.fatal("Could not install SDK: " + ex.getMessage());
        } finally {
            anim.stop();
        }
    }
}
