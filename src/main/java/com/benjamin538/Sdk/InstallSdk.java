package com.benjamin538.Sdk;

// animation
import com.benjamin538.LoadingAnim;

// for nightly
import com.benjamin538.config.ConfigGet;

// da logging
import com.benjamin538.util.Logging;
import java.io.PrintWriter;

// file stuff
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.File;

// git
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;

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
    @Option(names = {"-v", "--verbose"}, description = "Hide loading animation & show git output")
    boolean verbose;
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
            if(!verbose) {
                Thread.startVirtualThread(anim);
            }
            CloneCommand command = Git.cloneRepository().setURI("https://github.com/geode-sdk/geode").setDirectory(new File(path));
            if(verbose) {
                command.setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)));
            }
            Git git = command.call();
            String latest = null;
            if(!ConfigGet.getSdkNightly()) {
                ListTagCommand listCommand = git.tagList();
                for(Ref tag : listCommand.call()) {
                    String tagName = tag.getName().replace("refs/tags/", "");
                    if(tagName.startsWith("v")) {
                        if(latest == null || tagName.compareTo(latest) > 0 ) {
                            latest = tagName;
                        }
                    }
                }
                CheckoutCommand checkout = git.checkout();
                checkout.setName(latest);
                checkout.call();
            }
            SetSdkPath setsdk = new SetSdkPath();
            setsdk.setPath(newPath.toAbsolutePath().toString());
            new SdkVersion().setVersion(new SdkVersion().getVersion());
            anim.stop();
            git.getRepository().close();
            git.close();
            logger.done("Successfully installed SDK");
            logger.info("Please restart your command line to have the GEODE_SDK enviroment variable set.");
            logger.info("Use `geode sdk install-binaries` to install pre-built binaries");
        } catch(Exception ex) {
            anim.stop();
            logger.fatal("Could not install SDK: " + ex.getMessage());
        }
    }
}
