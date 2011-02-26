package com.google.code.guidatv.client;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.code.guidatv.model.LoginInfo;
import com.google.code.guidatv.model.Schedule;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ScheduleRemoteServiceAsync {

    void getLoginInfo(String requestUri, AsyncCallback<LoginInfo> callback);

    void savePreferredChannels(Set<String> channels,
            AsyncCallback<Void> callback);

    void getDaySchedule(Date day, Set<String> channels,
            AsyncCallback<List<Schedule>> callback);
}
