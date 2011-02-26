package com.google.code.guidatv.server.service.impl.italy.generalistic.dao;

import java.util.Date;
import java.util.List;

import com.google.code.guidatv.model.Transmission;

public interface IrisTransmissionDao {

    List<Transmission> getTransmissions(Date day);
}
