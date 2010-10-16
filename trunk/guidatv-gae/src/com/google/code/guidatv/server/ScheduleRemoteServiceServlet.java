package com.google.code.guidatv.server;

import java.util.Date;
import java.util.Set;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.code.guidatv.client.ScheduleRemoteService;
import com.google.code.guidatv.client.model.Channel;
import com.google.code.guidatv.client.model.LoginInfo;
import com.google.code.guidatv.client.model.ScheduleResume;
import com.google.code.guidatv.client.service.ChannelService;
import com.google.code.guidatv.client.service.impl.ChannelServiceImpl;
import com.google.code.guidatv.server.service.ScheduleService;
import com.google.code.guidatv.server.service.impl.ScheduleServiceImpl;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ScheduleRemoteServiceServlet extends RemoteServiceServlet implements ScheduleRemoteService{

    private ScheduleService service = new ScheduleServiceImpl();
    
    private ChannelService channelService = new ChannelServiceImpl();

    @Override
    public LoginInfo getLoginInfo(String requestUri) {
        LoginInfo info;
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (user != null) {
            info = new LoginInfo("Benvenuto " + user.getNickname(),
                    userService.createLogoutURL(requestUri), "Esci");
        } else {
            info = new LoginInfo("Benvenuto!",
                    userService.createLoginURL(requestUri), "Login");
        }
        return info;
    }

    @Override
    public ScheduleResume getDayScheduleResume(Date day, Set<String> channels) {
        Date start = new Date(day.getTime());
        Date end = new Date(day.getTime() + 24*60*60*1000);
        ScheduleResume resume = new ScheduleResume(start, end, 30);
        for (String channelCode: channels) {
            Channel channel = channelService.getChannelByCode(channelCode);
            if (channel != null) {
                resume.add(service.getSchedule(channel, day));
            }
        }
        return resume;
    }

}
