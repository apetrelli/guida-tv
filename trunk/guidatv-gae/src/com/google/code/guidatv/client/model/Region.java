package com.google.code.guidatv.client.model;

public class Region {
    private String code;
    
    private String name;

    public Region(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
}
