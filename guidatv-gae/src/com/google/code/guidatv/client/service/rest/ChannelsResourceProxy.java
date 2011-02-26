package com.google.code.guidatv.client.service.rest;

import java.util.Collection;

import org.restlet.client.resource.ClientProxy;
import org.restlet.client.resource.Get;
import org.restlet.client.resource.Result;

import com.google.code.guidatv.model.Channel;

public interface ChannelsResourceProxy extends ClientProxy {

    @Get
    public void retrieve(Result<Collection<Channel>> callback);

}
