package com.benjamin538.util;

import java.util.Scanner;

public class Logging {
    // scanner
    private Scanner scanner = new Scanner(System.in);

    // "macroses"
    public void warn(String message) {
        System.out.println(Colors.BRIGHT_RED + "| Warn | " + Colors.RESET + message);
    }

    public void info(String message) {
        System.out.println(Colors.BRIGHT_CYAN + "| Info | " + Colors.RESET + message);
    }

    public void done(String message) {
        System.out.println(Colors.BRIGHT_GREEN + "| Done | " + Colors.RESET + message);
    }

    public void fail(String message) {
        System.err.println(Colors.BRIGHT_RED + "| Fail | " + Colors.RESET + message);
    }

    public void fatal(String message) {
        System.err.println(Colors.BRIGHT_RED + "| Fail | " + Colors.RESET + message);
        System.exit(1);
    }

    public void clearTerminal() {
        System.out.println((char)27 + "c");
    }

    public Boolean confirm(String message, Boolean defaultBool) {
        return askConfirm(message, defaultBool);
    }

    // methods
    public Boolean askConfirm(String text, Boolean bool) {
        System.out.print(
            Colors.BRIGHT_PURPLE + "| Okay | " + Colors.RESET + text + (bool ? " (Y/n) " : " (y/N) ")
        );
        try {
            String yes = scanner.nextLine();
            switch (yes.toLowerCase()) {
                case "yes", "ye", "y" : return true;
                case "no", "n" : return false;
                default: return bool;
            }
        } catch(Exception ex) {
            return bool;
        }
    }

    public String askValue(String prompt, String defaultStr, Boolean required) {
        System.out.print(prompt + (required ? "" : "(optional)") + ": ");
        String line;
        while (scanner.hasNextLine()) {
            try {
                line = scanner.nextLine();
                if (line.isEmpty()) {
                    if (required) {
                        fail("Please enter a value");
                    } else {
                        return defaultStr;
                    }
                } else {
                    return line;
                }
            } catch(Exception ex) {
                fail("Error reading line: " + ex.getMessage());
            }
        }
        return defaultStr; // returning it here too
    }
}