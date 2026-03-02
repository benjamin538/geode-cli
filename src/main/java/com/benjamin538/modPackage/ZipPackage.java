package com.benjamin538.modPackage;

// logging
import com.benjamin538.util.Logging;

import java.io.FileOutputStream;
import java.io.IOException;
// files
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.stream.Stream;

// json
import org.json.JSONObject;

// utils
import java.util.HashSet;
import java.util.Set;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "new",
    description = "Create a .geode package"
)
public class ZipPackage implements Runnable {
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Parameters(description = "Location of mod's folder")
    Path rootPath;
    @Option(names = {"-b", "--binary"}, description = "Add an external binary file. By default, all known binary files (.dll, .lib, .dylib, .so) named after the mod ID in the root path are included")
    Set<Path> binary = new HashSet<>();
    @Option(names = {"-o", "--output"}, description = "Location of output file. If not provided, the resulting file is named {mod.id}.geode and placed at the root path")
    Path output;
    @Option(names = {"-i", "--install"}, description = "Whether to install the generated package after creation")
    boolean install;
    @Override
    public void run() {
        logger.info("Zipping");
        try {
            Path jsonPath = rootPath.resolve("mod.json");
            if (!Files.exists(jsonPath)) {
                logger.fatal("mod.json not found");
            }
            String modid = new JSONObject(Files.readString(jsonPath)).getString("id");
            if (output == null) {
                output = Paths.get(modid + ".geode");
            }
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(output.toString()));
            Stream<Path> stream = Files.list(rootPath);
            stream.filter(Files::isRegularFile).forEach(path -> {
                try {
                    if(path.getFileName().toString().equals(output.getFileName().toString())) {
                        return;
                    }
                    zos.putNextEntry(new ZipEntry(path.getFileName().toString()));
                    Files.copy(path, zos);
                    zos.closeEntry();
                } catch(IOException ex) {
                    logger.fail("Cant put " + path.getFileName().toString() + " to " + output + ": " + ex.getMessage());
                }
            });
            if (binary != null) {
                for(Path binPath : binary) {
                    if (!Files.exists(binPath)) {
                        logger.fail("Binary file " + binPath.getFileName().toString() + " does not exists!");
                        continue;
                    }
                    if (Files.isDirectory(binPath)) {
                        logger.fail("Binary file should be a FILE, not a DIRECTORY");
                        continue;
                    }
                    zos.putNextEntry(new ZipEntry(binPath.getFileName().toString()));
                    Files.copy(binPath, zos);
                    zos.closeEntry();
                }
            }
            zos.close();
            logger.done("Successfully packaged " + output);
            if (install) {
                InstallPackage.installPackage(output);
            }

        } catch(Exception ex) {
            logger.fatal("Failed to zip file: " + ex.getMessage());
        }
    }
}
