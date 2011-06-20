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