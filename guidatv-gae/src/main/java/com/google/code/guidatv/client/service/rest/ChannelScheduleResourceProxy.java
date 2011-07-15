package com.google.code.guidatv.client.service.rest;

import org.restlet.client.resource.ClientProxy;
import org.restlet.client.resource.Get;
import org.restlet.client.resource.Result;

import com.google.code.guidatv.model.Schedule;

public interface ChannelScheduleResourceProxy extends ClientProxy {

    @Get
    public void retrieve(Result<Schedule> callback);

}
