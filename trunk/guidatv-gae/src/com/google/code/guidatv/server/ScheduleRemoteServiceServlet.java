package com.google.code.guidatv.server;

import java.util.Date;

import com.google.code.guidatv.client.ScheduleRemoteService;
import com.google.code.guidatv.client.model.Channel;
import com.google.code.guidatv.client.model.Schedule;
import com.google.code.guidatv.client.model.ScheduleResume;
import com.google.code.guidatv.server.service.ScheduleService;
import com.google.code.guidatv.server.service.impl.ScheduleServiceImpl;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ScheduleRemoteServiceServlet extends RemoteServiceServlet implements ScheduleRemoteService{

    private ScheduleService service = new ScheduleServiceImpl();

    @Override
    public ScheduleResume getDayScheduleResume(Date day) {
        Channel rai1 = new Channel("RaiUno", "Rai 1", "generalistic", "it_IT");
        Channel rai2 = new Channel("RaiDue", "Rai 2", "generalistic", "it_IT");
        Channel rai3 = new Channel("RaiTre", "Rai 3", "generalistic", "it_IT");
        Schedule rai1Schedule = service.getSchedule(rai1, day);
        Schedule rai2Schedule = service.getSchedule(rai2, day);
        Schedule rai3Schedule = service.getSchedule(rai3, day);
        Date start = new Date(day.getTime());
        Date end = new Date(day.getTime() + 24*60*60*1000);
        ScheduleResume resume = new ScheduleResume(start, end, 30);
        resume.add(rai1Schedule);
        resume.add(rai2Schedule);
        resume.add(rai3Schedule);
        return resume;
    }

}
