package com.benjamin538.modManagement;

// versionnnnn
import com.benjamin538.Sdk.SdkVersion;

// current dev
import com.benjamin538.profile.CurrentDeveloper;

// da logging
import com.benjamin538.util.Logging;

// jline
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.consoleui.prompt.builder.PromptBuilder;
import org.jline.consoleui.prompt.ConsolePrompt;

// json
import org.json.JSONObject;

// file stuff
import java.io.InputStream;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.nio.file.Path;

// java utils
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.Locale;

// picocli
import picocli.CommandLine.Command;

@Command(
    name = "new",
    description = "Initialize a new Geode project"
)
public class CreateMod implements Runnable {
    private Logging logger = new Logging();
    private String ghAction  = """
name: Build Geode Mod

on:
  workflow_dispatch:
  push:
    branches:
      - "**"

jobs:
  build:
    strategy:
      fail-fast: false
      matrix:
        config:
        - name: Windows
          os: windows-latest

        - name: macOS
          os: macos-latest

        - name: iOS
          os: macos-latest
          target: iOS

        - name: Android32
          os: ubuntu-latest
          target: Android32

        - name: Android64
          os: ubuntu-latest
          target: Android64

    name: ${{ matrix.config.name }}
    runs-on: ${{ matrix.config.os }}

    steps:
      - uses: actions/checkout@v4

      - name: Build the mod
        uses: geode-sdk/build-geode-mod@main
        with:
          combine: true
          target: ${{ matrix.config.target }}

  package:
    name: Package builds
    runs-on: ubuntu-latest
    needs: ['build']

    steps:
      - uses: geode-sdk/build-geode-mod/combine@main
        id: build

      - uses: actions/upload-artifact@v4
        with:
          name: Build Output
          path: ${{ steps.build.outputs.build-output }}
            """;
    @Override
    public void run() {
        Locale.setDefault(Locale.ENGLISH);
        try {
            // init shit
            Terminal terminal = TerminalBuilder.builder().system(true).build();
            ConsolePrompt prompt = new ConsolePrompt(terminal);

            // list
            PromptBuilder builder = prompt.getPromptBuilder();
            builder.createListPrompt()
                .name("templateMenu").message("Choose a template for the mod to be created:")
                .newItem("defaultTemplate").text("Default - Simple mod that adds a button to the main menu.").add()
                .newItem("minimalTemplate").text("Minimal - Minimal mod with only the bare minimum to compile.").add().addPrompt();
            // showing input
            logger.info("This utility will walk you through setting up a new mod.");
            logger.info("You can change any of the properties you set here later on by editing the generated mod.json file.");
            // building
            Map<String, org.jline.consoleui.prompt.PromptResultItemIF> result = prompt.prompt(builder.build());
            String selected = result.get("templateMenu").getResult();
            // gettin info
            String name = logger.askValue("Name", null, true);
            String version = logger.askValue("Version", "v1.0.0", false);
            String developer;
            if (CurrentDeveloper.getDev().equals("")) {
                developer = logger.askValue("Developer", CurrentDeveloper.getDev(), true);
            }
            else {
                developer = logger.askValue("Developer", CurrentDeveloper.getDev(), false);
            }
            String description = logger.askValue("Description", "", false);
            boolean actions = logger.askConfirm("Do you want to add the cross-platform Github action?", true);
            logger.info("Creating project...");
            InputStream stream = getClass().getClassLoader().getResourceAsStream(selected + ".zip");
            Files.createDirectory(Paths.get(name));
            ZipInputStream zis = new ZipInputStream(stream);
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path newPath = Paths.get(name).resolve(entry.getName());

                if (entry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    if (newPath.getParent() != null) {
                        Files.createDirectories(newPath.getParent());
                    }
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
            // unzip done, changing da mod.json
            String sdkVersion = new SdkVersion().getVersion();
            JSONObject modJSON = new JSONObject(Files.readString(Paths.get(name, "mod.json")));
            modJSON.put("geode", sdkVersion);
            String modID =  developer.toLowerCase().replace(" ", "-").replace("\"", "") + "." + name.toLowerCase().replace(" ", "-").replace("\"", "");
            modJSON.put("id", modID);
            modJSON.put("version", version);
            modJSON.put("name", name);
            modJSON.put("developer", developer);
            modJSON.put("description", description);
            Files.write(Paths.get(name, "mod.json"), modJSON.toString(4).getBytes());
            if (actions) {
                Files.createDirectories(Paths.get(name, ".github", "workflows"));
                Files.write(Paths.get(name, ".github", "workflows", "multi-platform.yaml"), ghAction.getBytes());
            }
            logger.info("Created project \"" + name + "\".");
            if (sdkVersion.equals("")) {
                logger.warn("Since program cant find GEODE_SDK, version of SDK in mod.json is set to an empty string. Change this field after installing SDK.");
            }
        } catch (Exception ex) {
           logger.fatal("Something went wrong: " + ex.getMessage());
        }
    }
}