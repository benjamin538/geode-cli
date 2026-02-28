package com.benjamin538.Sdk;

// da logging
import com.benjamin538.util.Logging;

// file stuff
import java.io.File;

// animation
import com.benjamin538.LoadingAnim;

// nightly
import com.benjamin538.config.ConfigGet;
import com.benjamin538.config.ConfigSet;

// git
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "update",
    description = "Update SDK"
)
public class UpdateSdk implements Runnable {
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Parameters(description = "Set update branch, can be nightly, stable, or any specific version", defaultValue = "")
    String branch;
    @Override
    public void run() {
        LoadingAnim anim = new LoadingAnim();
        try {
            if(branch.equals("")) {
                boolean nightly = ConfigGet.getSdkNightly();
            } else if(branch.equals("stable")) {
                logger.info("Switching to stable");
                ConfigSet.setSdkNightly(false);
            } else if(branch.equals("nightly")) {
                logger.info("Switching to nightly");
                ConfigSet.setSdkNightly(true);
            } else {
                logger.fatal("Unknown branch " + branch);
            }
            File path = new File(System.getenv("GEODE_SDK"), ".git");
            logger.info("Updating SDK");
            Repository repository = new FileRepositoryBuilder().setGitDir(path).build();
            Git git = new Git(repository);
            Thread.startVirtualThread(anim);
            git.fetch().call();
            switchRef(git, repository, branch);
            git.close();
            anim.stop();
            logger.done("Successfully updated SDK");
        } catch(Exception ex) {
            anim.stop();
            logger.fatal(ex.getMessage());
        }
    }

    private void switchRef(Git git, Repository repo, String name) throws Exception {
        Ref fetchHead = repo.findRef("FETCH_HEAD");
        if (fetchHead != null && fetchHead.getObjectId() != null) {
           RefUpdate update = repo.updateRef("refs/heads/main");
           update.setNewObjectId(fetchHead.getObjectId());
           update.forceUpdate();
        }
        git.checkout().setName("refs/heads/main").setForce(true).call();
        Ref ref = repo.findRef(name);
        if (ref != null) {
            git.checkout().setName(ref.getName()).call();
        }
    }
}
