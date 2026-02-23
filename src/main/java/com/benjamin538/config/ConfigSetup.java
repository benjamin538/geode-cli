package com.benjamin538.config;

// jline
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import com.benjamin538.profile.AddProfile;
import com.benjamin538.util.Logging;

import org.jline.consoleui.prompt.builder.PromptBuilder;
import org.jline.consoleui.prompt.ConsolePrompt;

import java.nio.file.Files;
import java.nio.file.Paths;
// java utils
import java.util.Locale;
import java.util.Map;

// picocli
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "setup",
    description = "Setup config (if you have manually installed Geode)"
)
public class ConfigSetup implements Runnable {
    private Logging logger = new Logging();
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Print this message or the help of the given subcommand(s)")
    boolean help;
    @Override
    public void run() {
        Locale.setDefault(Locale.ENGLISH);
        try {
            // as same as in CreateMod.java but with os
            Terminal terminal = TerminalBuilder.builder().system(true).build();
            ConsolePrompt prompt = new ConsolePrompt(terminal);
            PromptBuilder builder = prompt.getPromptBuilder();
            builder.createListPrompt()
                .name("osmenu").message("What platform you are using?")
                .newItem("win").text("Windows (select if you use linux)").add()
                .newItem("mac").text("MacOS").add()
                .newItem("android32").text("Android 32-bit").add()
                .newItem("android64").text("Android 64-bit").add().addPrompt();
            Map<String, org.jline.consoleui.prompt.PromptResultItemIF> result = prompt.prompt(builder.build());
            String selectedOs = result.get("osmenu").getResult();
            // path
            String path = "";
            while (true) {
                path = logger.askValue("Path to the Geometry Dash app/executable", "", true);
                if (!path.endsWith("GeometryDash.exe")) {
                    logger.fail("Path should end with Geometry Dash executable, not folder");
                    continue;
                }
                if (!Files.exists(Paths.get(path))) {
                    logger.fail("File does not exists");
                    continue;
                }
                break;
            }
            String profile = logger.askValue("Profile name", "", true);
            new AddProfile().addProfile(profile, path, selectedOs);
            logger.done("Configuration complete!");
        } catch(Exception ex) {
            logger.fatal("Something went wrong: " + ex.getMessage());
        }
    }
}
