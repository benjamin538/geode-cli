package com.benjamin538.index.mods;

// json
import org.json.JSONObject;

public class ModDeveloper {
    private String username;
    private String displayName;
    private int id;
    private boolean isOwner;

    public ModDeveloper(String username, String displayName, int id, boolean isOwner) {
        this.username = username;
        this.displayName = displayName;
        this.id = id;
        this.isOwner = isOwner; 
    }

    public String getUsername() {
        return username;
    }

    public String displayName() {
        return displayName;
    }

    public int getID() {
        return id;
    }

    public boolean getIsOwner() {
        return isOwner;
    }

    public JSONObject exportJSON() {
        JSONObject profile = new JSONObject();
        profile.put("id", id);
        profile.put("username", username);
        profile.put("display_name", displayName);
        profile.put("is_owner", isOwner);
        return profile;
    }
}
