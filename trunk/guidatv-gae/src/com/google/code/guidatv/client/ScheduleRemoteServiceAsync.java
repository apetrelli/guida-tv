package com.google.code.guidatv.client;

import java.util.Date;

import com.google.code.guidatv.client.model.ScheduleResume;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ScheduleRemoteServiceAsync {

    void getDayScheduleResume(Date day, AsyncCallback<ScheduleResume> callback);
}
