package com.google.code.guidatv.server.service.impl.italy.generalistic.dao;

import java.util.Date;
import java.util.List;

import com.google.code.guidatv.client.model.Channel;
import com.google.code.guidatv.client.model.Transmission;

public interface TelecomTransmissionDao {

    List<Transmission> getTransmissions(Channel channel, Date day);
}
