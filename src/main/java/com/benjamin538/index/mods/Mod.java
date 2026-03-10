package com.benjamin538.index.mods;

// list
import java.util.List;

public class Mod {
    private String version;
    private String name;
    private String id;
    private List<ModDeveloper> developers;
    private int downloadCounts;
    private boolean validated;

    public Mod(String version, String name, String id, List<ModDeveloper> developers, int downloadCounts, boolean validated) {
        this.version =  version;
        this.name = name;
        this.id = id;
        this.developers = developers;
        this.downloadCounts = downloadCounts;
        this.validated = validated;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public int getDownloadCounts() {
        return downloadCounts;
    }

    public boolean getValidated() {
        return validated;
    }

    public String getID() {
        return id;
    }

    public List<ModDeveloper> getDevelopers() {
        return developers;
    }
}
