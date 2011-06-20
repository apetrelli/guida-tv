/*
 *  Guida TV: una guida TV per canali italiani.
 *  Copyright (C) 2011 Antonio Petrelli
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.google.code.guidatv.model;

import java.io.Serializable;

public class Channel implements Serializable {

    private static final long serialVersionUID = -6692458541793432252L;

    private String code;

    private String name;

    private String genre;

    private String localeCode;

    private String network;

    public Channel() {
    }

    public Channel(String code, String name, String genre, String localeCode, String network) {
        init(code, name, genre, localeCode, network);
    }

    public void init(String code, String name, String genre, String localeCode, String network) {
        this.code = code;
        this.name = name;
        this.genre = genre;
        this.localeCode = localeCode;
        this.network = network;
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

    public String getNetwork() {
        return network;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + ((genre == null) ? 0 : genre.hashCode());
        result = prime * result
                + ((localeCode == null) ? 0 : localeCode.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((network == null) ? 0 : network.hashCode());
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
        if (genre == null) {
            if (other.genre != null)
                return false;
        } else if (!genre.equals(other.genre))
            return false;
        if (localeCode == null) {
            if (other.localeCode != null)
                return false;
        } else if (!localeCode.equals(other.localeCode))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (network == null) {
            if (other.network != null)
                return false;
        } else if (!network.equals(other.network))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Channel [" + (code != null ? "code=" + code + ", " : "")
                + (name != null ? "name=" + name + ", " : "")
                + (genre != null ? "genre=" + genre + ", " : "")
                + (localeCode != null ? "localeCode=" + localeCode + ", " : "")
                + (network != null ? "network=" + network : "") + "]";
    }

}
