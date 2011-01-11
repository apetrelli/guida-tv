package com.google.code.guidatv.client.service.rest;

import org.restlet.client.resource.ClientProxy;
import org.restlet.client.resource.Get;
import org.restlet.client.resource.Result;

import com.google.code.guidatv.client.model.LoginInfo;


public interface LoginInfoResourceProxy extends ClientProxy {

    @Get
    void retrieve(Result<LoginInfo> callback);
}
