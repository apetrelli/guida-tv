package com.google.code.guidatv.client.service.rest;

import org.restlet.resource.Get;

import com.google.code.guidatv.client.model.LoginInfo;

public interface LoginInfoResource {

    @Get
    public LoginInfo retrieve();

}
