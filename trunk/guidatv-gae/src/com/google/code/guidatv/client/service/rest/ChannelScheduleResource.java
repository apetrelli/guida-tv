package com.google.code.guidatv.client.service.rest;

import org.restlet.resource.Get;

import com.google.code.guidatv.client.model.Schedule;

public interface ChannelScheduleResource {

    @Get
    public Schedule retrieve();

}
