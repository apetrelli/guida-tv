package com.google.code.guidatv.server.service.impl.italy.generalistic.dao;

import java.util.Date;
import java.util.List;

import com.google.code.guidatv.model.Channel;
import com.google.code.guidatv.model.Transmission;

public interface RaiTransmissionDao {

    List<Transmission> getTransmissions(Channel channel, Date day);
}
