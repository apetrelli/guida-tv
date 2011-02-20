package com.google.code.guidatv.server.service.rest;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.restlet.routing.TemplateRoute;

import com.google.code.guidatv.client.service.rest.ChannelsResource;

public class GuidaTvApplication extends Application {

    @Override
    public Restlet createInboundRoot() {
        Router router = new Router(getContext());
        TemplateRoute route = router.attach("/login-info", LoginInfoServerResource.class);
        route.setMatchingQuery(false);
        router.attach("/channels", ChannelsResource.class);
        router.attach("/channels/{channel}/schedules/{date}", ChannelScheduleServerResource.class);
        return router;
    }

}
