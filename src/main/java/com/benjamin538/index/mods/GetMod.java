package com.benjamin538.index.mods;

// logging
import com.benjamin538.util.Logging;

// colors
import com.benjamin538.util.Colors;

// net
import java.net.http.HttpClient;

public abstract class GetMod {
    public static void getMod(HttpClient client, Logging logger, Mod mod) {
        logger.clearTerminal();
        System.out.println(Colors.BOLD + mod.getID() + Colors.RESET);
        System.out.println("---------------");
        System.out.println("Status: "  + mod.getStatus());
        System.out.println("Name: " + mod.getName());
        System.out.println("Download counts: " + mod.getDownloadCounts());
        System.out.println("Developers:");
        for (int i = 0; i < mod.getDevelopers().size(); i++) {
            System.out.println("  " + mod.getDevelopers().get(i).getDisplayName() 
            + " (" + mod.getDevelopers().get(i).getUsername() + ")" 
            + (mod.getDevelopers().get(i).getIsOwner() ? " [owner]" : ""));
        }
        logger.askValue("Press any key to exit", "", false);
    }
}
