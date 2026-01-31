package com.benjamin538.sdk;

// file stuff
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

// da logging
import com.benjamin538.util.Logging;

// animation
import com.benjamin538.LoadingAnim;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "uninstall",
    description = "Uninstall SDK"
)
public class UninstallSdk implements Runnable{
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Override
    public void run() {
        LoadingAnim anim = new LoadingAnim();
        try {
            Path path = Paths.get(System.getenv("GEODE_SDK"));
            if (!Files.isDirectory(path)) {
                throw new IOException("GEODE_SDK doesn't point to a directory");
            }
            if (!logger.askConfirm("Are you sure you want to uninstall Geode SDK?", false)) {
                logger.fail("Aboritng");
                return;
            }
            Thread.startVirtualThread(anim);
            Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).filter(item -> !item.getPath().equals(System.getenv("GEODE_SDK"))).forEach(file -> {
                file.setWritable(true);
                file.delete();
            });
            Files.delete(path);
            anim.stop();
        } catch(IOException ex) {
            logger.fatal("Unable to uninstall SDK: " + ex.getMessage());
        } catch(NullPointerException ex) {
            logger.fatal("Unable to install SDK: Unable to find GEODE_SDK");
        } finally {
            anim.stop();
        }
    }
}