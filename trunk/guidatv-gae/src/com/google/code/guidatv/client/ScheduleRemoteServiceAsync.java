package com.google.code.guidatv.client;

import java.util.Date;
import java.util.Set;

import com.google.code.guidatv.client.model.LoginInfo;
import com.google.code.guidatv.client.model.ScheduleResume;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ScheduleRemoteServiceAsync {
    
    void getLoginInfo(String requestUri, AsyncCallback<LoginInfo> callback);

    void getDayScheduleResume(Date day, Set<String> channels, AsyncCallback<ScheduleResume> callback);

    void savePreferredChannels(Set<String> channels,
            AsyncCallback<Void> callback);
}
