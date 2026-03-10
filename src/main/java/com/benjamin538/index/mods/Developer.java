package com.benjamin538.index.mods;

// json
import org.json.JSONObject;

public class Developer {
    private String username;
    private String displayName;
    private int id;
    private boolean verified;
    private boolean admin;

    public Developer(String username, String displayName, int id, boolean verified, boolean admin) {
        this.username = username;
        this.displayName = displayName;
        this.id = id;
        this.verified = verified;
        this.admin = admin;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getID() {
        return id;
    }

    public boolean getVerified() {
        return verified;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setDisplayName(String newDisplayName) {
        this.displayName = newDisplayName;
    }

    public JSONObject exportJSON() {
        JSONObject developerJSON = new JSONObject();
        developerJSON.put("id", id);
        developerJSON.put("username", username);
        developerJSON.put("display_name", displayName);
        developerJSON.put("verified", verified);
        developerJSON.put("admin", admin);
        return developerJSON;
    }
}
