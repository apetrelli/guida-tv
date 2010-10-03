package com.google.code.guidatv.server.service;

import java.util.Date;

import com.google.code.guidatv.client.model.Channel;
import com.google.code.guidatv.client.model.Schedule;

public interface ScheduleService {

    Schedule getSchedule(Channel channel, Date day);

    Schedule getSchedule(Channel channel, Date start, Date end);
}
