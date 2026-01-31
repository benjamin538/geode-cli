package com.benjamin538.sdk;

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

@Command(
    name = "update",
    description = "Update SDK"
)
public class UpdateSdk implements Runnable {
    private Logging logger = new Logging();
    @Override
    public void run() {
        LoadingAnim anim = new LoadingAnim();
        try {
            File path = new File(System.getenv("GEODE_SDK") + "\\.git");
            logger.info("Updating SDK");
            Repository repository = new FileRepositoryBuilder().setGitDir(path).build();
            Git git = new Git(repository);
            PullCommand command = git.pull();
            Thread.startVirtualThread(anim);
            command.call();
            git.close();
            logger.done("Successfully updated SDK");
        } catch(Exception ex) {
            logger.fatal(ex.getMessage());
        } finally {
            anim.stop();
        }
    }
}
