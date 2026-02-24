package com.benjamin538.config;

// picocli
import picocli.CommandLine.Command;

@Command(
    name = "list",
    description = "List possible values"
)
public class ConfigList implements Runnable {
    @Override
    public void run() {
        for (String config : Config.CONFIGURABLES) {
            System.out.println(config);
        }
    }
}
