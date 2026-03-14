package com.benjamin538.index.mods;

// list
import java.util.List;

// json
import org.json.JSONObject;

public class Mod {
    private String version;
    private String name;
    private String id;
    private List<ModDeveloper> developers;
    private int downloadCounts;
    private String status;

    public Mod(String version, String name, String id, List<ModDeveloper> developers, int downloadCounts, String status) {
        this.version =  version;
        this.name = name;
        this.id = id;
        this.developers = developers;
        this.downloadCounts = downloadCounts;
        this.status = status;
    }

    public Mod(JSONObject mod) {
        this.version = mod.getJSONArray("versions").getJSONObject(0).getString("version");
        this.name = mod.getJSONArray("versions").getJSONObject(0).getString("name");
        this.id = mod.getString("id");
        for (int i = 0; i < mod.getJSONArray("developers").length(); i++) {
            this.developers.add(new ModDeveloper(mod.getJSONArray("developers").getJSONObject(i)));
        }
        this.downloadCounts = mod.getInt("download_count");
        this.status = mod.getJSONArray("versions").getJSONObject(0).getString("status");
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

    public String getStatus() {
        return status;
    }

    public String getID() {
        return id;
    }

    public List<ModDeveloper> getDevelopers() {
        return developers;
    }
}
