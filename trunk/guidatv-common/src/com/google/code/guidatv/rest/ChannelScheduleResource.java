package com.google.code.guidatv.rest;

import org.restlet.resource.Get;

import com.google.code.guidatv.model.Schedule;

public interface ChannelScheduleResource {

    @Get
    public Schedule retrieve();

}
