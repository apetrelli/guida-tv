package com.google.code.guidatv.server.service.impl.italy.generalistic.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.code.guidatv.client.model.Transmission;

public interface TelecomTransmissionDao {

    Map<String, List<Transmission>> getTransmissions(Date day);
}
