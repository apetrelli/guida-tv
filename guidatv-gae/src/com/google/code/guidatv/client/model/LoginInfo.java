package com.google.code.guidatv.client.model;

import java.io.Serializable;
import java.util.Set;

public class LoginInfo implements Serializable{

    private static final long serialVersionUID = 7327393245678574218L;

    private String nickname;
    
    private String url;
    
    private String linkLabel;
    
    private Set<String> preferredChannels;

    public LoginInfo() {
    }
    
    public LoginInfo(String nickname, String url, String linkLabel, Set<String> preferredChannels) {
        init(nickname, url, linkLabel, preferredChannels);
    }

    public void init(String nickname, String url, String linkLabel, Set<String> preferredChannels) {
        this.nickname = nickname;
        this.url = url;
        this.linkLabel = linkLabel;
        this.preferredChannels = preferredChannels;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUrl() {
        return url;
    }

    public String getLinkLabel() {
        return linkLabel;
    }
    
    public Set<String> getPreferredChannels() {
        return preferredChannels;
    }
    
}
