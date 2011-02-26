package com.google.code.guidatv.rest;

import org.restlet.resource.Get;

import com.google.code.guidatv.model.LoginInfo;

public interface LoginInfoResource {

    @Get
    public LoginInfo retrieve();

}
