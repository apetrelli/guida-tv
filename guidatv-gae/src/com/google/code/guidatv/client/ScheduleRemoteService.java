package com.google.code.guidatv.client;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.code.guidatv.client.model.LoginInfo;
import com.google.code.guidatv.client.model.Schedule;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("schedule")
public interface ScheduleRemoteService extends RemoteService {
    
    LoginInfo getLoginInfo(String requestUri);

    List<Schedule> getDaySchedule(Date day, Set<String> channels);
    
    void savePreferredChannels(Set<String> channels);
}
