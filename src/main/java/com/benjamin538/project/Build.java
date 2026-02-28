package com.benjamin538.project;

// file
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

// logging
import com.benjamin538.util.Logging;

// utils
import java.util.ArrayList;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;

@Command(
    name = "build",
    description = "Builds the project at the current directory"
)
public class Build implements Runnable {
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, description = "Print help", usageHelp = true)
    boolean help;
    @Option(names = {"-p", "--platform"}, description = "Which platform to cross-compile to, if possible")
    void setPlatform(String s) {
        platform = OS.valueOf(s.replace("-", "_"));
    }
    OS platform;
    @Option(names = {"--ninja"}, description = "Whether to explicitly use Ninja instead of the VS generator (Windows only)")
    boolean ninja;
    @Option(names = {"-b", "--build-only"}, description = "Whether to only build project")
    boolean buildOnly;
    @Option(names = {"-c", "--configure-only"}, description = "Whether to only configure cmake")
    boolean configOnly;
    @Option(names = {"--ndk"}, description = "Android NDK path, uses ANDROID_NDK_ROOT env var otherwise")
    String ndk;
    @Parameters(description = "Extra cmake arguments when configuring")
    Set<String> extraConfArgs = new HashSet<>();
    @Override
    public void run() {
        Path CMakePath = Paths.get("CMakeLists.txt");
        if(!Files.exists(CMakePath)) {
            logger.fatal("Could not find CMakeLists.txt. Please run this within a Geode project!");
        }
        if(platform == OS.android) {
            logger.warn("Assuming 64-bit Android, use \"-p android32\" to build for 32-bit Android");
            platform = OS.android64;
        }
        OS detectedOS = DetectOS.detectOS();
        boolean crossCompiling;
        if(detectedOS == OS.windows) {
            crossCompiling = (platform != OS.windows);
        } else if(detectedOS == OS.linux) {
            crossCompiling = true;
        } else if(detectedOS == OS.mac_os) {
            crossCompiling = (platform != OS.mac_os);
        } else {
            crossCompiling = true;
        }
        String buildFolder = (crossCompiling ? "build-" + platform.toString().replace("_", "-") : "build");
        ArrayList<String> args = new ArrayList<>();
        args.add("cmake");
        switch(platform) {
            case OS.win:
            case OS.windows:
            case OS.linux:
                if(crossCompiling) {
                    Path path;
                    if (System.getenv("LOCALAPPDATA") != null) {
                        path = Paths.get(System.getenv("LOCALAPPDATA"), "Geode", "cross-tools");
                    }
                    else {
                        path = Paths.get(System.getProperty("user.home"),".local", "share", "Geode", "cross-tools");
                    }
                    Path splatPath = path.resolve("splat");
                    Path toolchainPath = splatPath.resolve("clang-msvc-sdk");
                    if(!extraConfArgs.contains("-DCMAKE_TOOLCHAIN_FILE")) {
                        args.add("-DCMAKE_TOOLCHAIN_FILE=" + toolchainPath.resolve("clang-msvc.cmake").toString());
                    }
                    if(!extraConfArgs.contains("-DSPLAT_DIR")) {
                        args.add("-DSPLAT_DIR=" + splatPath.toString());
                    }
                    args.add("-DHOST_ARCH=x64");
                } else if(ninja) {
                    Collections.addAll(args, "-G", "Ninja");
                } else {
                    Collections.addAll(args, "-A", "x64");
                }
                break;
            case OS.mac_os:
            case OS.mac_intel:
            case OS.mac_arm:
                if(crossCompiling) {
                    logger.fatal("Sorry! But we do not know of any way to cross-compile to MacOS.");
                }
                args.add("-DCMAKE_OSX_DEPLOYMENT_TARGET=10.15");
                break;
            case OS.android:
            case OS.android64:
            case OS.android32:
                if (!buildOnly) {
                    if(ndk == null) {
                        if(System.getenv("ANDROID_NDK_ROOT") == null) {
                            logger.fatal("Failed to get NDK path, either pass it through --ndk or set the ANDROID_NDK_ROOT enviroment variable");
                        }
                        ndk = System.getenv("ANDROID_NDK_ROOT");
                    }
                    Path toolchainPath = Paths.get(ndk, "build", "cmake", "android.toolchain.cmake");
                    if(!Files.exists(toolchainPath)) {
                        logger.fatal("Invalid NDK path " + ndk + ", could not find toolchain");
                    }
                    args.add("-DCMAKE_TOOLCHAINFILE=" + toolchainPath.toString());
                    if(platform == OS.android32) {
                        args.add("-DANDROID_ABI=armeabi-v7a");
                    } else {
                        args.add("-DANDROID_ABI=arm64-v8a");
                    }
                    args.add("-DANDROID_PLATFORM=23");
                    if(detectedOS == OS.windows && !extraConfArgs.contains("-G")) {
                        Collections.addAll(args, "-G", "Ninja");
                    }
                    args.add("-DCMAKE_EXPORT_COMPILE_COMMANDS=1");
                    // i might actually make cli install mods on android if any adb library exists or install in media folder
                    args.add("-DGEODE_DONT_INSTALL_MODS=1");
                }
                break;
            case OS.ios:
                if(crossCompiling && detectedOS != OS.mac_os) {
                    logger.fatal("Sorry! but we do not know of any way to cross-compile to iOS when not using MacOS.");
                }
                args.add("-DCMAKE_SYSTEM_NAME=iOS");
                args.add("-DGEODE_TARGET_PLATFORM=iOS");
                // im not gonna do that with ios
                args.add("-DGEODE_DONT_INSTALL_MODS=1");
            default:
                logger.fatal("Unknown OS " + platform.toString());
        }
        String buildType = (platform == OS.windows ? "RelWithDebInfo" : "Debug");

        if(!buildOnly) {
            Collections.addAll(args, "-B", buildFolder);
            args.addAll(extraConfArgs);
            args.add("-DCMAKE_BUILD_TYPE=" + buildType);
            ProcessBuilder builder = new ProcessBuilder(args);
            try {
                builder.inheritIO();
                Process cmake = builder.start();
                int status = cmake.waitFor();
                if(status != 0) {
                    logger.fail("CMake returned code " + status);
                    logger.info("Tip: there's a little chance that deleting build folder might ACTUALLY help");
                    System.exit(1);
                }
            } catch(Exception ex) {
                logger.fatal("Failed to start cmake");
            }
        }

        if(!configOnly) {
            ArrayList<String> buildArgs = new ArrayList<>();
            Collections.addAll(buildArgs, "cmake", "--build", buildFolder, "--config", buildType);
            ProcessBuilder builder = new ProcessBuilder(buildArgs);
            try {
                builder.inheritIO();
                Process cmake = builder.start();
                int status = cmake.waitFor();
                if(status != 0) {
                    logger.fatal("CMkae returned code " + status);
                }
            } catch(Exception ex) {
                logger.fatal("Failed to run CMake: " + ex.getMessage());
            }
        }
    }
}
