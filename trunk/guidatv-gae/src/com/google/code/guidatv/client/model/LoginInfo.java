package com.google.code.guidatv.client.model;

import java.io.Serializable;

public class LoginInfo implements Serializable{

    private static final long serialVersionUID = 7327393245678574218L;

    private String nickname;
    
    private String url;
    
    private String linkLabel;

    public LoginInfo() {
    }
    
    public LoginInfo(String nickname, String url, String linkLabel) {
        init(nickname, url, linkLabel);
    }

    public void init(String nickname, String url, String linkLabel) {
        this.nickname = nickname;
        this.url = url;
        this.linkLabel = linkLabel;
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
    
    
}
