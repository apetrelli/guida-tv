package com.google.code.guidatv.server.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.code.guidatv.client.model.Channel;
import com.google.code.guidatv.client.model.Schedule;
import com.google.code.guidatv.server.service.ScheduleService;
import com.google.code.guidatv.server.service.impl.italy.generalistic.IrisScheduleService;
import com.google.code.guidatv.server.service.impl.italy.generalistic.MediasetBaseScheduleService;
import com.google.code.guidatv.server.service.impl.italy.generalistic.RaiScheduleService;

public class ScheduleServiceImpl implements ScheduleService {

    private Map<String, ScheduleService> code2service;

    public ScheduleServiceImpl() {
        code2service = new HashMap<String, ScheduleService>();
        ScheduleService raiService = new RaiScheduleService();
        ScheduleService mediasetBaseService = new MediasetBaseScheduleService();
        code2service.put("RaiUno", raiService);
        code2service.put("RaiDue", raiService);
        code2service.put("RaiTre", raiService);
        code2service.put("Rai4", raiService);
        code2service.put("RaiNews", raiService);
        code2service.put("RaiSport1", raiService);
        code2service.put("RaiSport2", raiService);
        code2service.put("RaiEducational", raiService);
        code2service.put("Extra", raiService);
        code2service.put("Premium", raiService);
        code2service.put("Yoyo", raiService);
        code2service.put("CinemaWorld", raiService);
        code2service.put("RaiGulp", raiService);
        code2service.put("RaiEDU2", raiService);
        code2service.put("EuroNews", raiService);
        code2service.put("C5", mediasetBaseService);
        code2service.put("I1", mediasetBaseService);
        code2service.put("R4", mediasetBaseService);
        code2service.put("Iris", new IrisScheduleService());
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
