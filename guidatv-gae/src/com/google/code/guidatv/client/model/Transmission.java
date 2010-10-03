package com.google.code.guidatv.client.model;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Transmission implements Serializable, IsSerializable {

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
