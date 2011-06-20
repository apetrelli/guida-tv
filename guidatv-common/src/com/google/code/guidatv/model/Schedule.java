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
import java.util.List;

public class Schedule implements Serializable {

    private Channel channel;

    private List<Transmission> transmissions;

    public Schedule() {
    }

    public Schedule(Channel channel, List<Transmission> transmissions) {
        init(channel, transmissions);
    }

    public void init(Channel channel, List<Transmission> transmissions) {
        this.channel = channel;
        this.transmissions = transmissions;
    }

    public Channel getChannel() {
        return channel;
    }

    public List<Transmission> getTransmissions() {
        return transmissions;
    }

    @Override
    public String toString() {
        return "DaySchedule ["
                + (channel != null ? "channel=" + channel + ", " : "")
                + (transmissions != null ? "transmissions=" + transmissions
                        : "") + "]";
    }
}
