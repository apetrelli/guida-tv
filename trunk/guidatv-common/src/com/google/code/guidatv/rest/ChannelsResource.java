package com.google.code.guidatv.rest;

import java.util.Collection;

import org.restlet.resource.Get;

import com.google.code.guidatv.model.Channel;

public interface ChannelsResource {

    @Get
    public Collection<Channel> retrieve();

}
