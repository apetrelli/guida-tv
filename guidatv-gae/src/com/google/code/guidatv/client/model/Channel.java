package com.google.code.guidatv.client.model;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Channel implements Serializable, IsSerializable {

    private static final long serialVersionUID = -6692458541793432252L;

    private String code;

    private String name;

    private String genre;

    private String localeCode;
    
    private String network;

    public Channel() {
    }

    public Channel(String code, String name, String genre, String localeCode) {
        init(code, name, genre, localeCode);
    }

    public void init(String code, String name, String genre, String localeCode) {
        this.code = code;
        this.name = name;
        this.genre = genre;
        this.localeCode = localeCode;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Channel other = (Channel) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Channel [code=" + code + ", name=" + name + ", genre=" + genre
                + ", localeCode=" + localeCode + "]";
    }

}
