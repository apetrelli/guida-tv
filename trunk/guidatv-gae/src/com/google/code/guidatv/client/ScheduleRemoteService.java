package com.google.code.guidatv.client;

import java.util.Date;
import java.util.Set;

import com.google.code.guidatv.client.model.LoginInfo;
import com.google.code.guidatv.client.model.ScheduleResume;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("schedule")
public interface ScheduleRemoteService extends RemoteService {
    
    LoginInfo getLoginInfo(String requestUri);

    ScheduleResume getDayScheduleResume(Date day, Set<String> channels);
}
