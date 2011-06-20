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
import java.util.Date;

public class Transmission implements Serializable {

    private Date start;

    private Date end;

    private String name;

    private String description;

    private String mainLink;

    public Transmission() {
    }

    public Transmission(String name, String description, Date start, Date end, String mainLink) {
        init(name, description, start, end, mainLink);
    }

    public void init(String name, String description, Date start, Date end,
            String mainLink) {
        this.name = name;
        this.description = description;
        this.start = start;
        this.end = end;
        this.mainLink = mainLink;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public String getMainLink() {
        return mainLink;
    }

    @Override
    public String toString() {
        return "Transmission ["
                + (start != null ? "start=" + start + ", " : "")
                + (end != null ? "end=" + end + ", " : "")
                + (name != null ? "name=" + name + ", " : "")
                + (description != null ? "description=" + description + ", "
                        : "")
                + (mainLink != null ? "mainLink=" + mainLink : "") + "]";
    }
}
