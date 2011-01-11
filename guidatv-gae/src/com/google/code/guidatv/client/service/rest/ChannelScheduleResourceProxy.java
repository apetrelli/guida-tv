package com.google.code.guidatv.client.service.rest;

import org.restlet.client.resource.Result;
import org.restlet.resource.Get;

import com.google.code.guidatv.client.model.Schedule;

public interface ChannelScheduleResourceProxy {

    @Get
    public void retrieve(Result<Schedule> callback);

}
