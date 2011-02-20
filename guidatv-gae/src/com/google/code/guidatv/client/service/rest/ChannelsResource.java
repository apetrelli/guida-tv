package com.google.code.guidatv.client.service.rest;

import java.util.Collection;

import org.restlet.resource.Get;

import com.google.code.guidatv.client.model.Channel;

public interface ChannelsResource {

    @Get
    public Collection<Channel> retrieve();

}
