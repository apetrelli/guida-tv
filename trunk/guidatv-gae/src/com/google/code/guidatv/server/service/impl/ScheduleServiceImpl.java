package com.google.code.guidatv.server.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.code.guidatv.client.model.Channel;
import com.google.code.guidatv.client.model.Schedule;
import com.google.code.guidatv.server.service.ScheduleService;
import com.google.code.guidatv.server.service.impl.italy.generalistic.RaiScheduleService;

public class ScheduleServiceImpl implements ScheduleService {

    private Map<String, ScheduleService> code2service;

    public ScheduleServiceImpl() {
        code2service = new HashMap<String, ScheduleService>();
        ScheduleService raiService = new RaiScheduleService();
        code2service.put("RaiUno", raiService);
        code2service.put("RaiDue", raiService);
        code2service.put("RaiTre", raiService);
    }

    @Override
    public Schedule getSchedule(Channel channel, Date day) {
        return code2service.get(channel.getCode()).getSchedule(channel, day);
    }

    @Override
    public Schedule getSchedule(Channel channel, Date start, Date end) {
        return code2service.get(channel.getCode()).getSchedule(channel, start, end);
    }

}
