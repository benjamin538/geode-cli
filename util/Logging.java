package util;

import java.util.Scanner;

public class Logging {
    // codes
    private String brightCyan = "\u001B[36m";
    private String brightRed = "\u001B[31m";
    private String brightYellow = "\u001B[33m";
    private String brightGreen = "\u001B[32m";
    private String brightPurple = "\u001B[35m";
    private String resetCode = "\u001B[0m";

    // scanner
    private Scanner scanner = new Scanner(System.in);

    // "macroses"
    public void warn(String message) {
        System.out.println(brightYellow + "| Warn | " + resetCode + message);
    }

    public void info(String message) {
        System.out.println(brightCyan + "| Info | " + resetCode + message);
    }

    public void done(String message) {
        System.out.println(brightGreen + "| Done | " + resetCode + message);
    }

    public void fail(String message) {
        System.err.println(brightRed + "| Fail | " + resetCode + message);
    }

    public void fatal(String message) {
        System.err.println(brightRed + "| Fail | " + resetCode + message);
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
            brightPurple + "| Okay | " + resetCode + text + (bool ? " (Y/n) " : " (y/N) ")
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