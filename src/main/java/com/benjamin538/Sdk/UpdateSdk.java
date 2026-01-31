package com.benjamin538.Sdk;

// da logging
import com.benjamin538.util.Logging;

// file stuff
import java.io.File;

// animation
import com.benjamin538.LoadingAnim;

// git
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "update",
    description = "Update SDK"
)
public class UpdateSdk implements Runnable {
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Override
    public void run() {
        LoadingAnim anim = new LoadingAnim();
        try {
            File path = new File(System.getenv("GEODE_SDK"), ".git");
            logger.info("Updating SDK");
            Repository repository = new FileRepositoryBuilder().setGitDir(path).build();
            Git git = new Git(repository);
            PullCommand command = git.pull();
            Thread.startVirtualThread(anim);
            command.call();
            git.close();
            anim.stop();
            logger.done("Successfully updated SDK");
        } catch(Exception ex) {
            anim.stop();
            logger.fatal(ex.getMessage());
        }
    }
}
