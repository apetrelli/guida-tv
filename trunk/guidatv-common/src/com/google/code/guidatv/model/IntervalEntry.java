package com.google.code.guidatv.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class IntervalEntry implements Serializable {
    private Date start;

    private Map<Channel, ChannelEntry> entries = new LinkedHashMap<Channel, ChannelEntry>();

    public IntervalEntry() {
    }

    public IntervalEntry(Date start) {
        init(start);
    }

    public void init(Date start) {
        this.start = start;
    }

    public Date getStart() {
        return start;
    }

    public ChannelEntry getEntry(Channel channel) {
        return entries.get(channel);
    }

    public void add(Channel channel, Transmission transmission) {
        ChannelEntry entry = entries.get(channel);
        if (entry == null) {
            entry = new ChannelEntry();
            entries.put(channel, entry);
        }
        entry.add(transmission);
    }

    @Override
    public String toString() {
        return "IntervalEntry ["
                + (start != null ? "start=" + start + ", " : "")
                + (entries != null ? "entries=" + entries : "") + "]";
    }
}