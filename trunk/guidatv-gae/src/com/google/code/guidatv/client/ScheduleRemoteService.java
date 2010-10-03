package com.google.code.guidatv.client;

import java.util.Date;

import com.google.code.guidatv.client.model.ScheduleResume;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("schedule")
public interface ScheduleRemoteService extends RemoteService {

    ScheduleResume getDayScheduleResume(Date day);
}
